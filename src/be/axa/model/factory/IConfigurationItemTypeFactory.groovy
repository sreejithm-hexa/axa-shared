package be.axa.model.factory

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType

/**
 * Created by DTDJ857 on 03/11/2017.
 */
interface IConfigurationItemTypeFactory {

    /**
     * Create a CI Type based on his artefact Type, deployment Target and deployment Platform
     * @param artefactType the artefact type
     * @param deploymentTarget the deployment target
     * @param deploymentPlatform the deployment platform
     * @return the CI Type
     */
    ConfigurationItemType createConfigurationItemType(ArtefactType artefactType, DeploymentTarget deploymentTarget, DeploymentPlatform deploymentPlatform)

}