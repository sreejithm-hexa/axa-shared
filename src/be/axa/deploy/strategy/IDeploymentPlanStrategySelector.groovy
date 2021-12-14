package be.axa.deploy.strategy

import be.axa.deploy.IDeploymentStrategy
import be.axa.model.VersionType
import be.axa.model.configurationitem.strategy.IConfigurationItemTypeStrategy

/**
 * Created by DTDJ857 on 21/12/2017.
 */
interface IDeploymentPlanStrategySelector {

    /**
     * Select the correct deployment plan strategy based on the configuration item type strategy
     * @param branchName
     * @param configurationItemType
     * @return
     */
    IDeploymentStrategy selectDeploymentPlanStrategy(VersionType versionType, IConfigurationItemTypeStrategy configurationItemType)

}