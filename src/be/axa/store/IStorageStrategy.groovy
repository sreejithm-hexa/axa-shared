package be.axa.store

import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.environment.Environment

/**
 * Created by DTDJ857 on 08/01/2018.
 * Modified by DSND714 on 12/09/2019
 */
interface IStorageStrategy {

    /**
     * Store the binary produced by the build strategy
     * @param binariesRepository
     * @param fileToDeploy
     * @deprecated use fileToDeploy only when standard deploy phase is not sufficient. @AXA: when the parent pom has been updated and one repository is used
     */
    @Deprecated
    void store(IBinariesRepository binariesRepository, String fileToDeploy, String staticFileName)

    /**
     * Store the binary produced by the build strategy
     * @param binariesRepository
     */
    void store(IBinariesRepository binariesRepository)    
}