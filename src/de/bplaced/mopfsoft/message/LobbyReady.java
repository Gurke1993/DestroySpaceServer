package de.bplaced.mopfsoft.message;

import java.util.Map;

import de.bplaced.mopfsoft.gameLogic.GameController;
import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class LobbyReady extends Message implements ExecutableServer{

	public LobbyReady(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		player.setReady(args);
		
		boolean allready = true;
		for (ConnectedPlayer cp: GameController.getInstance().getConnectedPlayers()) {
			if (!cp.getReady(args)) {
				allready = false;
			}
		}
		
		ServerThread.getInstance().broadcast(new LobbyAllReady(args.get("ForWhat"), allready)+"");
		
		if (allready && args.get("ForWhat").equals("Start")) {
			GameController.getInstance().startGame();
		}
	}

}
