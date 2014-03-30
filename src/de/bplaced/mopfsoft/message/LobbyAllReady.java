package de.bplaced.mopfsoft.message;

public class LobbyAllReady extends Message{
	
	public LobbyAllReady(String forWhat, boolean ready) {
		super("Class=LobbyAllReady:ForWhat="+forWhat+":Ready="+ready);
	}


}
