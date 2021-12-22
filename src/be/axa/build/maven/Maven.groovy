#!/usr/bin/env groovy
package be.axa.build.maven

import be.axa.model.VersionType
import be.axa.model.configurationitem.JavaDevelopmentKit
//import be.axa.config.AxaProperties
/**
 * Created by DTDJ857 on 25/07/2017.
 *
 * This class is responsible to handle all maven processes
 */
class Maven implements Serializable {

    // Goals
    public static final String MVN = "mvn"
    public static final String SEPARATOR = " "
    public static final String CLEAN = "clean"
    public static final String PACKAGE = "package"
    public static final String TEST = "test"
    public static final String INSTALL = "install"
    public static final String DEPLOY = "deploy"

    // Maven profile
    public static final String PROFILE_UFEMS = "-Pufems"
    public static final String PROFILE_SNAPSHOT = "-Psnapshot"
    public static final String PROFILE_RELEASE = "-Prelease"

    // Never skip test phase
    public static final String SKIP_TESTS_FALSE = "-DskipTests=false"
    public static final String SKIP_TESTS_TRUE = "-DskipTests=true"

    // Always enforce that the tests are executed
    public static final String TEST_SKIP_EXEC_FALSE = "-Dmaven.test.skip.exec=false"

    // No generation of Javadoc for time consuming gain
    public static final String JAVADOC_SKIP_TRUE = "-Dmaven.javadoc.skip=true"

    // Always/Never compile the tests.
    public static final String TEST_SKIP_FALSE = "-Dmaven.test.skip=false"
    public static final String TEST_SKIP_TRUE = "-Dmaven.test.skip=true"

    // Always taking test result to determine build stability
    public static final String TEST_FAILURE_IGNORE = "-Dmaven.test.failure.ignore=false"

    // Jenkins job has not UI
    public static final String AWT_HEADLESS_TRUE = "-Djava.awt.headless=true"

    /* Build Options for Continuous Integration EnvironmentOnPremise
     * "B" Batch mode
     * "U" downloading and verifying dependencies
     * "c" warning if checksums don't match
     * "V" printing version without exit
     * "fae" only fail the build afterwards for multi-module
     * "-T 1C" one thread by CPU core on the node for multithreading support
     */
    public static final String BUILD_OPTS_CI = ['-B','-V','-U','-c','-fae','-T 1C'].join(SEPARATOR)

    // Maven java code coverage plugin : prepare unit testing code coverage report for Sonarqube
    public static final String JACOCO = "org.jacoco:jacoco-maven-plugin:0.8.1:prepare-agent"

    // Maven Sonarqube static code analysis plugin : launch asynchronously the analysis on the sonarqube Server
    public static final String SONARQUBE = "org.sonarsource.scanner.maven:sonar-maven-plugin:3.5.0.1254:sonar"
    public static final String SONAR_LOGIN = "-Dsonar.login="
    public static final String SONAR_HOST = "-Dsonar.host.url="
    public static final String SONAR_EXCLUSION = "-Dsonar.exclusions=*/.doc,*/.docx,*/.ipch"
    public static final String SONAR_ENCODING = "-Dsonar.sourceEncoding=UTF-8"

    // Jenkins Installation Tool ID for maven
    public static final String MVN_JENKINS_TOOL_ID = "maven 3.5.3"

    def pipeline
    def jdk_version
    def mvn_version
    def pom
    def ALT_DEP_REPO = " "

    Maven(pipeline, jdk_version, mvn_version, pom){
        this.pipeline = pipeline
        this.jdk_version = jdk_version
        this.mvn_version = mvn_version
        this.pom = pom
    }

    def build(String optionsAndGoals){
        pipeline.steps.withEnv(["JAVA_HOME=${ pipeline.steps.tool jdk_version }", "PATH+MAVEN=${pipeline.steps.tool mvn_version}/bin:${pipeline.env.JAVA_HOME}/bin"]) {
            pipeline.steps.sh MVN + SEPARATOR + optionsAndGoals
        }
    }

    def install(String secureTokenFileName){

        if (secureTokenFileName.equals(null)) {
            
        this.build([JAVADOC_SKIP_TRUE, TEST_SKIP_EXEC_FALSE, SKIP_TESTS_FALSE, TEST_SKIP_FALSE, TEST_FAILURE_IGNORE,
                    AWT_HEADLESS_TRUE, BUILD_OPTS_CI, CLEAN, JACOCO, PROFILE_UFEMS, INSTALL].join(SEPARATOR))
        }
        else {
            
            this.build([JAVADOC_SKIP_TRUE, TEST_SKIP_EXEC_FALSE, SKIP_TESTS_FALSE, TEST_SKIP_FALSE, TEST_FAILURE_IGNORE,
                    AWT_HEADLESS_TRUE, BUILD_OPTS_CI, CLEAN, JACOCO, INSTALL].join(SEPARATOR))
        }
    }

    def deploy(String profile){
        this.build([profile,SKIP_TESTS_TRUE, TEST_SKIP_TRUE, DEPLOY].join(SEPARATOR))
    }

    def sonarQube(){
        pipeline.steps.withEnv(["JAVA_HOME=${ pipeline.steps.tool JavaDevelopmentKit.JDK8.jenkinsToolLocationID }",
                                "PATH+MAVEN=${pipeline.steps.tool mvn_version}/bin:${pipeline.env.JAVA_HOME}/bin"]) {
            pipeline.steps.withCredentials([pipeline.steps.string(credentialsId: 'sonar.login', variable: 'SONAR_USER')]) {
                pipeline.steps.sh MVN +
                        SEPARATOR +
                        [SONAR_HOST+pipeline.env.SONAR_URL,
                         SONAR_LOGIN +'"$SONAR_USER"',
                         SONAR_ENCODING,
                         SONARQUBE].join(SEPARATOR)
            }
        }
    }

    /**
     * Check if the maven version is compliant to AXA standard rules
     * @param version the maven version to check
     * @param branchName the branch where we are building as referential
     * @return break the build with explicit error messages if version does not meet AXA Maven criterias
     */
    def checkVersionCompliance(VersionType type){

        def axaGuidelines =
                """
                AXA guidelines for maven version :\n
                - Version should be with four numbers separated by a dot.\n
                - Version ends with -SNAPSHOT if you are in development branch.\n
                - Version does not ends with -SNAPSHOT if you are in release/ or hotfix/ branches.\n
                - Version does not contains 0 before digits like 002.01.04.0001, correct one is 2.1.4.1\n
                """

        if (VersionType.DEVELOPMENT.equals(type)) {

            def isSnapshotCompliant = pom.version ==~ "^[1-9][0-9]*((\\.(0|[1-9][0-9]*)){2})(|\\.(0|[1-9][0-9]*))-SNAPSHOT\$"
            if(isSnapshotCompliant){
                pipeline.steps.echo("The following version is compliant to maven SNAPSHOT AXA standard: " + pom.version + ".")
                ALT_DEP_REPO = "-DaltDeploymentRepository=artifactory.snapshots::default::" + pipeline.env.EE_ARTIFACTORY_URL + "/maven-library-snapshots/ -Psnapshot"
            } else {
                pipeline.steps.echo(axaGuidelines)
                pipeline.steps.error("The following version is not compliant to maven SNAPSHOT AXA standard: " + pom.version + ".")
            }
        }

        if (VersionType.RELEASE.equals(type)) {
            def isReleaseCompliant = pom.version ==~ "^[1-9][0-9]*((\\.(0|[1-9][0-9]*)){2})(|\\.(0|[1-9][0-9]*))\$"
            if(isReleaseCompliant){
                pipeline.steps.echo("The following version is compliant to maven RELEASE AXA standard: " + pom.version + ".")
                ALT_DEP_REPO = "-DaltDeploymentRepository=artifactory.releases::default::" + pipeline.env.EE_ARTIFACTORY_URL + "/maven-library-releases/ -Prelease"
            } else {
                pipeline.steps.echo(axaGuidelines)
                pipeline.steps.error("The following version is not compliant to maven RELEASE AXA standard: " + pom.version + ".")
            }
        }
    }
}
