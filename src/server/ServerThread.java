package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Random;

/**
 * Created by Dario on 2015-09-08.
 */
public class ServerThread extends Thread {

    private DatagramSocket socket;
    private int port;
    private int clientPort;
    private InetAddress clientHostname;
    private boolean clientConnected;
    private boolean serverActive;

    public ServerThread() throws IOException {
        port = 6478;
        socket = new DatagramSocket(port);
        clientHostname = null;
        clientConnected = false;
        serverActive = true;
    }
    public void run() {
        while (serverActive) {
            System.out.println("Server start!");
            waitingForConnection();
            whileClientConnected();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Thread: " + e.getMessage());
        }
        System.out.println("Server bye!");
        socket.close();
    }

    private void waitingForConnection() {
        while (!clientConnected) {
            System.out.println("Waiting for connection....");
            DatagramPacket packet = getPacket(0);
            if (validateClient(packet)) {
                if (connect(packet)) {
                    clientConnected = true;
                    sendResponse("ok");
                } else {
                    sendRefuse(packet, "no hello");
                }
            } else {
                sendRefuse(packet, "busy");
            }
        }
    }

    private void whileClientConnected() {
        while (clientConnected) {
            System.out.println("whileClientConnected......");
            if(startGame()){
                play();
            }
        }
    }

    private boolean startGame() {
        DatagramPacket packet;
        String msg;

        while (clientConnected) {
            System.out.println("waiting for start!!!!!!");
            packet = getPacket(7000);
            if(packet != null){
                if (validateClient(packet)) {
                    msg = getMessage(packet);
                    if (msg.equalsIgnoreCase("bye")) {
                        closeConnection();
                        return false;
                    }
                    else if (msg.equalsIgnoreCase("start")) {
                        return true;
                    }
                    else {
                        sendResponse("no start");
                    }
                } else {
                    sendRefuse(packet, "busy");
                }
            } else {
                sendResponse("Timeout server close connection!");
                closeConnection();
                return false;
            }
        }
        return false;
    }

    private void play() {
        boolean done = false;
        DatagramPacket packet;
        String msg;
        int rnd = randInt(0,1000);
        int response;
        sendResponse("ready");

        while (!done) {
            packet = getPacket(7000);
            if(packet != null){
                if (validateClient(packet)) {
                    msg = getMessage(packet);
                    if (msg.equalsIgnoreCase("bye")) {
                        done = true;
                        closeConnection();
                    } else if (isNumber(msg)) {
                        response = Integer.parseInt(msg);
                        System.out.println("Response: " + response);
                        System.out.println("RND: " + rnd);
                        if (response == rnd) {
                            sendResponse("correct!");
                            done = true;
                        } else if (response < rnd) {
                            sendResponse("up");
                        } else {
                            sendResponse("down");
                        }
                    } else {
                        sendResponse("is not number");
                    }
                } else {
                    sendRefuse(packet, "busy");
                }
            } else{
                sendResponse("Timeout server close connection!");
                closeConnection();
                done = true;
            }
        }
    }

    private boolean validateClient(DatagramPacket packet) {
        String address = packet.getAddress().getHostAddress();
        if (!clientConnected) {
            System.out.println("Authorized: " + address);
            return true;
        } else if (clientHostname.getHostAddress().equals(address)) {
            System.out.println("Authorized: " + address);
            return true;
        } else {
            System.out.println("No Authorized: " + address);
            return false;
        }
    }

    private boolean connect(DatagramPacket packet) {
        String data = getMessage(packet);
        if (data.equalsIgnoreCase("hello")) {
            clientPort = packet.getPort();
            clientHostname = packet.getAddress();
            System.out.println("Connected to: " + clientHostname);
            return true;
        }
        return false;
    }

    private void closeConnection() {
        clientConnected = false;
    }

    private DatagramPacket getPacket(int timeOut) {
        try {
            byte[] bufferIn = new byte[256];
            DatagramPacket packetIn = new DatagramPacket(bufferIn, bufferIn.length);
            //Set Timeout
            socket.setSoTimeout(timeOut);
            try {
                socket.receive(packetIn);
                return packetIn;
            }
            catch (SocketTimeoutException e) {
                // timeout exception.
                System.out.println("Timeout reached!!! " + e);
                return null;
            }
            //socket.receive(packetIn);
            //return packetIn;
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMessage(DatagramPacket packet) {
        String msg = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Client: " + msg);
        return msg;
    }

    private void sendRefuse(DatagramPacket packet, String msg) {
        int rPort = packet.getPort();
        InetAddress rAddress = packet.getAddress();
        try {
            byte[] buffer;
            buffer = msg.getBytes();
            packet = new DatagramPacket(buffer, buffer.length, rAddress, rPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(String msg) {
        try {
            byte[] bufferOut = msg.getBytes();
            DatagramPacket packetOut = new DatagramPacket(bufferOut, bufferOut.length, clientHostname, clientPort);
            socket.send(packetOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isNumber(String msg) {
        try {
            Integer.parseInt(msg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;

    }
}
