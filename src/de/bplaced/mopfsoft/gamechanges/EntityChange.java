package de.bplaced.mopfsoft.gamechanges;

import de.bplaced.mopfsoft.entitys.Entity;

public class EntityChange extends GameChange{

	private Entity entity;

	public EntityChange(Entity entity, Entity issuer) {
		super(issuer);
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return this.entity;
	}
	
	@Override
	public String toString() {
		return super.toString()+":entity="+entity;
	}
}
