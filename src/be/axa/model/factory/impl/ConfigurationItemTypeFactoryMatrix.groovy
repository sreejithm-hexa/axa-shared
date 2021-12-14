package be.axa.model.factory.impl

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType
import be.axa.model.configurationitem.strategy.impl.*
import be.axa.model.factory.IConfigurationItemTypeFactory

/**
 * This Factory is responsible to bind from the CMDB the CI TYPE Singleton Bean
 * If nothing is found in CMDB, it means that CI TYPE is not yet supported in our CI/CD process
 *
 * Created by DTDJ857 on 26/10/2017.
 */
class ConfigurationItemTypeFactoryMatrix implements IConfigurationItemTypeFactory {

    /**
     * matrice M(artefactType * deploymentTarget)
     * M(i,j) exist => CI TYPE(i,j) is supported
     * M(i,j) null => CI TYPE(i,j) is not yet supported
     */
    private static final Map<Object, Class<? extends ConfigurationItemType>> cmdbSingletonBeanResolver = [
            ([ArtefactType.EAR, DeploymentTarget.WEBLOGIC]) : EarWeblogicType.class,
            ([ArtefactType.ZIP, DeploymentTarget.JVM]) : ZipJvmType.class,
            ([ArtefactType.EAR, DeploymentTarget.ARTEFACTORY]): EarArtefactoryType.class,
            ([ArtefactType.WAR, DeploymentTarget.ARTEFACTORY]): WarArtefactoryType.class,
            ([ArtefactType.JAR, DeploymentTarget.ARTEFACTORY]): JarArtefactoryType.class,
            ([ArtefactType.ZIP, DeploymentTarget.ARTEFACTORY]) : ZipArtefactoryType.class,
            ([ArtefactType.WAR, DeploymentTarget.TOMCAT]): WarTomcatType.class,
            ([ArtefactType.JAR, DeploymentTarget.TOMCAT]): JarTomcatType.class,
            ([ArtefactType.EAR, DeploymentTarget.WEBSPHERE]): EarWebsphereType.class,
            ([ArtefactType.JAR, DeploymentTarget.JVM]): JarJvmType.class,
            ([ArtefactType.WAR, DeploymentTarget.JBOSS]): WarJbossType.class,
            ([ArtefactType.EAR, DeploymentTarget.WILDFLY]): EarWildflyType.class
    ]//TODO ci type depends only on artefactType

    public ConfigurationItemType createConfigurationItemType(ArtefactType artefactType, DeploymentTarget deploymentTarget, DeploymentPlatform deploymentPlatform){
        Class<? extends ConfigurationItemType> configurationItemType = cmdbSingletonBeanResolver[[artefactType, deploymentTarget]]
        if (configurationItemType) {
            return (ConfigurationItemType) configurationItemType.newInstance(deploymentPlatform)
        } else {
            return configurationItemType
        }
    }

}
