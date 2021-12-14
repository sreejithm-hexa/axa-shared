package be.axa.model.configurationitem

/**
 * Created by DTDJ857 on 23/10/2017.
 */
enum ArtefactType {

    EAR("ear"),WAR("war"),JAR("jar"),ZIP("zip")

    /**
     * pom.xml java.version property
     */
    private final String packaging

    static ArtefactType getDefaultArtefactType(){
        return "EAR" as ArtefactType
    }

    ArtefactType(String packaging){
        this.packaging = packaging
    }

    String getPackaging() {
        return packaging
    }
}