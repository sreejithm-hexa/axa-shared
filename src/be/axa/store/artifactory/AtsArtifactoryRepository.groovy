#!/usr/bin/env groovy
package be.axa.store.artifactory
/**
 * Created by DTDJ857 on 24/07/2017.
 */
abstract class AtsArtifactoryRepository implements Serializable, IBinariesRepository {

    //TODO: move to HttpClient and AqlClient ?
    protected static final String artifactoryTokenKey = 'AKCp2V77Vd55MHdn1sLMCZEP79GTGVNGTAHbtwoHYGae6auMBHP7cgXxAu4hiBiNDaDZHNArP'

    protected static final String artifactoryUrl = 'https://atsartifactory.axa.be/artifactory'

    public static final String atsBinaryStore = 'axatech-buildstore'

    public static final String binaryStore = 'axa-buildstore'

    public static final String atsPropertiesBinaryStore = 'axatech-tokenstore-java'

    public static final String propertiesBinaryStore = 'axa-tokenstore-java'

    protected steps

    AtsArtifactoryRepository(steps) {
        this.steps = steps
    }

}