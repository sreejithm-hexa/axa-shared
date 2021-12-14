#!/usr/bin/env groovy
package be.axa.deploy.ca.ra.factory

import be.axa.deploy.ca.ra.factory.impl.ReleaseAutomation
// TODO Seems useless to keep this here
/**
 * Created by dlbe828 on 08/11/2017.
 */
class ReleaseAutomationFactory implements Serializable{

    /**
     * Build a Release Automation class based on the environment
     * @param steps
     * @param environment // pipeline.env.RA_BASE_URL
     * @return
     */
    be.axa.deploy.ca.ra.factory.IReleaseAutomation getReleaseAutomation(steps, environment){

        def auth = 'RA'
        if (environment.contains("stg")) {
            auth = auth + '_STG'
        }
        return new ReleaseAutomation(steps, environment, auth)
    }
}