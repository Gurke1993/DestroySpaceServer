package de.bplaced.mopfsoft.gamechanges;

import de.bplaced.mopfsoft.entitys.Entity;

public abstract class GameChange {

	private Entity issuer;

	public GameChange(Entity issuer) {
		this.issuer = issuer;
	}
	
	public Entity getIssuer() {
		return this.issuer;
	}
	
	@Override
	public String toString() {
		return "action=gamechange:issuer="+issuer.toString()+":";
	}
}
