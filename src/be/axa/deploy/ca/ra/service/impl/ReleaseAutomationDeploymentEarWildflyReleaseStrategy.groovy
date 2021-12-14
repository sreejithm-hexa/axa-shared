package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentOnPremise
import be.axa.store.artifactory.IBinariesRepository

/**
 * JBOSS Wildfly deployment strategy for DPM
 */
class ReleaseAutomationDeploymentEarWildflyReleaseStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {


    ReleaseAutomationDeploymentEarWildflyReleaseStrategy(IBinariesRepository repository, Object properties) {
        super(repository, properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        return "jboss"
    }

    @Override
    protected String getApplication() {
        return "DPM"
    }

    @Override
    protected String getTemplateCategory() {
        return "dpm"
    }

    @Override
    protected String getDeploymentTemplate() {
        return "Deployment"
    }

    @Override
    protected boolean notifyCDE() {
        return true
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentOnPremise.FT
    }

    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "artifact-id"             : "${pom.groupId}.${pom.artifactId}",                
                "artifact-version"        : "${pom.version}",
                "artifact-url"            : "${repository.getArtefactURL("ear",VersionType.RELEASE)}",
                "jfrogArtifactTokenURL"   : "${repository.getEnvPropertiesURL(environment,VersionType.RELEASE)}",
                "devChannel"              : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"              : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"       : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}"
        ]
    }
}
