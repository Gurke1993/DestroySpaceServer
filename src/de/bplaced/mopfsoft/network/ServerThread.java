package de.bplaced.mopfsoft.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.gameLogic.GameController;

public class ServerThread implements Runnable {
	private static ServerThread instance = null;
	
	Thread listener;
	ServerSocket mainServer, fileTransferServer;

	private ServerThread(Integer mainPort, Integer fileTransferPort) {
		try {
			Log.info("Starting Server at ports " + mainPort + " and "
					+ fileTransferPort);
			mainServer = new ServerSocket(mainPort);
			fileTransferServer = new ServerSocket(fileTransferPort);
			Log.info("This server is running on IP: "
					+ mainServer.getInetAddress().toString());
			listener = new Thread(this);
			listener.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	synchronized void send(String message, ConnectedPlayer player) {
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
	
	public static void init(Integer mainPort, Integer fileTransferPort) {
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

		
		String action = args.get("action");
		
		if (action.equals("clientupdate")) {
			GameController.getInstance().getGameLoop().queueClientUpdate(args, player);
		} else
			
		if (action.equals("playerchat")) {
			if (player.isHost()) {
				if (args.get("message").startsWith("/")) {
					if (args.get("message").startsWith("/changemap")) {
						GameController.getInstance().changeMap(args.get("message").split(" ",2)[1]);
					}
					return;
				}
			}
			ServerThread.getInstance().broadcast("action=playerchat:message="+args.get("message")+":player="+player.getName());
		} else
			
		if (action.equals("getlobbyinfo")) {
			
			player.setName(args.get("playername"));
			System.out.println("Client asked for game info. Answering...");
			String answer = "action=givelobbyinfo"
					+":mapname="+GameController.getInstance().getMap().getMapName()
					+":mapdescription="+GameController.getInstance().getMap().getMapDescription()
					+":amountofplayers="+GameController.getInstance().getPlayerNames().size()
					+":maxamountofplayers="+GameController.getInstance().getMap().getPlayers().size()
					+":ishost="+player.isHost()
					+":players="; //END OF MESSAGE
			
			List<String> playernames = GameController.getInstance().getPlayerNames();
			for (int i = 0; i<playernames.size(); i++) {
				if (i!= 0) answer += ",";
				answer += playernames.get(i);
			}
			
			ServerThread.getInstance().send(answer, player);
			sendPlayerChangeMessage();
		} else
			
		if (action.equals("getfiletransferinfo")) {

			System.out.println("Sending filetransferinfo");
			
			String answer = "action=givefiletransferinfo"
					+":filename="+args.get("filename")
					+":filelength="+(new File(args.get("path"))).length()
					+":path="+args.get("path");
					
			ServerThread.getInstance().send(answer, player);
			
		} else 
			
		if (action.equals("readyto")) {
				System.out.println("Client is ready");
			player.setReady(args);
			
			boolean allready = true;
			for (ConnectedPlayer cp: GameController.getInstance().getConnectedPlayers()) {
				if (!cp.getReady(args)) {
					allready = false;
				}
			}
			
			ServerThread.getInstance().broadcast(
					"action=allplayersready"+
					":type="+args.get("type")+
					":areready="+allready);
			
			if (allready && args.get("type").equals("start")) {
				GameController.getInstance().startGame();
			}
			
		} else 
			
		if (action.equals("starttransfer")) {
			
			System.out.println("Starting transfer...");
			
			player.getFileClient().sendFile(args.get("path"));
		} else
			
		if (action.equals("closeserver")) {
			ServerThread.getInstance().stop();
		} else 
			
		if (action.equals("loadupgame")) {
			System.out.println("Client requested loadup. Broadcasting...");
			ServerThread.getInstance().broadcast("action=loadupgame");
		} else 
			
		if (action.equals("clientdisconnect")) {
			System.out.println("Client disconnecting... Sending updates...");
			player.close();
			sendPlayerChangeMessage();
			
			ServerThread.getInstance().broadcast("action=mapchange");
		} else 
			
		if (action.equals("getMapString")) {
		System.out.println("Sending mapstring...");
			
		
		String line;
		try {
			FileReader fr = new FileReader(new File(args.get("path")));
			BufferedReader reader = new BufferedReader(fr);
			while ((line = reader.readLine()) != null) {
				ServerThread.getInstance().send("action=givemapstring:partofstring="+line+System.getProperty("line.separator")+":finished=false", player);
			}
			
			reader.close();
			fr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
		ServerThread.getInstance().send("action=givemapstring:finished=true", player);
		}
			
			
		
	}
	
	public void sendPlayerChangeMessage() {
		for (ConnectedPlayer player: GameController.getInstance().getConnectedPlayers()) {
		String answer = "action=playerchange"
				+":amountofplayers="+GameController.getInstance().getPlayerNames().size()
				+":maxamountofplayers="+GameController.getInstance().getMap().getPlayers().size()
				+":ishost="+player.isHost()
				+":players="; //END OF MESSAGE
		
		List<String> playernames = GameController.getInstance().getPlayerNames();
		for (int i = 0; i<playernames.size(); i++) {
			if (i!= 0) answer += ",";
			answer += playernames.get(i);
		}
		ServerThread.getInstance().send(answer, player);
	}
	}
}
