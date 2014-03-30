package de.bplaced.mopfsoft.gameLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.entitys.Player;
import de.bplaced.mopfsoft.map.Map;
import de.bplaced.mopfsoft.map.MapToSmallException;
import de.bplaced.mopfsoft.message.LobbyMapChange;
import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class GameController {
	
	private static GameController instance = null;
	
	private GameLoop gameLoop;
	private Map map;
	private final List<ConnectedPlayer> connectedPlayers = new ArrayList<ConnectedPlayer>();
	private List<Player> freePlayerEntitys;
	
	public static void init(String mapPath) {
		setInstance(new GameController(mapPath));
	}

	private static void setInstance(GameController gameController) {
		instance = gameController;
	}
	
	public static GameController getInstance() {
		return instance;
	}

	private GameController(String mapPath) {
		try {
			this.map = new Map(new File(mapPath));
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
		Log.info("Game started on the map "+map.getMapName()+" with "+connectedPlayers.size()+" playing!");
	}
	
	public Map getMap() {
		return this.map;
	}

	public GameLoop getGameLoop() {
		return this.gameLoop;
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
			
			if (tempMap.getPlayers().size() < connectedPlayers.size()) {
				throw new MapToSmallException();
			}
			
			this.map = tempMap;
			this.freePlayerEntitys = this.map.getPlayers();
			
			for (ConnectedPlayer player: connectedPlayers ) {
				player.setPlayer(freePlayerEntitys.remove(0));
			}
			
			ServerThread.getInstance().broadcast(new LobbyMapChange()+"");
		} catch (FileNotFoundException e) {
			Log.error("Could not locate "+file.getName());
		} catch (MapToSmallException e1) {
			Log.error("Map has to few player spots!");
		}
	}

	public void removeConnectedPlayer(ConnectedPlayer player) {
		this.freePlayerEntitys.add(player.getPlayer());
		this.connectedPlayers.remove(player);
		
	}
}
