package be.axa.model.scm

import be.axa.model.VersionType

/**
 * This class is responsible to define the acceptance criterias for a branching model on SCM tools
 *
 * https://jenkins.io/doc/pipeline/steps/workflow-scm-step/
 *
 * Created by DTDJ857 on 02/11/2017.
 */
interface ISCMStrategy {

    /**
     * Accept the branch name according to the branching strategy model
     * @param branchName the branch to test for acceptance
     * @return true if it's compliant, false if not
     */
    boolean accept(String branchName)

    /**
     * returns the SCM model
     * @param scmProperties a map of SCM properties retrieved by Jenkins, as well as changes retrived by jenkins
     * @return a SCM model
     */
    SCM getSCM(Map scmProperties, changeSets)

    /**
     * Returns true if the checked commit corresponds to a development version, not appropriate for release
     * but used in dev envs
     * @param scm
     * @return
     */
    VersionType getVersionType(SCM scm);

    /**
     * Add tag to the current SCM branch
     * @param tagName
     */
    void addTag(String tagName)

    /**
     * Push tag "tagName" to the current SCM branch
     * @param scm
     * @param tagName
     */
    void pushTag(SCM scm, String tagName)
}