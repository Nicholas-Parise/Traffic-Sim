package Vehicles;

import java.awt.*;
import java.io.Serializable;

public abstract class Vehicle implements Serializable,Mechanic{

  int velocity;
  int topSpeed;
  int health;
  int reputation;
  int position;
  int currentLane;
  int currentRoadId;
  int destinationID;
  int size;
  int acceleration;
  int price;
  int money;

  Color colour;

  Signals signal;

  boolean inIntersection;


  /**
   * fix famage to vehicle
   */
  public void fixDamage(){
    health = 100;
    money -= calculateCost();
  }

  /**
   * calculate cost of repair
   * @return cost
   */
  public int calculateCost(){
    return price/10;
  }


  /**
   * updates the vehicles position relative to the velocity
   */
  public void move() {
    position += velocity;
  }

  /**
   * accelerates the vehicle and keeps it within range
   */
  public void accelerate(){

    if(velocity < topSpeed){
      velocity += acceleration;
    }else{
      velocity = topSpeed;
    }
  }

  /**
   * decelerates the vehicle and keeps it within range
   */
  public void decelerate(){

    if(velocity < 0){
      velocity -= acceleration;
    }else{
      velocity = 1;
    }
  }



  /**
   *
   * @return true if in an intersection
   */
  public boolean isInIntersection() {
    return inIntersection;
  }

  /**
   * sets the vehicles intersection
   * @param inIntersection
   */
  public void setInIntersection(boolean inIntersection) {
    this.inIntersection = inIntersection;
  }

  /**
   *
   * @param vehicle second vehicle
   * @return true if colliding
   */
  public boolean collides(Vehicle vehicle) {

    if(!inIntersection && !vehicle.isInIntersection() &&
            currentLane == vehicle.getCurrentLane() &&
            currentRoadId == vehicle.getRoad() &&
            position < vehicle.getPosition() + vehicle.getSize() &&
            position + size > vehicle.getPosition()){
    // Math.abs((position + size/2) - (vehicle.getPosition() + vehicle.getSize()/2)) < 3){
      return true;
    }



    return false;
  }


  public int getSize() {
    return size;
  }

  /**
   *
   * @return current position
   */
  public int getPosition(){
      return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  /**
   *
   * @return current speed
   */
  public int getSpeed() {
  return velocity;
  }

  /**
   *
   * @return health
   */
  public int getHealth() {
  return health;
  }

  /**
   * do damage to vehicle
   * @param damage amount of damage
   */
  public void doDamage(int damage) {
    health = health - damage;
  }

  /**
   *
   * @return current lane
   */
  public int getCurrentLane() {
  return currentLane;
  }

  /**
   * set the lane of the vehicle
   * @param lane set lane
   */
  public void setLane(int lane) {
    currentLane = lane;
  }

  /**
   * set destination intersection index
   * @param destination index number
   */
  public void setDestination(int destination) {
    destinationID = destination;
  }

  /**
   * get destination intersection index
   * @return index number
   */
  public int getDestination() {
  return destinationID;
  }

  /**
   * set reputation
   * @param reputation value
   */
  public void setReputation(int reputation) {
    this.reputation = reputation;

    if(this.reputation > 100){
      this.reputation = 100;
    }
    if(this.reputation < 0){
      this.reputation = 0;
    }
  }

  /**
   * get reputation
   * @return reputation
   */
  public int getReputation() {
  return reputation;
  }

  /**
   * get road index
   * @param road index number
   */
  public void setRoad(Integer road) {
    currentRoadId = road;
  }

  /**
   * get road index
   * @return index number
   */
  public int getRoad() {
  return currentRoadId;
  }

  /**
   *
   * @return vehicle colour
   */
  public Color getColour() {
  return colour;
  }

  /**
   *
   * @return current turn signal
   */
  public Signals getSignal() {
  return signal;
  }

  /**
   * set turn signal
   * @param signal
   */
  public void setSignal(Signals signal) {
    this.signal = signal;
  }

  public void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  @Override
  public String toString() {
    return "Vehicle{" +
            "velocity=" + velocity +
            ", position=" + position +
            ", currentLane=" + currentLane +
            ", currentRoadId=" + currentRoadId +
            ", destinationID=" + destinationID +
            '}';
  }


  /**
   * roll the dice on action
   * @return true if won gamble
   */
  public boolean gamble() {

    int probability = (int)(Math.random()*100);

    if(probability > reputation){
      doDamage(10);
      reputation -=5;
      return false;
    }else{
      reputation +=5;
      return true;
    }
  }


  /**
   * returns a random colour of a vehicle
   * @return a awt color
   */
  public static Color randcolour(){
    return new Color((int)(Math.random() * 0x1000000));
  }


}