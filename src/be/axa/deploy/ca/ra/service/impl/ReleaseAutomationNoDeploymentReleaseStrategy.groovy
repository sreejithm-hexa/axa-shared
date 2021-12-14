package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentNone
import be.axa.store.artifactory.IBinariesRepository

/**
 * Strategy defined when no deployment is required, f.i. only storage in Artefactory.
 * Oddity: One for dev and one for release because VersionType is fixed.
 */
class ReleaseAutomationNoDeploymentReleaseStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {

    ReleaseAutomationNoDeploymentReleaseStrategy(IBinariesRepository repository, properties) {
        super(repository,properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        ""
    }

    @Override
    protected String getApplication() {
        ""
    }

    @Override
    protected String getTemplateCategory() {
        ""
    }

    @Override
    protected String getDeploymentTemplate() {
        ""
    }

    @Override
    protected boolean notifyCDE() {
        return false
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentNone
    }

    @Override
    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "jfrogArtifactManifestURL": "${repository.getArtefactManifestURL(VersionType.RELEASE)}",
                "jfrogArtifactTokenURL"   : "",
                "mavenArtifactType"       : "",
                "devChannel"              : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"              : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"       : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}",
                "checkDeploymentWindow"   : "",
                "enableSmokeTest"         : "",
                "smokeTestEndpoint"       : ""
        ]

    }
}
