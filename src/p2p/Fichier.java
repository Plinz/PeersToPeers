package p2p;

import java.io.File;

public class Fichier {
	private int hashcode;
	private String name;
	private String uuid;
	private String path;

	public Fichier(File fichier, String uuid, String path) {
		this.name = fichier.getName();
		this.hashcode = fichier.hashCode();
		this.uuid = uuid;
		this.path = path;
	}

	public Fichier(String name, Integer hashcode, String uuid) {
		this.name = name;
		this.hashcode = hashcode;
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

	public String toString() {
		String msg = this.getName() + "|" + this.getHashcode() + "|"
				+ this.getUuid() + "|";
		return msg;
	}

	public String toStringWithOutUuid() {
		String msg = this.getName() + "|" + this.getHashcode() + "|";
		return msg;
	}

	public int compareTo(Fichier f) {
		if (this.hashcode == f.getHashcode() && this.name == f.getName()
				&& this.uuid == f.getUuid()) {
			return 0;
		}
		return -1;
	}

	public File getFile(String path) {
		return new File(path);
	}

}
