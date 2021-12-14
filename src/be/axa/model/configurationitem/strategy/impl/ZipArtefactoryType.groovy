package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Copy of JarArtefactory
 */
class ZipArtefactoryType extends ConfigurationItemType {

    ZipArtefactoryType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.ZIP, DeploymentTarget.ARTEFACTORY, deploymentPlatform)
    }

    @Override
    String getArtefactPath(Object pom) {
        return ["target/${pom.artifactId}-${pom.version}.",this.getPackaging()].join()
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
