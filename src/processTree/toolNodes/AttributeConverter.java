package processTree.toolNodes;

public class AttributeConverter
{

    /**
     * Converts a string attribute to an int
     * @param attribute The attribute to be converted
     * @return An int which was represented by the string
     */
    public static int convertToInt(String attribute)
    {
        return Integer.parseInt(attribute);
    }

    /**
     * Converts a string attribute to an int array
     * @param attribute The attribute to be converted
     * @return An array of ints which was represented by the string
     */
    public static int[] convertToIntArray(String attribute)
    {
        String[] attrString = attribute.split(",");
        int[] intArray = new int[attrString.length];
        for(int i = 0; i < attrString.length; i++)
        {
            intArray[i] = Integer.parseInt(attrString[i]);
        }
        return intArray;
    }

    /**
     * Converts an int array to a string attribute
     * @param intArray The array to be converted
     * @return The attribute
     */
    public static String convertToAttribute(int[] intArray)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(intArray[0]);
        for(int i = 1; i < intArray.length; i++)
        {
            builder.append(",");
            builder.append(intArray[i]);
        }
        return builder.toString();
    }

}
