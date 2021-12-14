package be.axa.model.environment.impl

import be.axa.model.environment.Environment

/**
 * This environment can be used while nothing is provisioned or available.
 */
enum EnvironmentNone implements Environment {

    None("None"),

    private String environmentNameValue

    EnvironmentNone(String environmentNameValue) {
        this.environmentNameValue = environmentNameValue
    }

    String getTokenNameValue() {
    }

    String getEnvironmentNameValue() {
        return environmentNameValue
    }

    @Override
    String getPropertiesBinaryStoreName() {
    }

    @Override
    String getCheckDeploymentWindow() {
    }

    @Override
    String getDeploymentStageToPerform() {
    }
}