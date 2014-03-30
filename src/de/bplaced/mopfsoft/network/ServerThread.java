package de.bplaced.mopfsoft.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.gameLogic.GameController;
import de.bplaced.mopfsoft.message.ExecutableServer;
import de.bplaced.mopfsoft.message.Message;

public class ServerThread implements Runnable {
	private static ServerThread instance = null;
	
	Thread listener;
	ServerSocket mainServer, fileTransferServer;

	private ServerThread(Integer mainPort, Integer fileTransferPort) throws IOException{
			Log.info("Starting Server at ports " + mainPort + " and "
					+ fileTransferPort);
			mainServer = new ServerSocket(mainPort);
			fileTransferServer = new ServerSocket(fileTransferPort);
			Log.info("This server is running on IP: "
					+ mainServer.getInetAddress().toString());
			listener = new Thread(this);
			listener.start();
	}

	public void run() {
		try {
			while (true) {
				// Waiting for new Connection
				Socket clientSocket = mainServer.accept();
				Socket fileClientSocket = fileTransferServer.accept();

				ConnectedPlayer player = new ConnectedPlayer(
						clientSocket, fileClientSocket,
						GameController.getInstance().getConnectedPlayers()
								.size() == 0);
				GameController.getInstance().addConnectedPlayer(player);
				player.startThreads();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Broadcasts a message to all registered players
	 * 
	 * @param message
	 */
	
	public synchronized void broadcast(String message) {
		for (ConnectedPlayer player : GameController.getInstance()
				.getConnectedPlayers()) {
			player.getClient().send(message);
		}
	}

	public synchronized void send(String message, ConnectedPlayer player) {
		player.getClient().send(message);
	} 
	
	public synchronized void closeConnection(ConnectedPlayer player) {
		player.close();
	} 


	/** Stops the server and all his corresponding Sockets
	 * 
	 */
	public void stop() {
		try {
			for (ConnectedPlayer player : GameController.getInstance()
					.getConnectedPlayers()) {
				player.close();
			}
			mainServer.close();
			fileTransferServer.close();
		} catch (Exception e) {
			Log.error("[ERROR] Server did not shut down like it should!",e);
		}
	}
	
	private static void setInstance(ServerThread serverThread) {
		instance = serverThread;
	}
	
	public static void init(Integer mainPort, Integer fileTransferPort) throws IOException{
		setInstance(new ServerThread(mainPort, fileTransferPort));
	}

	public static ServerThread getInstance() {
		return instance;
	}
	
	/** This method gets called for every new clientside message. It processes the input and calls the correct functions
	 * @param message
	 * @param player
	 */
	public synchronized void analyseClientMessage(String message, ConnectedPlayer player) {
		
		System.out.println(player.getName()+" says: "+message);
		
		//Structure message
		Map<String,String> args= new HashMap<String,String>();
		
		String[] argArray;
		for (String split: message.split(":")) {
			argArray = split.split("=");
			args.put(argArray[0], argArray[1]);
		}
		
		analyseClientMessage(args, player);
		
	}
	private void analyseClientMessage(Map<String, String> args,
			ConnectedPlayer player) {
			((ExecutableServer)Message.getMessage(args)).execute(player);
		
	}
}
