#!/usr/bin/env groovy
package be.axa.deploy.ca.ra.status

enum  ReleaseAutomationStatus implements Serializable{
    CANCELED ("Canceled"),
    FAILED ("Failed"),
    SUCCEEDED ("Succeeded"),
    ACTIVE ("Active")

    private String name

    ReleaseAutomationStatus(String name){
        this.name = name
    }
}