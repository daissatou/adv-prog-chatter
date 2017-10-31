package chatter;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ChatClient extends JFrame implements Runnable, ActionListener {

	  private static Socket socket = null;
	  private static PrintStream outputStream = null;
	  private static BufferedReader inputStream = null;
	  private static BufferedReader inputLine = null;
	  private static boolean closed = false;
	  
	  JPanel panelChat = new JPanel();
	  JPanel panelConnectionInfo = new JPanel();
	  JTextField chatTextField;
	  JTextArea chatTextArea;
	  JTextField hostTextField;
	  JTextField portTextField;

	  
	  JButton sendButton;
	  
	  
	  
	  public static void main(String[] args) {

	    
		  
		int portNumber = 5155;
	    String host = "localhost";

	     //Open a socket on a given host and port. Open input and output streams.
	    
	    try {
	    	
	    		//open socket on the host and port, datainput stream to recieve messages from server, dataoutput stream to send messages to server 
	    	  socket = new Socket(host, portNumber);
	      inputLine = new BufferedReader(new InputStreamReader(System.in));
	      outputStream = new PrintStream(socket.getOutputStream());
		inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    } catch (UnknownHostException e) {
	      System.err.println("Unknown host: " + host);
	    } catch (IOException e) {
	      System.err.println("I/O error");
	    }

	    
	     //If all initialized correctly
	    if (socket != null && outputStream != null && inputStream != null) {
	      try {

	        //thread to read from server
	        new Thread(new ChatClient()).start();
	        while (!closed) {
	        		
	        		outputStream.println(inputLine.readLine().trim());
	        }
	        
	         //Close that were opened 
	        outputStream.close();
	        inputStream.close();
	        socket.close();
	      } catch (IOException e) {
	        System.err.println("IOException:  " + e);
	      }
	    }
	  }

	
	  public void run() {
	   
	    String responseLine;
	    try {
	      while ((responseLine = inputStream.readLine()) != null) {
	        System.out.println(responseLine);
	        String s = inputStream.readLine();
	        chatTextArea.append(s+"\n");
	        if (responseLine.indexOf("Bye") != -1)
	          break;
	      }
	      closed = true;
	    } catch (IOException e) {
	      System.err.println("IOException:  " + e);
	    }
	  }
	  
	  public ChatClient(){
	        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	        setTitle("Chat");
	        setLayout(new FlowLayout());

	        add(panelChat, BorderLayout.PAGE_END);	        

	        chatTextArea = new JTextArea(12, 20);
	        chatTextArea.setBackground(Color.white);
	        
	        portTextField = new JTextField(15);
	        
	        panelChat.add(chatTextArea);
	        
	        
	        chatTextField = new JTextField(15);
	        chatTextField.setMaximumSize( chatTextField.getPreferredSize() );
	        //chatTextField.addActionListener(this);
	        //chatTextField.setEditable(false);
	        panelChat.add(chatTextField);

	        setupButtons();
	        

	        // window settings
	        setSize(new Dimension(640, 480));
	        setVisible(true);
	        
	        

	  }
	  
	  private void setupButtons() {
		  
	        sendButton =new JButton("Send");
	        
	        
	        sendButton.addActionListener(this);
	        
	        panelChat.add(sendButton);


	  }
	  
	  public void actionPerformed(ActionEvent ae)
	  
	   {
	  
  		outputStream.println(chatTextField.getText());
	    chatTextField.setText("");
	  
	   }

	}

