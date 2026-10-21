/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: RequestStatus.java
 * Description: The states a delivery request goes through.
 */

package edu.bu.met.cs665.delivery;

/**
 * Life cycle of a DeliveryRequest: OPEN when submitted, ASSIGNED when one driver accepted it,
 * COMPLETED when that driver delivered it. Abandoning moves it back from ASSIGNED to OPEN.
 * Only DeliveryDispatcher changes the status.
 */
public enum RequestStatus {
    /** Submitted and waiting for a driver. */
    OPEN,
    /** Accepted by exactly one driver. */
    ASSIGNED,
    /** Delivered by the assigned driver; final state. */
    COMPLETED,
}
