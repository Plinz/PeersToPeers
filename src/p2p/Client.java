package p2p;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Client {

	private final static int _dgLength = 1500;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	private String uuid;
	private InetAddress address;
	private Integer port;
	public ArrayList<Fichier> fichiers;
	public ArrayList<Fichier> ownFichiers;
	public Hashtable<String, PeerInfo> peers;

	/**
	 * Constructeur d'un client
	 * 
	 * @param address
	 *            adresse du client
	 * @param port
	 *            numero de port du client
	 * @throws IOException
	 */
	public Client(InetAddress address, Integer port) {
		try {
			dgSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.address = address;
		this.port = port;
		this.fichiers = new ArrayList<Fichier>();
		this.ownFichiers = new ArrayList<Fichier>();
		this.peers = new Hashtable<String, PeerInfo>();
	}

	public int initialisation() throws IOException {
		this.register();
		if (this.receiveUuid()==0)
			return 0;
		this.initInformations();
		this.receiveList();
		return 1;
	}

	/**
	 * Methode permettant de recevoir un message du serveur
	 * 
	 * @return
	 * @throws IOException
	 */
	private String receive() {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		try {
			dgSocket.setSoTimeout(1000);
			try {
				dgSocket.receive(dgPacket);
			} catch (SocketTimeoutException e) {
				return null;
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}

		return new String(dgPacket.getData(), dgPacket.getOffset(),
				dgPacket.getLength());

	}

	/**
	 * Reception de la confirmation du serveur du quittage
	 * 
	 * @throws IOException
	 */
	private int receiveQuit() throws IOException {
		String reponse = this.receive();
		if (reponse == null)
			return 0;
		if (reponse.equals("OK")) {
			System.out.println("le serveur a bien quitter");
		} else if (reponse.equals("ERROR")) {
			System.out
					.println("le serveur n'a pas pu quitter suite a une erreur");
		}
		return 1;
	}

	/**
	 * Reception de l'identifiant unique
	 * 
	 * @throws IOException
	 */
	private int receiveUuid() throws IOException {
		String reponse = this.receive();
		if (reponse == null)
			return 0;
		String[] tmp = reponse.split(":");
		String uuid = tmp[1];
		this.uuid = uuid;
		return 1;
	}

	/**
	 * Methode permettant d'envoyer un message au serveur en UDP
	 * 
	 * @param msg
	 *            message a envoyer
	 * @param address
	 *            adresse du serveur
	 * @param port
	 *            numero de port du serveur
	 * @throws IOException
	 */
	private int send(String msg, InetAddress address, int port){
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		try {
			dgSocket.send(dgPacket);
		} catch (IOException e) {
			return 0;
		}
		return 1;
	}

	/**
	 * Demande de quittage du reseau au serveur
	 * 
	 * @throws IOException
	 */
	public int sendQuit() throws IOException {
		return this.send("QUIT:" + this.uuid, this.address, this.port);
	}

	/**
	 * Demande d'identifiant au serveur
	 * 
	 * @throws IOException
	 */
	private int register() throws IOException {
		return this.send("RGTR", this.address, this.port);
	}

	/**
	 * Demande des liste des pairs et des fichiers au serveur
	 * 
	 * @throws IOException
	 */
	private int initInformations() throws IOException {
		return this.send("RTRV:" + this.uuid, this.address, this.port);
	}

	/**
	 * Reception de la liste des pairs et des fichiers
	 * 
	 * @throws IOException
	 */
	private int receiveList() throws IOException {
		String reponse = this.receive();
		if (reponse == null)
			return 0;
		reponse = this.receive();
		if (reponse == null)
			return 0;
		String[] list = reponse.split("[|]");
		for (int i = 0; list.length > 2 && i < list.length; i += 3) {
			this.peers.put(list[i], new PeerInfo(list[i], list[i + 1], list[i + 2]));
		}
		
		String[] files = this.receive().split("[|]");
		for (int i = 0; files.length > 2 && i < files.length; i += 3) {
			this.fichiers.add(new Fichier(files[i], Integer
					.parseInt(files[i + 1]), files[i + 2]));
		}
		return 1;
	}

	/**
	 * Methode permettant d'ajouter des fichiers au reseau
	 * 
	 * @param files
	 *            les nouveaux fichiers a ajouter
	 * @throws IOException
	 */
	public int sendNewFiles(ArrayList<File> files) throws IOException {
		ArrayList<Fichier> outfiles = new ArrayList<Fichier>();
		for (File f : files) {
			outfiles.add(new Fichier(f, this.uuid));
		}
		String msg = "NEWFILE:"+this.uuid+":";
		for (Fichier g : outfiles) {
			this.ownFichiers.add(g);
			msg += g.toStringWithOutUuid();
		}
		msg.substring(0, msg.length() - 1);
		return this.send(msg, this.address, this.port);
	}

	/**
	 * Methode permettant de supprimer des fichiers du reseau
	 * 
	 * @param files
	 *            les nouveaux fichiers a envoyer
	 * @throws IOException
	 */
	public int sendRemoveFiles(ArrayList<File> files) throws IOException {
		ArrayList<Fichier> outfiles = new ArrayList<Fichier>();
		for (File f : files) {
			outfiles.add(new Fichier(f, this.uuid));
		}
		String msg = "RMVFILE:"+this.uuid+":";
		for (Fichier g : outfiles) {
			for (int i = 0; i < this.fichiers.size(); i++) {
				if (g.compareTo(this.fichiers.get(i)) == 0) {
					msg += g.toStringWithOutUuid();
					this.ownFichiers.remove(i);
				}
			}
		}
		msg.substring(0, msg.length() - 1);
		return this.send(msg, this.address, this.port);
	}

	/**
	 * Methode permettant la reception de changement de fichier Add/Remove ou
	 * ajout de pairs
	 * 
	 * @throws IOException
	 */
	private int receiveChange() throws IOException {
		String reponse = this.receive();
		if (reponse == null)
			return 0;
		String[] change = reponse.split(":");
		String[] list = change[1].split("[|]");
		switch (change[0]) {
		case "NEWFILE":
			for (int i = 0; i < list.length; i += 3) {
				this.fichiers.add(new Fichier(list[i], Integer
						.parseInt(list[i + 1]), list[i + 2]));
			}
			break;
		case "RMVFILE":
			for (int i = 0; i < list.length; i += 3) {
				Fichier f = new Fichier(list[i], Integer.parseInt(list[i + 1]),
						list[i + 2]);
				for (int j = 0; j < this.fichiers.size(); j++) {
					if (f.compareTo(this.fichiers.get(j)) == 0) {
						this.fichiers.remove(j);
					}
				}
			}
			break;
		case "NEWPEER":
			for (int i = 0; i < list.length; i += 3) {
				this.peers.put(list[i], new PeerInfo(list[i], list[i + 1], list[i + 2]));
			}
			break;
		case "RMVPEER":
			this.peers.remove(change[1]);
			break;
		}
		return 1;
	}
	

	/**
	 * Suite d'instruction lancant un client
	 * 
	 * @param args
	 *            String Adresse + int port
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Client client = new Client(InetAddress.getByName("localhost"), 5001);
		client.register();
		client.receiveUuid();
		client.initInformations();
		client.receiveList();
		client.receiveChange();
	}
}
