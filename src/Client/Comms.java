package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

// Nicholas Parise
// client side communication class
public class Comms implements Runnable{

    protected String hostName="localhost";
    protected int portNumber=1024;

    ConnectionState state;
    Parser parser;

    String username;
    String password;

    public Comms(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void run() {

        try (
                Socket conn = new Socket(hostName, portNumber);
                PrintWriter sockOut = new PrintWriter(conn.getOutputStream(),true);
                BufferedReader sockIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                ) {

            System.out.println("Connection accepted!");
            String userInput;
            String msgServer;
            String data;

            while (true) {
                // take in data from the server
                msgServer = sockIn.readLine();

                // try to parse this data
                try {
                    parser = new Parser(msgServer);
                    state = parser.getState();
                    data = parser.getMsg();
                }catch (Exception e){
                    continue; // received un displayable data from the server. skip
                }

                // check the state supplied from the server
                switch (state){

                    case PUSH: // if its push just display the message to the user
                        System.out.println(data);
                        break;
                    case REPLY: // of its a reply display message and get the users input
                        System.out.println(data);
                        userInput = stdIn.readLine();

                        if(userInput == null){
                            break;
                        }

                        sockOut.println(userInput);

                        break;
                    case AUTHENTICATE: // if it's authenticate, send the username and password to the server when it's being requested.
                        /// handle authentication
                        if(data.equals("user")){
                            sockOut.println(username);
                        }else if(data.equals("pass")){
                            sockOut.println(password);
                        }
                        break;
                    case KILL: // if it's kill, close the connection
                        System.out.println(data);
                        conn.close(); // close connection at request of server
                        break;
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("I think there's a problem with the host name.");
        } catch (IOException e) {
            System.out.println("Had an IO error for the connection.");
        }
    }


}
