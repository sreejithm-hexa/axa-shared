package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentOnPremise
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.environment.impl.EnvironmentNone

/**
 * Created by DTDJ857 on 07/12/2017.
 */
class ReleaseAutomationDeploymentEarWeblogicDevelopmentStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {

    ReleaseAutomationDeploymentEarWeblogicDevelopmentStrategy(IBinariesRepository repository, properties) {
        super(repository,properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        "java-ear"
    }

    @Override
    protected String getApplication() {
        "JAVA"
    }

    @Override
    protected String getTemplateCategory() {
        "generic_WebLogic12"
    }

    @Override
    protected String getDeploymentTemplate() {
        "WebLogic12_single_EAR_IT"
    }

    @Override
    protected boolean notifyCDE() {
        return false
    }

    @Override
    protected Environment getEnvironmentType() {
        def envType = properties[AxaProperties.CONTINOUS_INTEGRATION_ON] as String;
        if(envType.equalsIgnoreCase("IT")|| envType.equalsIgnoreCase("IT1")){
            return EnvironmentNone
        }
        if(envType.equalsIgnoreCase("FT")|| envType.equalsIgnoreCase("FT1")){
            return EnvironmentOnPremise.FT
        } 
        
    }

    @Override
    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "jfrogArtifactManifestURL": "${repository.getArtefactManifestURL(VersionType.DEVELOPMENT)}",
                "jfrogArtifactTokenURL"   : "${repository.getEnvPropertiesURL(environment,VersionType.DEVELOPMENT)}",
                "mavenArtifactType"       : "ear",
                "devChannel"              : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"              : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"       : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}",
                "checkDeploymentWindow"   : "${environment.getCheckDeploymentWindow()}",
                "enableSmokeTest"         : "${properties[AxaProperties.ENABLE_SMOKE_TEST]}",
                "smokeTestEndpoint"       : "${properties[AxaProperties.SMOKE_TEST_ENDPOINT]}"
        ]

    }
}
