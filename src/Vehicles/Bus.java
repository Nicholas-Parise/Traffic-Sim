package Vehicles;

import java.awt.*;

public class Bus extends Vehicle {

  int passengers;

  public Bus(int position, int currentLane, int currentRoad, int destinationId, Color colour) {
    this.position = position;
    this.currentRoadId = currentRoad;
    this.destinationID = destinationId;
    this.currentLane = currentLane;
    this.colour = colour;
    signal = Signals.NONE;
    health = 100;
    reputation = 75;
    velocity = 8;
    topSpeed = 8;
    size = 5;
    acceleration = 3;
    inIntersection = false;
    money = 15000;
    price = (int)(Math.random()*15000);

    passengers = 0;
  }

  public int getPassengers() {
    return passengers;
  }

  public void setPassengers(int passengers) {
    this.passengers = passengers;
  }



}