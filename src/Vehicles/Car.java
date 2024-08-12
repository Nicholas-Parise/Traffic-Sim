package Vehicles;

import java.awt.*;

public class Car extends Vehicle {

  int doors;
  String manufacturer;

  public Car(int position, int currentLane, int currentRoad, int destinationId , Color colour) {
    this.position = position;
    this.currentRoadId = currentRoad;
    this.destinationID = destinationId;
    this.currentLane = currentLane;
    this.colour = colour;
    signal = Signals.NONE;
    health = 100;
    reputation = 75;
    velocity = 12;
    topSpeed = 12;
    size = 2;
    acceleration = 4;
    inIntersection = false;
    money = 10000;
    price = (int)(Math.random()*10000);

    doors = 2;
    manufacturer = "lamborghini";
  }

  public void setDoors(int doors) {
    this.doors = doors;
  }

  public int getDoors() {
    return doors;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }



}