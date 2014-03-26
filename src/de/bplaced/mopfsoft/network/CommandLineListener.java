package de.bplaced.mopfsoft.network;

import java.util.Scanner;

import org.newdawn.slick.util.Log;

import de.bplaced.mopfsoft.gameLogic.GameController;

public class CommandLineListener extends Thread{
	
	private static CommandLineListener instance = null;

	private CommandLineListener() {
	}
	
	public static void init() {
		if (instance == null) {
		setInstance(new CommandLineListener());
		instance.start();
		}
	}
	
	private static void setInstance(CommandLineListener commandLineListener) {
		instance = commandLineListener;
	}
	
	public static CommandLineListener getInstance() {
		return instance;
	}

	public void run() {
		String text;
		String [] args;
		Scanner in = new Scanner(System.in);
		
		while (true) {
			text = in.nextLine();
			args = text.split(" ");
			if (args[0].equals("stop")) {
				Log.info("Stopping server...");
				System.exit(0);
			} else
			if (args[0].equals("start")) {
				Log.info("Starting Game...");
				GameController.getInstance().startGame();
			} else
			if (args[0].equals("changemap")) {
				Log.info("Changing map to "+args[1]);
				GameController.getInstance().changeMap(args[1]);
			}
		}
	}

}
