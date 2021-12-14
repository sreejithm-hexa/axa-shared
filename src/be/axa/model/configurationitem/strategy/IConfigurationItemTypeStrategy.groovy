package be.axa.model.configurationitem.strategy
/**
 * Build and deploy Strategy Pattern for configuration item type
 * Created by DTDJ857 on 27/10/2017.
 */
interface IConfigurationItemTypeStrategy {

    /**
     * Build Strategy : retrieve the path where the artefact is generated from the maven
     * @param pom
     * @return
     */
    String getArtefactPath(pom)

    /**
     * Build and Deploy Strategy : validator to check if SDK version is supported for Configuration item type
     * @param sdkVersion
     * @return
     */
    boolean accept(String sdkVersion)

    /**
     * Build and Deploy Strategy : Get The Packaging extension
     * @return the packaging extension
     */
    String getPackaging()

    /**
     * Get the artefact Type
     * @return the artefact type
     */
    String getArtefactType()

    /**
     * Get the Deployment Target
     * @return the deployment target
     */
    String getDeploymentTarget()

    /**
     * Get the Deployment Platform
     * @return the deployment platform
     */
    String getDeploymentPlatform()

    boolean isDeploymentPlatformSupported()

    /**
     * whether or not the artifact can be be run (standalone, on a server, etc).
     * @return
     */
    boolean isRunnable();

}