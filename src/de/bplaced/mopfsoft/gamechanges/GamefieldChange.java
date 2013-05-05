package de.bplaced.mopfsoft.gamechanges;

import de.bplaced.mopfsoft.entitys.Entity;

public class GamefieldChange extends GameChange{

	
	private final int x;
	private final int y;
	private final int id;

	public GamefieldChange(int x, int y, int id, Entity issuer) {
		super(issuer);
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return super.toString()+":x="+x+":y="+y+":id="+id;
	}
}
