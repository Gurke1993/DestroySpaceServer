package de.bplaced.mopfsoft.message;

import java.util.Map;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.network.ConnectedPlayer;

public class StartTransfer extends Message implements ExecutableServer{

	public StartTransfer(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		Log.info("Starting transfer of "+args.get("Path")+"...");
		player.getFileClient().sendFile(args.get("Path"));
	}

}
