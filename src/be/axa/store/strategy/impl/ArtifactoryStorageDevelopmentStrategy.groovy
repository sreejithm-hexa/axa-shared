package be.axa.store.strategy.impl

import be.axa.store.IStorageStrategy
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.environment.Environment
import be.axa.model.EnviromentTarget
/**
 * Created by DTDJ857 on 04/01/2018.
 * Modified by DSND714 on 12/09/2019.
 */
class ArtifactoryStorageDevelopmentStrategy implements IStorageStrategy {

    def EnviromentTarget envTarget
    ArtifactoryStorageDevelopmentStrategy(EnviromentTarget envTarget){
        this.envTarget=envTarget
    }

    @Override
    void store(IBinariesRepository binariesRepository, String fileToDeploy = null, String staticFileName = null) {
        binariesRepository.deployReleaseCandidate(fileToDeploy,envTarget,staticFileName)
    }
}
