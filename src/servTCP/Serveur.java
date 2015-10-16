package servTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

// TODO: Auto-generated Javadoc
/**
 * The Class Serveur.
 */
public class Serveur extends Thread {
	
	/** The sock. */
	static Socket sock;
	
	/** The sv. */
	static ServerSocket sv;
	
	/** The client. */
	public p2p.Client client;

	/**
	 * Instantiates a new serveur.
	 *
	 * @param port the port
	 * @param c the c
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Serveur(int port, p2p.Client c) throws UnknownHostException, IOException {
		this.sv = new ServerSocket(port);
		this.client = c;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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