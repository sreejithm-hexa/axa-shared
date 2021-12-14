#!/usr/bin/env groovy
package be.axa.deploy.ca.ra.dto

import groovy.json.JsonBuilder

/**
 * Data Transfer Object in order to run RA deployment Plan via REST API
 *
 * More info here :
 * https://docops.ca.com/ca-release-automation/6-2/en/reference/rest-api-reference/rundeploymentplanapidto
 */
class RunDeploymentPlanApiDto implements Serializable {

    String deploymentPlan
    String build
    String project
    String deploymentTemplate
    String templateCategory
    String application
    String deployment
    String[] environments
    String deploymentStageToPerform
    String deploymentDescription
    def properties

    RunDeploymentPlanApiDto(Map map) {
        map?.each { k, v -> this[k] = v }
    }

    def toJSON(){
        return new JsonBuilder(this).toPrettyString()
    }
}