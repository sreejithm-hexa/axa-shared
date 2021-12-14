package be.axa.validators

import be.axa.build.maven.Maven
import be.axa.model.VersionType
import be.axa.model.configurationitem.JavaDevelopmentKit
import be.axa.model.configurationitem.strategy.IConfigurationItemTypeStrategy
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.EnviromentTarget

class SolutionValidator {

    Map<VersionType, String> propertiesFiles = [
            (VersionType.DEVELOPMENT):"it1.properties",
            (VersionType.RELEASE):"ft1.properties"
    ]

    def pipeline
    private VersionType versionType
    private IConfigurationItemTypeStrategy ci
    def EnviromentTarget envTarget 

    SolutionValidator(pipeline, IConfigurationItemTypeStrategy ci, VersionType versionType,EnviromentTarget envTarget ) {
        this.ci = ci
        this.versionType = versionType
        this.pipeline = pipeline
        this.envTarget=envTarget
    }

    void checkDeploymentPlatformSupported() {
        if(!ci.isDeploymentPlatformSupported()) {
            pipeline.steps.error("Configuration Item Type (artefact type : ${ci.artefactType}, deployment target :${ci.deploymentTarget}) not yet supported for platform ${ci.deploymentPlatform}")
        }
    }

    void checkJavaVersionSupported(JavaDevelopmentKit jdk){
        if (!ci.accept(jdk.version)) {
            pipeline.steps.error("Configuration Item Type (artefact type : ${ci.artefactType}, deployment target :${ci.deploymentTarget}) not yet supported for java version ${jdk.version}")
        }
    }

    void checkVersionCompliant(Maven maven){
        maven.checkVersionCompliance(versionType)
    }

    /**
     * Check if tokens files exist for IT and FT environments and alert DEVs if not
     */
    void checkRuntimePropertiesArePresent(){
        if (ci.isRunnable()) {
           
            def fileName = propertiesFiles.get(versionType)
            if(versionType==VersionType.DEVELOPMENT && ( envTarget== EnviromentTarget.FT || envTarget== EnviromentTarget.FT1)){
                fileName=propertiesFiles.get(VersionType.RELEASE)
            }
            if(fileName != null){
                checkProperties("envProperties/${fileName}")
            }
        }
    }

    /**
     * Check if the version doesn't override another existing one, and if it does, if it is allowed
     */
    void checkWriteAccess(IBinariesRepository repository){
        repository.checkArtifactWritePermission(versionType);
    }

    private void checkProperties(propertyPath) {
        def tokenExist = pipeline.steps.fileExists propertyPath
        if (tokenExist) {
            String tokenFile = pipeline.steps.readFile propertyPath
            if (tokenFile.isEmpty()) {
                pipeline.steps.echo("WARNING: Token file ${propertyPath} is empty.")
            }
            pipeline.steps.echo("${propertyPath} TOKENS ${propertyPath} used for deployment tokenization process : " + tokenFile)
        } else {
            pipeline.steps.echo("WARNING: No ${propertyPath} file.")
        }
    }
}