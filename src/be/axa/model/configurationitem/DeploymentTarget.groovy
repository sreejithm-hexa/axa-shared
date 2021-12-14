package be.axa.model.configurationitem

/**
 * Created by DTDJ857 on 23/10/2017.
 */
enum DeploymentTarget {

    WEBLOGIC,WEBSPHERE,TOMCAT,JBOSS,WILDFLY,ARTEFACTORY,JVM

    static DeploymentTarget getDefaultDeploymentTarget(){
        return "WEBLOGIC" as DeploymentTarget
    }
}