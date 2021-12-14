package be.axa.model.configurationitem

/**
 * Created by DTDJ857 on 31/05/2018.
 */
enum DeploymentPlatform {

    PUREAPPLICATION([JavaDevelopmentKit.JDK7, JavaDevelopmentKit.JDK8]),
    BLUEMIX([JavaDevelopmentKit.JDK8]),
    ONPREMISE([JavaDevelopmentKit.JDK7, JavaDevelopmentKit.JDK8]),
    AZURE([JavaDevelopmentKit.JDK8]),
    OPENSHIFT([JavaDevelopmentKit.JDK8]),
    PRIVATEIAAS([JavaDevelopmentKit.JDK8]);

    List<JavaDevelopmentKit> suportedJDKs;

    DeploymentPlatform(List<JavaDevelopmentKit> suportedJDKs) {
        this.suportedJDKs = suportedJDKs;
    }

    static DeploymentPlatform getDefaultDeploymentPlatform(){
        return "ONPREMISE" as DeploymentPlatform
    }

}