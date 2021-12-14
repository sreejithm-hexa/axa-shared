package be.axa.model.scm.impl

import be.axa.model.VersionType
import be.axa.model.scm.ISCMStrategy
import be.axa.model.scm.SCM

/**
 * Created by DTDJ857 on 02/11/2017.
 */
class GitSCMStrategy implements ISCMStrategy, Serializable {

    def steps

    public static final String FEATURE = "feature/"
    public static final String BUGFIX = "bugfix/"
    public static final String DEVELOPMENT = "development"
    public static final String RELEASE = "release/"
    public static final String HOTFIX = "hotfix/"
    public static final String MASTER = "master"
    public static final String GIT_BRANCH = "GIT_BRANCH"
    public static final String GIT_COMMIT = "GIT_COMMIT"
    public static final String GIT_URL = "GIT_URL"
    public static final String GIT_CREDENTIALS = "GIT_CREDENTIALS"

    public static
    final String GIT_BRANCHING_GUIDELINE_HYPERLINK = "https://portal.paas.intraxa/confluence/display/AXABEICMD/Branching+strategy"

    GitSCMStrategy(steps) {
        this.steps = steps
    }

    @Override
    boolean accept(String branchName) {
        return isFeatureBranch(branchName)  || isBugfixBranch(branchName) || isDevelopmentBranch(branchName) || isReleaseBranch(branchName) || isHotfixBranch(branchName) || isMasterBranch(branchName)
    }

    @Override
    SCM getSCM(Map scmProperties, changeSets) {
        String branch = scmProperties[GIT_BRANCH]
        return new SCM(
                branch,
                scmProperties[GIT_COMMIT],
                scmProperties[GIT_URL],
                scmProperties[GIT_CREDENTIALS],
                getChangeString(changeSets)
        )
    }

    @Override
    VersionType getVersionType(SCM scm) {
        if (isDevelopmentBranch(scm.branchName)) {
            return VersionType.DEVELOPMENT
        } else if (isReleaseBranch(scm.branchName) || isHotfixBranch(scm.branchName)) {
            return VersionType.RELEASE;
        } else if (isBugfixBranch(scm.branchName)) {
            return VersionType.BUGFIX;
        } else {
            return VersionType.FEATURE;
        }
    }

    // Single tag is added and then pushed.
    // Git is safe and will return the relevant error message to read in the log.
    // Tag operations do not fail the build.
    @Override
    void addTag(String tagName){
        steps.sh(script:'git tag -f '+tagName, returnStatus: true)
    }

    @Override
    void pushTag(SCM scm, String tagName) {
        def gitUrl = scm.url 
        gitUrl = gitUrl.replaceAll("(github.axa.com)", scm.credentials["Password"] + "@\$1")
        steps.sh(script: '#!/bin/sh -e\n git push --porcelain ' + gitUrl + ' ' + tagName, returnStatus: true)
    }

    @NonCPS
    def getChangeString(changeSets) {
        def changeArrayString = changeSets*.logs.flatten().collect { "${it.msg} [${it.author}]" }

        if (!changeArrayString) {
            changeArrayString = ["No new changes"]
        }

        return changeArrayString
    }

    static boolean isMasterBranch(String branchName) {
        MASTER.equals(branchName)
    }

    static boolean isHotfixBranch(String branchName) {
        branchName.startsWith(HOTFIX)
    }

    static boolean isReleaseBranch(String branchName) {
        branchName.startsWith(RELEASE)
    }

    static boolean isDevelopmentBranch(String branchName) {
        DEVELOPMENT.equals(branchName)
    }

    static boolean isFeatureBranch(String branchName) {
        branchName.startsWith(FEATURE)
    }
    
    static boolean isBugfixBranch(String branchName) {
        branchName.startsWith(BUGFIX)
    }
}
