package de.bplaced.mopfsoft;

import java.io.IOException;
import java.net.Socket;

import de.bplaced.mopfsoft.entitys.Player;

public class ConnectedPlayer {

	private final ConnectedClientTransferFileThread fileClient;
	private final ConnectedClientThread client;
	private Player player;
	private String name = "PLAYER";
	private boolean isHost;

	public ConnectedPlayer(ServerThread server, Socket clientSocket, Socket fileClientSocket, boolean isHost) throws IOException {
		this.isHost = isHost;
		
		this.client = new ConnectedClientThread(server, clientSocket, this);
		this.client.start();
		
		this.fileClient = new ConnectedClientTransferFileThread(server, fileClientSocket, this);
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
}
