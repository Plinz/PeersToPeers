package p2p;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Dossier {
	private HashMap<Integer, String> map;
	private File dossier;

	public Dossier(String lien) {
		this.dossier = new File(lien);
		remplirMap();
	}

	public void remplirMap() {
		File[] tmp = dossier.listFiles();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].isDirectory()) {
				remplirMap();
			} else {
				map.put(tmp[i].hashCode(), tmp[i].getName());
			}
		}
	}

	public ArrayList<String> getNoms() {
		ArrayList<String> noms = new ArrayList<String>();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			noms.add((String)pair.getValue());
		}
		return noms;
	}
}
