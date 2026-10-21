/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: VehicleType.java
 * Description: The kinds of vehicle a driver delivers with.
 */

package edu.bu.met.cs665.participant;

/**
 * The vehicle a driver uses. It is only a property of a Driver: all drivers behave the same way,
 * so a new vehicle type is one new constant here and nothing else changes.
 */
public enum VehicleType {
    VAN,
    TAXI,
    SCOOTER,
}
