package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Created by DTDJ857 on 26/10/2017.
 */
class EarWeblogicType extends ConfigurationItemType {

    EarWeblogicType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.EAR, DeploymentTarget.WEBLOGIC, deploymentPlatform)
    }

    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.ONPREMISE.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
