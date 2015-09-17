package servTCP;

import java.net.*;
import java.io.*;

public class Client {
	static Socket s;

	public Client(String ip, int port, int hash, String pathname) throws UnknownHostException, IOException {
		s = new Socket(ip, port);
		ThreadClient c = new ThreadClient(s, hash, pathname);
		c.start();
		s.close();
	}

	
}