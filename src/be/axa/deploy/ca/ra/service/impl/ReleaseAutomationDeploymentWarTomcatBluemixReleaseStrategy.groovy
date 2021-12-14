package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentBluemix
import be.axa.store.artifactory.IBinariesRepository

// Bluemix flow production
class ReleaseAutomationDeploymentWarTomcatBluemixReleaseStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {


    ReleaseAutomationDeploymentWarTomcatBluemixReleaseStrategy(IBinariesRepository repository, Object properties) {
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
        return true
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentBluemix.FT1
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
                "sshRemoteScriptArgs": "${[repository.getArtefactURL("war", VersionType.RELEASE), repository.getEnvPropertiesURL(environment), environment.getEnvironmentNameValue()].join(" ")}"
        ]

    }
}
