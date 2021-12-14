package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * EAR running on websphere
 *
 * Created by DTDJ857 on 27/10/2017.
 */
class EarWebsphereType extends ConfigurationItemType {

    EarWebsphereType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.EAR, DeploymentTarget.WEBSPHERE, deploymentPlatform)
    }


    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.PUREAPPLICATION.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
