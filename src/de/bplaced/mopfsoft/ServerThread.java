package de.bplaced.mopfsoft;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class ServerThread implements Runnable{
	Thread listener;
	  ServerSocket mainServer, fileTransferServer;
	  DestroySpaceServer destroySpaceServer;
	  @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<ConnectedClientThread> mainConnections = new Vector();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<ConnectedClientTransferFileThread> fileTransferConnections = new Vector();

	  public ServerThread (Integer mainPort, Integer fileTransferPort, DestroySpaceServer destroySpaceServer) {
	    try {
	    	System.out.println("Starting Server at ports "+mainPort+" and "+fileTransferPort);
	    	this.destroySpaceServer = destroySpaceServer;
	      mainServer = new ServerSocket(mainPort);
	      fileTransferServer = new ServerSocket(fileTransferPort);
	      System.out.println("This server is running on IP: "+mainServer.getInetAddress().toString());
	      listener = new Thread(this);
	      listener.start();
	    }
	    catch(Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public void run() {
	    try {
	      while(true) {
	    	  //Adding Main
	        Socket client = mainServer.accept();
		      ConnectedClientThread c = new ConnectedClientThread(this, client);
	        c.start();
	        mainConnections.addElement(c);
	        //Adding fileTransfer
	        client = fileTransferServer.accept();
	        ConnectedClientTransferFileThread ctf = new ConnectedClientTransferFileThread(this, client,c);
	        c.setConnectedClientTransferThread(ctf);
	        ctf.start();
	        fileTransferConnections.addElement(ctf);

	      }
	    }
	    catch(Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public synchronized void broadcast(String message) {
	    for(int i=0; i < mainConnections.size(); i++) {
	      ((ConnectedClientThread)mainConnections.elementAt(i)).send(message);
	    }
	  }
	  
	  
	  //This function gets called every time a new Message arrives from a client
	  public synchronized void analyzeNewMessage(String message, ConnectedClientThread connectedClientThread) {

		  destroySpaceServer.analyzeNewMessage(message, connectedClientThread);
	  }

	  synchronized void send(String message, ConnectedClientThread connectedClientThread) {
		  connectedClientThread.send(message);
	}

	public synchronized void closeConnection(ConnectedClientThread client) {
	    	try {
	    		client.s.close();
	    	}
	    	catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	mainConnections.remove(client);
	  }
	
	public synchronized void closeConnection(ConnectedClientTransferFileThread client) {
    	try {
    		client.s.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	fileTransferConnections.remove(client);
  }

	public void stop() {
		for(int i=0; i < mainConnections.size(); i++) {
			try {
				((ConnectedClientThread)mainConnections.elementAt(i)).interrupt();
				((ConnectedClientThread)mainConnections.elementAt(i)).s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		for(int i=0; i < fileTransferConnections.size(); i++) {
			try {
				((ConnectedClientTransferFileThread)fileTransferConnections.elementAt(i)).interrupt();
				((ConnectedClientTransferFileThread)fileTransferConnections.elementAt(i)).s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		try {
			mainServer.close();
			fileTransferServer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Integer getMainConnections() {
		return mainConnections.size();
	}
	
	public Integer getFileTransferConnections() {
		return fileTransferConnections.size();
	}
	
}
