package be.axa.model

import be.axa.model.EnviromentTarget

enum VersionType {
    FEATURE(),
    BUGFIX(),
    RELEASE(),
    DEVELOPMENT()
    boolean requiresDeployment(EnviromentTarget envTarget){
        def targetEnv= envTarget as String
        targetEnv=targetEnv.toUpperCase()
        return ((RELEASE.equals(this))|| (DEVELOPMENT.equals(this) && !(targetEnv.equals("NONE") )))
    }    
}