package be.axa.model.environment

/**
 * Created by DTDJ857 on 14/12/2017.
 */
interface Environment {

    String getTokenNameValue()

    String getEnvironmentNameValue()

    String getPropertiesBinaryStoreName()

    String getCheckDeploymentWindow()

    String getDeploymentStageToPerform()

}