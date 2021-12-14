package be.axa.model.environment.impl

import be.axa.model.environment.Environment
import be.axa.store.artifactory.AtsArtifactoryRepository

/**
 * Created by DTDJ857 on 07/12/2017.
 */
enum EnvironmentBluemix implements Environment {

    IT1("it1","IT1", AtsArtifactoryRepository.propertiesBinaryStore, "false", "Post-Deployment"),
    IT2("it2","IT2", AtsArtifactoryRepository.propertiesBinaryStore, "false", "Post-Deployment"),
    FT1("ft1","FT1", AtsArtifactoryRepository.atsPropertiesBinaryStore, "true", "None"),
    FT2("ft2","FT2", AtsArtifactoryRepository.atsPropertiesBinaryStore, "true", "None"),
    STG("staging","STG", AtsArtifactoryRepository.atsPropertiesBinaryStore, "true", "None"),
    PRD("production","PRD", AtsArtifactoryRepository.atsPropertiesBinaryStore, "true", "None")

    private String tokenNameValue
    private String environmentNameValue
    private String propertiesBinaryStoreName
    private String checkDeploymentWindow
    private String deploymentStageToPerform

    EnvironmentBluemix(String tokenNameValue, String environmentNameValue, String propertiesBinaryStoreName, String checkDeploymentWindow, String deploymentStageToPerform) {
        this.tokenNameValue = tokenNameValue
        this.environmentNameValue = environmentNameValue
        this.propertiesBinaryStoreName = propertiesBinaryStoreName
        this.checkDeploymentWindow = checkDeploymentWindow
        this.deploymentStageToPerform = deploymentStageToPerform
    }

    @Override
    String getTokenNameValue() {
        return tokenNameValue
    }

    @Override
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