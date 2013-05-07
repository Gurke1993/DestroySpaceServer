package de.bplaced.mopfsoft;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {
	Thread listener;
	ServerSocket mainServer, fileTransferServer;
	DestroySpaceServer destroySpaceServer;

	public ServerThread(Integer mainPort, Integer fileTransferPort,
			DestroySpaceServer destroySpaceServer) {
		try {
			System.out.println("Starting Server at ports " + mainPort + " and "
					+ fileTransferPort);
			this.destroySpaceServer = destroySpaceServer;
			mainServer = new ServerSocket(mainPort);
			fileTransferServer = new ServerSocket(fileTransferPort);
			System.out.println("This server is running on IP: "
					+ mainServer.getInetAddress().toString());
			listener = new Thread(this);
			listener.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (true) {
				// Waiting for new Connection
				Socket clientSocket = mainServer.accept();
				Socket fileClientSocket = fileTransferServer.accept();

				destroySpaceServer.gameController
						.addConnectedPlayer(new ConnectedPlayer(this,
								clientSocket, fileClientSocket, destroySpaceServer.gameController.getConnectedPlayers().size() == 0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Broadcasts a message to all registered players
	 * 
	 * @param message
	 */
	public synchronized void broadcast(String message) {
		for (ConnectedPlayer player : destroySpaceServer.gameController
				.getConnectedPlayers()) {
			player.getClient().send(message);
		}
	}

	synchronized void send(String message, ConnectedPlayer player) {
		player.getClient().send(message);
	}

	public synchronized void closeConnection(ConnectedClientThread client) {
		try {
			client.s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void closeConnection(
			ConnectedClientTransferFileThread client) {
		try {
			client.s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Stops the server and all his corresponding Sockets
	 * 
	 */
	public void stop() {
		try {
			for (ConnectedPlayer player : destroySpaceServer.gameController
					.getConnectedPlayers()) {
				player.getClient().interrupt();
				player.getClient().s.close();

				player.getFileClient().interrupt();
				player.getFileClient().s.close();
			}
			mainServer.close();
			fileTransferServer.close();
		} catch (Exception e) {
			System.out
					.println("[ERROR] Server did not shut down like it should!");
			e.printStackTrace();
		}
	}
}
