package de.bplaced.mopfsoft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectedClientTransferFileThread extends Thread{
	Socket s;
	  DataOutputStream out;
	  DataInputStream in;
	  ServerThread server;
	private ConnectedClientThread thread;

	  public ConnectedClientTransferFileThread(ServerThread server, Socket s, ConnectedClientThread thread) throws IOException {
		  this.thread = thread;
	    this.server = server;
	    this.s = s;
	    out = new DataOutputStream(s.getOutputStream());
	    in = new DataInputStream(s.getInputStream());
	      System.out.println("FileTransfer is enabled for "+s.getInetAddress()+" at "+s.getLocalPort());
	  }

	  public void run() {
	    try {
	    	byte[] array = new byte [15000];
	      while (in.read(array) != -1) {
	      
	    }
	    }
	    catch(IOException e) {
	      e.printStackTrace();
	    }
	    server.closeConnection(this);
	  }
	  
	  public void sendFile(String path) {
		  try {
		  File file = new File(path);
		  FileInputStream fileInputStream = new FileInputStream(file);
	        long fileSize = file.length();
	        long completed = 0;
	        int step = 150000;
	 
	        byte[] buffer = new byte[step];
	        while (completed <= fileSize) {
	            fileInputStream.read(buffer);
	            out.write(buffer);
	            completed += step;
	        }
	        System.out.println("Succesfully send file: "+path);
	        fileInputStream.close();
	        //thread.send("1:-1:1:"+file.getName());
	        
		  } catch (Exception e) {
			  System.out.println("Could not send File: "+path);
			  e.printStackTrace();
		  }
	  }
}
