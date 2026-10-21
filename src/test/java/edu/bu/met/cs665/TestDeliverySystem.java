/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: TestDeliverySystem.java
 * Description: User interaction sequences of the delivery notification system.
 * (A shop broadcasts a request to five drivers, and drivers accept, abandon and complete it.)
 */

package edu.bu.met.cs665;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import edu.bu.met.cs665.delivery.DeliveryDispatcher;
import edu.bu.met.cs665.delivery.DeliveryRequest;
import edu.bu.met.cs665.delivery.RequestStatus;
import edu.bu.met.cs665.participant.Driver;
import edu.bu.met.cs665.participant.Shop;
import edu.bu.met.cs665.participant.VehicleType;
import org.junit.Test;

/**
 * Each test builds a fresh dispatcher, one shop and five drivers, then plays one scenario.
 */
public class TestDeliverySystem {

    /**
     * Create the five drivers required by the assignment, all subscribed to the dispatcher.
     *
     * @param dispatcher the dispatch center the drivers subscribe to
     * @return the drivers in creation order
     */
    private Driver[] fiveDrivers(DeliveryDispatcher dispatcher) {
        return new Driver[] {
                new Driver("A", VehicleType.VAN, dispatcher),
                new Driver("B", VehicleType.TAXI, dispatcher),
                new Driver("C", VehicleType.SCOOTER, dispatcher),
                new Driver("D", VehicleType.VAN, dispatcher),
                new Driver("E", VehicleType.SCOOTER, dispatcher)
        };
    }

    /**
     * The required scenario: one shop, one request, five drivers, every driver is notified.
     */
    @Test
    public void testBroadcastReachesAllFiveDrivers() {
        // Given
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();
        Shop shop = new Shop("Corner Shop", dispatcher);
        Driver[] drivers = fiveDrivers(dispatcher);

        // When: the shop receives an order
        DeliveryRequest request = shop.receiveOrder("2 kg apples", "12 Main St");

        // Then: every driver got exactly this request, and it is still open
        for (Driver driver : drivers) {
            assertEquals(1, driver.getReceivedRequests().size());
            assertSame(request, driver.getReceivedRequests().get(0));
        }
        assertEquals("Corner Shop", request.getShopName());
        assertEquals("2 kg apples", request.getProduct());
        assertEquals("12 Main St", request.getDestination());
        assertEquals(RequestStatus.OPEN, request.getStatus());
        assertNull(request.getAssignedDriver());
        assertTrue(dispatcher.getOpenRequests().contains(request));
    }

    /**
     * A driver who is off duty stays subscribed but is skipped by the broadcast.
     */
    @Test
    public void testOffDutyDriverIsNotNotified() {
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();
        Shop shop = new Shop("Corner Shop", dispatcher);
        Driver[] drivers = fiveDrivers(dispatcher);
        drivers[2].setAvailable(false);

        shop.receiveOrder("flowers", "5 Park Ave");

        assertEquals(0, drivers[2].getReceivedRequests().size());
        assertEquals(1, drivers[0].getReceivedRequests().size());
        assertEquals(1, drivers[4].getReceivedRequests().size());
    }

    /**
     * A driver who unsubscribed is not notified at all.
     */
    @Test
    public void testUnsubscribedDriverIsNotNotified() {
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();
        Shop shop = new Shop("Corner Shop", dispatcher);
        Driver[] drivers = fiveDrivers(dispatcher);
        dispatcher.unsubscribe(drivers[4]);

        shop.receiveOrder("a book", "9 Elm St");

        assertEquals(0, drivers[4].getReceivedRequests().size());
        assertEquals(1, drivers[3].getReceivedRequests().size());
    }

    /**
     * Accepting is first come, first served; the winner becomes busy.
     */
    @Test
    public void testAcceptIsFirstComeFirstServed() {
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();
        Shop shop = new Shop("Corner Shop", dispatcher);
        Driver[] drivers = fiveDrivers(dispatcher);
        DeliveryRequest request = shop.receiveOrder("pizza", "3 Oak St");

        assertTrue(drivers[0].accept(request));
        assertFalse(drivers[1].accept(request));

        assertEquals(RequestStatus.ASSIGNED, request.getStatus());
        assertSame(drivers[0], request.getAssignedDriver());
        assertFalse(drivers[0].isAvailable());
        assertTrue(drivers[1].isAvailable());
        assertFalse(dispatcher.getOpenRequests().contains(request));
    }

    /**
     * Abandoning reopens the request, frees the driver and broadcasts the request again.
     */
    @Test
    public void testAbandonReopensAndBroadcastsAgain() {
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();
        Shop shop = new Shop("Corner Shop", dispatcher);
        Driver[] drivers = fiveDrivers(dispatcher);
        DeliveryRequest request = shop.receiveOrder("pizza", "3 Oak St");
        drivers[0].accept(request);

        drivers[0].abandon(request);

        assertEquals(RequestStatus.OPEN, request.getStatus());
        assertNull(request.getAssignedDriver());
        assertTrue(drivers[0].isAvailable());
        // the other drivers were notified a second time
        assertEquals(2, drivers[1].getReceivedRequests().size());
        assertTrue(drivers[1].accept(request));
    }

    /**
     * Completing is final: the driver is free again and nobody can take the request.
     */
    @Test
    public void testCompleteIsFinal() {
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();
        Shop shop = new Shop("Corner Shop", dispatcher);
        Driver[] drivers = fiveDrivers(dispatcher);
        DeliveryRequest request = shop.receiveOrder("pizza", "3 Oak St");
        drivers[0].accept(request);

        drivers[0].complete(request);

        assertEquals(RequestStatus.COMPLETED, request.getStatus());
        assertTrue(drivers[0].isAvailable());
        assertFalse(drivers[1].accept(request));
        try {
            drivers[0].abandon(request);
            fail("A completed request cannot be abandoned");
        } catch (IllegalStateException expected) {
            // refused as required
        }
    }

    /**
     * Only the assigned driver may abandon a request.
     */
    @Test
    public void testOnlyAssignedDriverMayAbandon() {
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();
        Shop shop = new Shop("Corner Shop", dispatcher);
        Driver[] drivers = fiveDrivers(dispatcher);
        DeliveryRequest request = shop.receiveOrder("pizza", "3 Oak St");
        drivers[0].accept(request);

        try {
            drivers[1].abandon(request);
            fail("Only the assigned driver may abandon");
        } catch (IllegalStateException expected) {
            // refused as required
        }
        assertEquals(RequestStatus.ASSIGNED, request.getStatus());
        assertSame(drivers[0], request.getAssignedDriver());
    }

}
