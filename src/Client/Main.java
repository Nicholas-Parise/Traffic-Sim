package Client;

import Server.ThreadedServer;

public class Main {

  public Main() {

  }

  public static void main(String[] args) {
    new Thread(new Comms("admin","password")).start();
  }


}