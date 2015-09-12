package p2p;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Client {
	
	private final static int _dgLength = 50;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	private String uuid;
	private InetAddress address;
	private Integer port;
	private ArrayList<Fichier> fichiers;
	private ArrayList<PeerInfo> peers;

	/**
	 * Constructeur d'un client
	 * @param address adresse du client
	 * @param port numero de port du client
	 * @throws IOException
	 */
	public Client(InetAddress address, Integer port) throws IOException {
		dgSocket = new DatagramSocket();
		this.address = address;
		this.port = port;
		this.fichiers = new ArrayList<Fichier>();
		this.peers = new ArrayList<PeerInfo>();
	}

	/**
	 * Methode permettant de recevoir un message du serveur
	 * @return
	 * @throws IOException
	 */
	private String receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return new String(dgPacket.getData(), dgPacket.getOffset(),
				dgPacket.getLength());

	}
	
	/**
	 * Reception de la confirmation du serveur du quittage
	 * @throws IOException
	 */
	private void receiveQuit() throws IOException {
		String reponse = this.receive();
		if (reponse.equals("OK")) {
			System.out.println("le serveur a bien quitter");
		} else if (reponse.equals("ERROR")) {
			System.out.println("le serveur n'a pas pu quitter suite a une erreur");

		}
	}
	
	/**
	 * Reception de l'identifiant unique
	 * @throws IOException
	 */
	private void receiveUuid() throws IOException {
		String[] tmp = this.receive().split(":");
		String uuid = tmp[1];
		this.uuid = uuid;
		System.out.println(uuid + "\n");
	}

	/**
	 * Methode permettant d'envoyer un message au serveur en UDP
	 * @param msg message a envoyer
	 * @param address adresse du serveur
	 * @param port numero de port du serveur
	 * @throws IOException
	 */
	private void send(String msg, InetAddress address, int port)
			throws IOException {
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		dgSocket.send(dgPacket);
	}

	/**
	 * Demande de quittage du reseau au serveur
	 * @throws IOException
	 */
	private void sendQuit()
			throws IOException {
		this.send("QUIT:"+this.uuid, this.address, this.port);
	}

	/**
	 * Demande d'identifiant au serveur
	 * @throws IOException
	 */
	private void register()
			throws IOException {
		this.send("RGTR", this.address, this.port);
	}
	
	/**
	 * Demande des liste des pairs et des fichiers au serveur
	 * @throws IOException
	 */
	private void initInformations()			
			throws IOException {
		this.send("RTRV:"+this.uuid, this.address, this.port);
	}
	
	/**
	 * Reception de la liste des pairs et des fichiers
	 * @throws IOException
	 */
	private void receiveList()
			throws IOException {
		this.receive();
		String [] list = this.receive().split("[|]");
		for (int i=0; list.length>2 && i<list.length; i+=3){
			this.peers.add(new PeerInfo(list[i],list[i+1],list[i+2]));
		}
		
		String [] files = this.receive().split("[|]");
		for (int i=0; files.length>2 && i<files.length; i+=3){
			this.fichiers.add(new Fichier(files[i], Integer.parseInt(files[i+1]), files[i+2]));
		}
	}
	
	/**
	 * Methode permettant d'ajouter des fichiers au reseau
	 * @param files les nouveaux fichiers a ajouter 
	 * @throws IOException
	 */
	private void sendNewFiles(ArrayList<File> files)
			throws IOException {
		ArrayList<Fichier> outfiles = new ArrayList<Fichier>();
		for (File f : files){
			outfiles.add(new Fichier(f, this.uuid));
		}
		String msg = "NEWFILE:";
		for (Fichier g: outfiles){
			this.fichiers.add(g);
			msg+=g.toStringWithOutUuid();
		}
		msg.substring(0, msg.length()-1);
		this.send(msg, this.address, this.port);
	}

	/**
	 * Methode permettant de supprimer des fichiers du reseau
	 * @param files les nouveaux fichiers a envoyer 
	 * @throws IOException
	 */
	private void sendRemoveFiles(ArrayList<File> files)
			throws IOException {
		ArrayList<Fichier> outfiles = new ArrayList<Fichier>();
		for (File f : files){
			outfiles.add(new Fichier(f, this.uuid));
		}
		String msg = "RMVFILE:";
		for (Fichier g: outfiles){
			for (int i=0; i<this.fichiers.size(); i++){
				if (g.compareTo(this.fichiers.get(i))==0){
					msg+=g.toStringWithOutUuid();
					this.fichiers.remove(i);
				}
			}
		}
		msg.substring(0, msg.length()-1);
		this.send(msg, this.address, this.port);
	}
	
	/** 
	 * Methode permettant la reception de changement de fichier Add/Remove ou ajout de pairs
	 * @throws IOException
	 */
	private void receiveChange()
			throws IOException {
		String[] change = this.receive().split(":");
		String[] list = change[1].split("[|]");
		switch (change[0]){
		case "NEWFILE":
			for (int i=0; i<list.length; i+=3){
				this.fichiers.add(new Fichier(list[i], Integer.parseInt(list[i+1]), list[i+2]));
			}
			break;
		case "RMVFILE":
			for (int i=0; i<list.length; i+=3){
				Fichier f = new Fichier(list[i], Integer.parseInt(list[i+1]), list[i+2]);
				for (int j=0; j<this.fichiers.size(); j++){
					if (f.compareTo(this.fichiers.get(j))==0){
						this.fichiers.remove(j);
					}
				}
			}
			break;
		case "NEWPEER":
			for (int i=0; i<list.length; i+=3){
				this.peers.add(new PeerInfo(list[i],list[i+1],list[i+2]));
			}
			break;
		case "RMVPEER":
			for (int i=0; i<this.peers.size(); i++){
				if (this.peers.get(i).equals(change[1]))
					this.peers.remove(i);
			}
			break;
		}
	}
	
	/**
	 * Suite d'instruction lancant un client
	 * @param args String Adresse + int port
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
