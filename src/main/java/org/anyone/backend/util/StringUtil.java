package org.anyone.backend.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    static public final String FIND_AT = "@\\S+";
    static public final String FIND_TAG = "#[^#]+#";

    static public boolean hasAts(String s) {
        Pattern pattern = Pattern.compile(FIND_AT);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    static public boolean hasTags(String s) {
        Pattern pattern = Pattern.compile(FIND_TAG);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    static public ArrayList<String> matchAts(String s) {
        return matchStrings(s, FIND_AT);
    }

    static public ArrayList<String> matchTags(String s) {
        return matchStrings(s, FIND_TAG);
    }

    static public String matchedArrayToString(ArrayList<String> strings) {
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s).append(",");
        }
        return builder.toString();
    }

    private static ArrayList<String> matchStrings(String s, String findTag) {
        Pattern pattern = Pattern.compile(findTag);
        ArrayList<String> results = new ArrayList<>();
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            results.add(matcher.group());
        }
        return results;
    }
}
