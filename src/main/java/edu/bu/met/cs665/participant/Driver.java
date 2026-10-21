/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: Driver.java
 * Description: A freelance driver who receives delivery requests from the dispatcher and can accept, abandon and complete them.
 */

package edu.bu.met.cs665.participant;

import edu.bu.met.cs665.delivery.DeliveryDispatcher;
import edu.bu.met.cs665.delivery.DeliveryRequest;
import edu.bu.met.cs665.notification.Subscriber;
import java.util.ArrayList;
import java.util.List;

/**
 * Subscriber of the Observer pattern.
 * The dispatcher calls update() for every request while the driver is available.
 * The driver keeps every request it was told about and reports accept / abandon / complete back to the dispatcher.
 * The driver is responsible for its own availability: busy after accepting, free again after completing or abandoning, and it can go off duty with setAvailable(false).
 */
public class Driver implements Subscriber<DeliveryRequest> {
    private final String name;
    private final VehicleType vehicleType;
    private final DeliveryDispatcher dispatcher;    // All drivers point to the same dispatcher

    /** Every request pushed to this driver, in the order received. */
    private final List<DeliveryRequest> receivedRequests = new ArrayList<>();

    /** True when the driver can take a new request. */
    private boolean available;

    /**
     * Create an available driver and subscribe it to the dispatcher right away.
     *
     * @param name the driver's name
     * @param vehicleType what the driver delivers with
     * @param dispatcher the dispatch center to subscribe to
     * @throws IllegalArgumentException if any argument is null
     */
    public Driver(String name, VehicleType vehicleType, DeliveryDispatcher dispatcher) {
        if ((name == null) || (vehicleType == null) || (dispatcher == null)) {
            throw new IllegalArgumentException("Name, vehicle type and dispatcher are required.");
        }
        this.name = name;
        this.vehicleType = vehicleType;
        this.dispatcher = dispatcher;
        this.available = true;
        dispatcher.subscribe(this);
    }

    /**
     * Called by the dispatcher for every broadcast request.
     *
     * @param request the request that was broadcast
     */
    @Override
    public void update(DeliveryRequest request) {
        receivedRequests.add(request);
    }

    /**
     * Whether the dispatcher should push requests to this driver right now.
     *
     * @return true if on duty and not busy with a request
     */
    @Override
    public boolean isAvailable() {
        return available;
    }

    /**
     * Get the driver's name. Used in log messages.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the vehicle this driver delivers with.
     *
     * @return the vehicle type
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * On/off duty switch. Off-duty drivers stay subscribed but are skipped by broadcasts.
     *
     * @param available true to go on duty, false to go off duty
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Get every request this driver was told about. Returns a copy.
     *
     * @return the received requests, oldest first
     */
    public List<DeliveryRequest> getReceivedRequests() {
        return new ArrayList<>(receivedRequests);
    }

    /**
     * Try to take a request. If the dispatcher grants it, the driver is busy until complete() or abandon().
     *
     * @param request the request to accept
     * @return true if this driver got the request
     */
    public boolean accept(DeliveryRequest request) {
        boolean accepted = dispatcher.accept(this, request);
        if (accepted) {
            available = false;
        }
        return accepted;
    }

    /**
     * Give an accepted request back to the dispatcher and become available again.
     *
     * @param request the request to give up
     * @throws IllegalStateException if this driver is not assigned to the request
     */
    public void abandon(DeliveryRequest request) {
        dispatcher.abandon(this, request);
        available = true;
    }

    /**
     * Report that the request was delivered and become available again.
     *
     * @param request the request that was delivered
     * @throws IllegalStateException if this driver is not assigned to the request
     */
    public void complete(DeliveryRequest request) {
        dispatcher.complete(this, request);
        available = true;
    }

}
