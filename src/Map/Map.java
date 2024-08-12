package Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Map implements Serializable {

  public Intersection[] intersections;
  public RoadSegment[] roads;

  int finalIntersection;
  int finalRoad;

  public Map() {

    intersections = new Intersection[100];
    roads = new RoadSegment[100];

    finalIntersection = 0;
    finalRoad = 0;
  }


  /**
   * This method takes in a file name and then fills out the road and intersection array
   *
   * @param fileName String filename
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public void loadMapXML(String fileName) throws ParserConfigurationException, SAXException, IOException {

    if(fileName == "")
      throw new RuntimeException("The name of the XML file is required!");

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(fileName));

    NodeList nodeList = document.getElementsByTagName("intersection");
    for (int i = 0; i < nodeList.getLength(); i++) {

      Node node = nodeList.item(i);

      String intersectionID = node.getAttributes().getNamedItem("id").getNodeValue();

      //get list of all roads under intersection
      NodeList roadList = ((Element)nodeList.item(i)).getElementsByTagName("road");

        for (int j = 0; j < roadList.getLength(); j++) {
          node = roadList.item(j);

          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element elem = (Element) node;

            String roadId = node.getAttributes().getNamedItem("roadId").getNodeValue();


            // Get the value of all sub-elements.
            Integer destination = Integer.parseInt(elem.getElementsByTagName("destination")
                  .item(0).getChildNodes().item(0).getNodeValue());

            String direction = elem.getElementsByTagName("direction")
                  .item(0).getChildNodes().item(0).getNodeValue();

            String trafficState = elem.getElementsByTagName("state")
                  .item(0).getChildNodes().item(0).getNodeValue();

            Integer length = Integer.parseInt(elem.getElementsByTagName("length")
                  .item(0).getChildNodes().item(0).getNodeValue());

            Integer lanes = Integer.parseInt(elem.getElementsByTagName("lanes")
                  .item(0).getChildNodes().item(0).getNodeValue());


            finalRoad = Integer.valueOf(roadId);
            finalIntersection = Integer.valueOf(intersectionID);

            roads[finalRoad] = new RoadSegment(length, lanes);

            // add to linked list.
            if(intersections[finalIntersection] == null){
              intersections[finalIntersection] = new Intersection(destination, null, convertDirection(direction.charAt(0)), convertTrafficState(trafficState.charAt(0)), finalRoad);
            }else{
              Intersection I = intersections[finalIntersection];

              while(I.next != null){
                I = I.next;
              }
              I.next = new Intersection(destination, null, convertDirection(direction.charAt(0)), convertTrafficState(trafficState.charAt(0)), finalRoad);

            }
        }
      }
    }
  }





  /**
   * update the stop lights by fliping all their states
   * since on create the lights are set to be the right way.
   * (shouldn't run every frame)
   */
  public void lightUpdate() {

    for (int i = 0; i <= getFinalIntersection()-1; i++) {
      TrafficLight t = intersections[i].getTrafficLight();

      if(t == TrafficLight.GREEN){
        intersections[i].setTraffic(TrafficLight.RED);
      }else if(t == TrafficLight.RED){
        intersections[i].setTraffic(TrafficLight.GREEN);
      }
    }
  }



  /**
   * turns char representation into a Direction
   * @param d char
   * @return corresponding Direction
   */
  public static Direction convertDirection(char d){
    switch (d){
      case 'L':
      case 'l':
        return Direction.LEFT;
      case 'F':
      case 'f':
        return Direction.FORWARD;
      case 'R':
      case 'r':
        return Direction.RIGHT;
      case 'W':
      case 'w':
      case 'N':
      case 'n':
        return Direction.NONE;
      default:
        return Direction.FORWARD;
    }
  }

  /**
   * turns char representation into a TrafficLight
   * @param t char
   * @return corresponding TrafficLight
   */
  private TrafficLight convertTrafficState(char t){
    switch (t){
      case 'G':
      case 'g':
        return TrafficLight.GREEN;
      case 'R':
      case 'r':
        return TrafficLight.RED;
      case 'S':
      case 's':
        return TrafficLight.STOPSIGN;
      default:
        return TrafficLight.STOPSIGN;
    }
  }



  /**
   * returns the avaliable destinations for the intersection
   * @param intersection intersection index
   * @return array of int destinations, in order.
   */
  public ArrayList<Integer> getDestinations(int intersection) {

    ArrayList<Integer> directions = new ArrayList<Integer>();
    Intersection temp = intersections[intersection];

    while(temp != null){
      directions.add(temp.destinationId);
      temp = temp.next;
    }

    return directions;
  }


  /**
   * returns the available directions for the intersection
   * @param intersection intersection index
   * @return arraylist of available directions, in order.
   */
  public ArrayList<Direction> getDirections(int intersection){

    Intersection i = intersections[intersection];

    ArrayList<Direction> directions = new ArrayList<Direction>();

    while (i != null){
      directions.add(i.getDirection());
      i = i.next;
    }

    return directions;
  }


  /**
   * returns the traffic state for a given intersection
   * @param intersection intersection index
   * @return arraylist of traffic states, in order.
   */
  public ArrayList<TrafficLight> getSignals(int intersection){

    Intersection i = intersections[intersection];

    ArrayList<TrafficLight> signals = new ArrayList<TrafficLight>();

    while (i != null){
      signals.add(i.getTrafficLight());
      i = i.next;
    }

    return signals;
  }


  /**
   * Determines if a given direction at an intersection is red
   * @param intersection intersection id
   * @param direction given direction
   * @return true if red
   */
  public boolean getDirectionRed(int intersection, Direction direction){

    Intersection i = intersections[intersection];

    while (i != null) {

      if(i.getDirection() == direction){
        if(i.getTrafficLight() == TrafficLight.RED){
          return true;
        }else{
          return false;
        }
      }
      i = i.next;
    }

    return false;
  }



  /**
   * This function is used to get the distance of the road with the corresponding index
   * @param id road index
   * @return distance
   */
  public int getDist(int id) {
    return roads[id].length;
  }

  /**
   * This function is used to get the number of lanes in the road with the corresponding index
   * @param id road index
   * @return number of lanes
   */
  public int getLanes(int id) {
    return roads[id].lanes;
  }

  /**
   * Get the idex of the final intersection;
   * @return index
   */
  public int getFinalIntersection() {
    return finalIntersection;
  }

  /**
   * Get the idex of the final road;
   * @return index
   */
  public int getFinalRoad() {
    return finalRoad;
  }

  /**
   * returns a random destination intersection.
   * @return
   */
  public int randStart(){

    int lastIndex = getFinalIntersection();
    return (int)(Math.random()*lastIndex);
  }


  /**
   * returns a random position in a road given a road
   * @param road
   * @return
   */
  public int randPos(int road){
    return (int)(Math.random()*roads[road].length);
  }

  /**
   * returns a random lane in a road given a road
   * @param road
   * @return
   */
  public int randLane(int road){
    return (int)(Math.random()*roads[road].lanes);
  }


  /**
   * return a random destinationID from a starting point (current destination)
   * @param destination current destination
   * @return destination ID
   */
  public int decideDirection(int destination) {

    ArrayList<Integer> directions = getDestinations(destination);

    int size = directions.size();
    int rand = (int)(Math.random()*(size));

    return directions.get(rand);
  }


  /**
   * returns a random direction
   * @return
   */
  public Direction getRandomDirection(int intersection){

    ArrayList<Direction> directions = getDirections(intersection);

    int size = directions.size();
    int rand = (int)(Math.random()*(size));

    return directions.get(rand);
  }

  /**
   * returns a new road for the vehicle to go down given a starting position and a destination
   * @param current current intersection
   * @param newDestination new destination intersection
   * @return
   */
  public int getNewRoad(int current, int newDestination){

    Intersection intersection = intersections[current];

    while(intersection != null){
      if(intersection.destinationId == newDestination){

        return intersection.getRoad();
      }
      intersection = intersection.next;
    }

    return 0;
  }


  /**
   * determines if a lane exists
   * @param lane lane
   * @param currentRoad current road
   * @return true if lane exists
   */
  public boolean laneExists(int lane, int currentRoad){

    if(lane >= 0 && lane <= roads[currentRoad].lanes) {
      return true;
    }
    return false;
  }


}