package p2p;

import java.io.File;

// TODO: Auto-generated Javadoc
/**
 * The Class Fichier.
 */
public class Fichier {
	
	/** The hashcode. */
	private int hashcode;
	
	/** The name. */
	private String name;
	
	/** The uuid. */
	private String uuid;
	
	/** The path. */
	private String path;

	/**
	 * Instantiates a new fichier.
	 *
	 * @param fichier the fichier
	 * @param uuid the uuid
	 * @param path the path
	 */
	public Fichier(File fichier, String uuid, String path) {
		this.name = fichier.getName();
		this.hashcode = fichier.hashCode();
		this.uuid = uuid;
		this.path = path;
	}

	/**
	 * Instantiates a new fichier.
	 *
	 * @param name the name
	 * @param hashcode the hashcode
	 * @param uuid the uuid
	 */
	public Fichier(String name, Integer hashcode, String uuid) {
		this.name = name;
		this.hashcode = hashcode;
		this.uuid = uuid;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Sets the uuid.
	 *
	 * @param uuid the new uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Gets the hashcode.
	 *
	 * @return the hashcode
	 */
	public int getHashcode() {
		return hashcode;
	}

	/**
	 * Sets the hashcode.
	 *
	 * @param hashcode the new hashcode
	 */
	public void setHashcode(int hashcode) {
		this.hashcode = hashcode;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String msg = this.getName() + "|" + this.getHashcode() + "|"
				+ this.getUuid();
		return msg;
	}

	/**
	 * To string with out uuid.
	 *
	 * @return the string
	 */
	public String toStringWithOutUuid() {
		String msg = this.getName() + "|" + this.getHashcode();
		return msg;
	}

	/**
	 * Compare to.
	 *
	 * @param f the f
	 * @return the int
	 */
	public int compareTo(Fichier f) {
		if (this.hashcode == f.getHashcode() && this.name == f.getName()
				&& this.uuid == f.getUuid()) {
			return 0;
		}
		return -1;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return new File(path);
	}

}
