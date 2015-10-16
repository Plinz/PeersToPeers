package p2p;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class Dossier.
 */
public class Dossier {
	
	/** The map. */
	private HashMap<Integer, String> map;
	
	/** The dossier. */
	private File dossier;

	/**
	 * Instantiates a new dossier.
	 *
	 * @param lien the lien
	 */
	public Dossier(String lien) {
		this.dossier = new File(lien);
		remplirMap(dossier);
	}

	/**
	 * Remplir map.
	 *
	 * @param file the file
	 */
	public void remplirMap(File file) {
		File[] tmp = file.listFiles();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].isDirectory()) {
				remplirMap(tmp[i]);
			} else {
				map.put(tmp[i].hashCode(), tmp[i].getName());
			}
		}
	}

	/**
	 * Gets the noms.
	 *
	 * @return the noms
	 */
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
