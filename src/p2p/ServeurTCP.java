package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurTCP {
	private ServerSocket serveurSocket = null;
	
	public ServeurTCP(int port) {
		try {
			serveurSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void miseEnService() {
		Socket unClient = null;
		
		while (true ) {
			try {
				unClient = serveurSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
<<<<<<< HEAD
		
			realiseService(unClient);
		}
	}
	
	private void realiseService(Socket unClient) {
		PrintWriter envoi = null;
		BufferedReader reception = null;
		try {
	
			envoi = new PrintWriter(unClient.getOutputStream(), true);
			
			reception = new BufferedReader(
                    new InputStreamReader(unClient.getInputStream()));
	
			String message = reception.readLine();			
			envoi.println(message + " World !");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
=======
			new ThreadServeurTCP(unClient).run();
>>>>>>> f0db53f57f0eefab4275cd62bd4b6386d323a742
		}
	}

	
	public static void main(String[] args) {
		ServeurTCP serveur = new ServeurTCP(Integer.parseInt(args[0]));
		
		serveur.miseEnService();
	}

}
