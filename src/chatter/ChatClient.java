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
                // Thread to read from server
                new Thread(new ChatClient()).start();
                while (!closed) {
                    // gets information from layout
                    String line = inputLine.readLine();
                    // sends to the server
                    String message = "";
                    if (line != null) {
                        if (line.startsWith("/nick") || line.startsWith("/q")){
                            message = "C=" + line.trim();
                        } else if (layout.clientList.isSelectionEmpty()) {
                            // no selection -> send to everyone
                            message = "M=" + line.trim();
                        } else if (layout.clientList.getSelectedIndex() == 0){
                            message = "M=" + line.trim();
                        } else {
                            Object recipient = layout.clientList.getSelectedValue();
                            message = "P=" + recipient + "=" + line.trim();
                        }
                        outputStream.println(message);
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
         * Keep on reading from the socket till we receive "C=quit" from the
         * server. Once we received that then we want to break.
         */
        String responseLine;
        try {
            // gets information from the server:
            while ((responseLine = inputStream.readLine()) != null) {
                if (responseLine.equals("C=quit")) {
                    break;
                } else if (responseLine.startsWith("C=add=")){
                    String newName = responseLine.substring(6);
                    layout.clients.addElement(newName);
                } else if (responseLine.startsWith("C=remove=")){
                    String oldName = responseLine.substring(9);
                    layout.clients.removeElement(oldName);
                } else if (responseLine.startsWith("M=")){
                    // sends to the layout, get rid of M=
                    layout.putString(responseLine.substring(2));
                } else {
                    // only messages that are hardcoded in will start with no prefix
                    layout.putString(responseLine);
                }
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
