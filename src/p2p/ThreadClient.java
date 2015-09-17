package p2p;

import ihm.FenetrePrincipale;

import java.util.Observable;

public class ThreadClient extends Observable implements Runnable {

	Client client;
	String reponse;

	public ThreadClient(Client client, FenetrePrincipale f) {
		this.addObserver(f);
		this.client = client;
	}

	@Override
	public void run() {
		while (true) {
			reponse = this.client.receive();
			String[] change = reponse.split("[:]");
			String[] list = change[2].split("[|]");
			String uuid = change[1];
			if (uuid.equals(this.client.uuid)) {
				switch (change[0]) {
				case "NEWFILE":
					for (int i = 0; i < list.length; i += 3) {
						client.otherFichiers.add(new Fichier(list[i], Integer
								.parseInt(list[i + 1]), uuid));
					}
					System.out.println("recu :" + this.client.uuid);
					System.out.println(reponse);
					this.setChanged();
					this.notifyObservers("file");
					break;
				case "RMVFILE":
					for (int i = 0; i < list.length; i += 3) {
						Fichier f = new Fichier(list[i],
								Integer.parseInt(list[i + 1]), uuid);
						for (int j = 0; j < client.otherFichiers.size(); j++) {
							if (f.compareTo(client.otherFichiers.get(j)) == 0) {
								client.otherFichiers.remove(j);
							}
						}
					}
					this.setChanged();
					this.notifyObservers("file");
					break;
				case "NEWPEER":
					for (int i = 0; i < list.length; i += 3) {
						client.peers.put(list[i], new PeerInfo(uuid, list[i],
								list[i + 1]));
					}
					this.setChanged();
					this.notifyObservers("peer");
					break;
				case "RMVPEER":
					client.peers.remove(change[1]);
					this.setChanged();
					this.notifyObservers("peer");
					break;
				}
			}
		}
	}

}
