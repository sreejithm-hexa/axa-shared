package be.axa.deploy.ca.ra.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.ca.ra.service.AbstractReleaseAutomationDeploymentStrategy
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentOnPremise
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.environment.impl.EnvironmentNone

/**
 * Created by DTDJ857 on 07/12/2017.
 */
class ReleaseAutomationDeploymentJarJvmOnPremiseDevelopmentStrategy extends AbstractReleaseAutomationDeploymentStrategy implements IDeploymentStrategy, Serializable {


    ReleaseAutomationDeploymentJarJvmOnPremiseDevelopmentStrategy(IBinariesRepository repository, Object properties) {
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
        return false
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentOnPremise.IT       
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
                // pipeline is not in the arguments. Getting buildnumber from POM seems unfeasible
                "sshRemoteScriptArgs": "${[environment.getEnvironmentNameValue(), "${pom.groupId}.${pom.artifactId}", "${pom.version}", "draa000", "${timestamp}"].join(" ")}"
        ]

    }
}
