package p2p;

import java.io.File;
import java.util.HashMap;

public class Dossier {
	private HashMap<Integer, String>  map;
	private File dossier;
	
	public Dossier(String lien){
		this.dossier = new File(lien);
		remplirMap();
	}
	
	public void remplirMap(){
		File[] tmp = dossier.listFiles();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].isDirectory()) {
				remplirMap();
			}
			else {
				map.put(tmp[i].hashCode(), tmp[i].getName());
			}
		}
	}
}
