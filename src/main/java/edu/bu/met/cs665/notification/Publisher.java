/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: Publisher.java
 * Description: Subject role of the Observer pattern.
 */

package edu.bu.met.cs665.notification;

/**
 * A publisher keeps subscribers and pushes events of type T to them.
 * DeliveryDispatcher implements it with T = DeliveryRequest.
 *
 * @param <T> the type of event this publisher sends
 */
public interface Publisher<T> {

    /**
     * Add a subscriber. Adding the same subscriber twice has no effect.
     *
     * @param subscriber who wants to receive events
     */
    void subscribe(Subscriber<T> subscriber);

    /**
     * Remove a subscriber. Removing one that is not subscribed has no effect.
     *
     * @param subscriber who no longer wants events
     */
    void unsubscribe(Subscriber<T> subscriber);

    /**
     * Push one event to every subscriber that is currently available.
     *
     * @param event the event to deliver
     */
    void notifySubscribers(T event);

}
