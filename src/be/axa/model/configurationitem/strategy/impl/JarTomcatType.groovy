package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Jar running on a Tomcat
 *
 * Created by DNBR981 on 05/03/2020.
 */
class JarTomcatType extends ConfigurationItemType {

    JarTomcatType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.JAR, DeploymentTarget.TOMCAT, deploymentPlatform)
    }

    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.OPENSHIFT.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
