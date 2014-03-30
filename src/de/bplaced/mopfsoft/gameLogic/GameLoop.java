package de.bplaced.mopfsoft.gameLogic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


import de.bplaced.mopfsoft.entitys.Entity;
import de.bplaced.mopfsoft.entitys.Player;
import de.bplaced.mopfsoft.gamechanges.GameChange;
import de.bplaced.mopfsoft.message.GameChangeUtil;
import de.bplaced.mopfsoft.message.GameEntityChange;
import de.bplaced.mopfsoft.message.Message;
import de.bplaced.mopfsoft.network.ClientUpdate;
import de.bplaced.mopfsoft.network.ConnectedPlayer;
import de.bplaced.mopfsoft.network.ServerThread;

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
		List<Message> messages = new ArrayList<Message>();

		
		while ((clientUpdate = clientUpdateQueue.poll()) != null) {
			// Process new client update

			Player issuer = clientUpdate.getIssuer().getPlayer();
			issuer.setInitialPosition();
			
			args = clientUpdate.getArgs();
			if (args.get("type").equals("move")) {
				issuer.move(args.get("direction"));
			} else if (args.get("type").equals("jump")) {
				issuer.jump();
			} else if (args.get("type").equals("moveanduse")) {
				for (GameChange gameChange: issuer.useItem(Integer.parseInt(args.get("iid")))) {
					messages.add(GameChangeUtil.getGameChangeAsMessage(gameChange));
				}
				issuer.move(args.get("direction"));
			} else if (args.get("type").equals("use")) {
				for (GameChange gameChange: issuer.useItem(Integer.parseInt(args.get("iid")))) {
					messages.add(GameChangeUtil.getGameChangeAsMessage(gameChange));
				}
			}

			//Process gamefield collisions
			issuer.resolveWorldCollisions();
			
			//Process entity collisions
			issuer.resolveEntityCollisions();

			if (issuer.hasMoved() && !messages.contains(issuer)) {
				messages.add(new GameEntityChange(issuer, issuer));
			}
		}
		
		//Apply gravity
		for (Entity e: gameController.getMap().getEntitys()) {
			e.setInitialPosition();
			
			e.applyGravity();
			//Process gamefield collisions
			e.resolveWorldCollisions();
			
			//Process entity collisions
			e.resolveEntityCollisions();
			
			if (e.hasMoved() && !messages.contains(e)) {
				messages.add(new GameEntityChange(e, gameController.getMap().getWorld()));
			}
		}

		// Broadcast game changes
		for (Message message : messages) {
			ServerThread.getInstance().broadcast(message+"");
		}
		
		
		//Wait if to fast
		try {
			Thread.sleep(Math.max(0, loopTime-(System.currentTimeMillis()-startTime)));
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
		this.clientUpdateQueue.add(new ClientUpdate(player, args));
		
	}
}
