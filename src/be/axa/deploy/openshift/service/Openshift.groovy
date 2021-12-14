package be.axa.deploy.openshift.service

import be.axa.store.artifactory.impl.ArtifactoryUploader
import be.axa.deploy.openshift.client.OC
/**
 *
 * This class is meant to collect openshift functions that are used in various solutions.
 *
 */
class Openshift implements Serializable {

    public static final String TOKEN_ID = "Openshift_token" 
    public static final String TOKEN_ID_PRD = "Openshift_token_prd" 

    private def pipeline
    private def steps
    // static values
    private static final String openshift_console_url = "https://osconsole.pink.eu-central-1.aws.openpaas.axa-cloud.com"
    private static final String HIDE_CMD = "#!/bin/sh -e\n "
    private static final String OC = "/appl/oc_client/oc"
    private static final String DEFAULT_MOUNT_PATH = "/data/config/"

    // globally available, will always be needed
    private final String resource_name = ""

    // initializing class with parameters which we'll always need
    Openshift(pipeline, String resourceName) {
        this.pipeline = pipeline
        this.steps = pipeline.steps
        this.resource_name = resourceName
    }

    def passFile(String paramValue){
        def fileName
        try{
            fileName = pipeline.unstashParam paramValue
        }
        catch(Exception e){
            steps.echo e.toString()
            steps.echo "No file uploaded"
            fileName = null
        }
        return fileName
    }

    def login(openshift_token) {
        if(!openshift_token){
            return "No credentials available for Openshift"
        } else {
            steps.sh HIDE_CMD + "${OC} login ${openshift_console_url} --token=${openshift_token}" // login to Openshift
        }  
    }

    def selectProject(String project_name){ 
        def exitCode = steps.sh(script: "${OC} project ${project_name}", returnStatus: true)
        if(exitCode == 1)
            return false
    }

    def getCurrentProject(){
        return steps.sh(script: "${OC} project --short", returnStdout: true).trim()
    }

    def buildFromDockerFile(String dir){
        steps.sh "${OC} start-build ${resource_name} --from-dir=${dir} --follow"
    }

    def setResources(String cpu, String memory){
        steps.sh "${OC} set resources dc/${resource_name} --limits=cpu=${cpu},memory=${memory}"
    }

    // def getCurrentActiveImage(){
    //     // retrieve the ? (id or tag) of the current active image
    //     steps.sh "${OC} get istag/${resource_name}:release"
    // }

    def tagVersion(String version){
        // update deployment with version tag
        steps.sh "${OC} tag ${resource_name}:latest ${resource_name}:${version}"
    }

    def moveTagAlias(String version, String type="latest"){
        steps.sh "${OC} tag ${resource_name}:${version} ${resource_name}:${type} --alias"
    }

    // def configmap(file, String mount_path=DEFAULT_MOUNT_PATH){
    //     // create configmap -> overwrite if exists
    //     steps.sh "${OC} create configmap <name> --from-file=${file} --dry-run -o yaml | ${OC} apply -f -"
    //     // Mount configmap
    //     steps.sh "${OC} set volumes dc/${resource_name} --overwrite --add -t configmap -m ${mount_path} --name=<> --configmap-name=<name>"
    // }

    def secrets(file,String type, String mount_path=DEFAULT_MOUNT_PATH){
        switch(type) {
            case "keystore": 
                steps.sh "${OC} create secret generic ${resource_name}-${type} --from-file=${file} --type=opaque --dry-run -o yaml | ${OC} apply -f -"
                steps.sh "${OC} set volumes dc/${resource_name} --overwrite --add -t secret -m ${mount_path} --name=keystore-location --secret-name=${resource_name}-${type}"
            default: // token
                steps.sh "${OC} create secret generic ${resource_name} --from-file=${file} --dry-run -o yaml | ${OC} apply -f -"
                steps.sh "${OC} set volumes dc/${resource_name} --overwrite --add -t secret -m ${mount_path} --name=${type} --secret-name=${resource_name}"
        }
    }

    def setTriggers(String imageName){ 
        steps.sh "${OC} set triggers dc/${resource_name} --containers=${resource_name} --from-image=${imageName}" 
    }

    def removeAllTriggers(){ 
        steps.sh "${OC} set triggers dc/${resource_name} --remove-all" 
    }

    def removeProbes(){ 
        steps.sh "${OC} set probe dc/${resource_name} --remove --readiness --liveness" 
    }

    def rolloutLatest(){ 
        // will deploy the latest revision and to clarify no, it has no relation to latest tag
        steps.sh "${OC} rollout latest dc/${resource_name}" 
    }

    def promoteImage(String project, String tag){
        // import from one imagestream into another, lower to higher environment (can also have been done by tag)
        // docker-registry.default.svc:5000/automation-test-axa-be/automation-be:<tag>
        steps.sh "${OC} tag ${project}/${resource_name}:${tag} automation-be:${tag}"
        this.moveTagAlias(tag, "release")
    }

    def isContainerUp(){
        def cpt = 0
        Boolean is_container_ready = false
        def max = 12
        sleep(10)
        while(!is_container_ready && cpt < max){
            def containers = steps.sh(script: "${OC} get pod --selector app=${resource_name} --output custom-columns=:.status.phase", returnStdout: true)
            println containers
            
            sleep(10)  
            is_container_ready = (containers.trim() == "Running")            
            cpt += 1          
        }

        if(cpt == max) {
            return "Timeout reached."
        } else {
            return "Running"
        }
    }

    def updateTokens(String application_name, String version, String env, non_secure, Boolean updateTokenVersion){
        String properties_file = "${resource_name}.${env}.properties"
        def merged_tokens = "${application_name}.properties"
        application_name = application_name.replace('.', '/')

        // init Artifactory
        def artifactory = new ArtifactoryUploader(steps)
        def artifactory_relative_url = "axatech-tokenstore-java/${application_name}"

        def tokens_get_url = "${artifactory_relative_url}/${properties_file}"
        def tokens_push_url = "${artifactory_relative_url}/${version}/${properties_file}"

        if(!updateTokenVersion)
            tokens_get_url = tokens_push_url

        // download secure tokens from Artifactory
        def result = artifactory.getArtifact(tokens_get_url)
        if(result != '200')
            return "Something went wrong while downloading artifact '${tokens_get_url}' (code: ${result}"
        steps.echo non_secure
        // upload secure tokens to right version only for new versions
        // when updating tokens, previous file will be overwritten
        if(updateTokenVersion)
            artifactory.deployRelease(tokens_push_url)

        def part1 = steps.readFile(properties_file).trim()
        if(non_secure.equals(null)){
            steps.writeFile file: merged_tokens, text: part1, encoding: "UTF-8"
            steps.echo "Renamed secure token file " + merged_tokens
        } else {
            def part2 = steps.readFile(non_secure).trim()
            steps.writeFile file: merged_tokens, text: part1 + "\r\n" + part2, encoding: "UTF-8"
            steps.echo "Contents of files merged and written to: " + merged_tokens  
        }
        this.secrets(merged_tokens)
    }

    def buildTargetProjectName(String src_project, String env_name){
        // Get project name prefix
        // Example : 
        //   Project "automation-dev-axa-be"
        def project_prefix_array = src_project.substring(0, src_project.lastIndexOf("-axa-be")).split('-') // => "[0:automation][1:dev]"    
        def current_env = "-${project_prefix_array[project_prefix_array.length - 1]}-" // => -dev-
        def target_env = "-${env_name}-" // => -test-
        def target_project = src_project.replace(current_env, target_env) // => automation-test-axa-be
        return target_project
    }
}
