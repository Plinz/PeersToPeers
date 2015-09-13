package servTCP;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
 
public class Serveur{
    static Socket sock;
    static ServerSocket sv;
    private File fichier;
    public Serveur(int port, File fichier) throws UnknownHostException, IOException {
    	this.sv = new ServerSocket(port);
    	this.sock = sv.accept();
    	this.fichier = fichier;
    	ThreadServeur ts = new ThreadServeur(sock, fichier);
    	ts.run();  
    	sock.close();
    	sv.close();
    }
 
}