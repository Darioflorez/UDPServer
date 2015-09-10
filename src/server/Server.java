package server;

import java.io.IOException;

/**
 * Created by Dario on 2015-09-08.
 */
public class Server {
    public static void main(String[] args){
        String s = "123";
        if(isNumber(s)){
            System.out.println("is number");
        }
        else {
            System.out.println("No number");
        }

        try{
            ServerThread server = new ServerThread();
            server.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isNumber(String msg){
        try{
            Integer.parseInt(msg);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
}
