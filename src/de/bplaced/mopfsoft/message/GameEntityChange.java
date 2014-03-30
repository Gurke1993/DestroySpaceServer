package de.bplaced.mopfsoft.message;


import de.bplaced.mopfsoft.entitys.Entity;

public class GameEntityChange extends Message{

	public GameEntityChange(Entity entity, Entity issuer) {
		super("Class=GameEntityChange:Entity="+entity+":Issuer="+issuer);
	}

}
