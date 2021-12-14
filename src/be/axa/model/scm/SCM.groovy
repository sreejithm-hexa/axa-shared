package be.axa.model.scm

class SCM implements Serializable {

    def branchName
    def versionId
    def url
    def credentials
    String[] changeDescription

    SCM(branchName, versionId, url, credentials, versionMessage){
        this.branchName = branchName
        this.versionId = versionId
        this.url = url
        this.credentials = credentials
        this.changeDescription = versionMessage
    }

}
