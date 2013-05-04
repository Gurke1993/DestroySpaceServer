package de.bplaced.mopfsoft;

public class ClientUpdate {

	private final ConnectedClientThread issuer;
	private final String[] updateAsArray;

	public ClientUpdate(ConnectedClientThread issuer, String updateAsString) {
		this.issuer = issuer;
		this.updateAsArray = updateAsString.split(":");
		}

	public String getType() {
		return this.updateAsArray[0];
	}
	
	public String[] getTypeAndParameters() {
		return this.updateAsArray;
	}
	
	public ConnectedClientThread getIssuer() {
		return this.issuer;
	}
}
