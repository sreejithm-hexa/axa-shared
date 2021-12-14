package be.axa.config

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentPlatform
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.util.Properties
import be.axa.model.VersionType
import be.axa.model.scm.impl.GitSCMStrategy
import be.axa.model.scm.ISCMStrategy
import be.axa.model.scm.SCM

/**
 * Object Mapping for the axa.properties configuration file
 */
class AxaProperties implements Serializable {

    private static final String[] REQUIRED_PROPERTIES = [OPS_CHANNEL, DEV_CHANNEL, MANAGEMENT_CHANNEL]
    private static final String[] OPENSHIFT_REQUIRED_PROPERTIES=[OPENSHIFT_PROJECT, OPENSHIFT_RESOURCES_NAME]
    private static final String[] ONPRIMESE_REQUIRED_PROPERTIES=[CONTINOUS_INTEGRATION_ON]
    private static final String axaPropertiesFileName = 'axa.properties'
    public static final String OPS_CHANNEL = "opsChannel"
    public static final String DEV_CHANNEL = "devChannel"
    public static final String MANAGEMENT_CHANNEL = "managementChannel"
    public static final String ENABLE_SMOKE_TEST = "enableSmokeTest"
    public static final String SMOKE_TEST_ENDPOINT = "smokeTestEndpoint"

    public static final String ARTEFACT_TYPE = "artefactType"
    public static final String DEPLOYMENT_TARGET = "deploymentTarget"
    public static final String DEPLOYMENT_PLATFORM = "deploymentPlatform"
    public static final String STATIC_FILE_NAME = "staticFileName"

    public static final String OPENSHIFT_PROJECT = "openshiftProject"
    public static final String OPENSHIFT_RESOURCES_NAME = "openshiftResourcesName"

    public static final String CONTINOUS_INTEGRATION_ON = "continuousIntegrationTo"

    public static final String RESTART_APPLICATION_SERVER = "restartApplicationServer"
    public static final String SECURE_TOKEN_FILENAME = "secureTokenFileName"


    def steps;
    def versionType;

    AxaProperties(steps) {
        this.steps = steps;       
    }

    def load(VersionType versionType) {
        if (!steps.fileExists(axaPropertiesFileName)) {
            steps.error("axa.properties file is missing")
        }
        this.versionType=versionType;
        def file = steps.readFile encoding: 'UTF-8', file: axaPropertiesFileName
        def propertiesMap = new Properties(file).load();
        checkMissingProperties(propertiesMap)
        propertiesMap = setDefaultProperties(propertiesMap)
        ensurecontinuousIntegrationTo(propertiesMap)
        ensurePropertiesCorrectness(propertiesMap)
        propertiesMap= ensureJspONPREMISE(propertiesMap)        
        return propertiesMap
    }

    private def ensurecontinuousIntegrationTo(propertiesMap){
        // OnPriMese chanegs for CONTINOUS_INTEGRATION_ON Mandatory for development branch
        DeploymentPlatform Platform=propertiesMap[DEPLOYMENT_PLATFORM] as DeploymentPlatform;
        DeploymentTarget target = propertiesMap[DEPLOYMENT_TARGET] as DeploymentTarget;
        steps.echo("${Platform} + ${versionType}")
        if(target != DeploymentTarget.ARTEFACTORY && Platform == DeploymentPlatform.getDefaultDeploymentPlatform() && (VersionType.DEVELOPMENT.equals(versionType)))
        {
            for (property in ONPRIMESE_REQUIRED_PROPERTIES) {
                if (propertyIsMissing(propertiesMap, property)) {
                    steps.error("The following properties is Missing : ${property}")                  
                }
            }
        }

    }

    private def ensureJspONPREMISE(propertiesMap){
        if (propertyIsMissing(propertiesMap, CONTINOUS_INTEGRATION_ON)) {
            DeploymentPlatform Platform=propertiesMap[DEPLOYMENT_PLATFORM] as DeploymentPlatform;
            if(Platform==DeploymentPlatform.getDefaultDeploymentPlatform()){        
                propertiesMap.put(CONTINOUS_INTEGRATION_ON,"FT")
            }
            else{
                propertiesMap.put(CONTINOUS_INTEGRATION_ON,"IT")
            }
        }
        return propertiesMap
    }

    private void checkMissingProperties(Map propertiesMap) {
        def missingProperties = []
        for (property in REQUIRED_PROPERTIES) {
            if (propertyIsMissing(propertiesMap, property)) {
                missingProperties.add(property);
            }
        }

        if(DEPLOYMENT_PLATFORM == "OPENSHIFT"){
            for (property in OPENSHIFT_REQUIRED_PROPERTIES) {
                if (propertyIsMissing(propertiesMap, property)) {
                    missingProperties.add(property);
                }
            }
        }      

        if (!missingProperties.isEmpty()) {
            steps.echo("WARNING: The following properties are empty: " + missingProperties + ".")
        }
    }

    static def setDefaultProperties(propertiesMap) {
        if (propertyIsMissing(propertiesMap, ENABLE_SMOKE_TEST)) {
            propertiesMap.put(ENABLE_SMOKE_TEST, "false")
        }
        if (propertyIsMissing(propertiesMap, SMOKE_TEST_ENDPOINT)) {
            propertiesMap.put(SMOKE_TEST_ENDPOINT, "version.jsp")
        }
        if (propertyIsMissing(propertiesMap, ARTEFACT_TYPE)) {
            propertiesMap.put(ARTEFACT_TYPE, ArtefactType.getDefaultArtefactType())
        }
        if (propertyIsMissing(propertiesMap, DEPLOYMENT_TARGET)) {
            propertiesMap.put(DEPLOYMENT_TARGET, DeploymentTarget.getDefaultDeploymentTarget())
        }
        if (propertyIsMissing(propertiesMap, DEPLOYMENT_PLATFORM)) {
            propertiesMap.put(DEPLOYMENT_PLATFORM, DeploymentPlatform.getDefaultDeploymentPlatform())
        }
        if (propertyIsMissing(propertiesMap, RESTART_APPLICATION_SERVER)) {
            propertiesMap.put(RESTART_APPLICATION_SERVER, "false")
        }
        if (propertyIsMissing(propertiesMap, SECURE_TOKEN_FILENAME)) {
            propertiesMap.put(SECURE_TOKEN_FILENAME, null)
        }
        return propertiesMap;
    }

    static def propertyIsMissing(propertiesMap, property) {
        return !propertiesMap.containsKey(property) || propertiesMap.get(property)==null || ((propertiesMap.get(property) instanceof String ) && !propertiesMap.get(property).trim());
    }

    def ensurePropertiesCorrectness(propertiesMap) {
        def wrongProperties = []

        try {
            propertiesMap.put(DEPLOYMENT_TARGET, propertiesMap[DEPLOYMENT_TARGET] as DeploymentTarget)
        } catch (IllegalArgumentException e) {
            wrongProperties.add("${DEPLOYMENT_TARGET} must be of: ${DeploymentTarget.values()}")
        }

        try {
            propertiesMap.put(ARTEFACT_TYPE, propertiesMap[ARTEFACT_TYPE] as ArtefactType)
        } catch (IllegalArgumentException e) {
            wrongProperties.add("${ARTEFACT_TYPE} must be of: ${ArtefactType.values()}")
        }

        try {
            propertiesMap.put(DEPLOYMENT_PLATFORM, propertiesMap[DEPLOYMENT_PLATFORM] as DeploymentPlatform)
        } catch (IllegalArgumentException e) {
            wrongProperties.add("${DEPLOYMENT_PLATFORM} must be of: ${DeploymentPlatform.values()}")
        }

        if(!wrongProperties.isEmpty()){
            steps.error("The following properties are wrong: ${wrongProperties}.")
        }
    }
}
