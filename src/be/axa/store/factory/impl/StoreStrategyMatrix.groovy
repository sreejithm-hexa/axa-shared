package be.axa.store.factory.impl

import be.axa.model.VersionType
import be.axa.store.IStorageStrategy
import be.axa.store.factory.IStoreStrategySelector
import be.axa.store.strategy.impl.ArtifactoryStorageDevelopmentStrategy
import be.axa.store.strategy.impl.ArtifactoryStorageReleaseStrategy
import be.axa.model.EnviromentTarget

/**
 * Created by DTDJ857 on 08/01/2018.
 */
class StoreStrategyMatrix implements IStoreStrategySelector {

    /**
     * matrice M(branchName * configurationItemType)
     */
    def EnviromentTarget envTarget 

    StoreStrategyMatrix(EnviromentTarget envTarget){
        this.envTarget=envTarget
    }

    private final def sdbSingletonBeanResolver = [
            (VersionType.DEVELOPMENT) : new ArtifactoryStorageDevelopmentStrategy(envTarget),
            (VersionType.RELEASE)     : new ArtifactoryStorageReleaseStrategy()
    ]

    @Override
    IStorageStrategy selectStoreStrategy(VersionType versionType) {
        return sdbSingletonBeanResolver[versionType]
    }
}
