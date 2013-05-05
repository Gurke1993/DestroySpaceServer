package de.bplaced.mopfsoft;

import java.util.ArrayList;
import java.util.List;

import de.bplaced.mopfsoft.entitys.Player;
import de.bplaced.mopfsoft.map.Map;

public class GameController {
	
	private GameLoop gameLoop;
	private DestroySpaceServer server;
	private Map map;
	private final List<ConnectedPlayer> connectedPlayers = new ArrayList<ConnectedPlayer>();
	private final List<Player> freePlayerEntitys;

	public GameController(String path, DestroySpaceServer server) {
		this.server = server;
		this.map = new Map(path);
		this.freePlayerEntitys = this.map.getPlayers();
	}
	
	/** Starts a new run of DestroySpace by enabling the game loop
	 * 
	 */
	public void startGame() {
		//Starts the game
		this.gameLoop = new GameLoop(this);
		gameLoop.start();
		System.out.println("Game started on the map "+map.getMapName()+" with "+connectedPlayers.size()+" playing!");
		
		server.serverThread.broadcast("action=loadupgame");
	}
	
	public Map getMap() {
		return this.map;
	}

	public GameLoop getGameLoop() {
		return this.gameLoop;
	}

	public DestroySpaceServer getServer() {
		return server;
	}

	/**
	 * Gets called by the serverThread after a new client has arrived. This
	 * method registers the player in the gamecontroller and matches a free
	 * Player entity to the ConnectedPlayer
	 * 
	 * @param connectedPlayer
	 */
	public void addConnectedPlayer(ConnectedPlayer connectedPlayer) {
		this.connectedPlayers.add(connectedPlayer);
		connectedPlayer.setPlayer(this.freePlayerEntitys.remove(0));
	}

	/** Returns all currently registered Players
	 * @return
	 */
	public List<ConnectedPlayer> getConnectedPlayers() {
		return this.connectedPlayers;
	}

	/** Returns a list of all connected players' names
	 * @return
	 */
	public List<String> getPlayerNames() {
		List<String> playerNames = new ArrayList<String>();
		if (connectedPlayers.isEmpty()) {
			playerNames.add("NONE");
			return playerNames;
		}
		for (ConnectedPlayer player: connectedPlayers) {
			playerNames.add(player.getName());
		}
		return playerNames;
	}
}
