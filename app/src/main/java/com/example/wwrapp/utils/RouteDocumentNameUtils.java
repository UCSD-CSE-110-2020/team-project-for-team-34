package com.example.wwrapp.utils;

/**
 * Initializes the names of Route documents
 */
public class RouteDocumentNameUtils {

    private RouteDocumentNameUtils() {

    }

    /**
     * Creates a document name for a route given the email of its owner and the route name
     *
     * @param ownerEmail non-null, non-empty
     * @param routeName  non-null, non-empty
     * @return a document name for a route given the email of its owner and the route name
     * The name is the format ownerEmail + routeName (concatenated)
     */
    public static String getRouteDocumentName(String ownerEmail, String routeName) {
        assert ownerEmail != null;
        assert routeName != null;
        return ownerEmail + routeName;
    }

    /**
     * Returns the route name from the path to the route document (i.e. the string after the last
     * slash)
     *
     * @param pathToRouteDocument slash-separated path to route (e.g. /users/user1/myRoutes/routeDocName)
     * @return The name of the route document (i.e. the string after the last
     * slash)
     */
    public static String getRouteDocumentNameFromPath(String pathToRouteDocument) {
        int routeNameStartIndex = pathToRouteDocument.lastIndexOf(FirestoreConstants.PATH_SLASH_SEPARATOR) + 1;
        return pathToRouteDocument.substring(routeNameStartIndex);
    }

    public static String getNestedFieldName(String outerFieldName, String innerFieldName) {
        return outerFieldName + FirestoreConstants.DOT_STR + innerFieldName;
    }
}
