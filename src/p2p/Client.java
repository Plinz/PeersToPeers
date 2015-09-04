package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	
	private final static int _dgLength = 50;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	private String uuid;
	private InetAddress address;
	private Integer port;

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

	public static void main(String[] args) throws IOException {
		Client client = new Client(InetAddress.getByName("localhost"), 5001);
		String msg = "RGTR";
		while (true) {
			client.send(msg, InetAddress.getByName("localhost"), 5001);
			System.out.println(client.receive());
		}
	}
}
