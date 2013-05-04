package de.bplaced.mopfsoft;

import de.bplaced.mopfsoft.entitys.Entity;
import de.bplaced.mopfsoft.entitys.Player;
import de.bplaced.mopfsoft.map.Map;

public class GameController {
	
	private GameLoop gameLoop;
	private DestroySpaceServer server;
	private Map map;

	public GameController(String path, DestroySpaceServer server) {
		this.server = server;
		this.map = new Map(path);
	}
	
	/** Starts a new run of DestroySpace by enabling the game loop
	 * 
	 */
	public void startGame() {
		//Starts the game
		this.gameLoop = new GameLoop(this);
		gameLoop.start();
	}
	
	public Map getMap() {
		return this.map;
	}

	public GameLoop getGameLoop() {
		return this.gameLoop;
	}

	public Player getPlayer(ConnectedClientThread issuer) {
		// TODO Auto-generated method stub
		return null;
	}


	public Entity[] getEntitys() {
		// TODO Auto-generated method stub
		return null;
	}

	public DestroySpaceServer getServer() {
		return server;
	}
}
