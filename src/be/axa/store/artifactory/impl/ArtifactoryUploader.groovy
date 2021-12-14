package be.axa.store.artifactory.impl

import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.store.artifactory.AtsArtifactoryRepository
import be.axa.model.EnviromentTarget

class ArtifactoryUploader extends AtsArtifactoryRepository{

    ArtifactoryUploader(steps){
        super(steps)
    }

    /**
     * Get artifact from Artifactory
     * @param artefactLocation
     * @param tokenKey token key to call Artifactory API 
     */
    @Deprecated
    def getArtifact(String artefactLocation, String tokenKey) {
        steps.sh("curl -k -H \"X-JFrog-Art-Api: ${tokenKey}\" -O \"${artefactLocation}\"")
    }

    def getArtifact(String artefactLocation) {
        def artifactUrl = "${artifactoryUrl}/${artefactLocation}"
        def result = steps.sh(script: "curl -k -H \"X-JFrog-Art-Api: ${artifactoryTokenKey}\"  -w '%{http_code}' -O \"${artifactUrl}\"", returnStdout: true)
        return result
    }

    /**
     * Push artifacts to Artifactory
     * @param artefactLocation
     * @return artifact URL
     */
    @Override
    def deployRelease(String artefactLocation = "", String staticFileName = null) {
        def filePath = artefactLocation.tokenize('/').last()
        def artifactUrl = "${artifactoryUrl}/${artefactLocation}"

        steps.sh("curl -k -H \"X-JFrog-Art-Api: ${artifactoryTokenKey}\" -X PUT \"${artifactUrl}\" -T ${filePath}")
        return artifactUrl
    }

    def deployRelease(String artefactLocation, String filePath, String tokenKey)
    {
        def artifactUrl = "${artifactoryUrl}/${artefactLocation}"
        
        if(filePath=="")
            filePath = artefactLocation.tokenize('/').last()
      
        steps.sh("curl -k -H \"X-JFrog-Art-Api: ${tokenKey}\" -X PUT \"${artifactUrl}\" -T ${filePath}")
        return artifactUrl
    }

    @Override
    def deployReleaseCandidate(String artefactLocation = "",EnviromentTarget environment,String staticFileName = null) {
        throw new MissingMethodException("Release candidate not allowed")
    }

    @Override
    String getArtefactManifestURL(VersionType versionType) {
        return null
    }

    @Override
    String getArtefactURL(String packaging, VersionType versionType) {
        return null
    }

    @Override
    String getEnvPropertiesURL(Environment environment) {
        return null
    }

    @Override
    String getEnvPropertiesURL(Environment environment,VersionType versionType) {
        return null
    }

    @Override
    def checkArtifactWritePermission(VersionType versionType) {
        return null
    }
}