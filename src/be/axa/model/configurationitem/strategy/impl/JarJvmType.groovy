package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Spring Boot executable Jar
 *
 * Created by DTDJ857 on 27/10/2017.
 */
class JarJvmType extends ConfigurationItemType {

    JarJvmType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.JAR, DeploymentTarget.JVM, deploymentPlatform)
    }

    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.ONPREMISE.equals(this.deploymentPlatform as DeploymentPlatform) ||
                DeploymentPlatform.PUREAPPLICATION.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
