package be.axa.deploy.openshift.service.impl

import be.axa.config.AxaProperties
import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.openshift.service.AbstractOpenshiftDeploymentStrategy
import be.axa.model.environment.Environment
import be.axa.model.environment.impl.EnvironmentOpenshift
import be.axa.store.artifactory.IBinariesRepository

class OpenshiftDeploymentJarTomcatReleaseStrategy extends AbstractOpenshiftDeploymentStrategy implements IDeploymentStrategy, Serializable {
    OpenshiftDeploymentJarTomcatReleaseStrategy(IBinariesRepository repository, properties) {
        super(repository,properties)
    }

    @Override
    protected String getProjectExtension() {
        return "openshift-jar"
    }

    @Override
    protected boolean notifyCDE() {
        return true
    }

    @Override
    protected boolean isTagNeeded(){
        return true
    }

    @Override
    protected Environment getEnvironmentType() {
        return EnvironmentOpenshift.TEST
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