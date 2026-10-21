/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: Subscriber.java
 * Description: Observer role of the Observer pattern.
 */

package edu.bu.met.cs665.notification;

/**
 * A subscriber receives events of type T from a Publisher. Driver implements it with T = DeliveryRequest.
 * The publisher only knows this interface, never the concrete class.
 *
 * @param <T> the type of event this subscriber receives
 */
public interface Subscriber<T> {

    /**
     * Called by the publisher for every event. Implements what the subscriber does with it.
     *
     * @param event the event that happened
     */
    void update(T event);

    /**
     * Whether this subscriber currently wants events.
     *
     * @return true if events should be delivered to this subscriber now. The publisher skips subscribers that return false.
     */
    boolean isAvailable();

    /**
     * Human-readable name, used in log messages.
     *
     * @return the name of this subscriber
     */
    String getName();

}
