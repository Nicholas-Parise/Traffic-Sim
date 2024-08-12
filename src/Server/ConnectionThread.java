package Server;

import Game.Database;
import Player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.Socket;

/**
 * @author Nicholas Parise
 * @version 1.0
 * @course COSC 2P13
 * @assignment #2
 * @student Id 7242530
 * @since July 2nd , 2023
 */

public class ConnectionThread implements Runnable{

    private Socket client1;
    private Player player;
    private ConnectionState state;

    public ConnectionThread(Socket client1, Player player) {
        this.client1=client1;
        this.player = player;
        state = ConnectionState.AUTHENTICATE;
    }

    public void run() {
        try (
                PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
                BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        ) {
            while (true) {
                // authenticate user
                if(state == ConnectionState.AUTHENTICATE){
                    out1.println("user\t"+state.getValue());
                    String user = in1.readLine();
                    out1.println("pass\t"+state.getValue());
                    String pass = in1.readLine();

                    if(Database.getAccount(user, pass)){
                        state = ConnectionState.PUSH;
                        out1.println("Authenticated\t"+state.getValue());
                    }else{
                        state = ConnectionState.KILL;
                        out1.println("User Not found\t"+state.getValue());
                        client1.close();
                        player.setConnected(false);
                        return; // break connection
                    }

                }else{
                    // used to send extra information to the player.
                    if(player.getExtraMessage().length() < 1) {
                        out1.println(player.getExtraMessage());
                        player.resetExtraMessage();
                    }

                    player.prompt(out1,in1);

                }

            }
        } catch (IOException e) {
            player.setConnected(false);
            System.out.println("Connection TERMINATED");
        }
    }
}