#!/usr/bin/env groovy
package be.axa.deploy.ca.ra.factory

import be.axa.deploy.ca.ra.dto.RunDeploymentPlanApiDto
import be.axa.deploy.ca.ra.status.ReleaseAutomationStatus

/**
 * Created by dlbe828 on 09/11/2017.
 */
interface IReleaseAutomation{

    /**
     * Minutes to wait before for a service response
     */
    def final maxMinutesToWait=59

    /**
     * Create and run a Deployment Plan in Release Automation
     * @return deployment Id
     */
    def runDeploymentPlan(RunDeploymentPlanApiDto dto)

    /**
     * Wait until the deployment plan in RA is in status "statusToReach" or until maxMinutesToWait is reached (by default 59 min)
     * @param deploymentId
     * @param statusToReach
     * @return
     */
    def waitForStatus(String deploymentId, ReleaseAutomationStatus statusToReach)

    /**
     * Get the current deployment URL based on the deploymentId
     * @param deploymentId
     * @return current deployment URL
     */
    def getDeploymentURL(String deploymentId)
    
}