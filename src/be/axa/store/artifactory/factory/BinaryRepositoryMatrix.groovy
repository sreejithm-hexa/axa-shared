package be.axa.store.artifactory.factory

import be.axa.build.maven.Maven
import be.axa.model.configurationitem.strategy.common.ConfigurationItemType
import be.axa.model.configurationitem.strategy.impl.JarArtefactoryType
import be.axa.store.artifactory.IBinariesRepository
import be.axa.store.artifactory.impl.LibArtifactory
import be.axa.store.artifactory.impl.MavenRepositoryImpl

class BinaryRepositoryMatrix {

    private Maven mvn;

    private Map<Class<?>, IBinariesRepository> sdbSingletonBeanResolver;

    BinaryRepositoryMatrix(Maven mvn) {
        this.mvn = mvn;
        this.sdbSingletonBeanResolver = [
                (JarArtefactoryType.class): new LibArtifactory(mvn)
        ];
    }

    IBinariesRepository select(ConfigurationItemType configurationItemType) {
        this.sdbSingletonBeanResolver.getOrDefault(
                configurationItemType.class,
                new MavenRepositoryImpl(this.mvn.pipeline.steps, this.mvn.pom)
        );
    }
}
