package de.bplaced.mopfsoft;


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
	
	
	public synchronized void analyzeNewMessage(String message, ConnectedClientThread connectedClientThread) {
		  if (message.split(":")[0].equals("0")) {
			  
			  message = message.split(":", 2)[1];
			  System.out.println("New Message: "+message);
			  
			  int index = Integer.parseInt(message.split(":", 2)[0]);
			  message = message.split(":", 2)[1];
			  
			  switch (index) {
			  
			  case 0: {
				  //Lobby info
				  System.out.println("Client asked for game info. Answering...");
				  String text = "1:0:1:"+gameController.getMap().getMapName()+":"+gameController.getMap().getMapDescription()+":"+gameController.getMap().getPlayerNames().length+":"+gameController.getMap().getPlayers().size();
				  for (String playerName: gameController.getMap().getPlayerNames()) {
					  text += ":"+playerName;
				  }
				  serverThread.send(text, connectedClientThread);
				  break;
			  }
			  
			  case 1: {
				  //INGAME UPDATES FROM CLIENTS
				  
				  this.gameController.getGameLoop().queueClientUpdate(message, connectedClientThread);
				  break;
			  }
			  
			  case -1: {
				  int index2 = Integer.parseInt(message.split(":", 3)[1]);
				  
				  switch (index2) {
				  
				  case 2: {
					  System.out.println("Sending fileinfo");
					  //File information
					  String text = "1:-1:0:"+gameController.getMap().getMapName()+".gif:"+gameController.getMap().getPreviewImageFile().length();
					  serverThread.send(text, connectedClientThread);
					  break;
				  }
				  
				  case 3: {
					  System.out.println("Starting transfer...");
					  //Start FileTransfer from MapPreview
					  connectedClientThread.getFileTransferThread().sendFile("maps/"+gameController.getMap().getMapName()+".gif");
					  break;
				  }
				  
				  }
			  }
			  
			  }
		  }
	}

}
