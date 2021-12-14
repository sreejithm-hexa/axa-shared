package be.axa.util

class Properties implements Serializable {

    private static final String TRUE = "TRUE"
    private static final String FALSE = "FALSE"

    def file;

    Properties(file) {
        this.file = file;
    }

    def load() {
        def axaPropertiesArray = file.split("\\r?\\n")
                .findAll(isNotBlankOrCommentLine)
        return axaPropertiesArray.collectEntries(propertyLineToProperty)
    }

    def isNotBlankOrCommentLine = { line -> line.trim().length() > 0 && !line.trim().startsWith("#") }

    def propertyLineToProperty = { line ->
        def tokens = line.tokenize("=");
        def numberOfTokens = tokens.size()
        def propertyKey = tokens[0]
        if (numberOfTokens < 2) return [propertyKey, null]
        else {
            String value = tokens.subList(1, numberOfTokens).join()
            value = value.trim()
            if (TRUE.equalsIgnoreCase(value)) {
                return [propertyKey, true]
            } else if (FALSE.equalsIgnoreCase(value)) {
                return [propertyKey, false]
            } else {
                return [propertyKey, value]
            }
        }
    }

}
