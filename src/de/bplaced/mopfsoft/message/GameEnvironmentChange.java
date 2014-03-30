package de.bplaced.mopfsoft.message;

import org.newdawn.slick.geom.Shape;

import de.bplaced.mopfsoft.entitys.Entity;

public class GameEnvironmentChange extends Message {

	public GameEnvironmentChange(Shape shape, int bid, Entity issuer) {
		super("Class=GameEnvironmentChange:Polygon=");
		String pointString = "";
		for (Float floatV: shape.getPoints()) {
			pointString += ","+floatV;
		}
		pointString = pointString.substring(1);
		super.message = super.message+pointString;
	}

}
