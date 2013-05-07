package de.bplaced.mopfsoft;

import java.io.File;
import java.io.FileNotFoundException;
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
		try {
			this.map = new Map(new File(path));
		} catch (FileNotFoundException e) {
			Map.copyDefaultMap();
			try {
				this.map = new Map(new File("maps"+System.getProperty("file.separator")+"defaultMap.map"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}		}
		
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

	public void changeMap(String mapName) {
		changeMap(new File("maps"+System.getProperty("file.separator")+mapName+".map"));
	}

	private void changeMap(File file) {
		Map tempMap;
		try {
			tempMap = new Map(file);
			this.map = tempMap;
			server.serverThread.broadcast("action=mapchange");
		} catch (FileNotFoundException e) {
			System.out.println("Could not locate "+file.getName());
		}
	}
}
