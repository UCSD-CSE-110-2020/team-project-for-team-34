package com.example.wwrapp;

import com.example.wwrapp.utils.InitialsExtracter;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for extracting initials from a string
 */
public class InitialsExtracterUnitTest {

    @Test
    public void testNameFieldHasSpace() {
        String name = "FirstName LastName";
        String expectedFirstInitial = "F";
        String expectedSecondInitial = "L";
        assertEquals(expectedFirstInitial, InitialsExtracter.getFirstInitial(name));
        assertEquals(expectedSecondInitial, InitialsExtracter.getSecondInitial(name));
    }

    @Test
    public void testNameFieldHasNoSpace() {
        String name = "FirstNameLastName";
        String expectedFirstInitial = "F";
        String expectedSecondInitial = "F";
        assertEquals(expectedFirstInitial, InitialsExtracter.getFirstInitial(name));
        assertEquals(expectedSecondInitial, InitialsExtracter.getSecondInitial(name));
    }
}
