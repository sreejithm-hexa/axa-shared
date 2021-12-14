package unit.test

import be.axa.model.configurationitem.ArtefactType
import be.axa.model.configurationitem.DeploymentTarget
import be.axa.model.configurationitem.JavaDevelopmentKit
import be.axa.model.configurationitem.strategy.impl.EarWeblogicType
import be.axa.model.configurationitem.strategy.impl.ZipJvmType
import be.axa.model.factory.IConfigurationItemTypeFactory
import be.axa.model.factory.impl.ConfigurationItemTypeFactoryMatrix

class ConfigurationItemTypeFactoryMatrixTest extends GroovyTestCase {

    String java7 = JavaDevelopmentKit.JDK7.getVersion()
    String java8 = JavaDevelopmentKit.JDK8.getVersion()
    IConfigurationItemTypeFactory cmdb = new ConfigurationItemTypeFactoryMatrix()

    void test_ear_weblogic() {
        def configurationItemType = cmdb.createConfigurationItemType(ArtefactType.EAR, DeploymentTarget.WEBLOGIC);
        assertNotNull configurationItemType
        assertTrue configurationItemType instanceof EarWeblogicType
        assertTrue configurationItemType.accept(java7)
        assertFalse configurationItemType.accept(java8)
    }

    void test_Spring_batch() {
        def configurationItemType = cmdb.createConfigurationItemType(ArtefactType.ZIP,DeploymentTarget.JVM);
        assertNotNull configurationItemType
        assertTrue configurationItemType instanceof ZipJvmType
        assertTrue configurationItemType.accept(java7)
        assertFalse configurationItemType.accept(java8)
    }

    void test_other_not_supported() {
        def configurationItemType = cmdb.createConfigurationItemType(ArtefactType.EAR, DeploymentTarget.ARTEFACTORY);
        assertNull configurationItemType
    }
}
