package Client;

import java.util.StringTokenizer;

// Nicholas Parise
// simple class to parse the data from the server.
public class Parser {

    private String msg;
    private int state;

    /**
     * takes in string value and sets the instance variables
     * @param input
     */
    public Parser(String input){

        StringTokenizer st = new StringTokenizer(input,"\t");
        msg = st.nextToken();
        state = Integer.valueOf(st.nextToken());
    }

    /**
     * @return data state from server
     */
    public ConnectionState getState(){
        return ConnectionState.find(state);
    }

    /**
     * @return message from server
     */
    public String getMsg(){
        return msg;
    }

}
