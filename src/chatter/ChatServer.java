package chatter;

import java.net.*;
import java.io.*;


public class ChatServer {

    private static ServerSocket serverSocket = null;
    private static Socket conn = null;
    // maximum number of connected users
    private static final int maxConnections = 10;
    private static final clientThread[] clientConns = new clientThread[maxConnections];

    public static void main(String args[]) {

        //open socket on server
        int portNumber = 5155;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        // Create a connection and new client thread for each new client
        while (true) {
            try {

                System.out.println("Listening for connections...");
                conn = serverSocket.accept();
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
                System.out.println(e);
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

    public void run() {
        int maxConnections = this.maxConnections;
        clientThread[] clientConns = this.clientConns;

        try {
            // open for one client, adds them to the list and informs everyone
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new PrintStream(clientSocket.getOutputStream());
            outputStream.println("Please enter your name.");
            String name = inputStream.readLine().trim();
            outputStream.println("Welcome " + name + ".\nEnter /q to leave the chat room.");
            for (int i = 0; i < maxConnections; i++) {
                if (clientConns[i] != null && clientConns[i] != this) {
                    clientConns[i].outputStream.println(name
                            + " has entered.");
                }
            }

            while (true) {
                String line = inputStream.readLine();
                // to quit the chat
                if (line.startsWith("/q")) {
                    break;
                }

                // echos sent message to everyone in the chat
                for (int i = 0; i < maxConnections; i++) {
                    if (clientConns[i] != null) {
                        clientConns[i].outputStream.println(name + ": " + line);
                    }
                }
            }

            // user exists the program
            for (int i = 0; i < maxConnections; i++) {
                if (clientConns[i] != null && clientConns[i] != this) {
                    clientConns[i].outputStream.println(name+ " has left.");
                }
            }
            outputStream.println("Bye " + name);


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
            // TODO: catch the exception
        }
    }
}
