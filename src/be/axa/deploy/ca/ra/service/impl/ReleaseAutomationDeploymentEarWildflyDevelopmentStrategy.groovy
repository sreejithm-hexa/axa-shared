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
 * JBOSS Wildfly deployment strategy for DPM
 */
class ReleaseAutomationDeploymentEarWildflyDevelopmentStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {


    ReleaseAutomationDeploymentEarWildflyDevelopmentStrategy(IBinariesRepository repository, Object properties) {
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

    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "artifact-id"             : "${pom.groupId}.${pom.artifactId}",                
                "artifact-version"        : "${pom.version}",
                "artifact-url"            : "${repository.getArtefactURL("ear",VersionType.DEVELOPMENT)}",
                "jfrogArtifactTokenURL"   : "${repository.getEnvPropertiesURL(environment,VersionType.DEVELOPMENT)}",
                "devChannel"              : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"              : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"       : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}"
        ]
    }
}
