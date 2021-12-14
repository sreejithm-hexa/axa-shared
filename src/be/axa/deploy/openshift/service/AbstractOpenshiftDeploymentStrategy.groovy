package be.axa.deploy.openshift.service

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.build.maven.Maven
import be.axa.model.environment.Environment
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.scm.SCM
import be.axa.deploy.ca.cdd.CDD
import be.axa.store.artifactory.impl.ArtifactoryUploader
import be.axa.deploy.openshift.service.Openshift

abstract class AbstractOpenshiftDeploymentStrategy implements IDeploymentStrategy, Serializable {

    public static final String DOCKER_FOLDER = "docker"

    protected IBinariesRepository repository

    def properties

    AbstractOpenshiftDeploymentStrategy(IBinariesRepository repository, properties) {
        this.repository = repository
        this.properties = properties
    }

    @Override
    void deploy(Maven maven, pipeline, SCM scm) {       
        def pom = maven.pom
        def os = new Openshift(pipeline, getResourcesName()) // initializing openshift class, passing pipeline object

        def openshift_console_url = pipeline.env.OPENSHIFT_CONSOLE_URL
        if(!openshift_console_url)
            pipeline.steps.error("No URL for Openshift console")

        def openshift_credentials = pipeline.getCredentialsFromId(Openshift.TOKEN_ID)
        if(!openshift_credentials)
            pipeline.steps.error("No credentials with id : ${Openshift.TOKEN_ID}")
        def openshift_token = openshift_credentials["Password"]    

        // Login to Openshift console
        def loginResponse = os.login(openshift_token)
        if(loginResponse!=null) {
            pipeline.steps.error response
        }

        // Select a project    
        def projectExist = os.selectProject(getProjectName())
       
        if(projectExist)
            pipeline.steps.error("Openshift project does not exist")

        // Check "docker" folder exists
        if (!pipeline.steps.fileExists(DOCKER_FOLDER))
            pipeline.steps.error("'docker' folder does not exist")

        // Trigger a build  
        os.buildFromDockerFile(DOCKER_FOLDER)
        
        // Tag the newly created image
        if(isTagNeeded()){
            os.tagVersion(pom.version)
        }

        def deploy_params = [os.getCurrentProject(), getResourcesName(), pom.groupId + "." + pom.artifactId]
        
        // Add smoke test endpoint to deploy parameters if needed
        if(properties[AxaProperties.ENABLE_SMOKE_TEST]){
            deploy_params.add(properties[AxaProperties.SMOKE_TEST_ENDPOINT])
        }else{
            deploy_params.add("null")
        }

        def applicationName = [pom.groupId, pom.artifactId, getProjectExtension()].join(".")
        def mails = ["${properties[AxaProperties.DEV_CHANNEL]}",
                     "${properties[AxaProperties.OPS_CHANNEL]}",
                     "${properties[AxaProperties.MANAGEMENT_CHANNEL]}"].join(" ")
        
        def map = [
            "${applicationName}.emails" : mails,
            "${applicationName}.parameters" : deploy_params.join("&")
        ]

        // Call CDD for deployment
        if(notifyCDE()){
            // Time of the deployment : used as unicity purpose of the deployment name
            def now = new Date().format("yyyyMMddHHmmss")        

            def cdd = new CDD(pipeline.steps, pipeline.env.CDD_BASE_URL, pipeline.env.CDD_TOKEN)
            cdd.updateTokenAndNotifyCDE(
                    "${pom.groupId}.${pom.artifactId}.${getProjectExtension()}",
                    "${pom.version}",
                    "${cdd.getApplicationVersionNumber("${pom.version}")}",
                    "${pom.version}",
                    map
            )      
  
        }else{
            def openshift_deploy_job = pipeline.env.OPENSHIFT_DEPLOY_JOB   

            pipeline.steps.build(job: openshift_deploy_job.replace('job','').replace('//','/'), 
                                 parameters: [[$class: 'StringParameterValue', name: 'project_and_resources_names', value: deploy_params.join("&")], 
                                              [$class: 'StringParameterValue', name: 'environment_name', value: "dev"],
                                              [$class: 'StringParameterValue', name: 'version', value: pom.version],
                                              [$class: 'StringParameterValue', name: 'emails', value: mails]],
                                 wait: true)
        }     
    }    

    protected abstract String getProjectExtension()    

    protected abstract boolean notifyCDE()

    protected abstract Environment getEnvironmentType()

    protected abstract String getProjectName()

    protected abstract String getResourcesName()

    protected abstract boolean isTagNeeded()
}