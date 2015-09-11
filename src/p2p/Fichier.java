package p2p;

import java.io.File;

public class Fichier {
	private int hashcode;
	private String name;
	private String uuid;
	
	public Fichier(File fichier, String uuid){
		this.name = fichier.getName();
		this.hashcode  = fichier.hashCode();
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getHashcode() {
		return hashcode;
	}

	public void setHashcode(int hashcode) {
		this.hashcode = hashcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
