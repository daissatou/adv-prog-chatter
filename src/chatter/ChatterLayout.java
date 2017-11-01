package chatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.io.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;

class ChatterLayout extends JFrame implements KeyListener, ActionListener {

    private JPanel panelChat;
    private JPanel panelList;

    private TextArea chatArea;
    private TextArea typeArea;
    private JButton sendButton;
    private JList clientList;

    // Current index in the last byte array read
    private int index; // TODO: do i need to edit this?
    private List<byte[]> inputBuffer = new ArrayList<byte[]>();
    private InputStream inputStream;

    public ChatterLayout() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        setupPanelChat();
        setupPanelList();
        setupInputStream();

        // window settings
        setTitle("Chatter");
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
        chatArea = new TextArea("", 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY);
        chatArea.setEditable(false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weighty = 0.9;
        panelChat.add(chatArea, constraints);

        // add text area
        typeArea = new TextArea("", 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.9;
        constraints.weighty = 0.1;
        panelChat.add(typeArea, constraints);
        typeArea.addKeyListener(this);

        // add send button
        sendButton = new JButton("Send");
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        panelChat.add(sendButton, constraints);
        sendButton.addActionListener(this);
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

    private void setupInputStream() {
        // TODO: this code was partially taken from a tutorial, need to disclose that
        inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                if (inputBuffer.isEmpty()) {
                    return -1;
                }
                // get the next byte[] from the inputBuffer
                byte[] bytes = inputBuffer.get(0);
                if (bytes == null) {
                    // TODO: not sure why this should be happening...
                    // maybe should be just doing this: ?
                    // inputBuffer.remove(0);
                    // index = 0;
                    return -1;
                }
                // read the next byte from bytes[] and increment our index
                byte result = bytes[index++];
                if (index >= bytes.length) {
                    // we've finished reading through the byte array
                    // so we remove it from inputBuffer and start back at index 0
                    inputBuffer.remove(0);
                    index = 0;
                }
                return result;
            }
        };
    }

    // KeyListener methods

    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getSource() == typeArea) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)  {
                send(typeArea.getText());
                // prevent enter from typing in the box
                keyEvent.consume();
            }
        }
    }

    public void keyReleased(KeyEvent keyEvent) { }
    public void keyTyped(KeyEvent keyEvent) { }

    // ActionListener methods
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == sendButton) {
            send(typeArea.getText());
        }
    }

    private void send(String text) {
        if (!text.isEmpty()) {
            // add typed text to inputBuffer, so it can be read by client
            try {
                byte[] data = text.getBytes("UTF-8");
                if (data != null) {
                    inputBuffer.add(data);
                }
            } catch (UnsupportedEncodingException e) {
                System.out.println("UnsupportedEncodingException: " + e);
            }
            // clear text
            typeArea.setText("");
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void putString(String string) {
        chatArea.setText(chatArea.getText() + string + '\n');
        chatArea.setCaretPosition(chatArea.getText().length());
    }
}
