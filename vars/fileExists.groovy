#!/usr/bin/env groovy

/**
 * Check if file exists under path "path"
 * @param path
 * @param fileName
 */
def call(path, String fileName){
    boolean fileExists = false
    for (subPath in path.list()) {
        fileExists = subPath.getName() == fileName
        if(fileExists)
            break
    }
    return fileExists
}
