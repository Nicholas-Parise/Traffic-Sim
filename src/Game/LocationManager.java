package Game;

import Vehicles.Vehicle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LocationManager {

    ArrayList<ArrayList<Vehicle>> locations;
    ArrayList<Queue<Vehicle>> intersectionQueues;

    public LocationManager(int roadSize, int intersectionSize){

        locations = new ArrayList<ArrayList<Vehicle>>(roadSize+1);
        intersectionQueues = new ArrayList<Queue<Vehicle>>(intersectionSize+1);

        for(int i = 0; i <= roadSize; i++){
            locations.add(i,new ArrayList<Vehicle>());
        }

        for(int i = 0; i <= intersectionSize; i++){
            intersectionQueues.add(i,new LinkedList<Vehicle>());
        }

    }


    /**
     * add the vehicle index to a linked list centered around the road index
     * @param road index
     * @param vehicle vehicle
     */
    public synchronized void addLocation(int road, Vehicle vehicle){
        ArrayList<Vehicle> temp = locations.get(road);
        temp.add(vehicle);
        locations.set(road,temp);
    }

    /**
     * remove the vehicle index from the road.
     * @param road index
     * @param vehicle vehicle
     */
    public synchronized void removeLocation(int road, Vehicle vehicle){
         ArrayList<Vehicle> temp = locations.get(road);
        temp.remove(vehicle);
        locations.set(road,temp);
    }

    /**
     * this method is used to retrieve all the vehicles on a road.
     * @param road index
     * @return the linked list containing all the cars on this road
     */
    public synchronized ArrayList<Vehicle> getLocationsAt(int road) {
        return locations.get(road);
    }

    /**
     * this method is used to retrieve all the vehicles in a given intersection
     * @param intersection index
     * @return the queue containing all the cars int this intersection
     */
    public synchronized Queue<Vehicle> getQueueAt(int intersection) {
        return intersectionQueues.get(intersection);
    }

    /**
     * add the vehicle to the queue
     * @param intersection index
     * @param vehicle vehicle
     */
    public synchronized void addToQueue(int intersection, Vehicle vehicle){
        Queue<Vehicle> temp = intersectionQueues.get(intersection);
        temp.add(vehicle);
        intersectionQueues.set(intersection,temp);
    }


    /**
     * remove element from queue at intersection
     * @param intersection index
     * @return vehicle
     */
    public synchronized Vehicle deQueue(int intersection) {
        return intersectionQueues.get(intersection).poll();
    }


}
