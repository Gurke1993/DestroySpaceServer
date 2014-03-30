package de.bplaced.mopfsoft.message;

public class GiveMapString extends Message{
	
	public GiveMapString(String line, boolean finished) {
		super("Class=GiveMapString:Line="+line+System.getProperty("line.separator")+":Finished="+finished);
	}


}
