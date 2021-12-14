package be.axa.deploy.strategy.impl

import be.axa.deploy.IDeploymentStrategy
import be.axa.deploy.openshift.service.impl.*
import be.axa.deploy.ca.ra.service.impl.*
import be.axa.deploy.strategy.IDeploymentPlanStrategySelector
import be.axa.model.VersionType
import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.strategy.IConfigurationItemTypeStrategy
import be.axa.store.artifactory.IBinariesRepository

/**
 * Created by DTDJ857 on 21/12/2017.
 */
class DeploymentPlanStrategyMatrix implements IDeploymentPlanStrategySelector, Serializable {

    private IBinariesRepository repository

    def properties

    DeploymentPlanStrategyMatrix(IBinariesRepository repository, properties) {
        this.repository = repository
        this.properties = properties
    }
/**
 * matrix M(branchName * artefactType * deploymentTarget)
 * The bean is resolved using the axa.properties parameters
 */
    private final def dpdbSingletonBeanResolver = [
            ([VersionType.DEVELOPMENT, ArtefactType.EAR, DeploymentTarget.WEBLOGIC, DeploymentPlatform.ONPREMISE])         : new ReleaseAutomationDeploymentEarWeblogicDevelopmentStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.ZIP, DeploymentTarget.JVM, DeploymentPlatform.ONPREMISE])              : new ReleaseAutomationDeploymentZipJvmDevelopmentStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.EAR, DeploymentTarget.WEBSPHERE, DeploymentPlatform.PUREAPPLICATION])  : new ReleaseAutomationDeploymentEarWebsphereDevelopmentStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.PUREAPPLICATION])     : new ReleaseAutomationDeploymentWarTomcatDevelopmentStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.PRIVATEIAAS])     : new ReleaseAutomationDeploymentWarTomcatPrivateIaaSDevelopmentStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.EAR, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.EAR, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.WAR, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.WAR, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.JAR, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.JAR, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.ZIP, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.ZIP, DeploymentTarget.ARTEFACTORY, DeploymentPlatform.ONPREMISE])      : new ReleaseAutomationNoDeploymentReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.JAR, DeploymentTarget.JVM, DeploymentPlatform.ONPREMISE])              : new ReleaseAutomationDeploymentJarJvmOnPremiseDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.JAR, DeploymentTarget.JVM, DeploymentPlatform.ONPREMISE])              : new ReleaseAutomationDeploymentJarJvmOnPremiseReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.JAR, DeploymentTarget.JVM, DeploymentPlatform.PUREAPPLICATION])        : new ReleaseAutomationDeploymentJarJvmPureAppDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.JAR, DeploymentTarget.JVM, DeploymentPlatform.PUREAPPLICATION])              : new ReleaseAutomationDeploymentJarJvmPureAppReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.ZIP, DeploymentTarget.JVM, DeploymentPlatform.PUREAPPLICATION]): new ReleaseAutomationDeploymentZipJvmPureAppDevelopmentStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.EAR, DeploymentTarget.WILDFLY, DeploymentPlatform.ONPREMISE])         : new ReleaseAutomationDeploymentEarWildflyDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.EAR, DeploymentTarget.WILDFLY, DeploymentPlatform.ONPREMISE])         : new ReleaseAutomationDeploymentEarWildflyReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.WAR, DeploymentTarget.JBOSS, DeploymentPlatform.ONPREMISE])         : new ReleaseAutomationDeploymentWarJbossOnPremiseDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.WAR, DeploymentTarget.JBOSS, DeploymentPlatform.ONPREMISE])         : new ReleaseAutomationDeploymentWarJbossOnPremiseReleaseStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.EAR, DeploymentTarget.WEBLOGIC, DeploymentPlatform.ONPREMISE])             : new ReleaseAutomationDeploymentEarWeblogicReleaseStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.ZIP, DeploymentTarget.JVM, DeploymentPlatform.ONPREMISE])                  : new ReleaseAutomationDeploymentZipJvmReleaseStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.EAR, DeploymentTarget.WEBSPHERE, DeploymentPlatform.PUREAPPLICATION])      : new ReleaseAutomationDeploymentEarWebsphereReleaseStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.PUREAPPLICATION])         : new ReleaseAutomationDeploymentWarTomcatReleaseStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.PRIVATEIAAS])         : new ReleaseAutomationDeploymentWarTomcatPrivateIaaSReleaseStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.ZIP, DeploymentTarget.JVM, DeploymentPlatform.PUREAPPLICATION])    : new ReleaseAutomationDeploymentZipJvmPureAppReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.BLUEMIX])             : new ReleaseAutomationDeploymentWarTomcatBluemixDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.BLUEMIX])             : new ReleaseAutomationDeploymentWarTomcatBluemixReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.OPENSHIFT])             : new OpenshiftDeploymentWarTomcatDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.WAR, DeploymentTarget.TOMCAT, DeploymentPlatform.OPENSHIFT])             : new OpenshiftDeploymentWarTomcatReleaseStrategy(repository, properties),
            ([VersionType.DEVELOPMENT, ArtefactType.JAR, DeploymentTarget.TOMCAT, DeploymentPlatform.OPENSHIFT])             : new OpenshiftDeploymentJarTomcatDevelopmentStrategy(repository, properties),
            ([VersionType.RELEASE, ArtefactType.JAR, DeploymentTarget.TOMCAT, DeploymentPlatform.OPENSHIFT])             : new OpenshiftDeploymentJarTomcatReleaseStrategy(repository, properties)
    ]

    @Override
    IDeploymentStrategy selectDeploymentPlanStrategy(VersionType versionType, IConfigurationItemTypeStrategy configurationItemType) {
        return dpdbSingletonBeanResolver.get(
                [
                        versionType,
                        configurationItemType.getArtefactType() as ArtefactType,
                        configurationItemType.getDeploymentTarget() as DeploymentTarget,
                        configurationItemType.getDeploymentPlatform() as DeploymentPlatform
                ]
        )
    }

}
