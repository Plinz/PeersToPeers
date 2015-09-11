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
	private ArrayList<String> nameFiles;
	private ArrayList<File> Files;

	public Client(InetAddress address, Integer port) throws IOException {
		dgSocket = new DatagramSocket();
		this.address = address;
		this.port = port;
	}

	private String receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return new String(dgPacket.getData(), dgPacket.getOffset(),
				dgPacket.getLength());

	}
	
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
	
	private void receiveUuid() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		String[] tmp = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength()).split(":");
		String uuid = tmp[1];
		this.uuid = uuid;
		System.out.println(uuid + "\n");
	}


	private void send(String msg, InetAddress address, int port)
			throws IOException {
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		dgSocket.send(dgPacket);
	}

	private void sendQuit()
			throws IOException {
		String msg = "QUIT:"+this.uuid;
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(this.address);
		dgPacket.setPort(this.port);
		dgSocket.send(dgPacket);
	}

	private void register()
			throws IOException {
		String msg = "RGTR";
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(this.address);
		dgPacket.setPort(this.port);
		dgSocket.send(dgPacket);
	}
	
	private void sendList()			
			throws IOException {
		String msg = "RTRV:"+this.uuid;
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(this.address);
		dgPacket.setPort(this.port);
		dgSocket.send(dgPacket);
	}

	private String[] receiveList()
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
		return l;
	}
	
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
	
	private ArrayList<Fichier> receiveFiles()
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
		return out;
	}
	

	
	public static void main(String[] args) throws IOException {
		Client client = new Client(InetAddress.getByName("localhost"), 5001);
		client.register();
		client.receiveUuid();
		client.sendList();
		client.receiveList();
		Client client2 = new Client(InetAddress.getByName("localhost"), 5001);
		client2.register();
		client2.receiveUuid();
		client2.sendList();
		client2.receiveList();
	}
}
