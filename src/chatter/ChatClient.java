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

    public static void main(String[] args) {
        new ChatterLayout();

        int portNumber = 5155;
        String host = "localhost";

        // Open a socket on a given host and port. Open input and output streams.

        try {
            // open socket on the host and port,
            // datainput stream to receive messages from server,
            // dataoutput stream to send messages to server
            socket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            outputStream = new PrintStream(socket.getOutputStream());
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("I/O error");
        }

        // If all initialized correctly
        if (socket != null && outputStream != null && inputStream != null) {
            try {
                // thread to read from server
                new Thread(new ChatClient()).start();
                while (!closed) {
                    outputStream.println(inputLine.readLine().trim());
                }

                // Close that were opened
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
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
                System.out.println(responseLine);
                if (responseLine.contains("Bye"))
                    break;
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}

class ChatterLayout extends JFrame {
    private JPanel panelChat;
    private JPanel panelList;

    private TextArea chatArea;
    private TextArea typeArea;
    private JList clientList;

    public ChatterLayout() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chatter");
        getContentPane().setLayout(new BorderLayout());

        setupPanelChat();
        setupPanelList();

        // window settings
        setSize(new Dimension(640, 480));
        setVisible(true);
    }

    private void setupPanelChat() {
        panelChat = new JPanel();
        add(panelChat, BorderLayout.CENTER);
        panelChat.setLayout(new GridBagLayout());
        panelChat.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        // add chat area
        chatArea = new TextArea("@peter: \"hello\"", 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY);
        chatArea.setEditable(false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0.8;
        panelChat.add(chatArea, constraints);

        // add text area
        typeArea = new TextArea("", 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 0.2;
        panelChat.add(typeArea, constraints);
    }

    private void setupPanelList() {
        panelList = new JPanel();
        add(panelList, BorderLayout.LINE_END);
        panelList.setLayout(new BoxLayout(panelList, BoxLayout.Y_AXIS));
        panelList.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 6)); // TODO: figure out how to add insets

        // add components
        String[] clients = {
            "Sally                  ",
            "Peter",
            "Aicha",
            "client 4",
            "client 5",
            "client 6",
        };
        clientList = new JList(clients);
        clientList.setPreferredSize(new Dimension(100, 0));

        panelList.add(clientList);
    }
}
