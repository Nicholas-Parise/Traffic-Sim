package Vehicles;

import java.awt.*;

public class Truck extends Vehicle {

  int maxLoad;
  int currentLoad;
  int wheels;

  public Truck(int position, int currentLane, int currentRoad, int destinationId, Color colour) {
    this.position = position;
    this.currentRoadId = currentRoad;
    this.destinationID = destinationId;
    this.currentLane = currentLane;
    this.colour = colour;
    signal = Signals.NONE;
    health = 100;
    reputation = 75;
    velocity = 10;
    topSpeed = 10;
    size = 3;
    acceleration = 3;
    inIntersection = false;
    money = 5000;
    price = (int)(Math.random()*8000);

    wheels = 18;
    maxLoad = 10000;
    currentLoad = 0;
  }

  public int getMaxLoad() {
    return maxLoad;
  }

  public int getCurrentLoad() {
    return currentLoad;
  }

  public void setCurrentLoad(int currentLoad) {
    this.currentLoad = currentLoad;
  }

  public int getWheels() {
    return wheels;
  }
  public void setWheels(int wheels) {
    this.wheels = wheels;
  }

}