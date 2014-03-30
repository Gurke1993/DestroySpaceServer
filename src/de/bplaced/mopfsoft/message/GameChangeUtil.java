package de.bplaced.mopfsoft.message;

import de.bplaced.mopfsoft.gamechanges.EntityChange;
import de.bplaced.mopfsoft.gamechanges.EnvironmentChange;
import de.bplaced.mopfsoft.gamechanges.GameChange;

public class GameChangeUtil {
	
	public static Message getGameChangeAsMessage(GameChange gameChange) {
	
		if (gameChange instanceof EntityChange) {
			return new GameEntityChange(((EntityChange) gameChange).getEntity(), gameChange.getIssuer());
		} else
		if (gameChange instanceof EnvironmentChange) {
			return new GameEnvironmentChange(((EnvironmentChange) gameChange).getShape(), ((EnvironmentChange) gameChange).getBid(), gameChange.getIssuer());
		} else
		
		return null;
		
	}
}
