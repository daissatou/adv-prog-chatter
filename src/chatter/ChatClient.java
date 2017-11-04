package chatter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.*;
import java.io.*;

public class ChatClient implements Runnable {

    private static Socket socket = null;
    private static PrintStream outputStream = null;
    private static BufferedReader inputStream = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    private static ChatterLayout layout;

    public static void main(String[] args) {
    		
    		if(args.length < 2) {
    			System.out.println("Please pass host name and port number as command line arguments");
    			System.exit(0);
    		}
    		
    		String host = args[0];
    		int portNumber = Integer.parseInt(args[1]);
    		
        
        //int portNumber = 5155;
        //String host = "localhost";

        // Open a socket on a given host and port. Open input and output streams.

        try {
            // open socket on the host and port,
            // datainput stream to receive messages from server,
            // dataoutput stream to send messages to server
            socket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(layout.getInputStream()));
            outputStream = new PrintStream(socket.getOutputStream());
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("I/O error: " + e);
        }
       

        // If all initialized correctly
        if (socket != null && outputStream != null && inputStream != null) {
            try {
                layout = new ChatterLayout();

                // Thread to read from server
                new Thread(new ChatClient()).start();
                while (!closed) {
                    String line = inputLine.readLine();
                    if (line != null) {
                        outputStream.println(line.trim());
                    }
                }

                // Close streams that were opened
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("IOException: " + e);
            }
        }
    }

    public void run() {
        //Read from server "bye"
        String responseLine;
        try {
            while ((responseLine = inputStream.readLine()) != null) {
                layout.putString(responseLine);
                if (responseLine.contains("Bye")) {
                    break;
                }
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
