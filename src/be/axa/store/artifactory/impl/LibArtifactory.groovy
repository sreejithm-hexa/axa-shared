package be.axa.store.artifactory.impl

import be.axa.build.maven.Maven
import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.EnviromentTarget

class LibArtifactory implements IBinariesRepository {

    final static String RELEASE_REPO = "libs-releases";
    final static String SNAPSHOT_REPO = "libs-snapshots";

    final static Map REPOS = [
            (VersionType.RELEASE) : RELEASE_REPO,
            (VersionType.DEVELOPMENT) : SNAPSHOT_REPO
    ]

    Maven mvn;

    LibArtifactory(Maven maven) {
        this.mvn = maven;
    }

    @Override
    def deployRelease(String artefactLocation = null,String staticFileName = null) {
        mvn.deploy(Maven.PROFILE_RELEASE)
    }

    @Override
    def deployReleaseCandidate(String artefactLocation = null,EnviromentTarget envTarget,String staticFileName = null) {
        mvn.deploy(Maven.PROFILE_SNAPSHOT)
    }
    
    @Override
    String getArtefactManifestURL(VersionType versionType) {
        return this.getArtefactURL("pom", versionType)
    }

    @Override
    String getArtefactURL(String packaging, VersionType versionType) {
        def repo = REPOS[versionType]
        def pom = mvn.pom;
        def jfrogArtifactURL = "https://artifacts.axa.be/artifactory/${repo}/${getGroupIdWithSlashes()}/${pom.artifactId}/${pom.version}/${pom.artifactId}-${pom.version}.${packaging}"
        return jfrogArtifactURL
    }

    @Override
    String getEnvPropertiesURL(Environment environment) {
        // no env properties are stored
        return null
    }

    @Override
    String getEnvPropertiesURL(Environment environment,VersionType versionType) {
        return null
    }

    @Override
    def checkArtifactWritePermission(VersionType versionType) {
        if (VersionType.RELEASE.equals(versionType)) {
            mvn.pipeline.steps.echo(REPOS[versionType]+' is currently re-writable')
            // checkIfArtifactAlreadyExists(REPOS[versionType])
        }
    }
    
    private String getGroupIdWithSlashes(){
        def pom = mvn.pom;
        def groupIdWithSlashes = "${pom.groupId}".replace('.', '/')
        return groupIdWithSlashes
    }
}
