package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * EAR JBoss Wildfly for DPM application
 */
class EarWildflyType extends ConfigurationItemType {

    EarWildflyType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.EAR, DeploymentTarget.WILDFLY, deploymentPlatform)
    }


    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.ONPREMISE.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
