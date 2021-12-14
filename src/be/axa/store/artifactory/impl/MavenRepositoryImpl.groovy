package be.axa.store.artifactory.impl

import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.store.artifactory.AtsArtifactoryRepository
import be.axa.store.artifactory.http.HttpClient
import be.axa.build.maven.Maven
import groovy.json.JsonSlurperClassic
import be.axa.model.EnviromentTarget

/**
 * Created by DTDJ857 on 31/07/2017.
 * Modified by DSND714 on 12/09/2019
 */
class MavenRepositoryImpl extends AtsArtifactoryRepository {

    /**
     * The maven pom XML object
     */
    def pom

    static final REPOS = [
            (VersionType.RELEASE):atsBinaryStore,
            (VersionType.DEVELOPMENT):binaryStore
    ]

     static final PropertyREPOS = [
            (VersionType.RELEASE):atsPropertiesBinaryStore,
            (VersionType.DEVELOPMENT):propertiesBinaryStore
    ]
    
    MavenRepositoryImpl(Object steps, Object pom) {
        super(steps)
        this.pom = pom
    }

    @Override
    def deployRelease(String artefactLocation = "",String staticFileName = null) {
        this.deployToJfrogArtifactory("ft1.properties", "${atsBinaryStore}", "${atsPropertiesBinaryStore}", artefactLocation, staticFileName)
    }

    @Override
    def deployReleaseCandidate(String artefactLocation = "",EnviromentTarget envTarget,String staticFileName = null) {
        this.deployToJfrogArtifactory("${envTarget.getEnviromentTargetName()}.properties", "${binaryStore}", "${propertiesBinaryStore}", artefactLocation, staticFileName)
    }

    @Override
    String getArtefactManifestURL(VersionType versionType) {
        //RA DP property : Manifest file URL used during Pre-plan init phase for JAVA Application only
        return this.getArtefactURL("pom", versionType)
    }
    /*
    Added to dynamic selection of repo based on branch name
    
    */
    @Override
    String getArtefactURL(String packaging, VersionType versionType) {
        // RA DP property : Artefact URL used during Pre-plan init phase for PURE Application only
        def jfrogArtifactURL = "https://atsartifactory.axa.be/artifactory/${getStore(versionType)}/${getGroupIdWithSlashes()}/${pom.artifactId}/${pom.version}/${pom.artifactId}-${pom.version}.${packaging}"
        return jfrogArtifactURL
    }


    @Override
    String getEnvPropertiesURL(Environment environment) {
        // RA DP property : Token URL used during the Pre-deploy phase for JAVA and PURE Applications
        def jfrogArtifactTokenURL = "https://atsartifactory.axa.be/artifactory/${environment.getPropertiesBinaryStoreName()}/${getGroupIdWithSlashes()}/${getArtifactIdWithSlashes()}/${pom.version}/${environment.getTokenNameValue()}.properties"
        return jfrogArtifactTokenURL
    }

    @Override
    String getEnvPropertiesURL(Environment environment,VersionType versionType) {
       // RA DP property : Token URL used during the Pre-deploy phase for JAVA and PURE Applications
       // todo change hardcode propeties name
        def jfrogArtifactTokenURL = "https://atsartifactory.axa.be/artifactory/${getPropertyStore(versionType)}/${getGroupIdWithSlashes()}/${getArtifactIdWithSlashes()}/${pom.version}/${environment.getTokenNameValue()}.properties"
        return jfrogArtifactTokenURL
    }

    private String getGroupIdWithSlashes(){
        def groupIdWithSlashes = "${pom.groupId}".replace('.', '/')
        return groupIdWithSlashes
    }
    
    private String getArtifactIdWithSlashes(){
        def artifactIdIdWithSlashes = "${pom.artifactId}".replace('_', '/')
        return artifactIdIdWithSlashes
    }
    def deployToJfrogArtifactory(String tokenFile, String buildStoreName, String tokenStoreName, String artifactWorkspaceLocation, String staticFileName){
            
        //No checkpoint here. Only the result of the build is uploaded.
        //Upload into artifactory axa-buildstore of the artefact file previously generated using the maven deploy-file inline plugin
        steps.withEnv(["PATH+MAVEN=${steps.tool Maven.MVN_JENKINS_TOOL_ID}/bin"]){
            def files = steps.findFiles(glob: artifactWorkspaceLocation)
            if (files.size() >= 1)
            {
                steps.echo "Files : ${files}"
                files.each{file ->  
                    steps.sh "mvn -V deploy:deploy-file -DrepositoryId=artifactory.buildStore -Durl=https://atsartifactory.axa.be/artifactory/"+buildStoreName+" -Dfile=${file.path} -Dtype=${pom.packaging} -DgroupId=${pom.groupId} -DartifactId=${pom.artifactId} -Dversion=${pom.version}"
                }
            }
            else
                steps.error("No artifact(s) found to be uploaded to artifactory")
        }

        if (staticFileName != null)
        {
            try 
            {
                steps.echo "Static content files: ${staticFileName}"
                def tempArray = staticFileName.split(" ")

                def artifactFolder = artifactWorkspaceLocation.tokenize('/').first()

                tempArray.each {fileName ->
                    def fullFilePath = artifactFolder + '/target/' + fileName
                    def artifactoryStaticFileURL = 'https://atsartifactory.axa.be/artifactory/' + buildStoreName + '/' + getGroupIdWithSlashes() + '/' + getArtifactIdWithSlashes() + '/' + pom.version + '/' + fileName
                    steps.sh('curl --insecure -H \"X-JFrog-Art-Api: ' + artifactoryTokenKey + '\" -X PUT \"' + artifactoryStaticFileURL + '\" -T ' + fullFilePath)
                }
                
            } catch (Exception e) {
                steps.error(e.printStackTrace())
            }
        }

        //Deployment on artifactory axa-tokenstore-java the token file dedicated for IT
        def tokenFileTarget = 'envProperties/' + tokenFile
        def artifactoryTokenStoreURL = 'https://atsartifactory.axa.be/artifactory/'+tokenStoreName+'/' + getGroupIdWithSlashes() + '/' + getArtifactIdWithSlashes() + '/' + pom.version + '/' + tokenFile
        // -k option is mandatory because of curl: (60) Peer certificate cannot be authenticated with known CA certificates
        def putArtifact = 'curl --insecure -H \"X-JFrog-Art-Api: ' + artifactoryTokenKey + '\" -X PUT \"' + artifactoryTokenStoreURL + '\" -T ' + tokenFileTarget

        try {
            //TODO checkpoint with jenkins Enterprise and timeout if request hang after 3 retry
            steps.sh(putArtifact)
        } catch (Exception e) {
            steps.error(e.printStackTrace())
        }
    }

    @Override
    def checkArtifactWritePermission(VersionType versionType){
        if(VersionType.RELEASE.equals(versionType)){
            checkIfArtifactAlreadyExists()
        }
    }
    
    /**
     * This method check if the given artifact GAV exist in ATS artifactory axatech-buildstore
     * The goal is to avoid that a release build start if the EAR version already exist
     * @param artifactName the artifact name
     */
    private void checkIfArtifactAlreadyExists() {
        String groupId = pom.groupId
        String artifactID = pom.artifactId
        String version = pom.version
        boolean artifactAlreadyExistWithReleaseVersion = false
        HttpClient httpClient = new HttpClient(steps)
        try {
            steps.retry(3) {
                def httpResponse = httpClient.getMavenArtifact(groupId, artifactID, version, "${atsBinaryStore}")
                def responseJSON = new JsonSlurperClassic().parseText(httpResponse)
                artifactAlreadyExistWithReleaseVersion = (responseJSON.results.size() > 0)
            }
        } catch (Exception e) {
            steps.error("No reply from artifactory." + e.getMessage())
        }

        if (artifactAlreadyExistWithReleaseVersion) {
            steps.error([groupId, '.', artifactID, '-', version].join() + " already exists in ${atsBinaryStore}, please update version.")
        } else {
            steps.echo([groupId, '.', artifactID, '-', version].join() + " does not exist in ${atsBinaryStore}, CI process continues.")
        }
    }

    private static getStore(VersionType versionType) {
        return REPOS[versionType];
    }

    private static getPropertyStore(VersionType versionType) {
        return PropertyREPOS[versionType];
    }
}
