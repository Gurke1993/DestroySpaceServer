package de.bplaced.mopfsoft.message;

import java.util.Map;

import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class CloseServer extends Message implements ExecutableServer{

	public CloseServer(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		ServerThread.getInstance().stop();
	}

}
