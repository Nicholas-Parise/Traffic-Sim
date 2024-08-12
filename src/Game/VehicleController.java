package Game;

import Vehicles.*;
import Map.*;

import java.util.ArrayList;

public class VehicleController {

  Map map;
  LocationManager locations;

  public VehicleController(Map map, LocationManager locations) {
    this.map = map;
    this.locations = locations;
  }


  /**
   * determines if vehicle is at an intersection
   * @param vehicle
   */
  public boolean atIntersection(Vehicle vehicle) {

    int length = map.roads[vehicle.getRoad()].length;

    if(vehicle.getPosition() >= length){
      return true;
    }
    return false;
  }





  /**
   * determines if the vehicle can move through the intersection,
   * if its red, green or a stop sign
   * @return true if can, false if not.
   */
  public boolean canMoveThrough(Vehicle vehicle, int newDestination){
  // get current intersection.
    int current = vehicle.getDestination();

    Intersection intersection = map.intersections[current];

    while(intersection != null){

      if(intersection.destinationId == newDestination){

        if(intersection.getTrafficLight() == TrafficLight.RED){
          return false;
        }else{
          return true;
        }
      }
      intersection = intersection.next;
    }

    // shouldn't reach this state but default to false just in case.
    return false;
  }




  /**
   * Determines if a lane is clear at position
   * @param position
   * @param road index
   * @param lane
   * @return true if lane is clear
   */
  public boolean roadClearAt(int position, int road, int lane){

    ArrayList<Vehicle> vehicleAr = locations.getLocationsAt(road);
    Vehicle v;

    // made to be thread safe, if index no longer exists
    for (int i = 0; i < vehicleAr.size(); i++) {

      try {
        v = vehicleAr.get(i);
      }catch (IndexOutOfBoundsException e){
        break;
      }

      if(v == null){
        break;
      }

      if(v.getCurrentLane() == lane && Math.abs((position - v.getPosition()))< 5){
        return false;
      }
    }
    return true;
  }


  /**
   * This method gets the closest vehicle in front of the given vehicle
   * @param vehicle current vehicle
   * @return range of 1 -> int.MAX value where number is distance to the closest vehicle
   */
  public int closestVehicle(Vehicle vehicle){

    int distance = Integer.MAX_VALUE;
    ArrayList<Vehicle> vehicleAr = locations.getLocationsAt(vehicle.getRoad());
    Vehicle v;

    // made to be thread safe, if index no longer exists
    for (int i = 0; i < vehicleAr.size(); i++) {

      try {
        v = vehicleAr.get(i);
      }catch (IndexOutOfBoundsException e){
        break;
      }

      if(v == null){
        break;
      }

      int tempPos = v.getPosition() - vehicle.getPosition();
      if(v.getCurrentLane() == vehicle.getCurrentLane() // same lane
              && v != vehicle // this makes sure the vehicle isn't the same one
              && vehicle.getPosition() < v.getPosition() // if v is infront of vehicle
              && tempPos < distance){  // smaller value of temp
        distance = tempPos;
      }
    }

    return distance;
  }


  /**
   * sets the vehicle with a new location
   * @param v vehicle
   */
  public void newRandomLocation(Vehicle v){

    locations.removeLocation(v.getRoad(),v);

    int startingPoint = 0;
    int dest = 0;
    int road = 0;
    int lane = 0;

    boolean temp = true;

    while(temp) {
      startingPoint = map.randStart();
      dest = map.decideDirection(startingPoint);
      road = map.getNewRoad(startingPoint, dest);
      lane = map.randLane(road);

      v.setDestination(dest);
      v.setRoad(road);
      v.setPosition(map.randPos(road));
      v.setLane(lane);
      v.setVelocity(5);
      v.setInIntersection(false);

      temp = false;

      // test if we can break out of loop
      // made to be thread safe, if index no longer exists
        ArrayList<Vehicle> vehicleAr = locations.getLocationsAt(road);
        Vehicle vehicle;

        for (int i = 0; i < vehicleAr.size(); i++) {

          try {
            vehicle = vehicleAr.get(i);
          }catch (IndexOutOfBoundsException e){
            break;
          }

          if(vehicle == null){
            break;
          }

          if (v.collides(vehicle) && v != vehicle) {
          temp = true;
          break;
        }
      }
    }

    locations.addLocation(road,v);
  }



  /**
   * This method moves the veicle to the new direction in the current intersection
   * @param vehicle
   * @param direction
   */
  public void chosenDirection(Vehicle vehicle, Direction direction){

    Intersection i = map.intersections[vehicle.getDestination()];
    int newDest = 0;
    int newRoad = 0;

    while (i != null) {

      if(i.getDirection() == direction){
        newDest = i.getDestinationId();
        newRoad = i.getRoad();
      }
      i = i.next;
    }

    vehicle.setDestination(newDest);
    vehicle.setRoad(newRoad);
    vehicle.setPosition(0);
    vehicle.setVelocity(5);
    vehicle.setLane(0);
    vehicle.setInIntersection(false);
  }


  /**
   * determines if a vehicle collides with another in a road
   * @param vehicle vehicle
   * @return the vehicle that hit the other
   */
  public Vehicle roadCollision(Vehicle vehicle){

    ArrayList<Vehicle> vehicleAr = locations.getLocationsAt(vehicle.getRoad());
    Vehicle v;

    // made to be thread safe, if index no longer exists
    for (int i = 0; i < vehicleAr.size(); i++) {

      try {
        v = vehicleAr.get(i);
      }catch (IndexOutOfBoundsException e){
        break;
      }

      if(v == null){
        break;
      }


      if(v!=vehicle && v.collides(vehicle)){  // same lane
        return v;
      }
    }
    return null;
  }



}