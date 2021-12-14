package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * War running on a Tomcat
 *
 * Created by DTDJ857 on 27/10/2017.
 */
class WarTomcatType extends ConfigurationItemType {

    WarTomcatType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.WAR, DeploymentTarget.TOMCAT, deploymentPlatform)
    }

    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.BLUEMIX.equals(this.deploymentPlatform as DeploymentPlatform) ||
                DeploymentPlatform.PUREAPPLICATION.equals(this.deploymentPlatform as DeploymentPlatform) ||
                DeploymentPlatform.PRIVATEIAAS.equals(this.deploymentPlatform as DeploymentPlatform) ||
                DeploymentPlatform.OPENSHIFT.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
