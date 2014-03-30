package de.bplaced.mopfsoft.message;

import java.util.List;

public class LobbyPlayerChange extends Message{
	
	public LobbyPlayerChange(int count, int maxCount, boolean isHost, List <String> playerNames) {
		super("Class=LobbyPlayerChange:Count="+count+":MaxCount="+maxCount+":IsHost="+isHost+":Players="+getAsString(playerNames));
	}

	private static String getAsString(List<String> names) {
		String result = "";
		for (String name: names) {
			result += ","+name;
		}
		return result.substring(1);
	}


}
