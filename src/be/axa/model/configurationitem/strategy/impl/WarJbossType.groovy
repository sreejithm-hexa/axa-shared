package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * WAR running on Jboss
 *
 * Created by DTDJ857 on 27/10/2017.
 */
class WarJbossType extends ConfigurationItemType {

    WarJbossType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.WAR, DeploymentTarget.JBOSS, deploymentPlatform)
    }


    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.ONPREMISE.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
