package de.bplaced.mopfsoft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import de.bplaced.mopfsoft.entitys.Player;

public class ConnectedClientThread extends Thread {
	final Socket s;
	DataOutputStream out;
	DataInputStream in;
	private final ServerThread server;
	private ConnectedClientTransferFileThread connectedClientTransferThread;
	private Player player;

	public ConnectedClientThread(ServerThread server, Socket s)
			throws IOException {
		// setup connection
		this.server = server;
		this.s = s;
		out = new DataOutputStream(s.getOutputStream());
		in = new DataInputStream(s.getInputStream());
		System.out.println("New Client connected from IP: "
				+ s.getLocalAddress() + " " + s.getLocalPort());
		

	}
	
	public void setPlayer (Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return this.player;
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
