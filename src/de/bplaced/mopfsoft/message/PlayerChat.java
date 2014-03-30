package de.bplaced.mopfsoft.message;

import java.util.Map;

import de.bplaced.mopfsoft.gameLogic.GameController;
import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class PlayerChat extends Message implements ExecutableServer{

	public PlayerChat(Map<String, String> args) {
		super(args);
	}
	
	public PlayerChat(String message, String player) {
		super("Class=PlayerChat:Message="+message+":Player="+player);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		if (player.isHost()) {
			if (args.get("Message").startsWith("/")) {
				if (args.get("Message").startsWith("/changemap")) {
					GameController.getInstance().changeMap(args.get("Message").split(" ",2)[1]);
				}
				return;
			}
		}
		ServerThread.getInstance().broadcast(new PlayerChat(args.get("Message"),player.getName())+"");
	}

}
