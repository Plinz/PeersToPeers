package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadServeurTCP implements Runnable {

	Socket client;

	public ThreadServeurTCP(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {

		PrintWriter envoi = null;
		BufferedReader reception = null;
		try {
			envoi = new PrintWriter(client.getOutputStream(), true);

			reception = new BufferedReader(new InputStreamReader(
					client.getInputStream()));

			String message = reception.readLine();
			envoi.println(message + " World !");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
