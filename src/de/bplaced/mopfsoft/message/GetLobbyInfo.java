package de.bplaced.mopfsoft.message;

import java.util.Map;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.gameLogic.GameController;
import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class GetLobbyInfo extends Message implements ExecutableServer{

	public GetLobbyInfo(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		{
		player.setName(args.get("PlayerName"));
		Log.debug("Client asked for game info. Answering...");

		ServerThread.getInstance().send(new GiveLobbyInfo(GameController.getInstance().getMap(), player.isHost(), GameController.getInstance().getPlayerNames())+"", player);
		}
		
		for (ConnectedPlayer p: GameController.getInstance().getConnectedPlayers()) {
			ServerThread.getInstance().send(new LobbyPlayerChange(GameController.getInstance().getPlayerNames().size(), GameController.getInstance().getMap().getPlayers().size(), p.isHost(), GameController.getInstance().getPlayerNames())+"", p);	
		}
	}

}
