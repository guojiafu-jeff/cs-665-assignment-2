/**
 * Name: JIAFU GUO
 * Course: CS-665 Software Designs & Patterns
 * Date: 10/20/2026
 * File Name: Main.java
 * Description: Console demo of the delivery notification system (the real demonstration is in the JUnit tests).
 */

package edu.bu.met.cs665;

import edu.bu.met.cs665.delivery.DeliveryDispatcher;
import edu.bu.met.cs665.delivery.DeliveryRequest;
import edu.bu.met.cs665.participant.Driver;
import edu.bu.met.cs665.participant.Shop;
import edu.bu.met.cs665.participant.VehicleType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * One shop, five drivers, one request: broadcast, accept, abandon, re-broadcast, complete.
 */
public class Main {
  private static final Logger LOGGER = LogManager.getLogger(Main.class);

  /**
   * Run the demo.
   *
   * @param args not used
   */
  public static void main(String[] args) {
    DeliveryDispatcher dispatcher = new DeliveryDispatcher();
    Shop shop = new Shop("Corner Shop", dispatcher);
    Driver a = new Driver("A", VehicleType.VAN, dispatcher);
    Driver b = new Driver("B", VehicleType.TAXI, dispatcher);
    new Driver("C", VehicleType.SCOOTER, dispatcher);
    new Driver("D", VehicleType.VAN, dispatcher);
    new Driver("E", VehicleType.SCOOTER, dispatcher);

    DeliveryRequest request = shop.receiveOrder("2 kg apples", "12 Main St");
    LOGGER.info("B received " + b.getReceivedRequests().size() + " request(s)");

    a.accept(request);
    b.accept(request);          // too late, logged as "could not accept"
    a.abandon(request);         // broadcast again
    b.accept(request);
    b.complete(request);
    LOGGER.info("Final state: " + request);
  }
}
