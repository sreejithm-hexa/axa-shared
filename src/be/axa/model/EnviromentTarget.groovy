package be.axa.model

/**
 * Created by DSND714 on 12/09/2019.
 */
enum EnviromentTarget {

    IT("it1"),IT1("it1"),FT("ft1"),FT1("ft1"),NONE("NONE"),None("None")

    /**
     * pom.xml java.version property
     */
    private final String TargetEnv

    EnviromentTarget(String TargetEnv){
        this.TargetEnv = TargetEnv
    }

    String getEnviromentTargetName() {
        return TargetEnv
    }
    boolean isEnvIT(){
        return ( IT.equals(this) || IT1.equals(this))
    }    
}