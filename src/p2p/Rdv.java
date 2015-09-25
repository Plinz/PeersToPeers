package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Rdv {

	/**
	 * Port du Serveur
	 */
	// private static final int _pSrv = 4242;

	/**
	 * Longeur d'un buffer
	 */
	private static final int _bfLength = 1500;

	/**
	 * Table des pairs connectes sur le reseau
	 */
	private Hashtable<String, PeerInfo> peers;

	/**
	 * Initialisation du buffer
	 */
	private byte[] buffer = null;

	/**
	 * La Socket permettant de communiquer avec les pairs
	 */
	private DatagramSocket dgSocket;

	/**
	 * Liste des fichiers sur le reseau
	 */
	private ArrayList<Fichier> fichiers;

	/**
	 * Constructeur du serveur
	 * 
	 * @throws IOException
	 */
	public Rdv(String addr, String port) throws IOException {
		dgSocket = new DatagramSocket(Integer.parseInt(port));
		peers = new Hashtable<String, PeerInfo>();
		fichiers = new ArrayList<Fichier>();
	}

	/**
	 * Methode qui ecoute sur le port et qui redirige les message et renvoie la
	 * reponse
	 * 
	 * @throws IOException
	 */
	private void serve() throws IOException {
		while (true) {
			DatagramPacket dgPacket = receive();
			String msg = new String(dgPacket.getData(), dgPacket.getOffset(),
					dgPacket.getLength());
			System.out.println(msg);
			InetAddress address = dgPacket.getAddress();
			int port = dgPacket.getPort();

			if (msg.equals("RGTR")) {
				String answer = register(address, port);
				send(address, port, answer);
			} else {
				String[] words = msg.split("[:]");
				if (words.length >= 2) {
					switch (words[0]) {
					case "RTRV":
						this.retrieve(words[1].trim(), address, port);
						break;
					case "NEWFILE":
						this.notifyPeersAddFiles(words[2], words[1]);
						break;
					case "RMVFILE":
						this.notifyPeersRemoveFiles(words[2], words[1]);
						break;
					case "QUIT":
						String answer = quit(words[1].trim());
						send(address, port, answer);
						break;
					}
				}
			}
		}
	}

	/**
	 * Methode permettant de recevoir les messages
	 * 
	 * @return un DatagramPacket contenant le message
	 * @throws IOException
	 */
	private DatagramPacket receive() throws IOException {
		buffer = new byte[_bfLength];
		DatagramPacket dgPacket = new DatagramPacket(buffer, _bfLength);
		dgSocket.receive(dgPacket);

		return dgPacket;
	}

	/**
	 * Methode permettant d'envoyer un message
	 * 
	 * @param address
	 *            L'adresse du client
	 * @param port
	 *            Le numero de port du client
	 * @param msg
	 *            Le message a envoyer au client
	 * @throws IOException
	 */
	private void send(InetAddress address, int port, String msg)
			throws IOException {
		buffer = msg.getBytes();
		DatagramPacket dgPacket = new DatagramPacket(buffer, 0, buffer.length,
				address, port);
		dgSocket.send(dgPacket);
	}

	/**
	 * Renvoie la liste des autres pairs (identifiant)
	 * 
	 * @param uuid
	 *            Identifiant personnel du client
	 * @return String des uuid|adresse|port de chaque client
	 * @throws IOException 
	 */
	private void retrieve(String uuid, InetAddress address, int port) throws IOException {
		System.out.println("ReTRieVing");
		String msg = "NEWPEER:" + (peers.size() - 1);
		this.send(address, port, msg);
		if (peers.containsKey(uuid)) {
			Enumeration<PeerInfo> p = peers.elements();
			while (p.hasMoreElements()) {
				PeerInfo peer = p.nextElement();
				if (!peer.getUUID().toString().equals(uuid)) {
					msg = peer.toString();
					this.send(address, port, msg);
				}
			}
		}
		msg = "NEWFILE:" + this.fichiers.size();
		this.send(address, port, msg);
		for (int i = 0; i < this.fichiers.size(); i++){
			msg = this.fichiers.get(i).toString();
			send(address, port, msg);
		}
	}

	/**
	 * Methode qui notifie les autres pairs d'ajout de nouveau fichiers sur le
	 * reseau
	 * 
	 * @param files
	 *            les nouveaux fichiers
	 * @param uuid
	 *            l'identifiant du pair possedant les nouveau fichiers
	 * @throws IOException
	 */
	private void notifyPeersAddFiles(String tmpnb, String uuid)
			throws IOException {
		int nb = Integer.parseInt(tmpnb);
		ArrayList<Fichier> fich = new ArrayList<Fichier>();
		for (int i = 0; i < nb; i++) {
			DatagramPacket dgPacket = this.receive();
			String msg = new String(dgPacket.getData(), dgPacket.getOffset(),
					dgPacket.getLength());
			System.out.println(msg);
			String[] list = msg.split("[|]");
			Fichier f = new Fichier(list[0], Integer.parseInt(list[1]), uuid);
			this.fichiers.add(f);
			fich.add(f);
		}
		Enumeration<PeerInfo> p = peers.elements();
		while (p.hasMoreElements()) {
			PeerInfo peer = p.nextElement();
			System.out.println(peer.getUUID().toString());
			System.out.println(uuid);
			if (!peer.getUUID().toString().equals(uuid)) {
				System.out.println("ok");
				try {
					String msg = "NEWFILE:" + uuid + ":" + fich.size();
					send(peer.getAddress(), peer.getPort(), msg);
					for (int i = 0; i < fich.size(); i++) {
						msg = i + "|" + fich.get(i).toStringWithOutUuid();
						send(peer.getAddress(), peer.getPort(), msg);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Methode qui notifie les autres pairs de la suppression fichiers sur le
	 * reseau et supprime les fichiers sur le serveur.
	 * 
	 * @param files
	 *            les fichiers supprimer
	 * @param uuid
	 *            l'identifiant du pair possedant les fichier qui sont supprimer
	 * @throws IOException
	 */
	private void notifyPeersRemoveFiles(String tmpnb, String uuid)
			throws IOException {

		int nb = Integer.parseInt(tmpnb);
		ArrayList<Fichier> fich = new ArrayList<Fichier>();
		for (int i = 0; i < nb; i++) {
			DatagramPacket dgPacket = this.receive();
			String msg = new String(dgPacket.getData(), dgPacket.getOffset(),
					dgPacket.getLength());
			System.out.println(msg);
			String[] list = msg.split("[|]");
			Fichier f = new Fichier(list[0], Integer.parseInt(list[1]), uuid);
			for (int j = 0; j < this.fichiers.size(); j++) {
				if (this.fichiers.get(j).compareTo(f) == 0) {
					fich.add(f);
					this.fichiers.remove(j);
				}
			}
		}

		Enumeration<PeerInfo> p = peers.elements();
		while (p.hasMoreElements()) {
			PeerInfo peer = p.nextElement();
			if (!peer.getUUID().toString().equals(uuid)) {
				try {
					String msg = "RMVFILE:" + uuid + ":" + fich.size();
					send(peer.getAddress(), peer.getPort(), msg);
					for (int i = 0; i < fich.size(); i++) {
						msg = i + "|" + fich.get(i).toStringWithOutUuid();
						send(peer.getAddress(), peer.getPort(), msg);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Methode notifiant les pairs qu'un pair s'est retirer du reseau
	 * 
	 * @param uuid
	 */
	private void notifyPeersRemovePeer(String uuid) {
		this.peers.remove(uuid);
		Enumeration<PeerInfo> p = peers.elements();
		while (p.hasMoreElements()) {
			PeerInfo peer = p.nextElement();
			try {
				send(peer.getAddress(), peer.getPort(), "RMVPEER" + uuid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Methode enlevant un pair de la liste
	 * 
	 * @param uuid
	 *            indentifiant du client
	 * @return OK s'il a bien ete enleve ERROR sinon
	 */
	private String quit(String uuid) {
		System.out.println("QUITing");
		ArrayList<Fichier> fileRemoving = new ArrayList<Fichier>();
		for (int i = 0; i < this.fichiers.size(); i++) {
			if (this.fichiers.get(i).getUuid().equals(uuid))
				fileRemoving.add(this.fichiers.get(i));
		}
		Enumeration<PeerInfo> p = peers.elements();
		while (p.hasMoreElements()) {
			PeerInfo peer = p.nextElement();
			if (!peer.getUUID().toString().equals(uuid)) {
				try {
					String msg = "RMVFILE:" + uuid + ":" + fileRemoving.size();
					send(peer.getAddress(), peer.getPort(), msg);
					for (int i = 0; i < fileRemoving.size(); i++) {
						msg = i + "|"
								+ fileRemoving.get(i).toStringWithOutUuid();
						send(peer.getAddress(), peer.getPort(), msg);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		this.notifyPeersRemovePeer(uuid);
		if (peers.remove(uuid) != null) {
			return "OK";
		}
		return "ERROR";
	}

	/**
	 * Methode enregistrant un nouveau pair
	 * 
	 * @param address
	 *            Adresse du client
	 * @param port
	 *            Numero de port du client
	 * @return OK:uuid Identifiant du client
	 * @throws IOException
	 */
	private String register(InetAddress address, int port) throws IOException {
		System.out.println("ReGisTeRing");

		PeerInfo p = new PeerInfo(address, port);
		notifyAddPeers(p);
		peers.put(p.getUUID().toString(), p);

		StringBuilder sb = new StringBuilder();
		sb.append("OK:");
		sb.append(p.getUUID());

		return sb.toString();
	}

	/**
	 * Methode permettant de notifier aux clients l'ajout d'un nouveau client
	 * 
	 * @param newPeer
	 *            Le nouveau pair
	 * @throws IOException
	 */
	private void notifyAddPeers(PeerInfo newPeer) throws IOException {
		String msg = "NEWPEER:" + newPeer.toString();
		Enumeration<PeerInfo> p = peers.elements();
		while (p.hasMoreElements()) {
			PeerInfo peer = p.nextElement();
			send(peer.getAddress(), peer.getPort(), msg);
		}
	}

	public static void main(String[] args) throws IOException {
		new Rdv("localhost", "5000").serve();
	}

}
