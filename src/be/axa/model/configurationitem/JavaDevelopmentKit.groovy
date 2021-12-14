package be.axa.model.configurationitem

/**
 * Created by DTDJ857 on 02/11/2017.
 */
enum JavaDevelopmentKit {

    JDK7("1.7","JDK 1.7.0"),JDK8("1.8","JDK 1.8.0")

    /**
     * pom.xml java.version property
     */
    private final String version

    private final String jenkinsToolLocationID

    JavaDevelopmentKit(String version, String jenkinsToolLocationID) {
        this.version = version
        this.jenkinsToolLocationID = jenkinsToolLocationID
    }

    String getVersion() {
        return version
    }

    String getJenkinsToolLocationID() {
        return jenkinsToolLocationID
    }

    static JavaDevelopmentKit fromJavaVersion(String javaVersion){
        return values().find {jdk -> jdk.version.equalsIgnoreCase(javaVersion)}
    }

}