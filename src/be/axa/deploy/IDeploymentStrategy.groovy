package be.axa.deploy

import be.axa.build.maven.Maven
import be.axa.model.scm.SCM

/**
 * Created by DTDJ857 on 21/12/2017.
 */
interface IDeploymentStrategy {

    /**
     * Deploy the CI TYPE
     * @param maven
     * @param pipeline
     * @param scm
     */
    void deploy(Maven maven, pipeline, SCM scm)

}