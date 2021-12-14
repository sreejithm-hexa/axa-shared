package unit.test

import be.axa.util.Properties

class PropertiesTest extends GroovyTestCase {

    static final String DEV_CHANNEL_KEY = "devChannel"
    static final String COMMENT_KEY = "comment"
    static final String BOOLEAN_KEY = "booleanProperties"
    String propertyFile = DEV_CHANNEL_KEY + "=a\r\n#" + COMMENT_KEY + "=comment\r\n"+ BOOLEAN_KEY + "=true";

    void test_read_properties_remove_comments(){
        def properties = new Properties(propertyFile).load();
        assertEquals 2, properties.size();
        assertTrue properties.containsKey(DEV_CHANNEL_KEY)
        assertTrue properties.containsKey(BOOLEAN_KEY)
        assertFalse properties.containsKey(COMMENT_KEY)
    }

    void test_read_boolean(){
        String key = "boolean";
        def properties = new Properties(key + "=true").load();
        assertTrue properties.containsKey(key)
        assertTrue properties.get(key)

        properties = new Properties(key + "=false").load();
        assertTrue properties.containsKey(key)
        assertFalse properties.get(key)
    }
}
