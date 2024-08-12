package Game;

import Player.Player;
import Server.ConnectionState;
import Server.ThreadedServer;
import Vehicles.*;
import Map.*;

import java.util.ArrayList;
import java.util.Queue;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Nicholas Parise
// this class controls and brings all the other classes together
public class Engine {

  private int time;

  private Map map;
  private VehicleController controller;
  private LocationManager locations;
  private RepairShop repairShop;
  private Vehicle[] vehicles;

  ArrayList<Player> players;

  ThreadPool threadPool;

  public Engine() {

    vehicles = new Vehicle[25];
    players = new ArrayList<Player>();
    map = new Map();

    try {
      map.loadMapXML("map.xml");
    }catch (Exception e){}

    repairShop = new RepairShop();

    locations = new LocationManager(map.getFinalRoad(), map.getFinalIntersection());

    controller = new VehicleController(map, locations);

    generateVehicles();

    new Thread(new ThreadedServer(players,controller,map)).start();

    //create db and populate
    //Database.createNewDatabase();
    //Database.createNewTable();
    //Database.insert("admin", "password");

    update();
  }


  /**
   * update entities
   */
  public void update() {

    while (true){

      clock();

      RunThreadPool();

      playerComs();

      if(time%5 == 0)
        map.lightUpdate();

      addToRepairShop();
      if(time%50 == 0)
        heal();
    }
  }


  /**
   * This method handles communication between the game thread and the socket threads
   * through their shared asset a pointer to the player object
   */
  private void playerComs() {

    boolean promptBlock = true;

    if(players.size()>0){ // if players are connected to the server

      if(time%3 == 0){  // we allow prompting every 3 time steps
        for (Player p: players) {
          p.canPrompt(true);
        }
      }

      // blocks until all plays answer their prompt.

      while(promptBlock) {
        promptBlock = false;

        for (Player p : players) {

          // if the user disconnected while waiting on input continue as this user is no longer exists
          if(!p.getConnected()){
            continue;
          }

          if(p.getCanPrompt()){
            promptBlock = true;
          }
        }
      }
    }
  }


  /**
   * creates instance of thread pool
   * then adds
   */
  void RunThreadPool(){
    threadPool = new ThreadPool(6);

    threadPool.runTask(accelerateAll());
    threadPool.runTask(moveAll());
    threadPool.runTask(checkAtInter());
    threadPool.runTask(intersectionControl());
    threadPool.runTask(changeLanes());
    threadPool.runTask(collisions());

    threadPool.join();
    System.out.println("All threads completed.");
  }


  /**
   * add damaged vehicles to repair shop
   */
  private void addToRepairShop(){
    for (Vehicle v:vehicles) {
      if(v.getHealth()<50){
        repairShop.add(v);
      }
    }
  }


  /**
   * heal all the vehicles
   */
  private void heal() {

    for (Player p : players) {
      p.setExtraMessage("the repair shop is finally open for business, they have made $"+repairShop.calculateCost()+" from fixing cars\t"+ ConnectionState.PUSH.getValue()+"\n");
    }
    repairShop.fixDamage();
    repairShop.removeAll();
  }


  /**
   * tests the collision of the bots
   */
  private Runnable collisions() {

    Runnable runnable = () -> {
      for (int i = 0; i < vehicles.length; i++) {
        for (int j = 0; j < vehicles.length; j++) {

          Vehicle v = vehicles[i];
          Vehicle v2 = vehicles[j];

          if (v != v2 && v.collides(v2)) {
            //System.out.print("Vehicle: " + i + " collided with vehicle: " + j);

            if (v.getReputation() > v2.getReputation()) {
              v.setReputation(v.getReputation() - 5);
              v.doDamage(20);
              controller.newRandomLocation(v);
              //  System.out.println(". Vehicle " + i + " lost reputation");
            } else {
              v2.setReputation(v2.getReputation() - 5);
              controller.newRandomLocation(v2);
              v2.doDamage(20);
              // System.out.println(". Vehicle " + j + " lost reputation");
            }
          }
        }
      }


      for (Player p : players) {
        // handles collision with the player and other vehicles, since the player is not in the array
        Vehicle collider = controller.roadCollision(p.getVehicle());
        if (collider != null) {
          p.setExtraMessage("you got hit!\t"+ ConnectionState.PUSH.getValue()+"\n");
          controller.newRandomLocation(collider);
          collider.setReputation(collider.getReputation() - 5);
        }
      }
    };
    return runnable;
  }





  /**
   * move cars through an intersection,
   * moves one through an intersection each time it's ran.
   */
  private Runnable intersectionControl(){
    Runnable runnable = () -> {
      for (int i = 0; i <= map.getFinalIntersection(); i++) {

        Queue<Vehicle> vehicleQueue = locations.getQueueAt(i); // make copy of queue

        if (vehicleQueue.isEmpty()) {
          continue;
        }

        Vehicle v = vehicleQueue.peek();

        Direction direction = map.getRandomDirection(v.getDestination());

        if (!map.getDirectionRed(v.getDestination(), direction)) {

          locations.deQueue(i); // if can move, remove element from real queue

          locations.removeLocation(v.getRoad(), v);
          controller.chosenDirection(v, direction);
          locations.addLocation(v.getRoad(), v);
        }
      }
    };
    return runnable;
  }




  /**
   * this method checks if the vehicle is at an intersection
   * if it is it adds the vehicle to the intersection queue
   * then it changes the bool to prevent it from moving more.
   */
  private Runnable checkAtInter(){
    Runnable runnable = () -> {
      for (Vehicle v : vehicles) {

        int dest = v.getDestination();

        if (controller.atIntersection(v) && !v.isInIntersection()) {
          v.setInIntersection(true);

          locations.addToQueue(dest, v);
        }
      }

      for (Player p : players) {
        if (controller.atIntersection(p.getVehicle())) {
          p.getVehicle().setInIntersection(true);
        }
      }
    };
    return runnable;
  }


  /**
   * advance clock
   */
  private void clock() {
    time++;
  }



  /**
   * this method will go through all the bots and change their lanes
   * if its open and they win the gamble.
   */
  private Runnable changeLanes() {

    Runnable runnable = () -> {
      for (int i = 0; i < vehicles.length - 1; i++) {
        if (!vehicles[i].isInIntersection()) {

          int currentLane = vehicles[i].getCurrentLane();
          int currentRoad = vehicles[i].getRoad();

          boolean filledLane = controller.closestVehicle(vehicles[i]) < 10; // if there is a car in front of them
          boolean filledLeftLane = controller.roadClearAt(vehicles[i].getPosition(), currentRoad, currentLane - 1);
          boolean filledRightLane = controller.roadClearAt(vehicles[i].getPosition(), currentRoad, currentLane + 1);
          boolean leftLaneExists = map.laneExists(currentLane-1,currentRoad);
          boolean rightLaneExists = map.laneExists(currentLane+1,currentRoad);

          int probability = (int) (Math.random() * 100);

          if (filledLane) {
            probability -= 10; // should change lanes so higher odds of moving lanes
          } else {
            probability += 10;
          }


          if (leftLaneExists) {
            // should not change into the left lane but could
            if (filledLeftLane) {

              // left exists. but shouldn't change lanes because crash
              // gamble. With larger reputations it makes it harder to lose
              if (probability > vehicles[i].getReputation()) {
                vehicles[i].setLane(currentLane - 1);
                continue;
              }

            } else {

              // left exists. gamble. with larger reputations it makes it easier to win.
              if (probability < vehicles[i].getReputation()) {
                vehicles[i].setLane(currentLane - 1);
                continue;
              }

            }
          }

          if (rightLaneExists) {
            // should not change into the right lane
            if (filledRightLane) {

              // right exists. but shouldn't change lanes because crash
              // gamble. with larger reputations it makes it harder to lose
              if (probability > vehicles[i].getReputation()) {
                vehicles[i].setLane(currentLane + 1);
                continue;
              }

            } else {

              // right exists. gamble. with larger reputations it makes it easier to win.
              if (probability < vehicles[i].getReputation()) {
                vehicles[i].setLane(currentLane + 1);
                continue;
              }

            }
          }

        }
      }
    };
    return runnable;
  }


  /**
   * accelerate and decelerate all the vehicles
   */
  private Runnable accelerateAll() {

    Runnable runnable = () -> {

      for (Vehicle v: vehicles) {
        if(!v.isInIntersection()) {
          if(controller.closestVehicle(v) > 15){
            v.accelerate();
          }else{
            v.decelerate();
          }
        }
      }

      for (Player p: players) {
        if (!p.getVehicle().isInIntersection()) {
          p.accelerate();
        }
      }
    };

    return runnable;
  }



  /**
   * move all the vehicles when not in an intersection
   */
  private Runnable moveAll(){

    Runnable runnable = () -> {
      for (int i = 0; i < vehicles.length - 1; i++) {
        if (!vehicles[i].isInIntersection()) {
          vehicles[i].move();
        }
      }
      for (Player p : players) {
        if (!p.getVehicle().isInIntersection()) {
          p.move();
        }
      }
    };
    return runnable;
  }



  /**
   * generate all vehicles and make sure they are not placed on top of each other.
   */
  private void generateVehicles(){

    int len = vehicles.length/3;
    int startingPoint, dest, road, lane, pos;

    boolean temp = true;

    // car
    for (int i = 0; i < vehicles.length; i++) {
      temp = true;
      do{
        startingPoint = map.randStart();
        dest = map.decideDirection(startingPoint);
        road = map.getNewRoad(startingPoint, dest);
        pos = map.randPos(road);
        lane = map.randLane(road);


        if (i < len) {
          vehicles[i] = new VehicleFactory().setPosition(pos).setDestination(dest).setLane(lane).setRoad(road).setColour(Vehicle.randcolour()).build("Car");
        } else if (i < len * 2 && i >= len) {
          vehicles[i] = new VehicleFactory().setPosition(pos).setDestination(dest).setLane(lane).setRoad(road).setColour(Vehicle.randcolour()).build("Bus");
        } else {
          vehicles[i] = new VehicleFactory().setPosition(pos).setDestination(dest).setLane(lane).setRoad(road).setColour(Vehicle.randcolour()).build("Truck");
        }

        // test if we can break out of loop
        temp = false;
        for (int j = 0; j < i; j++) {
          if (vehicles[i].collides(vehicles[j])) {
            temp = true;
          }
        }

      }while(temp);

        locations.addLocation(road,vehicles[i]);
    }
  }




}