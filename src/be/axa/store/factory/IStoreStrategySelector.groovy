package be.axa.store.factory

import be.axa.model.VersionType
import be.axa.store.IStorageStrategy

/**
 * Created by DTDJ857 on 08/01/2018.
 */
interface IStoreStrategySelector {

    /**
     *
     * @param branchName
     * @param configurationItemType
     * @return
     */
    IStorageStrategy selectStoreStrategy(VersionType versionType)

}