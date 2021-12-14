package be.axa.model.configurationitem.strategy.common

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.IConfigurationItemTypeStrategy

/**
 * CI Type POJO
 *
 * Created by DTDJ857 on 26/10/2017.
 */
abstract class ConfigurationItemType implements IConfigurationItemTypeStrategy {

    /**
     * The artefact type of the CI Type
     */
    protected ArtefactType artefactType;

    /**
     * The deployment target of the CI Type
     */
    protected DeploymentTarget deploymentTarget;

    /**
     * The deployment platform of the CI Type
     */
    protected DeploymentPlatform deploymentPlatform;

    ConfigurationItemType(ArtefactType artefactType, DeploymentTarget deploymentTarget, DeploymentPlatform deploymentPlatform) {
        this.artefactType = artefactType
        this.deploymentTarget = deploymentTarget
        this.deploymentPlatform = deploymentPlatform
    }

    boolean accept(String sdkVersion) {
        return this.deploymentPlatform.getSuportedJDKs().collect { sdk -> sdk.version }.contains(sdkVersion)
    }

    @Override
    String getPackaging() {
        return artefactType.getPackaging()
    }

    @Override
    String getArtefactType() {
        return artefactType
    }

    @Override
    String getDeploymentTarget() {
        return deploymentTarget
    }

    @Override
    String getDeploymentPlatform() {
        return deploymentPlatform
    }

    @Override
    String getArtefactPath(Object pom) {       
        return ["${pom.artifactId}-",this.getPackaging(),"/target/*.",this.getPackaging()].join()
    }

    @Override
    boolean isRunnable() {
        return true
    }
}
