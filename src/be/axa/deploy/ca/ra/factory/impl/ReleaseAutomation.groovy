#!/usr/bin/env groovy
package be.axa.deploy.ca.ra.factory.impl

import be.axa.deploy.ca.ra.factory.IReleaseAutomation
import be.axa.deploy.ca.ra.dto.RunDeploymentPlanApiDto
import be.axa.deploy.ca.ra.status.ReleaseAutomationStatus
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder

class ReleaseAutomation implements IReleaseAutomation, Serializable {

    private String base_url
    private String authentication

    private static final String API_BASE_URL = 'datamanagement/a/api/v4'
    private static final String API_RUN_DEPLOYMENT_PLAN = '/run-deployment-plan'
    private static final String API_RELEASE_STATUS = "/release-status/"
    private static final String DEPLOYMENT_URL = "datamanagement/index.jsp#/main/deployments/"
    private static final String API_RUN_RELEASE = "/run-release"    

    private def steps

    ReleaseAutomation(steps, String baseURL, String authentication){
        this.steps = steps
        base_url = baseURL
        this.authentication = authentication
    }

    @Override
    def runDeploymentPlan(RunDeploymentPlanApiDto dto) {
        def deploymentEndpoint = base_url + API_BASE_URL + API_RUN_DEPLOYMENT_PLAN

        def requestBody = dto.toJSON()
        def response = steps.httpRequest acceptType: 'APPLICATION_JSON',
                authentication: authentication,
                consoleLogResponseBody: true,
                contentType: 'APPLICATION_JSON_UTF8',
                ignoreSslErrors:true,
                httpMode: 'POST',
                requestBody: requestBody,
                responseHandle: 'NONE',
                url: deploymentEndpoint

        def responseJSON = new JsonSlurperClassic().parseText(response.getContent())

        if(!responseJSON.result)
            steps.error(responseJSON.description)

        def deploymentId = responseJSON.deploymentResults[0]?.id

        if(deploymentId == null)
            steps.error("RA deployment is null or empty")

        return deploymentId as String
    }

    @Override
    def waitForStatus(String deploymentId, ReleaseAutomationStatus statusToReach){
        def cpt = 0
        ReleaseAutomationStatus deploymentStatus = getDeploymentStatus(deploymentId)

        steps.echo("Release status :"+deploymentStatus)

        while(deploymentStatus != statusToReach && cpt < maxMinutesToWait){
            sleep(60000)
            deploymentStatus = getDeploymentStatus(deploymentId)

            if(deploymentStatus == ReleaseAutomationStatus.CANCELED)
                steps.error("Deployment with id : "+deploymentId+" has been cancelled.")

            if(deploymentStatus == ReleaseAutomationStatus.FAILED)
                steps.error("Deployment with id : "+deploymentId+" failed.")
        }

        if(cpt >= maxMinutesToWait)
            steps.error("Deployment with id : "+deploymentId+" is taking longer than expected")

        return true
    }

    @Override
    def getDeploymentURL(String deploymentId){
        return this.base_url + DEPLOYMENT_URL + deploymentId
    }

    private def getDeploymentStatus (String deploymentId){
        def releaseStatusEndpoint = base_url + API_BASE_URL + API_RELEASE_STATUS + deploymentId

        def response = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                authentication: authentication,
                responseHandle: 'NONE',
                ignoreSslErrors:true,
                url: releaseStatusEndpoint

        def responseJSON = new JsonSlurperClassic().parseText(response.getContent())

        if(!responseJSON.result)
            steps.error(responseJSON.description)

        String status = responseJSON.releaseStatus
        return status.toUpperCase() as ReleaseAutomationStatus
    }
}