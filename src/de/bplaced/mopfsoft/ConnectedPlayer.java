package de.bplaced.mopfsoft;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import de.bplaced.mopfsoft.entitys.Player;

public class ConnectedPlayer {

	private final ConnectedClientTransferFileThread fileClient;
	private final ConnectedClientThread client;
	private Player player;
	private String name = "PLAYER";
	private boolean isHost;
	private ServerThread server;
	private final Map<String,Boolean> readyMap= new HashMap<String,Boolean>();

	public ConnectedPlayer(ServerThread server, Socket clientSocket, Socket fileClientSocket, boolean isHost) throws IOException {
		this.server = server;
		this.isHost = isHost;
		
		this.client = new ConnectedClientThread(server, clientSocket, this);

		
		this.fileClient = new ConnectedClientTransferFileThread(server, fileClientSocket, this);

		
	}
	
	public void startThreads() {
		this.client.start();
		this.fileClient.start();
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public ConnectedClientThread getClient() {
		return this.client;
	}

	public ConnectedClientTransferFileThread getFileClient() {
		return this.fileClient;
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isHost() {
		return this.isHost;
	}

	public void close() {
		System.out.println("Closing connection to player...");
		server.destroySpaceServer.gameController.removeConnectedPlayer(this);
		try {
			getClient().s.close();
			getFileClient().s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setReady(Map<String, String> args) {
		readyMap.put(args.get("type"), Boolean.parseBoolean(args.get("isready")));
	}

	public boolean getReady(Map<String, String> args) {
		Boolean result = readyMap.get(args.get("type"));
		if (result != null && result)
			return true;
		return false;
	}
}
