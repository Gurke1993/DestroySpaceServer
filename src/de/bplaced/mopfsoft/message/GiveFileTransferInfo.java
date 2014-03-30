package de.bplaced.mopfsoft.message;

import java.io.File;

public class GiveFileTransferInfo extends Message{
	
	public GiveFileTransferInfo(File file) {
		super("Class=GiveFileTransferInfo:FileName="+file.getName()+":Length="+file.length()+":Path="+file.getPath());
	}


}
