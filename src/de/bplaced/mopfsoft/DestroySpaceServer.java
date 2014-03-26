package de.bplaced.mopfsoft;


import java.io.File;

import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.gameLogic.GameController;
import de.bplaced.mopfsoft.network.CommandLineListener;
import de.bplaced.mopfsoft.network.ServerThread;

import util.Util;

public class DestroySpaceServer {

	
	public DestroySpaceServer(String[] args) {

		String mainPortAsString = "27015";
		String fileTransferPortAsString = "27016";
		String path = "maps/DefaultMap.map";
		
		

		System.out.println("Setting up server...");
		for (int i = 0; i < args.length; i++) {
			if (args[i] == "-mainport") {
				if (Util.isInteger(args[i+1])) {
					mainPortAsString = args[i+1];
				}
			}
			
			if (args[i] == "-filetransferport") {
				if (Util.isInteger(args[i+1])) {
					fileTransferPortAsString = args[i+1];
				}
			}
			
			if (args[i] == "-map") {
					path = args[i+1];
			}
		}
		
		try {
			GameController.init(path);
			ServerThread.init(Integer.parseInt(mainPortAsString), Integer.parseInt(fileTransferPortAsString));
			CommandLineListener.init();
			
			Log.info("Server is set.");
			Log.info("type 'stop' to stop the server...");
		} catch (Exception e){
			Log.error("Could not set up Server at port "+"portAsString"+"... terminating...",e);
			System.exit(-1);
		}
		
		
	}
	public static void main(String[] args) {
		
		//set libarypaths
		System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		System.setProperty("net.java.games.input.librarypath", System.getProperty("org.lwjgl.librarypath"));	
				
		new DestroySpaceServer(args);
	}
	
	
	

}
