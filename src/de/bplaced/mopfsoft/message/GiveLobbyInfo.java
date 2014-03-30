package de.bplaced.mopfsoft.message;

import java.util.List;

import de.bplaced.mopfsoft.map.Map;

public class GiveLobbyInfo extends Message{
	
	public GiveLobbyInfo(Map map, boolean isHost, List<String> names) {
		super("Class=GiveLobbyInfo:MapName="+map.getMapName()+":MapDescription="+map.getMapDescription()+":MaxCount="+map.getPlayers().size()+":Count="+names.size()+":IsHost="+isHost+":Players="+getAsString(names));
	}
	
	private static String getAsString(List<String> names) {
		String result = "";
		for (String name: names) {
			result += ","+name;
		}
		return result.substring(1);
	}


}
