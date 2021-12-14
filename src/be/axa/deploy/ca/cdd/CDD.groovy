#!/usr/bin/env groovy
package be.axa.deploy.ca.cdd

import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import groovy.json.JsonSlurperClassic

/**
 * Created by DTDJ857 on 17/07/2017.
 *
 * This class is responsible of all integration between Jenkins and CDD
 *
 * We are using the REST API of CDD :
 * http://cde.axa.be/cdd/apis/
 */
class CDD implements Serializable {

    //TODO factorization of httprequest in single function or in a shared library http client

    /**
     * CDD DNS
     */
    private static final String RELEASES_URL = '#/releases/'

    /**
     * CDD REST API application endpoint
     */
    public static final String CDD_APPLICATION_ENDPOINT = "administration/0000/v1/applications"

    /**
     * CDD REST API releases endpoint
     */
    public static final String CDD_RELEASES_ENDPOINT = "design/0000/v1/releases"

    /**
     * CDD REST API application version build endpoint
     */
    public static final String CDD_APPLICATION_VERSION_BUILD_ENDPOINT = "design/0000/v1/applications/application-versions/application-version-builds"

    private def steps
    private def releaseId
    private String base_url
    private String authentication

    CDD(steps, String baseURL, String authentication) {
        this.steps = steps
        base_url = baseURL
        this.authentication = authentication
    }

    def getBaseURL(){
        if (base_url == null){
            steps.error("CDD_BASE_URL is not defined as environment variable.")
        }
        return base_url
    }

    def getAuth() {
        return authentication
    }

    /*
     * Method to use to update CDD token and notify CDD for new deployment
     * @param applicationName The name of the application define in CDD
     * @param applicationVersionBuildNumber The last successfull build value to send to CDD in the deployment task as Deployment Plan Build Name
     * @param applicationVersion The version define in the release content for this application Name
     * @param mavenArtifactVersion The value of the token to update used in the deployment task as Deployment Plan Name
     */
    def updateTokenAndNotifyCDE(String applicationName,
                                String applicationVersionBuildNumber,
                                String applicationVersion,
                                String mavenArtifactVersion,
                                def map=[:]) {

        //Logger
        def logger = "\napplicationName: " + applicationName +
                "\napplicationVersionBuildNumber: " + applicationVersionBuildNumber +
                "\napplicationVersion: " + applicationVersion +
                "\nmavenArtifactVersion: " + mavenArtifactVersion +
                "\nparameters: " + map
        steps.echo(logger)

        // Get The CDE application ID
        def applicationID = this.getApplicationId(applicationName)

        // Enable the correct task. The appVersion returned defines which CDD pipeline was selected
        def appVersionSelected = enableCorrectTask(Integer.toString(applicationID), applicationName, applicationVersion)
        steps.echo("CDD pipeline selected has version"+appVersionSelected)
        // Get the Release ID linked exactly to the application name and version
        def releaseID = this.getReleaseId(Integer.toString(applicationID), applicationVersion, applicationName)

        //Get the tokenID
        def tokenID = this.getTokenId(Integer.toString(releaseID), applicationName)

        //Update the token
        def contentRes = this.putReleaseTokenValue(Integer.toString(releaseID), Integer.toString(tokenID), applicationName, mavenArtifactVersion)

        //Parse and iterate each entry of parameter map to update release tokens
        if (map!=[:]) {
            map.each { key, value -> 
                tokenID = this.getTokenId(Integer.toString(releaseID), key)
                contentRes = this.putReleaseTokenValue(Integer.toString(releaseID), Integer.toString(tokenID), key, value)
            }
        }

        //Notify CDD for new deployment to launch
        def appVersionString = appVersionSelected.toString()
        def notifyRes = this.postNotify(applicationName,applicationVersionBuildNumber, appVersionString)
        return notifyRes
    }

    def enableCorrectTask(String applicationID, String applicationName, String applicationVersion) {

        //TODO manage where data is empty AND remove useless REST call
        def allReleases = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}?status=DESIGN&status=RUNNING&status=RUNNING_WITH_FAILURES&application=${applicationID}"
        def allReleasesJSON = new JsonSlurperClassic().parseText(allReleases.getContent())

        if (allReleasesJSON.totalResultsCount == 0) {
            steps.error("No CDD releases define for application:" + applicationName + ".")
        }


        String releaseId
        String appId
        def appVersion
        def appVersionSel // last match but if more than one, deployment is stopped.
        def sameAppAndVersionCpt = 0

        for (int i = 0; i < allReleasesJSON.data.size(); i++) {
            if (allReleasesJSON.data[i].applications.size() != 0) {
                for (int j = 0; j < allReleasesJSON.data[i].applications.size(); j++) {
                    def allVersions = steps.httpRequest acceptType: 'APPLICATION_JSON',
                            consoleLogResponseBody: true,
                            customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                            responseHandle: 'NONE',
                            url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}/${allReleasesJSON.data[i].id}/application-versions"
                    def allVersionsJSON = new JsonSlurperClassic().parseText(allVersions.getContent())

                    for (int k = 0; k < allVersionsJSON.data.size(); k++) {
                        String appName = allVersionsJSON.data[k].application.name
                        appVersion = allVersionsJSON.data[k].name
                        if (appName.equals(applicationName) && (applicationVersion ==~ appVersion+".*")) {
                            steps.echo("CDD: "+applicationVersion+" matches "+appVersion+".*")
                            appVersionSel = appVersion
                            sameAppAndVersionCpt++
                            if (releaseId == null && appId == null) {
                                releaseId = allReleasesJSON.data[i].id
                                appId = allVersionsJSON.data[k].id
                                steps.echo("Enable the correct task in releaseId " + releaseId + " for appId " + appId)
                                break
                            }
                        } else {
                            steps.echo("CDD: "+applicationVersion+" does not match "+appVersion+".*")
                        }
                    }
                    // First match stops the search. TODO Test is not functional. Consider releaseId.toString() != null
                    if (releaseId != null && appId != null) {
                        break
                    }
                }
            }
        }

        steps.echo("Number of CDD releases with content "+applicationName+" and version "+applicationVersion+" is "+sameAppAndVersionCpt)

        if (sameAppAndVersionCpt == 0) {
            steps.error("CI " + applicationName + " with version " + applicationVersion + " has no available pipeline in CDD.")
        } else if (sameAppAndVersionCpt > 1) {
            steps.error("CI " + applicationName + " with version " + applicationVersion + " has multiple matches.")
        }

        // Find first phase ID
        def phaseId
        def allPhases = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}/${releaseId}/phases"
        def allPhasesJSON = new JsonSlurperClassic().parseText(allPhases.getContent())
        if(allPhasesJSON.data.size() == 0) {
      		steps.error("No phase in release")
        }
      
        phaseId = allPhasesJSON.data[0].id
        steps.echo("The phase Id : " + phaseId)
        if (allPhasesJSON.data[0].executionData.status == "RUNNING") {
               steps.error("Phase: " + phaseId + " is currently running. Please redeploy later.")
        }
        if (allPhasesJSON.data[0].executionData.status == "RUNNING_WITH_FAILURES") {
               steps.error("Phase: " + phaseId + " is currently running with errors. Please stop it and redeploy.")
        }
        
        //Disable/Enable Tasks in this phase
        def taskId
        def requestBody
        def jsonBodyEnableTask = """{\"isDisabled\": false}"""
        def jsonBodyDisableTask = """{\"isDisabled\": true}"""

        def allTasks = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}/${releaseId}/phases/${phaseId}/tasks"
        def allTasksJSON = new JsonSlurperClassic().parseText(allTasks.getContent())

        boolean thereIsNoTaskEnable = true

        for (int i = 0; i < allTasksJSON.data.size(); i++) {
            if (allTasksJSON.data[i].applicationVersions.size() != 0) {
                taskId = allTasksJSON.data[i].id
                String contentAppId = allTasksJSON.data[i].applicationVersions[0].id

                if (contentAppId.equals(appId)) {
                    requestBody = jsonBodyEnableTask
                    thereIsNoTaskEnable = false
                    steps.echo("Enable Task : taskId " + taskId)
                } else {
                    requestBody = jsonBodyDisableTask
                    steps.echo("Disable Task : taskId " + taskId)
                }

                def responsePatchEnableDisable = steps.httpRequest acceptType: 'APPLICATION_JSON',
                        customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                        consoleLogResponseBody: true,
                        contentType: 'APPLICATION_JSON_UTF8',
                        httpMode: 'PATCH',
                        requestBody: requestBody,
                        responseHandle: 'NONE',
                        url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}/${releaseId}/phases/${phaseId}/tasks/${taskId}"
                steps.echo("CDD.patchReleaseTaskEnableDisable - response body : " + responsePatchEnableDisable.getContent())
            }
        }

        if (thereIsNoTaskEnable){
            steps.error("No enabled task in your release, please check version in your FT phase tasks")
        }
        return appVersionSel // Returning the Version as set in the CDD pipeline
    }

    /**
     * Retrieve application ID define in CDD via the application name value
     * @param applicationName application name value
     * @return the id of the application define in CDD
     */
    def getApplicationId(String applicationName) {
        def responseGetApplicationId = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_APPLICATION_ENDPOINT}?filter=${applicationName}"
        def responseGetApplicationIdJSON = new JsonSlurperClassic().parseText(responseGetApplicationId.getContent())
        def applicationID = responseGetApplicationIdJSON.data.id[0]
        if (applicationID == null) {
            steps.error(applicationName + " not found in CDD. Deployment will not start automatically.")
        }
        steps.echo("The application " + applicationName + " is defined in CDD with Id " + applicationID)
        return applicationID
    }

    /**
     * Retrieve the release Id which contains the application Id in the content with the correct version to deploy
     * @param applicationId application Id define in CDD
     * @param applicationVersion application version to deploy define in the content of the CDD release
     * @param applicationName application name to deploy define in the content of the CDD release
     * @return the release Id
     */
    def getReleaseId(String applicationId, String applicationVersion, String applicationName) {
        // Get All releases which contains the application id
        def responseGetReleaseId = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}?status=DESIGN&status=RUNNING&status=RUNNING_WITH_FAILURES&application=${applicationId}"
        def responseGetReleaseIdJSON = new JsonSlurperClassic().parseText(responseGetReleaseId.getContent())
        def releaseID
        def numberOfReleasesRunning = responseGetReleaseIdJSON.totalResultsCount
        steps.echo("numberOfReleasesRunning :"+numberOfReleasesRunning)
        def matchFound = false

        if (numberOfReleasesRunning == 0) {
            steps.error("No CDD release define for this application: " + applicationName)
        } else {
            // if Application name is defined in multiple releases :
            // Retrieve only the Release Id linked to the correct Application version in the content of the correct release
            for (int i = 0; i < numberOfReleasesRunning; i++) {
                releaseID = responseGetReleaseIdJSON.data.id[i]
                steps.echo(i+": releaseID == "+releaseID+"; applicationId == "+applicationId)
                def responseApplicationVersion = steps.httpRequest acceptType: 'APPLICATION_JSON',
                        consoleLogResponseBody: true,
                        customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                        responseHandle: 'NONE',
                        url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}/${releaseID}/applications/${applicationId}/application-versions"
                def responseGetApplicationVersionJSON = new JsonSlurperClassic().parseText(responseApplicationVersion.getContent())
                def appVersion = responseGetApplicationVersionJSON.data.name
                if (applicationVersion ==~ appVersion+".*") {
                    steps.echo(applicationVersion+" matches "+appVersion+".*")
                    matchFound = true
                    break
                }
            }
        }
        if (!matchFound) {
            steps.error("No CDD release define for this application: " + applicationName)
        }
        steps.echo("CDD Release Id selected for the deployment :" + releaseID)
        return releaseID
    }

    /**
     * Retrieve the token Id in the content of the correct release id and with the correct name
     * @param releaseId the release which contains the token definition
     * @param tokenToUpdateName the name of the token
     * @return the id of the token
     */
    def getTokenId(String releaseId, String tokenToUpdateName) {

        def responseGetTokenId = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}/${releaseId}/tokens?filter=${tokenToUpdateName}"
        def responseGetTokenIdJSON = new JsonSlurperClassic().parseText(responseGetTokenId.getContent())

        def tokenData
        for (int i = 0; i < responseGetTokenIdJSON.data.size(); i++) 
        {                   
            if(responseGetTokenIdJSON.data[i]!=null && responseGetTokenIdJSON.data[i].name == tokenToUpdateName)
            {  
                tokenData = responseGetTokenIdJSON.data[i]              
                break;
            }
        }
        
        if(tokenData == null){
            steps.echo("ERROR : Token ${tokenToUpdateName} not found in CDD pipeline.")
            steps.error("Token ${tokenToUpdateName} not found in CDD pipeline.")
        }

        def tokenID = tokenData.id        
        steps.echo("CDD token Id to update is " + tokenID)
        return tokenID
    }

    /**
     * Update the token value of a CDD release token
     * @param releaseID the id of the release
     * @param tokenID the id of the token
     * @param tokenName the name of the token to update
     * @param tokenValue the value to put for the token
     * @return JSON object which describe the status of the operation
     */
    def putReleaseTokenValue(String releaseID, String tokenID, String tokenName, String tokenValue) {
        def requestBody = """{
                "id": ${tokenID},
                "isSystem": true,
                "name": "${tokenName}",
                "release": {"id": ${releaseID}},
                "scope": "RELEASE_SCOPE",
                "value": "${tokenValue}"
        }"""
        def responsePutReleaseTokenValue = steps.httpRequest acceptType: 'APPLICATION_JSON',
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                consoleLogResponseBody: true,
                contentType: 'APPLICATION_JSON_UTF8',
                httpMode: 'PUT',
                requestBody: requestBody,
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_RELEASES_ENDPOINT}/${releaseID}/tokens/${tokenID}"
        steps.echo("CDD.putReleaseTokenValue - response body : " + responsePutReleaseTokenValue.getContent())
        return responsePutReleaseTokenValue.getContent()
    }

    /**
     * Notify CDD that a new deployment is available
     * @param applicationName the application to deploy
     * @param applicationVersionBuildNumber the version build number to deploy
     * @param applicationVersion the version to deploy
     * @return JSON object which describe the status of the operation
     */
    def postNotify(String applicationName, String applicationVersionBuildNumber, String applicationVersion) {
        steps.echo("applicationName == "+applicationName+" : applicationVersionBuildNumber == "+applicationVersionBuildNumber+ "applicationVersion == "+applicationVersion)
        def requestBody = """{
            "applicationName": "${applicationName}",
            "applicationVersionBuildNumber": "${applicationVersionBuildNumber}",
            "applicationVersionName": "${applicationVersion}"
        }"""
        def responsePostNotify = steps.httpRequest acceptType: 'APPLICATION_JSON',
                customHeaders: [[maskValue: true, name: 'Authorization', value: 'Bearer ' + getAuth()]],
                consoleLogResponseBody: true,
                contentType: 'APPLICATION_JSON_UTF8',
                httpMode: 'POST',
                requestBody: requestBody,
                responseHandle: 'NONE',
                url: "${getBaseURL()}${CDD_APPLICATION_VERSION_BUILD_ENDPOINT}"
        steps.echo("CDD.postNotify - response body: " + responsePostNotify.getContent())
        return responsePostNotify.getContent()
    }

    /**
     * Return the CDD application version number from the build tool version number
     * @param buildToolVersionNumber the build tool version number in 4 digits (X.Y.Z.W)
     * @return the CDD application version number in 3 digits (X.Y.Z)
     * Authorizing Semver requires to allow 2 dots. Remove truncation in this case.
     */
    def getApplicationVersionNumber(String buildToolVersionNumber) {
        def applicationVersion = buildToolVersionNumber
        def dots = applicationVersion.count(".")
        if (dots == 3) { // Build number is in version. Removing.
            def patchPosition = buildToolVersionNumber.lastIndexOf(".")
            return applicationVersion.substring(0, patchPosition)
        }
        return applicationVersion
    }

    /**
     * Build the CDD release URL based on the post£Notify response
     * @param postNotifyResponse
     * @return The URL of the current CDD release
     */
    def getReleaseURL(String postNotifyResponse){
        def jsonResponseBody = new JsonSlurperClassic().parseText(postNotifyResponse)
        releaseId = jsonResponseBody.data.phaseResponseDtoList[0].releaseId
        return "${getBaseURL()}${RELEASES_URL}${releaseId}"
    }
}
