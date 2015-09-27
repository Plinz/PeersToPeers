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
			if (change.length>1){
				String uuid = change[1];
				switch (change[0]) {
				case "NEWFILE":
					for (int i = 0; i < Integer.parseInt(change[2]); i++) {
						String[] list = this.client.receive().split("[|]");
						client.otherFichiers.add(new Fichier(list[1], Integer
								.parseInt(list[2]), uuid));
					}
					this.setChanged();
					this.notifyObservers("file");
					break;
		
				case "RMVFILE":
					for (int i = 0; i < Integer.parseInt(change[2]); i++) {
						String[] list = this.client.receive().split("[|]");
						Fichier f = new Fichier(list[1], Integer.parseInt(list[2]),
								uuid);
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
					client.peers.put(uuid, new PeerInfo(uuid, change[2], change[3]));
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
