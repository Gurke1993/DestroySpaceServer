package de.bplaced.mopfsoft.message;

import java.util.Map;

import de.bplaced.mopfsoft.gameLogic.GameController;
import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class ClientDisconnect extends Message implements ExecutableServer{

	public ClientDisconnect(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		System.out.println("Client disconnecting... Sending updates...");
		player.close();
		
		for (ConnectedPlayer p: GameController.getInstance().getConnectedPlayers()) {
			ServerThread.getInstance().send(new LobbyPlayerChange(GameController.getInstance().getPlayerNames().size(), GameController.getInstance().getMap().getPlayers().size(), p.isHost(), GameController.getInstance().getPlayerNames())+"", p);	
		}
		
		ServerThread.getInstance().broadcast(new LobbyMapChange()+"");
	}

}
