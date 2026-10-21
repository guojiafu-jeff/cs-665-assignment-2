/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: DeliveryDispatcher.java
 * Description: The central dispatch center shared by all shops and drivers.
 * It is the publisher of the Observer pattern and keeps the state of every delivery request.
 */

package edu.bu.met.cs665.delivery;

import edu.bu.met.cs665.notification.Publisher;
import edu.bu.met.cs665.notification.Subscriber;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;     // Log4j: the logging library used by the template. LogManager creates a Logger for this class.
import org.apache.logging.log4j.Logger;         // LOGGER.info(...) prints a timestamped line with the class name and the level.


/**
 * Shops hand delivery requests to the dispatcher (submit),
 * the dispatcher pushes them to every available subscriber (notifySubscribers),
 * and drivers report back when they accept, abandon or complete a request.
 * The dispatcher only knows the Subscriber interface, never the Driver class, so new kinds of subscribers need no change here.
 */
public class DeliveryDispatcher implements Publisher<DeliveryRequest> {
    private static final Logger LOGGER = LogManager.getLogger(DeliveryDispatcher.class);

    /** Everyone who registered for requests, available or not. */
    private final List<Subscriber<DeliveryRequest>> subscribers = new ArrayList<>();

    /** Every request ever submitted, in any state. */
    private final List<DeliveryRequest> requests = new ArrayList<>();

    /**
     * Add a subscriber. Called by the Driver constructor. A subscriber that is already in the list is not added twice.
     *
     * @param subscriber who wants to receive delivery requests
     */
    @Override
    public void subscribe(Subscriber<DeliveryRequest> subscriber) {
        if ((subscriber != null) && (!subscribers.contains(subscriber))) {
            subscribers.add(subscriber);
        }
    }

    /**
     * Remove a subscriber. Removing one that is not in the list does nothing.
     *
     * @param subscriber who no longer wants delivery requests
     */
    @Override
    public void unsubscribe(Subscriber<DeliveryRequest> subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * Push a request to every subscriber that is available right now. Used by submit() and abandon().
     * Iterates over a copy of the list so that a subscriber may subscribe or unsubscribe while being notified without breaking the loop.
     *
     * @param request the request to broadcast
     */
    @Override
    public void notifySubscribers(DeliveryRequest request) {
        for (Subscriber<DeliveryRequest> subscriber : new ArrayList<>(subscribers)) {       // The program will crash if we use the original list rather than a copy of it.
            if (subscriber.isAvailable()) {
                subscriber.update(request);
            }
        }
    }

    /**
     * Register a new request and broadcast it. Called by Shop.receiveOrder().
     *
     * @param request the request created by a shop
     * @throws IllegalArgumentException if request is null
     */
    public void submit(DeliveryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null.");
        }
        requests.add(request);
        LOGGER.info("Broadcasting " + request);
        notifySubscribers(request);
    }

    /**
     * Get the requests that nobody has accepted yet. Returns a new list, so the caller cannot change the dispatcher's own list.
     *
     * @return the OPEN requests, oldest first
     */
    public List<DeliveryRequest> getOpenRequests() {
        List<DeliveryRequest> open = new ArrayList<>();
        for (DeliveryRequest request : requests) {
            if (request.getStatus() == RequestStatus.OPEN) {
                open.add(request);
            }
        }
        return open;
    }

    /**
     * A driver accepts a request. First come, first served. Only an OPEN request can be accepted.
     * Called by Driver.accept().
     *
     * @param driver who accepts
     * @param request which request
     * @return true if the request was OPEN and is now assigned to the driver
     * @throws IllegalArgumentException if the request was never submitted here
     */
    public boolean accept(Subscriber<DeliveryRequest> driver, DeliveryRequest request) {
        requireKnown(driver, request);
        if (request.getStatus() != RequestStatus.OPEN) {
            LOGGER.info(driver.getName() + " could not accept " + request);
            return false;
        }
        request.setStatus(RequestStatus.ASSIGNED);
        request.setAssignedDriver(driver);
        LOGGER.info(driver.getName() + " accepted " + request);
        return true;
    }

    /**
     * The assigned driver gives the request back. It becomes OPEN again and is broadcast once more.
     * Called by Driver.abandon().
     *
     * @param driver who gives up
     * @param request which request
     * @throws IllegalStateException if the request is not assigned to this driver
     */
    public void abandon(Subscriber<DeliveryRequest> driver, DeliveryRequest request) {
        requireAssignedTo(driver, request);
        request.setStatus(RequestStatus.OPEN);
        request.setAssignedDriver(null);
        LOGGER.info(driver.getName() + " abandoned " + request + ". Broadcasting again");
        notifySubscribers(request);
    }

    /**
     * The assigned driver delivered the request. COMPLETED is the final state.
     * Called by Driver.complete().
     *
     * @param driver who delivered
     * @param request which request
     * @throws IllegalStateException if the request is not assigned to this driver
     */
    public void complete(Subscriber<DeliveryRequest> driver, DeliveryRequest request) {
        requireAssignedTo(driver, request);
        request.setStatus(RequestStatus.COMPLETED);
        LOGGER.info(driver.getName() + " completed " + request);
    }

    /**
     * Shared check.
     * Both arguments exist and the request was submitted to this dispatcher.
     *
     * @param driver the driver to check
     * @param request the request to check
     */
    private void requireKnown(Subscriber<DeliveryRequest> driver, DeliveryRequest request) {
        if ((driver == null) || (request == null)) {
            throw new IllegalArgumentException("Driver and request must not be null.");
        }
        if (!requests.contains(request)) {
            throw new IllegalArgumentException("Unknown request: " + request);
        }
    }

    /**
     * Shared check for abandon() and complete().
     * The request must be ASSIGNED to exactly this driver.
     *
     * @param driver the driver to check
     * @param request the request to check
     */
    private void requireAssignedTo(Subscriber<DeliveryRequest> driver, DeliveryRequest request) {
        requireKnown(driver, request);
        if ((request.getStatus() != RequestStatus.ASSIGNED) || (request.getAssignedDriver() != driver)) {
            throw new IllegalStateException(driver.getName() + " is not the assigned driver of " + request);
        }
    }

}
