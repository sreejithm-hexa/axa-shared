#!/usr/bin/env groovy

/**
 * Get authorization token key from credentials id of Jenkins
 * @param id Credentials Id of Jenkins
 * @return resultMap map which contains username/password
 */
def call(String id){
    def resultMap = [:]
    withCredentials([usernamePassword(credentialsId: id, usernameVariable: 'Key', passwordVariable: 'Token')]) {
        resultMap['Username'] = Key
        resultMap['Password'] = Token
    }
    return resultMap
}