package example;

public class NameGenerator implements org.eclipse.persistence.oxm.XMLNameTransformer {
 
    //Use the unqualified class name as our root element name.
    public String transformRootElementName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }
 
    //The same algorithm as root element name plus "Type" appended to the end.
    public String transformTypeName(String name) {
        return transformRootElementName(name) + "Type";
    }
 
    //The name will be lower case with word breaks represented by '-'.  
    //Note:  A capital letter in the original name represents the start of a new word.
    public String transformElementName(String name) {
        StringBuilder strBldr = new StringBuilder();
        for(char character : name.toCharArray()) {
            if(Character.isUpperCase(character)) {
                strBldr.append('-');
                strBldr.append(Character.toLowerCase(character));
            } else {
                strBldr.append(character);
            }
         }
        return strBldr.toString();
    }
 
    //The original name converted to upper case.
    public String transformAttributeName(String name) {
        return name.toUpperCase();
    }
 
}
