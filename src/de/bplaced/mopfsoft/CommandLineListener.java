package de.bplaced.mopfsoft;

import java.util.Scanner;

public class CommandLineListener extends Thread{
	
	private DestroySpaceServer server;

	public CommandLineListener(DestroySpaceServer server) {
		this.server = server;
	}
	
	public void run() {
		String text;
		String [] args;
		Scanner in = new Scanner(System.in);
		
		while (true) {
			text = in.nextLine();
			args = text.split(" ");
			if (args[0].equals("stop")) {
				System.out.println("Stopping server...");
				System.exit(0);
			} else
			if (args[0].equals("start")) {
				System.out.println("Starting Game...");
				server.gameController.startGame();
			} else
			if (args[0].equals("changemap")) {
				System.out.println("Changing map to "+args[1]);
				server.gameController.changeMap(args[1]);
			}
		}
	}

}
