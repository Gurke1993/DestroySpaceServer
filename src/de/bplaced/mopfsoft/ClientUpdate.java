package de.bplaced.mopfsoft;

import java.util.Map;

public class ClientUpdate {

	private final ConnectedPlayer issuer;
	private Map<String, String> args;

	public ClientUpdate(ConnectedPlayer issuer, Map<String,String> args) {
		this.issuer = issuer;
		this.args = args;
		}

	public String getType() {
		return args.get("type");
	}
	
	public Map<String,String> getArgs() {
		return this.args;
	}
	
	public ConnectedPlayer getIssuer() {
		return this.issuer;
	}
}
