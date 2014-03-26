package de.bplaced.mopfsoft.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.newdawn.slick.util.Log;


public class ConnectedClientThread extends Thread {
	final Socket s;
	DataOutputStream out;
	DataInputStream in;
	private ConnectedPlayer player;

	public ConnectedClientThread(Socket s, ConnectedPlayer player)
			throws IOException {
		
		this.player = player;
		
		this.s = s;
		out = new DataOutputStream(s.getOutputStream());
		in = new DataInputStream(s.getInputStream());
		Log.info("New Client connected from IP: "
				+ s.getLocalAddress() + " " + s.getLocalPort());
		

	}

	public void run() {
		String text;
		try {
			while ((text = in.readUTF()) != null) {
				ServerThread.getInstance().analyseClientMessage(
						text + ":timerecieved=" + System.currentTimeMillis(), player);

			}
		} catch (SocketException e1) {
			player.close();
		} catch (IOException e) {
			Log.error(e);
		}
	}

	  public void send(String message) {
	    try {
	    	out.writeUTF(message);
		} catch (SocketException e1) {
		    player.close();
		}
	    catch(IOException e) {
	    	Log.error(e);
	    }
	  }
	  
	  
}
