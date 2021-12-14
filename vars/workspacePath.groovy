#!/usr/bin/env groovy
import hudson.FilePath
import jenkins.model.Jenkins

/**
 * Return the current directory path as string for master and slaves
 * @param path : local path
 * @return : Directory path as string
 */
def call(String path){
    if(!env['NODE_NAME'])
        error "env var NODE_NAME is not set, probably not inside an node {} or running an older version of Jenkins!"

    if(env['NODE_NAME'].equals("master"))
        return new FilePath(path)

    return new FilePath(Jenkins.getInstance().getComputer(env['NODE_NAME']).getChannel(), path)
}
