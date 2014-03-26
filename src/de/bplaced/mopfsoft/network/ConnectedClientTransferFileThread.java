package de.bplaced.mopfsoft.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.newdawn.slick.util.Log;


public class ConnectedClientTransferFileThread extends Thread{
	Socket s;
	  DataOutputStream out;
	  DataInputStream in;
	private final ConnectedPlayer player;

	  public ConnectedClientTransferFileThread(Socket s, ConnectedPlayer player) throws IOException{
		  this.player = player;
		  this.s = s;
		  out = new DataOutputStream(s.getOutputStream());
		  in = new DataInputStream(s.getInputStream());
		  Log.info("FileTransfer is enabled for "+s.getInetAddress()+" at "+s.getLocalPort());
	  }

	  public void run() {
	    try {
	    	byte[] array = new byte [15000];
	      while (in.read(array) != -1) {
	      
	    }
	    } catch (SocketException e1) {
		    player.close();
	    } catch(IOException e) {
	    	Log.error(e);
	    }
	  }
	  
	  public void sendFile(String path) {
		  try {
		  File file = new File(path);
		  FileInputStream fileInputStream = new FileInputStream(file);
		  
		  restartOutputStream();
	        long fileSize = file.length();
	        long completed = 0;
	        int step = 15000;
	 
	        byte[] buffer = new byte[step];
	        while (completed <= fileSize) {
	            fileInputStream.read(buffer);
	            out.write(buffer);
	            completed += step;
	        }
	        Log.info("Succesfully send file: "+path+" to "+player.getName());
	        fileInputStream.close();
	        
		  } catch (Exception e) {
			  Log.error("Could not send File: "+path,e);
		  }
	  }

	private void restartOutputStream() throws IOException {
		out.flush();
		this.out = new DataOutputStream(s.getOutputStream());
	}
}
