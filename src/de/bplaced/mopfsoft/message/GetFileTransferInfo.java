package de.bplaced.mopfsoft.message;

import java.io.File;
import java.util.Map;

import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class GetFileTransferInfo extends Message implements ExecutableServer{

	public GetFileTransferInfo(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
				
		ServerThread.getInstance().send(new GiveFileTransferInfo(new File(args.get("Path")))+"", player);
	}

}
