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
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		String reponse = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
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
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		String[] tmp = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength()).split(":");
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
		String msg = "QUIT:"+this.uuid;
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(this.address);
		dgPacket.setPort(this.port);
		dgSocket.send(dgPacket);
	}

	/**
	 * Demande d'identifiant au serveur
	 * @throws IOException
	 */
	private void register()
			throws IOException {
		String msg = "RGTR";
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(this.address);
		dgPacket.setPort(this.port);
		dgSocket.send(dgPacket);
	}
	
	/**
	 * Demande des liste des pairs et des fichiers au serveur
	 * @throws IOException
	 */
	private void initInformations()			
			throws IOException {
		String msg = "RTRV:"+this.uuid;
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(this.address);
		dgPacket.setPort(this.port);
		dgSocket.send(dgPacket);
	}

	/**
	 * Reception de la liste des pairs et des fichiers
	 * @throws IOException
	 */
	private void receiveList()
			throws IOException {
		byte[] buf = new byte[_dgLength];
		dgPacket = new DatagramPacket(buf, _dgLength);
		dgSocket.receive(dgPacket);
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		String list = new String(dgPacket.getData(), dgPacket.getOffset(),
				dgPacket.getLength());
		String [] l = list.split("|");
		for (int i=0; i<l.length; i++){
			String[] temp = l[i].split(":");
			this.peers.add(new PeerInfo(temp[0],temp[1],temp[2]));
		}
		byte[] buff = new byte[_dgLength];
		dgPacket = new DatagramPacket(buff, _dgLength);
		dgSocket.receive(dgPacket);
		String files = new String(dgPacket.getData(), dgPacket.getOffset(),
				dgPacket.getLength());
		String [] temp = files.split("|");
		for (int i=0; i<temp.length; i+=3){
			this.fichiers.add(new Fichier(temp[i], Integer.parseInt(temp[i+1]), temp[i+2]));
		}
	}
	
	/** A voir
	 * Methode permettant d'envoyer des nouveaux fichiers au serveur
	 * @param files les nouveaux fichiers a envoyer 
	 * @throws IOException
	 */
	private void sendFiles(ArrayList<File> files)
			throws IOException {
		ArrayList<Fichier> outfiles = new ArrayList<Fichier>();
		for (File f : files){
			outfiles.add(new Fichier(f, this.uuid));
		}
		String msg = "FILE:";
		for (Fichier g: outfiles){
			msg+=g.getName()+"|"+g.getHashcode()+"|";
		}
		msg+="END";
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(this.address);
		dgPacket.setPort(this.port);
		dgSocket.send(dgPacket);
	}
	
	/** A voir
	 * Methode permettant la reception des nouveaux fichiers mis sur le serveur
	 * @throws IOException
	 */
	private void receiveFiles()
			throws IOException {
		byte[] buf = new byte[_dgLength];
		dgPacket = new DatagramPacket(buf, _dgLength);
		dgSocket.receive(dgPacket);
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		String list = new String(dgPacket.getData(), dgPacket.getOffset(),
				dgPacket.getLength());
		list = list.substring(5);
		String [] files = list.split("|");
		ArrayList<Fichier> out= new ArrayList<Fichier>();
		for (int i=0; i<files.length-1; i+=3){
			out.add(new Fichier(files[i], Integer.parseInt(files[i+1]), files[i+2]));
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

	}
}
