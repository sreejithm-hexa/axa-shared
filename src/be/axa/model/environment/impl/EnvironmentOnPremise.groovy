package be.axa.model.environment.impl

import be.axa.model.environment.Environment
import be.axa.store.artifactory.AtsArtifactoryRepository

/**
 * Created by DTDJ857 on 07/12/2017.
 */
enum EnvironmentOnPremise implements Environment {

    IT("it1","IT", AtsArtifactoryRepository.binaryStore, AtsArtifactoryRepository.propertiesBinaryStore, "false", "Post-Deployment"),
    FT("ft1","FT", AtsArtifactoryRepository.atsBinaryStore, AtsArtifactoryRepository.atsPropertiesBinaryStore, "true", "None"),
    STG("staging","STG", AtsArtifactoryRepository.atsBinaryStore, AtsArtifactoryRepository.atsPropertiesBinaryStore, "true", "None"),
    PRD("production","PRD", AtsArtifactoryRepository.atsBinaryStore, AtsArtifactoryRepository.atsPropertiesBinaryStore, "true", "None")

    private String tokenNameValue
    private String environmentNameValue
    private String artefactBinaryStoreName
    private String propertiesBinaryStoreName
    private String checkDeploymentWindow
    private String deploymentStageToPerform

    EnvironmentOnPremise(String tokenNameValue, String environmentNameValue, String artefactBinaryStoreName, String propertiesBinaryStoreName, String checkDeploymentWindow, String deploymentStageToPerform) {
        this.tokenNameValue = tokenNameValue
        this.environmentNameValue = environmentNameValue
        this.artefactBinaryStoreName = artefactBinaryStoreName
        this.propertiesBinaryStoreName = propertiesBinaryStoreName
        this.checkDeploymentWindow = checkDeploymentWindow
        this.deploymentStageToPerform = deploymentStageToPerform
    }

    String getTokenNameValue() {
        return tokenNameValue
    }

    String getEnvironmentNameValue() {
        return environmentNameValue
    }

    @Override
    String getPropertiesBinaryStoreName() {
        return propertiesBinaryStoreName
    }

    @Override
    String getCheckDeploymentWindow() {
        return checkDeploymentWindow
    }

    @Override
    String getDeploymentStageToPerform() {
        return deploymentStageToPerform
    }
}