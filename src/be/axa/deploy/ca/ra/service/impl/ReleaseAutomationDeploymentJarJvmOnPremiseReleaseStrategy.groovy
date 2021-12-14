package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentOnPremise
import be.axa.store.artifactory.IBinariesRepository

/**
 * Created by DTDJ857 on 07/12/2017.
 */
class ReleaseAutomationDeploymentJarJvmOnPremiseReleaseStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {


    ReleaseAutomationDeploymentJarJvmOnPremiseReleaseStrategy(IBinariesRepository repository, Object properties) {
        super(repository, properties)
    }

    @Override
    protected String getReleaseAutomationProjectExtension() {
        return "java-jar"
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
        return true
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentOnPremise.FT
    }

    protected LinkedHashMap<String, String> createDeploymentPlanProperties(Environment environment, pom) {
        def timestamp = new Date().format("yyyyMMddHHmmss")
        return [
                "devChannel"         : "${properties[AxaProperties.DEV_CHANNEL]}",
                "opsChannel"         : "${properties[AxaProperties.OPS_CHANNEL]}",
                "managementChannel"  : "${properties[AxaProperties.MANAGEMENT_CHANNEL]}",
                "environmentTarget"  : "ldvjapa0",
                "deploymentType"     : "sshRemoteScriptCall",
                "sshUser"            : "deploy",
                "sshRemoteScriptId"  : "deploy",
                "sshRemoteScriptArgs": "${[environment.getEnvironmentNameValue(), "${pom.groupId}.${pom.artifactId}", "${pom.version}", "draa000", "RELEASE"].join(" ")}"
        ]

    }
}
