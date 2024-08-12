package Player;

import Game.VehicleController;
import Map.*;
import Server.ConnectionState;
import Vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Scanner;

// nicholas parise
// this class deals with prompts to the user.
public class Prompts {

    private Map map;
    private VehicleController controller;

    public Prompts(Map map, VehicleController controller){
        this.map = map;
        this.controller = controller;
    }

    /**
     * displays which lanes are available for a vehicle to go to
     * @param v the vehicle
     */
    public String printLanes(Vehicle v){

        int currentRoad = v.getRoad();
        int currentLane = v.getCurrentLane();
        String message = "";

        if(!map.laneExists(currentLane-1,currentRoad)) {
            message+=("There is no left lane\t"+ ConnectionState.PUSH.getValue()+"\n");
        }

        if(!map.laneExists(currentLane+1,currentRoad)) {
            message+=("There is no right lane\t"+ ConnectionState.PUSH.getValue()+"\n");
        }
        message+="Which lane would you like to switch to? (L or R)\t"+ ConnectionState.REPLY.getValue();

        return message;
    }


 /**
 * this method prints the available directions to the screen
 */
 public String printDirections(Vehicle v){

    ArrayList<Direction> directions = map.getDirections(v.getDestination());

    String message = "";

     message+= ("You've reached an intersection, which direction would you like to go?\t"+ ConnectionState.PUSH.getValue()+"\n");

     message+="You can go: ";

    for (Direction d:directions) {

        switch (d){
            case FORWARD:
                message+=d+"(F) ";
                break;
            case RIGHT:
                message+=d+"(R) ";
                break;
            case LEFT:
                message+=d+"(L) ";
        }

        if(map.getDirectionRed(v.getDestination(),d)){
            message+="but the light is red, ";
        }
    }
     message+="Or WAIT(W)\t"+ ConnectionState.REPLY.getValue();

     return message;
}


    /**
     * return the surroundings to the player
     * display only
     */
    public String surroundings(Vehicle v) {

        int currentRoad = v.getRoad();
        int currentLane = v.getCurrentLane();
        int currentPosition = v.getPosition();
        int closest = controller.closestVehicle(v);

        String message = "";

        // in front
        if(closest < Integer.MAX_VALUE){
            message+=("there is a vehicle "+closest+" units in front of you\t"+ ConnectionState.PUSH.getValue()+"\n");
        }else{
            message+=("there no one in front of you\t"+ ConnectionState.PUSH.getValue()+"\n");
        }


        //behind
        if(controller.roadClearAt(currentPosition-10, currentRoad, currentLane)){
            message+=("there is nothing behind you\t"+ ConnectionState.PUSH.getValue()+"\n");
        }else{
            message+=("there is a vehicle behind you\t"+ ConnectionState.PUSH.getValue()+"\n");
        }


        //left
        if(!map.laneExists(currentLane-1,currentRoad)) {
            message+=("There is no left lane\t"+ ConnectionState.PUSH.getValue()+"\n");
        }else {
            // left lane
            if(controller.roadClearAt(currentPosition, currentRoad, currentLane - 1)){
                message+=("your left is empty\t"+ ConnectionState.PUSH.getValue()+"\n");
            }else{
                message+=("there is a vehicle to your left\t"+ ConnectionState.PUSH.getValue()+"\n");
            }

        }

        //right
        if(!map.laneExists(currentLane+1,currentRoad)) {
            message+=("There is no right lane\t"+ ConnectionState.PUSH.getValue()+"\n");
        }else {
            // right lane
            if(controller.roadClearAt(currentPosition, currentRoad, currentLane + 1)){
                message+=("your right is empty\t"+ ConnectionState.PUSH.getValue()+"\n");
            }else{
                message+=("there is a vehicle to your right\t"+ ConnectionState.PUSH.getValue()+"\n");
            }
        }
        return message;
    }


    // The following are static String prompts that are sent to the user.
    // they are in the format of: "msg\t"+State Code
    // as with every message we send to the user we tell the client what it has to do with each message

    public static String noDir(Direction d){
        return("There is no "+d+" lane to switch to\t"+ ConnectionState.PUSH.getValue());}

    public static String laneSuccess() {
        return ("Lane changed\t" + ConnectionState.PUSH.getValue());}

    public static String doesntExist(){
        return("you cannot go in this direction\t"+ ConnectionState.PUSH.getValue());
    }

    public static String directionSuccess(){
        return("Changed direction\t"+ ConnectionState.PUSH.getValue());
    }

    public static String waitInstead(){
        return("you chose to wait instead\t"+ ConnectionState.PUSH.getValue());
    }

    public static String gambleWon(){
        return("you won the gamble, your action was successful\t"+ ConnectionState.PUSH.getValue());
    }

    public static String gambleLost(){
        return("you lost the gamble and damaged your car\t"+ ConnectionState.PUSH.getValue());
    }

    public static String middlePrompt(){return("would you like to (L)ook at your surroundings? (C)hange lanes? or continue the (S)imulation? (L, C or S)\t"+ ConnectionState.REPLY.getValue());}

    public static String confirm(){
        return("Are you sure? The light is red (Y/N): \t"+ ConnectionState.REPLY.getValue());
    }

    public static String laneConfirm(Direction d){ return("there is a vehicle to your "+d+", would you like to go anyway? (Y or N)\t"+ ConnectionState.REPLY.getValue());}




}
