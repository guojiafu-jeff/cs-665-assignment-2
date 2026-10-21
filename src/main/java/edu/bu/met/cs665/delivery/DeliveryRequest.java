/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: DeliveryRequest.java
 * Description: A request to deliver one product from a shop to a customer address.
 */

package edu.bu.met.cs665.delivery;

import edu.bu.met.cs665.notification.Subscriber;
import java.util.concurrent.atomic.AtomicInteger;   // Every request needs a unique id

/**
 * The event that is pushed to the drivers. A Shop creates it, hands it to the DeliveryDispatcher,
 * and the dispatcher pushes it to every available Driver.
 * The four fields that describe the delivery never change. The status and the assigned driver do change,
 * but only DeliveryDispatcher may change them.
 * The two setters have no access modifier, so only classes in this package can call them.
 */
public class DeliveryRequest {
    /** Shared counter that gives every request a unique id, starting at 1. */
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private final int id;
    private final String shopName;
    private final String product;
    private final String destination;
    private RequestStatus status;
    private Subscriber<DeliveryRequest> assignedDriver;

    /**
     * Create an OPEN request with the next free id. Called by Shop.receiveOrder().
     *
     * @param shopName name of the shop
     * @param product what has to be delivered
     * @param destination customer address
     */
    public DeliveryRequest(String shopName, String product, String destination) {
        this.id = NEXT_ID.getAndIncrement();
        this.shopName = shopName;
        this.product = product;
        this.destination = destination;
        this.status = RequestStatus.OPEN;
        this.assignedDriver = null;
    }

    /**
     * Get the unique id of this request. Used in log messages and tests.
     *
     * @return the id, unique across all requests
     */
    public int getId() {
        return id;
    }

    /**
     * Get the name of the shop that created this request.
     *
     * @return the shop name
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * Get the product to deliver.
     *
     * @return the product description
     */
    public String getProduct() {
        return product;
    }

    /**
     * Get the customer address.
     *
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Get the current state of this request. Read by the tests and Main.
     *
     * @return OPEN, ASSIGNED or COMPLETED
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Get the driver who accepted this request.
     *
     * @return the assigned driver, or null while the request is OPEN
     */
    public Subscriber<DeliveryRequest> getAssignedDriver() {
        return assignedDriver;
    }

    /**
     * Change the state. Only DeliveryDispatcher calls it, so it is package-private.
     *
     * @param status the new state
     */
    void setStatus(RequestStatus status) {
        this.status = status;
    }

    /**
     * Record who accepted the request. Only DeliveryDispatcher calls it.
     *
     * @param driver the driver, or null when the request becomes OPEN again
     */
    void setAssignedDriver(Subscriber<DeliveryRequest> driver) {
        this.assignedDriver = driver;
    }

    /**
     * One-line description used by log messages, for example in Main.
     *
     * @return "Request #id from shopName: product to destination (RequestStatus)"
     */
    @Override
    public String toString() {
        return "Request #" + id + "from " + shopName + ": " + product + " to " + destination + " (" + status + ")";
    }

}
