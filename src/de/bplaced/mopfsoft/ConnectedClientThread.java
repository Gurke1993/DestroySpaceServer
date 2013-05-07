package de.bplaced.mopfsoft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ConnectedClientThread extends Thread {
	final Socket s;
	DataOutputStream out;
	DataInputStream in;
	private final ServerThread server;
	private ConnectedPlayer player;

	public ConnectedClientThread(ServerThread server, Socket s, ConnectedPlayer player)
			throws IOException {
		
		this.player = player;
		
		// setup connection
		this.server = server;
		this.s = s;
		out = new DataOutputStream(s.getOutputStream());
		in = new DataInputStream(s.getInputStream());
		System.out.println("New Client connected from IP: "
				+ s.getLocalAddress() + " " + s.getLocalPort());
		

	}

	public void run() {
		String text;
		try {
			while ((text = in.readUTF()) != null) {
				server.destroySpaceServer.analyseClientMessage(
						text + ":timerecieved=" + System.currentTimeMillis(), player);

			}
		} catch (SocketException e1) {
			player.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	  public void send(String message) {
	    try {
	      out.writeUTF(message);
		} catch (SocketException e1) {
		    player.close();
		}
	    catch(IOException e) {
	      e.printStackTrace();
	    }
	  }
	  
	  
}
