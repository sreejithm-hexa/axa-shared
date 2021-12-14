package unit.test

import be.axa.config.AxaProperties

class AXAPropertiesTest extends GroovyTestCase {

    static final String DEV_CHANNEL_KEY = "devChannel"
    static final String COMMENT_KEY = "comment"
    static final String BOOLEAN_KEY = "booleanProperties"
    String propertyFile = DEV_CHANNEL_KEY + "=a\r\n#" + COMMENT_KEY + "=comment\r\n"+ BOOLEAN_KEY + "=true";

    void test_read_empty_file_returns_8_default_properties(){

        def properties = new AxaProperties(new StepStub()).load();
        assertEquals 8, properties.size();
    }


    private class StepStub {
        def fileExists(String arg){
            return true;
        }

        def readFile(def map){
            return propertyFile;
        }

        def error(def error){
            println error
        }
    }

}