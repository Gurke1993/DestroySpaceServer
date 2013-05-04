package de.bplaced.mopfsoft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ConnectedClientThread extends Thread{
	Socket s;
	  DataOutputStream out;
	  DataInputStream in;
	  ServerThread server;
	private ConnectedClientTransferFileThread connectedClientTransferThread;

	  public ConnectedClientThread(ServerThread server, Socket s) throws IOException {
	    this.server = server;
	    this.s = s;
	    out = new DataOutputStream(s.getOutputStream());
	    in = new DataInputStream(s.getInputStream());
	      System.out.println("New Client connected from IP: "+s.getLocalAddress()+ " "+s.getLocalPort());
	  }
	  
	  public void setConnectedClientTransferThread(ConnectedClientTransferFileThread thread) {
		  connectedClientTransferThread = thread;
	  }

	  public void run() {
	    String text;
	    try {
	      while((text = in.readUTF()) != null) {
	        server.analyzeNewMessage(text+":"+System.currentTimeMillis(), this);
	      
	    }
	    }
	    catch(IOException e) {
	      e.printStackTrace();
	    }
	    server.closeConnection(this);
	  }

	  public void send(String message) {
	    try {
	      out.writeUTF(message);
	    }
	    catch(IOException e) {
	      e.printStackTrace();
	    }
	  }

	public ConnectedClientTransferFileThread getFileTransferThread() {
		return connectedClientTransferThread;
	}
	  
	  
}
