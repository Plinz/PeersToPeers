package servTCP;

import java.net.*;
import java.io.*;

public class Client {
	static Socket s;

	public Client(String ip, int port, File fichier) throws UnknownHostException, IOException {
		s = new Socket(ip, port);
		ThreadClient c = new ThreadClient(s, fichier);
		c.run();
		s.close();
	}

	
}