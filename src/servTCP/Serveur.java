package servTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Serveur extends Thread {
	static Socket sock;
	static ServerSocket sv;
	public p2p.Client client;

	public Serveur(int port, p2p.Client c) throws UnknownHostException, IOException {
		this.sv = new ServerSocket(port);
		this.client = c;
	}

	public void run() {
		try {
			while (true) {
				System.out.println("serveur waiting " + sv.getLocalPort());
				this.sock = sv.accept();
				System.out.println("serveur accepting ");
				ThreadServeur ts = new ThreadServeur(sock, this.client);
				ts.start();
				sock.close();
				sv.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}