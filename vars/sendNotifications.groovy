#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 * @param buildStatus
 * @param mails
 */
def call(String buildStatus = 'STARTED', String mails) {

    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def body = """
                <div style='font-family: "Times New Roman", Times, serif;'>
                    Dear @userName@,
                    <p>
                        <strong style='color:${buildStatus == 'FAILURE' ? "red" : buildStatus == 'SUCCESS' ? "green" : "black"}'>${buildStatus} :</strong> 
                           The following Jenkins job <strong>${env.JOB_NAME}</strong> with build number <strong>${env.BUILD_NUMBER}
                        </strong> is in status <strong>${buildStatus}</strong>
                    </p>
                    <p>Check console output at "<a href="${env.BUILD_URL}">${env.BUILD_URL}</a>"</p>
                    <p>Kind regards, <br /> Software Delivery Automation team</p>
                </div>
                <small style='color:grey;'> Please do not reply to this e-mail as it is automatically generated from Jenkins.<small> </small></small>
               """

    String[] mailList = mails.split("\\s+")

    for (int i = 0; i < mailList.length; i++) {
        if (mailList[i] ==~ /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[A-Za-z]{2,4}/) {       
            def temp = body.replaceFirst("@userName@", mailList[i].split("\\.")[0].capitalize())
            emailext(to: mailList[i], subject: subject, body: temp, from: "${env.JENKINS_EMAIL}", mimeType: "text/html", attachLog: buildStatus == 'FAILURE')
        }
    }
}