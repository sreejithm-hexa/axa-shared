package unit.test

import be.axa.model.scm.ISCMStrategy
import be.axa.model.scm.impl.GitSCMStrategy

/**
 * Created by DTDJ857 on 02/11/2017.
 */
class GitSCMStrategyTest extends GroovyTestCase {

    ISCMStrategy gitBranchingStrategy

    @Override
    void setUp() {
        super.setUp()
        gitBranchingStrategy = new GitSCMStrategy(null)
    }

    void test_Accept_Git_Branches() {
        assertTrue(gitBranchingStrategy.accept(GitSCMStrategy.MASTER))
        assertTrue(gitBranchingStrategy.accept(GitSCMStrategy.DEVELOPMENT))
        assertTrue(gitBranchingStrategy.accept([GitSCMStrategy.FEATURE, "jbcSecurity"].join()))
        assertTrue(gitBranchingStrategy.accept([GitSCMStrategy.RELEASE, "2.0.0"].join()))
        assertTrue(gitBranchingStrategy.accept([GitSCMStrategy.HOTFIX, "2.0.1"].join()))
    }

    void test_Reject_Bad_Branches() {
        assertFalse(gitBranchingStrategy.accept(GitSCMStrategy.MASTER + 'R'))
        assertFalse(gitBranchingStrategy.accept(GitSCMStrategy.DEVELOPMENT + 'T'))
        assertFalse(gitBranchingStrategy.accept("features/jbcSecurity"))
        assertFalse(gitBranchingStrategy.accept("releases/2.0.0"))
        assertFalse(gitBranchingStrategy.accept("hotfixes/2.0.1"))
    }

    void test_Is_Master_Branch() {
        assertTrue(GitSCMStrategy.isMasterBranch(GitSCMStrategy.MASTER))
        assertFalse(GitSCMStrategy.isMasterBranch("maaster"))
    }

    void test_Is_Hotfix_Branch() {
        assertTrue(GitSCMStrategy.isHotfixBranch([GitSCMStrategy.HOTFIX, "1.0.1"].join()))
        assertFalse(GitSCMStrategy.isHotfixBranch("hotifx/1.0.1"))
        assertFalse(GitSCMStrategy.isHotfixBranch("HOTFIX/1.0.1"))
    }

    void test_Is_Release_Branch() {
        assertTrue(GitSCMStrategy.isReleaseBranch([GitSCMStrategy.RELEASE, "2.0.0"].join()))
        assertFalse(GitSCMStrategy.isReleaseBranch("releases/2.0.0"))
        assertFalse(GitSCMStrategy.isReleaseBranch("RELEASE/2.0.0"))
    }

    void test_Is_Development_Branch() {
        assertTrue(GitSCMStrategy.isDevelopmentBranch(GitSCMStrategy.DEVELOPMENT))
        assertFalse(GitSCMStrategy.isDevelopmentBranch("developer"))
        assertFalse(GitSCMStrategy.isDevelopmentBranch("DEVELOPMENT"))
    }

    void test_Is_Feature_Branch() {
        assertTrue(GitSCMStrategy.isFeatureBranch([GitSCMStrategy.FEATURE, "jbcSecurity"].join()))
        assertFalse(GitSCMStrategy.isFeatureBranch("function/jbcSecurity"))
        assertFalse(GitSCMStrategy.isFeatureBranch("FEATURE/jbcSecurity"))
    }
}
