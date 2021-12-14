package be.axa.store.artifactory.http

import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.store.artifactory.AtsArtifactoryRepository
import be.axa.model.EnviromentTarget

/**
 * https://www.jfrog.com/confluence/display/RTF/Artifactory+Query+Language
 * Created by DTDJ857 on 01/08/2017.
 */
class HttpClient extends AtsArtifactoryRepository {

    HttpClient(steps){
        super(steps)
    }
    
    def getMavenArtifact(String groupId, String artifactID, String version, String repository){
        def httpResponse = steps.httpRequest consoleLogResponseBody: true,
                customHeaders: [[maskValue: true, name: 'X-JFrog-Art-Api', value: 'AKCp2V77Vd55MHdn1sLMCZEP79GTGVNGTAHbtwoHYGae6auMBHP7cgXxAu4hiBiNDaDZHNArP'], [maskValue: false, name: 'Accept', value: 'application/vnd.org.jfrog.artifactory.search.GavcSearchResult+json']],
                httpMode: 'GET',
                responseHandle: 'NONE',
                timeout: 10,
                url: "${artifactoryUrl}/api/search/gavc?g=${groupId}&a=${artifactID}&v=${version}&repos=${repository}"
        return httpResponse.getContent()
    }

    def getArtifactByPatternSearch(String repositoryName, String patternSearch, String tokenKey)
    {
        def httpResponse = steps.httpRequest consoleLogResponseBody: true,
            customHeaders: [[maskValue: true, name: 'X-JFrog-Art-Api', value: tokenKey], [maskValue: false, name: 'Accept', value: 'application/vnd.org.jfrog.artifactory.search.PatternResultFileSet+json']],
            httpMode: 'GET',
            responseHandle: 'NONE',
            timeout: 10,
            url: "${artifactoryUrl}/api/search/pattern?pattern=${repositoryName}:${patternSearch}"
        return httpResponse.getContent()
    }

    def getFolderInformation(String repositoryName, String folderPath, String tokenKey)
    {
        def httpResponse = steps.httpRequest consoleLogResponseBody: true,
            customHeaders: [[maskValue: true, name: 'X-JFrog-Art-Api', value: tokenKey], [maskValue: false, name: 'Accept', value: 'application/vnd.org.jfrog.artifactory.storage.FolderInfo+json']],
            httpMode: 'GET',
            responseHandle: 'NONE',
            timeout: 10,
            url: "${artifactoryUrl}/api/storage/${repositoryName}/${folderPath}"
        return httpResponse.getContent()
    }

    @Override
    def deployRelease(String artefactLocation = "",String staticFileName = null) {
        return null
    }

    @Override
    def deployReleaseCandidate(String artefactLocation = "",EnviromentTarget environment,String staticFileName = null) {
        return null
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
