package de.bplaced.mopfsoft.message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

public class GetMapString extends Message implements ExecutableServer{

	public GetMapString(Map<String, String> args) {
		super(args);
	}

	@Override
	public void execute(ConnectedPlayer player) {
		Log.info("Sending mapstring...");
			
		
		String line;
		try {
			FileReader fr = new FileReader(new File(args.get("Path")));
			BufferedReader reader = new BufferedReader(fr);
			while ((line = reader.readLine()) != null) {
				ServerThread.getInstance().send(new GiveMapString(line, false)+"", player);
			}
			
			reader.close();
			fr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
		ServerThread.getInstance().send(new GiveMapString("X", true)+"", player);
	}

}
