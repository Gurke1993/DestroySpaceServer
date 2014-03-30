package de.bplaced.mopfsoft.message;

import java.util.Map;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class LobbyLoadGame extends Message implements ExecutableServer{

	public LobbyLoadGame(Map<String, String> args) {
		super(args);
	}
	
	public LobbyLoadGame() {
		super("Class=LobbyLoadGame");
	}

	@Override
	public void execute(ConnectedPlayer player) {
		Log.info("Client requested loadup. Broadcasting...");
		ServerThread.getInstance().broadcast(new LobbyLoadGame()+"");
	}

}
