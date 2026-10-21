/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: Shop.java
 * Description: A retailer that turns customer orders into delivery requests.
 */

package edu.bu.met.cs665.participant;

import edu.bu.met.cs665.delivery.DeliveryDispatcher;
import edu.bu.met.cs665.delivery.DeliveryRequest;

/**
 * A shop does not know any driver.
 * When a customer orders something, the shop creates a DeliveryRequest and hands it to the dispatcher, which broadcasts it to the drivers.
 */
public class Shop {
    private final String name;
    private final DeliveryDispatcher dispatcher;

    /**
     * Create a shop that sends its requests to the given dispatcher.
     *
     * @param name the shop's name, shown on every request it creates
     * @param dispatcher the dispatch center all shops share
     * @throws IllegalArgumentException if any argument is null
     */
    public Shop(String name, DeliveryDispatcher dispatcher) {
        if ((name == null) || (dispatcher == null)) {
            throw new IllegalArgumentException("Name and dispatcher are required.");
        }
        this.name = name;
        this.dispatcher = dispatcher;
    }

    /**
     * A customer ordered a product.
     * Create the delivery request and submit it, which gets broadcast to all available drivers.
     * The request is returned so the caller can follow its status.
     *
     * @param product what has to be delivered
     * @param destination the customer's address
     * @return the request that was broadcast
     */
    public DeliveryRequest receiveOrder(String product, String destination) {
        DeliveryRequest request = new DeliveryRequest(name, product, destination);
        dispatcher.submit(request);
        return request;
    }

    /**
     * Get the shop's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

}
