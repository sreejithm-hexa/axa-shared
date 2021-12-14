package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentBluemix
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.environment.impl.EnvironmentNone

/**
 * Created by DTDJ857 on 07/12/2017.
 */
class ReleaseAutomationDeploymentWarTomcatBluemixDevelopmentStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {


    ReleaseAutomationDeploymentWarTomcatBluemixDevelopmentStrategy(IBinariesRepository repository, Object properties) {
        super(repository, properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        return "bluemix-war"
    }

    @Override
    protected String getApplication() {
        return "BLUEMIX"
    }

    @Override
    protected String getTemplateCategory() {
        return "private_cloud"
    }

    @Override
    protected String getDeploymentTemplate() {
        return "ShellScript"
    }

    @Override
    protected boolean notifyCDE() {
        return false
    }

    @Override
    protected Environment getEnvironmentType() {        
        return EnvironmentBluemix.IT1        
    }

    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "devChannel"         : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"         : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"  : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}",
                "environmentTarget"  : "dbba2024.ppprivmgmt.intraxa",
                "deploymentType"     : "localScriptCall",
                "sshUser"            : "deploy_tomcat",
                "sshRemoteScriptId"  : "/data/tomcat/deployArtifact.sh",
                "sshRemoteScriptArgs": "${[repository.getArtefactURL("war", VersionType.DEVELOPMENT), repository.getEnvPropertiesURL(environment, VersionType.DEVELOPMENT), environment.getEnvironmentNameValue()].join(" ")}"
        ]

    }
}
