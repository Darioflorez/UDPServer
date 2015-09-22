package server;

import java.io.IOException;

/**
 * Created by Dario on 2015-09-08.
 */
public class Server {
    public static void main(String[] args){
        try{
            ServerThread server = new ServerThread();
            server.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
