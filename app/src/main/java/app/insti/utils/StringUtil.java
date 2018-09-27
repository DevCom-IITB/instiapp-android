package app.insti.utils;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shivam Sharma on 13-08-2018.
 */

public class StringUtil {

    public final static boolean isValidEmail(String target) {
        if (isBlank(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isEqual(String str1, String str2)
    {
        return str1 == null ? str2 == null : str1.equals(str2);

    }

    public static void printJSON(Object obj){
        Gson gson = new Gson();
        //System.out.println(gson.toJson(obj));
    }

    public static int getIntegerFromString(String s){
        try{
            if(StringUtil.isBlank(s))
                return 0;
            return Integer.parseInt(s);
        }
        catch(NumberFormatException e){
            return 0;
        }
    }

    public static int getIntegerFromBoolean(boolean b){
        if(b)
            return 1;
        else
            return 0;
    }

    public static boolean getBooleanFromInteger(int i){
        if(i == 0)
            return false;
        else
            return true;
    }

    public static boolean isEmpty(String str)
    {
        return (str == null) || (str.length() == 0);
    }

    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str)
    {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str)
    {
        return !isBlank(str);
    }

    public static String getStringWithoutWhiteSpaces(String string){
        if(isBlank(string))
            return new String();
        return string.replaceAll("\\s", "");
    }

    /**
     * Converts a collection of Strings to a comma separated String
     *
     * @param strings
     * @return
     */
    public static String convertListOfStringsToCommaSeparated(List<String> strings){
        if(strings == null || strings.size() == 0)
            return "";

        StringBuffer sb = new StringBuffer();

        for (String string : strings) {
            sb.append(string);
            sb.append(",");
        }

        // Remove last comma and return
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * Converts a collection of Strings to a Pipe separated String
     *
     * @param strings
     * @return
     */
    public static String convertListOfStringsToPipeSeparated(List<String> strings){
        if(strings == null || strings.size() == 0)
            return "";

        StringBuffer sb = new StringBuffer();

        for (String string : strings) {
            sb.append(string);
            sb.append("|");
        }

        // Remove last comma and return
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * Converts a comma separated list of String to a List of String objects
     *
     * @param string
     * @return
     */
    public static List<String> convertCommaSeparatedListToStringCollection(String string){
        List<String> strings = new ArrayList<String>();
        if(isEmpty(string))
            return strings;

        String[] strArray = string.split(",");
        for (String strVal : strArray) {
            strings.add(strVal);
        }
        return strings;
    }

    /**
     * Converts a comma separated list of String, List to a Map of List objects
     *
     * Example:
     * The String "shoes:a|b|c|d,books:a|e|f|g" gets converted to a Map whose
     * - Keys are (shoes, books)
     * - Values is a List of String
     * - For shoes, values is a list of String having a,b,c,d as entries
     * - For books, values is a list of String having a,e,f,g as entries
     *
     * @param string
     * @return
     */
    public static Map<String, List<String>> convertCommaSeparatedListToMap(String string){

        Map<String, List<String>> lists = new HashMap<String, List<String>>();

        if(isEmpty(string))
            return lists;

        String[] strArray = string.split(",");
        for (String strVal : strArray) {
            String[] listKeyValuePairs = strVal.split(":");

            if(listKeyValuePairs != null && listKeyValuePairs.length == 2){
                String key = listKeyValuePairs[0];
                String values = listKeyValuePairs[1];
                lists.put(key, convertCommaSeparatedListToStringCollection(values));
            }
        }
        return lists;
    }

    /**
     * Adds a String which is a number to a String which is also a number
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String addStringToString(String s1, String s2){
        if(isEmpty(s1) && isEmpty(s2))
            return "0";

        if(isEmpty(s1))
            return s2;

        if(isEmpty(s2))
            return s1;

        int newVal = Integer.valueOf(s1) + Integer.valueOf(s2);
        return new String("" + newVal);
    }

    /**
     * Adds a String which is a number to a String
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String addStringToStringAsCommaSeperated(String s1, String s2){
        if(isEmpty(s1) && isEmpty(s2))
            return "";

        if(isEmpty(s1))
            return s2;

        if(isEmpty(s2))
            return s1;

        return new String(s1 + "," +s2);
    }





    public static String capitalizeFully(String str){
        if(isBlank(str))
            return "";

        str = str.toLowerCase().replaceAll("-", " ");

        int strLen = str.length();
        StringBuffer buffer = new StringBuffer(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                capitalizeNext = true;
            }
            else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }



    public static String convertToUtf8(String src){
        if(src == null)
            return "";

        try{
            return URLEncoder.encode(src, "UTF-8");
        }
        catch (Exception e){
            return src;
        }

    }







    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
        }
        final String abrevMarker = "...";
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + abrevMarker;
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + maxWidth - 3 < str.length()) {
            return abrevMarker + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return abrevMarker + str.substring(str.length() - (maxWidth - 3));
    }

    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }
















}
