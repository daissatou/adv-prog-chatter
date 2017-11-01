//
//  Created by:
//      ad1229  Aissatou Diallo
//      pj202   Peter Johnston
//      sam439  Sally Matson
//
//  Disclosures:
//

package chatter;

import sun.font.TrueTypeFont;

import java.net.*;
import java.io.*;


public class ChatServer {

    private static ServerSocket serverSocket = null;
    // maximum number of connected users
    private static final int maxConnections = 10;
    private static final clientThread[] clientConns = new clientThread[maxConnections];

    public static void main(String args[]) {

        // open socket on server
        int portNumber = 5155;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }

        // Create a connection and new client thread for each new client
        while (true) {
            try {
                System.out.println("Listening for connections...");
                Socket conn = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxConnections; i++) {
                    if (clientConns[i] == null) {
                        (clientConns[i] = new clientThread(conn, clientConns)).start();
                        break;
                    }
                }
                if (i == maxConnections) {
                    PrintStream outputStream = new PrintStream(conn.getOutputStream());
                    outputStream.println("At capacity, please try again later");
                    outputStream.close();
                    conn.close();
                }
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }
}

class clientThread extends Thread {

    private BufferedReader inputStream = null;
    private PrintStream outputStream = null;
    private Socket clientSocket = null;
    private final clientThread[] clientConns;
    private int maxConnections;

    public clientThread(Socket clientSocket, clientThread[] clientConns) {
        this.clientSocket = clientSocket;
        this.clientConns = clientConns;
        maxConnections = clientConns.length;
    }
    private boolean nameIsUnique(String name){
        for (int i = 0; i < maxConnections; i++){
            if (clientConns[i] != null && clientConns[i].getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    public void run() {
        int maxConnections = this.maxConnections;
        clientThread[] clientConns = this.clientConns;

        try {
            // open for one client, adds them to the list and informs everyone
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new PrintStream(clientSocket.getOutputStream());
            outputStream.println("Please enter your name.");
            boolean needName = true;
            while (needName) {
                String name = inputStream.readLine().trim();
                if (nameIsUnique(name)) {
                    this.setName(name);
                    outputStream.println("Welcome " + this.getName() + ".\nEnter /q to leave the chat room.");
                    for (int i = 0; i < maxConnections; i++) {
                        if (clientConns[i] != null && clientConns[i] != this) {
                            clientConns[i].outputStream.println(name
                                    + " has entered.");
                        }
                    }
                    needName = false;
                } else {
                    outputStream.println("This username has been taken, please select a new one.");
                }
            }

            // INSIDE CHAT :
            while (true) {
                String line = inputStream.readLine();
                if (line == null) {
                    continue;
                }
                // QUIT CHAT : 
                if (line.startsWith("/q")) {
                    break;
                }
                // PRIVATE CHAT :
                else if (line.startsWith("/p")) {
                    String recipient = line.split(" ")[1];
                    // TODO : get whole message
                    String message = line.split(" ")[2];
                    outputStream.println(this.getName() + ": " + message);
                    for (int i = 0; i<maxConnections; i++){
                        if (clientConns[i] != null && clientConns[i].getName().equals(recipient)){
                            clientConns[i].outputStream.println(this.getName() + ":**P CHAT** " + message);
                        }
                    }
                }
                // CHANGE NICKNAME :
                else if (line.startsWith("/nick")){
                    String newName = line.split(" ")[1];
                    if (nameIsUnique(newName)) {
                        for (int i = 0; i < maxConnections; i++) {
                            if (clientConns[i] != null) {
                                clientConns[i].outputStream.println(this.getName()
                                        + " has changed name to " + line.split(" ")[1]);
                            }
                        }
                        this.setName(newName);
                    }
                    else {
                        outputStream.println("This username has already been taken.");
                    }
                }
                // SEND MESSAGE TO EVERYONE
                else {
                    for (int i = 0; i < maxConnections; i++) {
                        if (clientConns[i] != null) {
                            clientConns[i].outputStream.println(this.getName() + ": " + line);
                        }
                    }
                }
            }

            // user exists the program
            for (int i = 0; i < maxConnections; i++) {
                if (clientConns[i] != null && clientConns[i] != this) {
                    clientConns[i].outputStream.println(this.getName()+ " has left.");
                }
            }
            outputStream.println("Bye " + this.getName());


            // free the current thread
            for (int i = 0; i < maxConnections; i++) {
                if (clientConns[i] == this) {
                    clientConns[i] = null;
                }
            }

            // close all opened
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
