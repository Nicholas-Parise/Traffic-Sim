package Client;

import java.util.EnumSet;

public enum ConnectionState {
    AUTHENTICATE(0),
    PUSH(1),
    REPLY(2),
    KILL(3);

    // authenticate: authenticating payer
    // Push: don't need a reply from client
    // reply: want a reply from the client
    // Kill: end connection

    private int value;

    private ConnectionState(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ConnectionState find(int value){

        for(ConnectionState c : EnumSet.allOf(ConnectionState.class)){
            if(c.getValue() == value){
                return c;
            }
        }
        return null;
    }


}
