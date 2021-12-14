package be.axa.deploy.openshift.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.openshift.service.AbstractOpenshiftDeploymentStrategy
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentOpenshift
import be.axa.store.artifactory.IBinariesRepository

class OpenshiftDeploymentWarTomcatDevelopmentStrategy extends AbstractOpenshiftDeploymentStrategy implements IDeploymentStrategy, Serializable {
    OpenshiftDeploymentWarTomcatDevelopmentStrategy(IBinariesRepository repository, properties) {
        super(repository,properties)
    }

    @Override
    protected String getProjectExtension() {
        return "openshift-war"
    }

    @Override
    protected boolean notifyCDE() {
        return false
    }

    @Override
    protected boolean isTagNeeded(){
        return false
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentOpenshift.DEV
    }

    @Override
    protected String getProjectName(){
        return "${properties[AxaProperties.OPENSHIFT_PROJECT].toLowerCase()}-${getEnvironmentType().getEnvironmentNameValue()}-axa-be" 
    }

    @Override
    protected String getResourcesName(){
        return properties[AxaProperties.OPENSHIFT_RESOURCES_NAME]
    }
}