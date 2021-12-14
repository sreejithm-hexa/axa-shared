package be.axa.store.artifactory

import be.axa.model.VersionType
import be.axa.model.environment.Environment
import be.axa.model.EnviromentTarget

/**
 * Created by DTDJ857 on 13/11/2017.
 * Modified by DSND714 on 12/09/2019
 */
interface IBinariesRepository {

    /**
     * Deploy to artifactory release store with specific file to deploy
     * @return
     * @deprecated this methods exists only because mvn deploy cannot be used due to wrong parent pom. To be removed.
     */
    @Deprecated
    def deployRelease(String artefactLocation,String staticFileName)

    /**
     * Deploy to artifactory release store, with standard artefact
     * @return
     */
    def deployRelease()

    /**
     * Deploy to artifactory release candidate / SNAPSHOT store  with specific file to deploy
     * environment based deployment it or ft select properties file
     * @return
     * @deprecated this methods exists only because mvn deploy cannot be used due to wrong parent pom. To be removed.
     */
    
    def deployReleaseCandidate(String artefactLocation,EnviromentTarget envTarget,String staticFileName)

    /**
     * Get the artefact manifest URL
     * @return
     */
    String getArtefactManifestURL(VersionType versionType)

    /**
     * Get the artefact URL
     * @return
     */
    String getArtefactURL(String packaging, VersionType versionType)

    /* TODO: create separate store
    Technically speaking, env properties are not necessarily stored in the same repo as the artefacts, unlike the manifest file.
    So it doesn't make sense to have an interface IBinary store with both getEnvURL and getArtefactURL. rather, keep
     only one getArtefactURL() and create new PropertiesStore implementing it ?
    */
    /**
     * Get the artefact manifest URL
     * @return
     */
    String getEnvPropertiesURL(Environment environment)

     /**
     * Get the proprties manifest URL
     * @return
     */

    String getEnvPropertiesURL(Environment environment,VersionType versionType)

    

    def checkArtifactWritePermission(VersionType versionType)
}