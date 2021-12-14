package be.axa.store.strategy.impl

import be.axa.store.IStorageStrategy
import be.axa.store.artifactory.IBinariesRepository
import be.axa.model.environment.Environment
import be.axa.model.EnviromentTarget

/**
 * Created by DTDJ857 on 04/01/2018.
 */
class ArtifactoryStorageReleaseStrategy implements IStorageStrategy {

    @Override
    void store(IBinariesRepository binariesRepository, String fileToDeploy = null, String staticFileName = null) {
        binariesRepository.deployRelease(fileToDeploy,staticFileName)
    }
}
