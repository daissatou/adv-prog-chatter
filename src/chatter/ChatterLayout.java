package chatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ChatterLayout extends JFrame implements KeyListener {

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
        typeArea.addKeyListener(this);
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

    public void keyPressed(KeyEvent keyEvent)
    {
        if(keyEvent.getSource() == typeArea) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)  {
                String text = typeArea.getText();
                if (text != "") {
                    System.out.println("TYPED A THING.... " + text);
                    // clear text
                    typeArea.setText("");
                    // prevent enter from typing in the box
                    keyEvent.consume();
                }
            }
        }
    }

    public void keyReleased(KeyEvent keyEvent) { }
    public void keyTyped(KeyEvent keyEvent) { }
}