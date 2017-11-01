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
        layout = new ChatterLayout();

        int portNumber = 5155;
        String host = "localhost";

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
        /*
         * Keep on reading from the socket till we receive "Bye" from the
         * server. Once we received that then we want to break.
         */
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
