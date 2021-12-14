package be.axa.store.artifactory.aql
/**
 * https://www.jfrog.com/confluence/display/RTF/Artifactory+Query+Language
 * Created by DTDJ857 on 01/08/2017.
 */
class AQLClient implements Serializable {

    def steps

    AQLClient(steps){
        this.steps = steps
    }

    def sendAQLRequest(String aqlRequest){
        def httpResponse = steps.httpRequest acceptType: 'APPLICATION_JSON',
                consoleLogResponseBody: true,
                contentType: 'TEXT_PLAIN',
                customHeaders: [[maskValue: true, name: 'X-JFrog-Art-Api', value: 'AKCp2V77Vd55MHdn1sLMCZEP79GTGVNGTAHbtwoHYGae6auMBHP7cgXxAu4hiBiNDaDZHNArP']],
                httpMode: 'POST',
                requestBody: aqlRequest,
                responseHandle: 'NONE',
                timeout: 10,
                url: 'https://atsartifactory.axa.be/artifactory/api/search/aql'
        return httpResponse.getContent()
    }
}
