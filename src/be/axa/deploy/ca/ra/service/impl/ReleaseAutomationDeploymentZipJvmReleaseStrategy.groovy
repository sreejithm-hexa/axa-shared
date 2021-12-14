package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentOnPremise
import be.axa.store.artifactory.IBinariesRepository

/**
 * Created by DTDJ857 on 07/12/2017.
 */
class ReleaseAutomationDeploymentZipJvmReleaseStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {

    ReleaseAutomationDeploymentZipJvmReleaseStrategy(IBinariesRepository repository, properties) {
        super(repository, properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        return "java-zip"
    }

    @Override
    protected String getApplication() {
        return "JAVA"
    }

    @Override
    protected String getTemplateCategory() {
        return "generic_WebLogic12"
    }

    @Override
    protected String getDeploymentTemplate() {
        return "WebLogic12_single_EAR_FT"
    }

    @Override
    protected boolean notifyCDE() {
        return true
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentOnPremise.FT
    }

    @Override
    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "jfrogArtifactManifestURL"  : "${repository.getArtefactManifestURL(VersionType.RELEASE)}",
                "jfrogArtifactTokenURL"     : "${repository.getEnvPropertiesURL(environment)}",
                "mavenArtifactType"         : "zip",
                "devChannel"                : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"                : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"         : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}",
                "checkDeploymentWindow"     : "${environment.getCheckDeploymentWindow()}",
                "enableSmokeTest"           : "${properties[AxaProperties.ENABLE_SMOKE_TEST]}",
                "smokeTestEndpoint"         : "${properties[AxaProperties.SMOKE_TEST_ENDPOINT]}"
        ]

    }
}
