package servTCP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import p2p.ClientTCP;
import p2p.Dossier;

public class EnvoiReception {
	private Socket clientSocket = null;
	private PrintWriter envoi = null;
	private BufferedReader reception = null;
	private Dossier dossier;
	private int port = 4242;

	public EnvoiReception(String host, Dossier dossier) {
		
		try {
			clientSocket = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			envoi = new PrintWriter(clientSocket.getOutputStream(), true);
			reception = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void transfert(InputStream in, OutputStream out,
			boolean closeOnExit) throws IOException {
		byte buf[] = new byte[1024];

		int n;
		while ((n = in.read(buf)) != -1)
			out.write(buf, 0, n);

		if (closeOnExit) {
			in.close();
			out.close();
		}
	}
	
	

	public static void main(String[] args) {
		ClientTCP client = new ClientTCP(args[0], Integer.parseInt(args[1]));

		String reponse = client.envoyer("Hello");
		System.out.println(reponse);
	}

}
