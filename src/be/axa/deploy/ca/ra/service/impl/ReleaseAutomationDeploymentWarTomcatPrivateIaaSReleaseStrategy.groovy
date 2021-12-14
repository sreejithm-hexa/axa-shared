package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentPrivateIaaS
import be.axa.store.artifactory.IBinariesRepository

/**
 * Created by DTDJ857 on 07/12/2017.
 */
class ReleaseAutomationDeploymentWarTomcatPrivateIaaSReleaseStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {

    ReleaseAutomationDeploymentWarTomcatPrivateIaaSReleaseStrategy(IBinariesRepository repository, properties) {
        super(repository,properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        "privateiaas-war"
    }

    @Override
    protected String getApplication() {
        "PRIVATEIAAS-Tomcat-JAVA"
    }

    @Override
    protected String getTemplateCategory() {
        "war-tomcat-privateiaas"
    }

    @Override
    protected String getDeploymentTemplate() {
        "Deployment"
    }

    @Override
    protected boolean notifyCDE() {
        return true
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentPrivateIaaS.FT
    }

    @Override
    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "jfrogArtifactManifestURL": "${repository.getArtefactManifestURL(VersionType.RELEASE)}",
                "jfrogArtifactTokenURL"   : "${repository.getEnvPropertiesURL(environment)}",
                "artifact-url"            : "${repository.getArtefactURL("war",VersionType.RELEASE)}",
                "artifact-id"             : "${pom.groupId}.${pom.artifactId}",
                "artifact-version"       : "${pom.version}",
                "mavenArtifactType"       : "war",
                "devChannel"              : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"              : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"       : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}",
                "checkDeploymentWindow"   : "${environment.getCheckDeploymentWindow()}",
                "enableSmokeTest"         : "${properties[AxaProperties.ENABLE_SMOKE_TEST]}",
                "smokeTestEndpoint"       : "${properties[AxaProperties.SMOKE_TEST_ENDPOINT]}",
                "restartApplicationServer": "${properties[AxaProperties.RESTART_APPLICATION_SERVER]}",
                "secureTokenFileName"     : "${properties[AxaProperties.SECURE_TOKEN_FILENAME]}"
        ]

    }
}
