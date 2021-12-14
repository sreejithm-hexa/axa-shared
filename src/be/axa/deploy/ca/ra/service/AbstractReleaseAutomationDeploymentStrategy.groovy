package be.axa.deploy.ca.ra.service

import be.axa.store.artifactory.IBinariesRepository
import be.axa.build.maven.Maven
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.cdd.CDD
import be.axa.deploy.ca.ra.dto.RunDeploymentPlanApiDto
import be.axa.deploy.ca.ra.factory.ReleaseAutomationFactory
import be.axa.deploy.ca.ra.status.ReleaseAutomationStatus
import be.axa.model.environment.Environment
import be.axa.model.scm.SCM
import be.axa.config.AxaProperties
/**
 * Created by DTDJ857 on 21/12/2017.
 * Modified bt DSND714 on 11/09/2019
 */
abstract class AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {

    protected IBinariesRepository repository

    def properties

    AbstractReleaseAutomationDeploymentStrategy(IBinariesRepository repository, properties) {
        this.repository = repository
        this.properties = properties
    }

    /**
     * Get the RA project extension
     * @return
     */
    protected abstract String getReleaseAutomationProjectExtension()

    /**
     * Get RA application
     * @return
     */
    protected abstract String getApplication()

    /**
     * Get RA template category
     * @return
     */
    protected abstract String getTemplateCategory()

    /**
     * Get RA Deployment Template
     * @return
     */
    protected abstract String getDeploymentTemplate()

    /**
     *
     * @param pom
     * @param pipeline
     * @param branchName
     * @param gitCommit
     * @param now
     * @return
     */
    protected RunDeploymentPlanApiDto createRunDeploymentPlanApiDto(pom, pipeline, SCM scm, now){

        Environment environment = getEnvironmentType()

        LinkedHashMap<String, String> deploymentPlanProperties = createDeploymentPlanProperties(environment, pom)
        def  deploymentStageToPerform;

        if(environment.getEnvironmentNameValue().startsWith("FT") && !notifyCDE()){                
               // RA call for FT deployemnts 
               deploymentStageToPerform = "Post-Deployment";
        } 
        else {
           deploymentStageToPerform = "${environment.getDeploymentStageToPerform()}";
        }
        return new RunDeploymentPlanApiDto(
                deploymentPlan: "${pom.version}",
                build: "${pom.version}-${pipeline.currentBuild.number}_${now}",
                project: "${pom.groupId}.${pom.artifactId}.${getReleaseAutomationProjectExtension()}",
                deploymentTemplate: "${getDeploymentTemplate()}",
                templateCategory: "${getTemplateCategory()}",
                application: "${getApplication()}",
                deployment: "Deployment-${pom.groupId}.${pom.artifactId}-${pipeline.currentBuild.number}_${now}",
                deploymentDescription: "${scm.getBranchName()}-${scm.getVersionId()}",
                environments: ["${environment.getEnvironmentNameValue()}"],
                properties: deploymentPlanProperties,
                deploymentStageToPerform: "${deploymentStageToPerform}"
        )

    }

    @Override
    void deploy(Maven maven, pipeline, SCM scm) {

        // Time of the deployment : used as unicity purpose of the deployment name
        def now = new Date().format("yyyyMMddHHmmss")
        def pom = maven.pom

        def deploymentPlanApiDto = createRunDeploymentPlanApiDto(pom, pipeline, scm, now)

        def ra_url = pipeline.env.RA_BASE_URL
        if(!ra_url)
            pipeline.steps.error("No URL for release-automation")
        //Release Automation client
        def raFactory = new ReleaseAutomationFactory()
        def releaseAutomation = raFactory.getReleaseAutomation(pipeline.steps, ra_url)

        //Continuous Delivery Director client
        def cdd = new CDD(pipeline.steps, pipeline.env.CDD_BASE_URL, pipeline.env.CDD_TOKEN)

        //Create and run the RA DP, fire and forget mode, jenkins pipeline continue if DP is running in ACTIVE status
        pipeline.steps.echo("DTO Deployment Plan Release Automation : " + deploymentPlanApiDto.toJSON())
        def deploymentId = releaseAutomation.runDeploymentPlan(deploymentPlanApiDto)
        releaseAutomation.waitForStatus(deploymentId, ReleaseAutomationStatus.ACTIVE)

        if(notifyCDE()){
            cdd.updateTokenAndNotifyCDE(
                    "${pom.groupId}.${pom.artifactId}.${getReleaseAutomationProjectExtension()}",
                    "${pom.version}-${pipeline.currentBuild.number}_${now}",
                    "${cdd.getApplicationVersionNumber("${pom.version}")}",
                    "${pom.version}")
        }
        
    }

    protected abstract boolean notifyCDE()

    protected abstract Environment getEnvironmentType()

    protected abstract LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom)
}
