package de.bplaced.mopfsoft;

import java.util.Scanner;

public class CommandLineListener extends Thread{
	
	public void run() {
		String text;
		Scanner in = new Scanner(System.in);
		
		while (true) {
			text = in.nextLine(); 
			if (text.equals("stop")) {
				System.out.println("Stopping server...");
				System.exit(0);
			}
		}
	}

}
