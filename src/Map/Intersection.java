package Map;

public class Intersection {

  public int destinationId;
  public Intersection next;
  public Direction direction;
  public int roadSegment;
  public TrafficLight trafficLight;

  public Intersection(Integer destinationId, Intersection next, Direction direction, TrafficLight trafficLight, int road) {
    this.destinationId = destinationId;
    this.next = next;
    this.direction = direction;
    this.roadSegment = road;
    this.trafficLight = trafficLight;
  }


  public int getDestinationId() {
    return destinationId;
  }

  /**
   *
   * @return get the current road segment
   */
  public int getRoad() {
  return roadSegment;
  }


  /**
   * set the traffic state
   * @param trafficState TrafficLight
   */
  public void setTraffic(TrafficLight trafficState) {
    trafficLight = trafficState;
  }

  /**
   *
   * @return get the current traffic state
   */
  public TrafficLight getTrafficLight() {
  return trafficLight;
  }

  /**
   *
   * @return direction
   */
  public Direction getDirection() {
    return direction;
  }

}