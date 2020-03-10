package com.example.wwrapp.utils;

/**
 * Extracts a user's initials from their name field, which is a single String
 */
public class InitialsExtracter {

    private static final String SPACE_STR = " ";

    // Prevent instantiation
    private InitialsExtracter() {
    }

    public static String getFirstInitial(String name) {
        return String.valueOf(name.charAt(0));
    }

    public static String getSecondInitial(String name) {
        String secondInitial = null;

        int indexOfSpace = name.indexOf(SPACE_STR);
        // If the name field has a space (e.g. FirstName LastName):
        if (indexOfSpace > 0) {
            secondInitial = String.valueOf(name.charAt(indexOfSpace + 1));
        } else {
            // If there is no space (e.g. FirstNameLastName)
            secondInitial = String.valueOf(name.charAt(0));;
        }
        return secondInitial;
    }

    /**
     * Returns true if only one initial can be extracted from the given name, false otherwise
     * @param name
     * @return true if only one initial can be extracted from the given name
     */
    public static boolean hasOnlyOneInitial(String name) {
        return !name.contains(SPACE_STR);
    }
}
