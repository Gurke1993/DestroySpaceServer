package de.bplaced.mopfsoft;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.bplaced.mopfsoft.entitys.Player;
import de.bplaced.mopfsoft.gamechanges.EntityChange;
import de.bplaced.mopfsoft.gamechanges.GameChange;

public class GameLoop extends Thread {

	private static final long loopTime = 100;
	private GameController gameController;
	private Queue<ClientUpdate> clientUpdateQueue = new LinkedList<ClientUpdate>();

	public GameLoop(GameController gameController) {
		this.gameController = gameController;
	}

	// GameTick
	public void run() {
		while (true) {
		long startTime = System.currentTimeMillis();

		ClientUpdate clientUpdate;
		Map <String,String> args;
		List<GameChange> gameChanges = new ArrayList<GameChange>();

		
		while ((clientUpdate = clientUpdateQueue.poll()) != null) {
			// Process new client update

			Player issuer = clientUpdate.getIssuer().getPlayer();
			issuer.setInitialPosition();
			
			args = clientUpdate.getArgs();
			if (args.get("type").equals("move")) {
				issuer.move(args.get("direction"));
			} else if (args.get("type").equals("moveanduse")) {
				issuer.use(Integer.parseInt(args.get("tid")));
				issuer.move(args.get("direction"));
			} else if (args.get("type").equals("use")) {
				issuer.use(Integer.parseInt(args.get("tid")));
			}

			//Process gamefield collisions
			issuer.resolveWorldCollisions();
			
			//Process entity collisions
			issuer.resolveEntityCollisions();

			if (issuer.hasMoved() && !gameChanges.contains(issuer)) {
				gameChanges.add(new EntityChange(issuer, issuer));
			}
		}

		// Broadcast game changes
		for (GameChange gameChange : gameChanges) {
			System.out.println("Broadcasting...");
			gameController.getServer().serverThread.broadcast(gameChange.toString());
		}
		
		
		//Wait if to fast
		try {
			Thread.sleep(loopTime-(System.currentTimeMillis()-startTime));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}

	}

	/**
	 * Queues a new client update in a queue. This queue will be processed in
	 * the next gameLoop
	 * 
	 * @param args
	 * @param player
	 *            the thread referring to the connected Player
	 */
	public void queueClientUpdate(Map<String, String> args,
			ConnectedPlayer player) {
		System.out.println("Got new client update... adding to stack...");
		this.clientUpdateQueue.add(new ClientUpdate(player, args));
		
	}
}
