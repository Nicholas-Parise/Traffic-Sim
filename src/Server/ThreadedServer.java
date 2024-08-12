package Server;

import Game.VehicleController;
import Map.Map;
import Player.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedServer implements Runnable{

    int portNumber = 1024;
    ArrayList<Player> players;
    VehicleController controller;
    Map map;

    public ThreadedServer(ArrayList<Player> players, VehicleController controller, Map map){
        this.players = players;
        this.controller = controller;
        this.map = map;
    }

    @Override
    public void run() {
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (true) {
                Socket client1=serverSocket.accept();
                Player player = new Player(controller,map);
                players.add(player);

                new Thread(new ConnectionThread(client1,player)).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}