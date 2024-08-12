package Vehicles;

import java.awt.*;

public class VehicleFactory {

    private int destination;
    private int road;
    private int position;
    private int lane;
    private Color colour;

    public VehicleFactory(){

        destination = 0;
        road = 0;
        position = 0;
        lane = 0;
        colour = Color.black;
    }

    /**
     * set position
     * @param position
     * @return VehicleFactory
     */
    public VehicleFactory setPosition(int position){
        this.position = position;
        return this;
    }

    /**
     * set lane
     * @param lane
     * @return VehicleFactory
     */
    public VehicleFactory setLane(int lane){
        this.lane = lane;
        return this;
    }

    /**
     * set road
     * @param road
     * @return VehicleFactory
     */
    public VehicleFactory setRoad(int road){
        this.road = road;
        return this;
    }

    /**
     * set destination
     * @param destination
     * @return VehicleFactory
     */
    public VehicleFactory setDestination(int destination){
        this.destination = destination;
        return this;
    }

    /**
     * set colour
     * @param colour
     * @return VehicleFactory
     */
    public VehicleFactory setColour(Color colour){
        this.colour = colour;
        return this;
    }

    /**
     * create the type of vehicle
     * @param type what kind of vehicle
     * @return finished Vehicle
     */
    public Vehicle build(String type){
        if(type.equalsIgnoreCase("Car")){
            return new Car(position, lane, road, destination, colour);
        }else if (type.equalsIgnoreCase("Bus")){
            return new Bus(position, lane, road, destination, colour);
        }else if(type.equalsIgnoreCase("Truck")){
            return new Truck(position, lane, road, destination, colour);
        }
        return null;
    }



}
