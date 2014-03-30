package de.bplaced.mopfsoft.message;

import java.util.Map;

import de.bplaced.mopfsoft.gameLogic.GameController;
import de.bplaced.mopfsoft.network.ConnectedPlayer;

public class GamePlayerInteraction extends Message implements ExecutableServer{

	public GamePlayerInteraction(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		GameController.getInstance().getGameLoop().queueClientUpdate(args, player);
	}

}
