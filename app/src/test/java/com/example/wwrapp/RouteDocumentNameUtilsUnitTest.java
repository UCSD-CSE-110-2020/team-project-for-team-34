package com.example.wwrapp;

import com.example.wwrapp.utils.RouteDocumentNameUtils;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Unit test for creating a document name for a route
 */
public class RouteDocumentNameUtilsUnitTest {

    @Test
    public void testInitializeRouteDocumentName() {
        String ownerEmail = "ariana@gmail.com";
        String routeName = "Ariana's favorite route";
        String expected = ownerEmail + routeName;
        assertEquals(expected, RouteDocumentNameUtils.getRouteDocumentName(ownerEmail, routeName));
    }

    @Test
    public void testGetRouteNameFromPath() {
        String path = "users/user/ariana@gmail.com/Ariana's favorite route";
        String expected = "Ariana's favorite route";
        assertEquals(expected, RouteDocumentNameUtils.getRouteDocumentNameFromPath(path));
    }
}
