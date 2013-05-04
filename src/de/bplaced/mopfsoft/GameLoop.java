package de.bplaced.mopfsoft;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import de.bplaced.mopfsoft.entitys.Entity;
import de.bplaced.mopfsoft.entitys.Player;

public class GameLoop extends Thread {

	@SuppressWarnings("unused")
	private GameController gameController;
	private Queue<ClientUpdate> clientUpdateQueue;

	public GameLoop(GameController gameController) {
		this.gameController = gameController;
	}

	// GameTick
	public void run() {

		ClientUpdate clientUpdate;
		String[] updateAsArray;
		List<String> gamefieldChanges = new ArrayList<String>();
		List<Entity> changedEntitys = new ArrayList<Entity>();

		while ((clientUpdate = clientUpdateQueue.poll()) != null) {
			// Process new client update

			Player issuer = this.gameController.getPlayer(clientUpdate
					.getIssuer());
			issuer.setInitialPosition();
			updateAsArray = clientUpdate.getTypeAndParameters();
			if (updateAsArray[0].equalsIgnoreCase("move")) {
				issuer.move(Integer.parseInt(updateAsArray[1]),
						Integer.parseInt(updateAsArray[2]));
			} else if (updateAsArray[0].equalsIgnoreCase("moveanduse")) {
				issuer.use(Integer.parseInt(updateAsArray[3]));
				issuer.move(Integer.parseInt(updateAsArray[1]),
						Integer.parseInt(updateAsArray[2]));
			} else if (updateAsArray[0].equalsIgnoreCase("use")) {
				issuer.use(Integer.parseInt(updateAsArray[1]));
			}

			// TODO Colissioncheck

			// TODO Follow up to Co check

			if (issuer.hasMoved() && !changedEntitys.contains(issuer)) {
				changedEntitys.add(issuer);
			}
		}

		// Broadcast gamefield changes
		for (String changeInGameField : gamefieldChanges) {
			// TODO
			gameController.getServer().serverThread.broadcast("");
		}

		// Broadcast Player positions
		for (Entity changedEntity : changedEntitys) {
			// TODO
			gameController.getServer().serverThread.broadcast("");
		}

	}

	/**
	 * Queues a new client update in a queue. This queue will be processed in
	 * the next gameLoop
	 * 
	 * @param message
	 * @param issuer
	 *            the thread referring to the connected Player
	 */
	public void queueClientUpdate(String message, ConnectedClientThread issuer) {
		System.out.println("Got new client update... adding to stack...");
		this.clientUpdateQueue.add(new ClientUpdate(issuer, message));

	}
}
