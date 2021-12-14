package be.axa.model.environment.impl

import be.axa.model.environment.Environment

/**
* Openshift environments are aligned with AWS environmenst due to dependencies used behind the scenes
* prod
* preprod
* nonprod
* dev
* test
* qa
* uat
**/
enum EnvironmentOpenshift implements Environment {

    DEV("dev"),
    TEST("test"),
    PREPROD("preprod"),
    PROD("prod")
    
    private String environmentNameValue

    EnvironmentOpenshift(String environmentNameValue) {
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