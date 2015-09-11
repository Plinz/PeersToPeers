package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTCP {
    private Socket clientSocket = null;
    private PrintWriter envoi = null;
    private BufferedReader reception = null;
    
    public ClientTCP(String host, int port) {
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
			reception = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
    
    public String envoyer(String message) {
	    envoi.println(message);
		    
		    try {
				return reception.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		    return null;
	}
	public static void main(String[] args) {
		ClientTCP client = new ClientTCP(args[0], Integer.parseInt(args[1]));
		
		String reponse = client.envoyer("Hello");
		System.out.println(reponse);
	}

}
