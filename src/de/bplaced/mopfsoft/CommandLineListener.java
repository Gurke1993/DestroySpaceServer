package de.bplaced.mopfsoft;

import java.util.Scanner;

public class CommandLineListener extends Thread{
	
	private DestroySpaceServer server;

	public CommandLineListener(DestroySpaceServer server) {
		this.server = server;
	}
	
	public void run() {
		String text;
		Scanner in = new Scanner(System.in);
		
		while (true) {
			text = in.nextLine(); 
			if (text.equals("stop")) {
				System.out.println("Stopping server...");
				System.exit(0);
			} else
			if (text.equals("start")) {
				System.out.println("Starting Game...");
				server.gameController.startGame();
			}
		}
	}

}
