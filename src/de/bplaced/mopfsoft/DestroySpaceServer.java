package de.bplaced.mopfsoft;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Util;

public class DestroySpaceServer {

	ServerThread serverThread;
	GameController gameController;
	private CommandLineListener commandLineListener;
	/**
	 * @param args
	 */
	
	
	
	public DestroySpaceServer(String[] args) {

		String mainPortAsString = "27015";
		String fileTransferPortAsString = "27016";
		String path = "maps/DefaultMap.map";
		
		

		System.out.println("Setting up server...");
		for (int i = 0; i < args.length; i++) {
			if (args[i] == "-mainport") {
				if (Util.isInteger(args[i+1])) {
					mainPortAsString = args[i+1];
				}
			}
			
			if (args[i] == "-filetransferport") {
				if (Util.isInteger(args[i+1])) {
					fileTransferPortAsString = args[i+1];
				}
			}
			
			if (args[i] == "-map") {
					path = args[i+1];
			}
		}
		
		try {
			gameController = new GameController(path,this);
			serverThread = new ServerThread(Integer.parseInt(mainPortAsString), Integer.parseInt(fileTransferPortAsString), this);
			System.out.println("Server is set.");
			this.commandLineListener = new CommandLineListener(this);
			commandLineListener.start();
			System.out.println("type 'stop' to stop the server...");
		} catch (Exception e){
			System.out.println("Could not set up Server at port "+"portAsString"+"... terminating...");
			e.printStackTrace();
			try {
				wait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.exit(-1);
		}
		
		
	}
	public static void main(String[] args) {
		new DestroySpaceServer(args);
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
			this.gameController.getGameLoop().queueClientUpdate(args, player);
		} else
			
		if (action.equals("playerchat")) {
			if (player.isHost()) {
				if (args.get("message").startsWith("/")) {
					if (args.get("message").startsWith("/changemap")) {
						gameController.changeMap(args.get("message").split(" ",2)[1]);
					}
					return;
				}
			}
			serverThread.broadcast("action=playerchat:message="+args.get("message")+":player="+player.getName());
		} else
			
		if (action.equals("getlobbyinfo")) {
			
			player.setName(args.get("playername"));
			System.out.println("Client asked for game info. Answering...");
			String answer = "action=givelobbyinfo"
					+":mapname="+gameController.getMap().getMapName()
					+":mapdescription="+gameController.getMap().getMapDescription()
					+":amountofplayers="+gameController.getPlayerNames().size()
					+":maxamountofplayers="+gameController.getMap().getPlayers().size()
					+":ishost="+player.isHost()
					+":players="; //END OF MESSAGE
			
			List<String> playernames = gameController.getPlayerNames();
			for (int i = 0; i<playernames.size(); i++) {
				if (i!= 0) answer += ",";
				answer += playernames.get(i);
			}
			
			serverThread.send(answer, player);
			sendPlayerChangeMessage();
		} else
			
		if (action.equals("getfiletransferinfo")) {

			System.out.println("Sending filetransferinfo");
			
			String answer = "action=givefiletransferinfo"
					+":filename="+args.get("filename")
					+":filelength="+(new File(args.get("path"))).length()
					+":path="+args.get("path");
					
			serverThread.send(answer, player);
			
		} else 
			
		if (action.equals("readyto")) {
				System.out.println("Client is ready");
			player.setReady(args);
			
			boolean allready = true;
			for (ConnectedPlayer cp: gameController.getConnectedPlayers()) {
				if (!cp.getReady(args)) {
					allready = false;
				}
			}
			serverThread.broadcast(
					"action=allplayersready"+
					":type="+args.get("type")+
					":areready="+allready);
			
			if (allready && args.get("type").equals("start")) {
				gameController.startGame();
			}
			
		} else 
			
		if (action.equals("starttransfer")) {
			
			System.out.println("Starting transfer...");
			
			player.getFileClient().sendFile(args.get("path"));
		} else
			
		if (action.equals("closeserver")) {
			serverThread.stop();
		} else 
			
		if (action.equals("loadupgame")) {
			System.out.println("Client requested loadup. Broadcasting...");
			serverThread.broadcast("action=loadupgame");
		} else 
			
		if (action.equals("clientdisconnect")) {
			System.out.println("Client disconnecting... Sending updates...");
			player.close();
			sendPlayerChangeMessage();
			
			serverThread.broadcast("action=mapchange");
		} else 
			
		if (action.equals("getMapString")) {
		System.out.println("Sending mapstring...");
			
		
		String line;
		try {
			FileReader fr = new FileReader(new File(args.get("path")));
			BufferedReader reader = new BufferedReader(fr);
			while ((line = reader.readLine()) != null) {
				serverThread.send("action=givemapstring:partofstring="+line+System.getProperty("line.separator")+":finished=false", player);
			}
			
			reader.close();
			fr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
			serverThread.send("action=givemapstring:finished=true", player);
		}
			
			
		
	}
	
	public void sendPlayerChangeMessage() {
		for (ConnectedPlayer player: gameController.getConnectedPlayers()) {
		String answer = "action=playerchange"
				+":amountofplayers="+gameController.getPlayerNames().size()
				+":maxamountofplayers="+gameController.getMap().getPlayers().size()
				+":ishost="+player.isHost()
				+":players="; //END OF MESSAGE
		
		List<String> playernames = gameController.getPlayerNames();
		for (int i = 0; i<playernames.size(); i++) {
			if (i!= 0) answer += ",";
			answer += playernames.get(i);
		}
		serverThread.send(answer, player);
	}
	}

}
