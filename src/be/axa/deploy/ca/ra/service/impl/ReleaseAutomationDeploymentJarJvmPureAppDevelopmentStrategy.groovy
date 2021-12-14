package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentPureApp
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.environment.impl.EnvironmentNone

/**
 * Created by DTDJ857 on 07/12/2017.
 */
class ReleaseAutomationDeploymentJarJvmPureAppDevelopmentStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {


    ReleaseAutomationDeploymentJarJvmPureAppDevelopmentStrategy(IBinariesRepository repository, Object properties) {
        super(repository, properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        return "pure-jar"
    }

    @Override
    protected String getApplication() {
        return "PURE"
    }

    @Override
    protected String getTemplateCategory() {
        return "generic_IAAS_PureApp"
    }

    @Override
    protected String getDeploymentTemplate() {
        return "SSH"
    }

    @Override
    protected boolean notifyCDE() {
        return false
    }

    @Override
    protected Environment getEnvironmentType() {       
        return EnvironmentPureApp.IT1                 
    }

    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        return [
                "devChannel"         : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"         : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"  : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}",
                "environmentTarget"  : "ldvjapa0",
                "deploymentType"     : "sshRemoteScriptCall",
                "sshUser"            : "deploy",
                "sshRemoteScriptId"  : "deploy_pureapp",
                "sshRemoteScriptArgs": "${[repository.getArtefactURL("jar", VersionType.DEVELOPMENT), repository.getEnvPropertiesURL(environment, VersionType.DEVELOPMENT), environment.getEnvironmentNameValue()].join(" ")}"
        ]

    }
}
