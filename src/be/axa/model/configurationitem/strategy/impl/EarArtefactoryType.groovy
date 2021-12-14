package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Library
 *
 * Created by DNBR981 on 13/01/2020.
 */
class EarArtefactoryType extends ConfigurationItemType {

    EarArtefactoryType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.EAR, DeploymentTarget.ARTEFACTORY, deploymentPlatform)
    }

    @Override
    String getArtefactPath(Object pom) {
        return ["${pom.artifactId}-",this.getPackaging(),"/target/*.",this.getPackaging()].join()
    }

    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.ONPREMISE.equals(deploymentPlatform as DeploymentPlatform)
    }

    @Override
    boolean isRunnable() {
        return false
    }
}