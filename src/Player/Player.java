package Player;

import Vehicles.*;
import Game.VehicleController;
import Map.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

// Nicholas parise
// handles player communications
public class Player {

  public Vehicle vehicle ;
  private VehicleController controller;
  private Map map;
  private Prompts prompts;

  private String extraMessage;

  private boolean canPrompt;
  private boolean connected;

  public Player(VehicleController controller, Map map) {
    this.controller = controller;
    this.map = map;
    prompts = new Prompts(map,controller);
    canPrompt = false;
    connected = true;
    extraMessage = "";
    makePlayer();
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public synchronized void canPrompt(boolean canPrompt){
    this.canPrompt = canPrompt;
  }

  public synchronized boolean getCanPrompt(){return canPrompt;}

  public synchronized void setConnected(boolean connected){
    this.connected = connected;
  }

  public synchronized boolean getConnected(){return connected;}

  public synchronized void setExtraMessage(String extraMessage){
    this.extraMessage += extraMessage;
  }

  public synchronized String getExtraMessage(){return extraMessage;}

  public synchronized void resetExtraMessage(){extraMessage = "";}


  /**
   * sets up the player vehicle to have random position.
   */
  private void makePlayer(){

    int startingPoint = map.randStart();
    int dest = map.decideDirection(startingPoint);
    int road = map.getNewRoad(startingPoint,dest);
    int pos = map.randPos(road);
    int lane = map.randLane(road);

    vehicle = (new VehicleFactory().
            setPosition(pos).
            setDestination(dest).
            setLane(lane).
            setRoad(road).
            setColour(Vehicle.randcolour()).
            build("Car"));
  }


  /**
   * given the socket, this method handles the control logic of the player
   *
   * @param out1 PrintWriter, socket out
   * @param in1 BufferedReader, socket in
   */
  public void prompt(PrintWriter out1,BufferedReader in1) {

    // check lock canPrompt
    // if its false do nothing and return
    if(!getCanPrompt()) {
      return;
    }

    String input = "";

    // if a player is at an intersection
    if(controller.atIntersection(vehicle)){

      changeDirection(out1,in1);

    }else{
    // if the car is just in the middle of a road instead:

      do{
        // print the options available to the player
        out1.println(Prompts.middlePrompt());

        try {
          input = in1.readLine();
        }catch (IOException e){}

        if (input.toUpperCase().contains("L")) {

          out1.println(prompts.surroundings(vehicle));

        } else if (input.toUpperCase().contains("C")) {

          ChangeLanes(out1,in1);
        }

      }while(!input.toUpperCase().contains("S"));
    }

    // give back lock after finishing prompt session with user
    canPrompt(false);
  }


  /**
   * Handles the logic for changing directions and getting IO to the player
   *
   * @param out1 PrintWriter, socket out
   * @param in1 BufferedReader, socket in
   */
  private void changeDirection(PrintWriter out1,BufferedReader in1){

    String input = "";

    // print all of the available directions
    out1.println(prompts.printDirections(vehicle));

    try {
      input = in1.readLine();
    }catch (IOException e){}

    // if input is garbage, break out and do it again
    if(input.length() <1){
      return;
    }

    // convert string input into the Direction Enum
    Direction dir = Map.convertDirection(input.charAt(0));

    // if user decides to wait instead
    if(dir == Direction.NONE){
      out1.println(Prompts.waitInstead());
      vehicle.setReputation(vehicle.getReputation()+5);
      return;
    }

    int lightState = redPrompter(dir,vehicle);

    switch (lightState){
      case -1:
        out1.println(Prompts.doesntExist()); // direction doesn't exist
        break;
      case 1:

        out1.println(Prompts.confirm());
        // the light is red in this direciton, get confirmation

        try {
          input = in1.readLine();
        }catch (IOException e){}

        if(input.equals("Y")){
          // light is read but user decided to say yes anyway

          gamble(out1);
        }else{
          // if user decides to wait instead and not gamble
          out1.println(Prompts.waitInstead());
          break;
        }

      // if everything passed to here or the case is 0 which is when it is safe to go anyway
        // change the direction
      case 0:
        out1.println(Prompts.directionSuccess());
        controller.chosenDirection(vehicle,dir);
    }
  }



  /**
   * Handles the logic for changing lanes and getting IO to the player
   *
   * @param out1 PrintWriter, socket out
   * @param in1 BufferedReader, socket in
   */
  private void ChangeLanes(PrintWriter out1,BufferedReader in1){

    String input = "";

    // print out available lanes for user to switch to
    out1.println(prompts.printLanes(vehicle));

    try {
      input = in1.readLine();
    }catch (IOException e){}

    // if input is garbage, break out and do it again
    if(input.length() <1){
      return;
    }

    Direction dir = Map.convertDirection(input.charAt(0));
    int currentLane = vehicle.getCurrentLane();
    // figure out which direction the user entered

    if(dir == Direction.LEFT){
      currentLane --;
    }else{
      currentLane ++;
    }

    // test if the lane even exists.
    // This just makes sure the user isn't stupid and didn't pick a place that doesn't exist
    if(map.laneExists(currentLane,vehicle.getRoad())){

      // tests if the lane has a vehicle in it
      if(!controller.roadClearAt(vehicle.getPosition(), vehicle.getRoad(), currentLane)){

        // ask player to confirm as they could hit something
        out1.println(Prompts.laneConfirm(dir));
        try {
          input = in1.readLine();
        }catch (IOException e){}

        if(input.equals("Y")){
          // if they choose to go anyway, gamble and continue to change the lane
          gamble(out1);
        }else{
          // if not return
          return;
        }
      }

      // send success message and actually change the lane
      out1.println(Prompts.laneSuccess());
      vehicle.setLane(currentLane);
      return;

    }else{
      // tell the user the direction doesn't exist
      out1.println(Prompts.noDir(dir));
    }
  }


  /**
   * handle I/O for gambling
   * @param out1
   */
  private void gamble(PrintWriter out1){
    if(vehicle.gamble()){
      out1.println(Prompts.gambleWon());
    }else{
      out1.println(Prompts.gambleLost());
    }
  }



  /**
   * this method returns the state of the light in an intersection
   *
   * @param direction the direction of the intersection
   * @param v the vehicle
   * @return 1 if red light, 0 can go, -1 direction doesn't exist
   */
  private int redPrompter(Direction direction,Vehicle v) {

    ArrayList<Direction> directions = map.getDirections(v.getDestination());

    if (directions.contains(direction)) {

      if (map.getDirectionRed(v.getDestination(), direction)) {
        return 1;
        // red light
      }
      // can go
      return 0;

    } else {
      // doesn't exist
      return -1;
    }
  }


  /**
   * move the player
   */
  public void move(){
    vehicle.move();
  }

  /**
   * accelerate the player
   */
  public void accelerate(){
    vehicle.accelerate();
  }




}