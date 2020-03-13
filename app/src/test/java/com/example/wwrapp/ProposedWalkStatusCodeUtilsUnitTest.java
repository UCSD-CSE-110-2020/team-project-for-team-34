package com.example.wwrapp;

import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.ProposedWalkStatusCodeUtils;
import com.example.wwrapp.utils.WWRConstants;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ProposedWalkStatusCodeUtilsUnitTest {

    @Test
    public void testGetStatusFromCode() {
        int pendingCode = WWRConstants.PROPOSED_WALK_PENDING_STATUS;
        int acceptCode = WWRConstants.PROPOSED_WALK_ACCEPT_STATUS;
        int badRouteCode = WWRConstants.PROPOSED_WALK_BAD_ROUTE_STATUS;
        int badTimeCode = WWRConstants.PROPOSED_WALK_BAD_TIME_STATUS;

        String expectedPending = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_PENDING;
        String expectedAccept = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_ACCEPTED;
        String expectedBadRoute = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_DECLINED_NOT_A_GOOD_ROUTE;
        String expectedBadTime = FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_DECLINED_BAD_TIME;

        assertEquals(expectedPending, ProposedWalkStatusCodeUtils.getStatusFromCode(pendingCode));
        assertEquals(expectedAccept, ProposedWalkStatusCodeUtils.getStatusFromCode(acceptCode));
        assertEquals(expectedBadRoute, ProposedWalkStatusCodeUtils.getStatusFromCode(badRouteCode));
        assertEquals(expectedBadTime, ProposedWalkStatusCodeUtils.getStatusFromCode(badTimeCode));
    }

    @Test
    public void testGetUserAndStatusDisplay() {
        String name = "Ariana";

        int pendingCode = WWRConstants.PROPOSED_WALK_PENDING_STATUS;
        int acceptCode = WWRConstants.PROPOSED_WALK_ACCEPT_STATUS;
        int badRouteCode = WWRConstants.PROPOSED_WALK_BAD_ROUTE_STATUS;
        int badTimeCode = WWRConstants.PROPOSED_WALK_BAD_TIME_STATUS;

        String expectedPendingUserAndStatus =
                name + ": " + FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_PENDING;
        String expectedAcceptUserAndStatus =
                name + ": " + FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_ACCEPTED;
        String expectedBadRouteUserAndStatus =
                name + ": " + FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_DECLINED_NOT_A_GOOD_ROUTE;
        String expectedBadTimeUserAndStatus =
                name + ": " + FirestoreConstants.FIRESTORE_ROUTE_INVITEE_STATUS_DECLINED_BAD_TIME;

        assertEquals(expectedPendingUserAndStatus, ProposedWalkStatusCodeUtils.getUserAndStatusDisplay(name, pendingCode));
        assertEquals(expectedAcceptUserAndStatus, ProposedWalkStatusCodeUtils.getUserAndStatusDisplay(name, acceptCode));
        assertEquals(expectedBadRouteUserAndStatus, ProposedWalkStatusCodeUtils.getUserAndStatusDisplay(name, badRouteCode));
        assertEquals(expectedBadTimeUserAndStatus, ProposedWalkStatusCodeUtils.getUserAndStatusDisplay(name, badTimeCode));
    }
}
