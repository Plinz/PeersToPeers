package p2p;

import java.io.File;

public class Fichier {
	private int hashcode;
	private String name;
	
	public Fichier(File fichier){
		this.name = fichier.getName();
		this.hashcode  = fichier.hashCode();
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
