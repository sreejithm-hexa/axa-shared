package be.axa.model.configurationitem.strategy.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Spring batch application build and deploy on JVM outside of PureApp in ATS
 *
 * Created by DTDJ857 on 27/10/2017.
 */
class ZipJvmType extends ConfigurationItemType {

    ZipJvmType(DeploymentPlatform deploymentPlatform) {
        super(ArtefactType.ZIP, DeploymentTarget.JVM, deploymentPlatform)
    }

    @Override
    String getArtefactPath(Object pom) {
        return ["target/${pom.artifactId}-${pom.version}.",this.getPackaging()].join()
    }

    @Override
    boolean isDeploymentPlatformSupported() {
        return DeploymentPlatform.ONPREMISE.equals(this.deploymentPlatform as DeploymentPlatform) ||
                DeploymentPlatform.PUREAPPLICATION.equals(this.deploymentPlatform as DeploymentPlatform)
    }
}
