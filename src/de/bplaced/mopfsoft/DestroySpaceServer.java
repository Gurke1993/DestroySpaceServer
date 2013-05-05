package de.bplaced.mopfsoft;


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
	public synchronized void analyzeClientMessage(String message, ConnectedPlayer player) {
		
		System.out.println("ClientSays:"+message);
		
		//Structure message
		Map<String,String> args= new HashMap<String,String>();
		
		String[] argArray;
		for (String split: message.split(":")) {
			argArray = split.split("=");
			args.put(argArray[0], argArray[1]);
		}
		
		analyzeClientMessage(args, player);
		
	}
	private void analyzeClientMessage(Map<String, String> args,
			ConnectedPlayer player) {

		
		String action = args.get("action");
		
		if (action.equals("clientupdate")) {
			this.gameController.getGameLoop().queueClientUpdate(args, player);
		} else
			
		if (action.equals("getlobbyinfo")) {
			System.out.println("Client asked for game info. Answering...");
			String answer = "action=givelobbyinfo"
					+":mapname="+gameController.getMap().getMapName()
					+":mapdescription="+gameController.getMap().getMapDescription()
					+":amountofplayers="+gameController.getPlayerNames().size()
					+":maxamountofplayers="+gameController.getMap().getPlayers().size()
					+":players=";
			
			List<String> playernames = gameController.getPlayerNames();
			for (int i = 0; i<playernames.size(); i++) {
				if (i!= 0) answer += ",";
				answer += playernames.get(i);
			}
			
			serverThread.send(answer, player);
		} else
			
		if (action.equals("getmappreviewinfo")) {

			System.out.println("Sending mappreviewinfo");
			
			String answer = "action=givemappreviewinfo"
					+":filename="+gameController.getMap().getMapName()+".gif"
					+":filelength="+gameController.getMap().getPreviewImageFile().length();
					
			serverThread.send(answer, player);
			
		} else 
			
		if (action.equals("starttransfer")) {
			
			System.out.println("Starting transfer...");
			
			player.getFileClient().sendFile("maps/"+gameController.getMap().getMapName()+".gif");
		}
		
	}

}
