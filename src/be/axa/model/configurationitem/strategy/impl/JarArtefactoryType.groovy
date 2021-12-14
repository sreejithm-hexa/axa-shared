package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Library
 *
 * Created by DTDJ857 on 27/10/2017.
 */
class JarArtefactoryType extends ConfigurationItemType {

    JarArtefactoryType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.JAR, DeploymentTarget.ARTEFACTORY, deploymentPlatform)
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
