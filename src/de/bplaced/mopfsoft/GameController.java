package de.bplaced.mopfsoft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import de.bplaced.mopfsoft.blocks.Block;


public class GameController {

	private String[] players = new String[0];
	private int maxPlayers;
	private String mapName;
	private Block[][] pitchArray;
	private String description;
	private File mapPreviewFile;
	
	public GameController(String path, int maxPlayers) {
		this.maxPlayers = maxPlayers;
		if (!loadMap(path)) {
			System.out.println("Could not load map from file ("+path+")!! Loading default.");
			loadMap("maps/DefaultMap.map");
		}
	}

	private boolean loadMap(String path) {
		
		File file = new File(path);
		mapPreviewFile = new File(path.split("\\n")[0]+".gif");
		//Falls defaultMap wird diese erstellt
		if (!file.exists() && file.equals(new File("maps/DefaultMap.map"))) {
			try {
				new File("maps").mkdirs();
				System.out.println("Creating new DefaultMap...");
				file.createNewFile();
				InputStream is = getClass().getResourceAsStream("/resources/other/DefaultMap.map");
				FileOutputStream os = new FileOutputStream(new File("maps/DefaultMap.map"));
				for(int read = 0; (read = is.read()) != -1;)
				    os.write(read);
					os.flush();
					
					
				System.out.println("Creating new DefaultMap preview...");
				is = getClass().getResourceAsStream("/resources/other/DefaultMap.gif");
				os = new FileOutputStream(mapPreviewFile);
				for(int read = 0; (read = is.read()) != -1;)
				    os.write(read);
					os.flush();
				
			} catch (IOException e) {
				System.out.println("[ERROR] Could not create default map!!");
				e.printStackTrace();
			}
		}
		
		//Falls keine Datei unter diesem Pfad
		if (!file.exists()) {
			return false;
			}
		
		
		//Start loading the map
		int i = 0,j = 0;
		try {
			System.out.println(file);
			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);
			String text;
			
			text = reader.readLine();
			String topLine = text.split(";")[0];
			int xMax = Integer.parseInt(topLine.split(":")[0]);
			int yMax = Integer.parseInt(topLine.split(":")[1]);
			pitchArray = new Block[xMax][yMax];
			this.mapName = file.getName().split("\\.")[0];
			System.out.println(mapName);
			this.description = topLine.split(":")[2];
			System.out.println(topLine);
			String pitch = text.split(";")[1];
			for (j = 0; j<yMax; j++) {
				for (i=0; i< xMax; i++) {
					pitchArray[i][j] = Block.getNewBlock(i,j,Integer.parseInt(pitch.split(":")[i+(j*xMax)]));
				}
				j++;
				
			}
			
			reader.close();
			fr.close();
			return true;
		} catch (Exception e) {
			System.out.println(i+"  "+j);
			e.printStackTrace();
			return false;
		}
	}
	
	public String[] getPlayerNames()  {
		return players;
	}
	
	public int getMaxPlayer() {
		return maxPlayers;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public void changeField(int x, int y, Block block) {
		pitchArray[x][y] = block;
	}
	
	public String getMapDescription() {
		return description;
	}

	public File getPreviewFile() {
		return mapPreviewFile;
	}
}
