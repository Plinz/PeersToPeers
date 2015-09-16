package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Rdv {
	
	/**
	 * Port du Serveur
	 */
//	private static final  int _pSrv = 4242;


	/**
	 * Longeur d'un buffer
	 */
	private static final int _bfLength = 1500;
	
	/**
	 * Table des pairs connectes sur le reseau
	 */
	private Hashtable<String, PeerInfo> peers;
	
	/**
	 * Initialisation du buffer
	 */
	private byte[] buffer = null;
	
	/**
	 * La Socket permettant de communiquer avec les pairs
	 */
	private DatagramSocket dgSocket;
	
	/**
	 * Liste des fichiers sur le reseau
	 */
	private ArrayList<Fichier> fichiers;
	
	/**
	 * Constructeur du serveur 
	 * @throws IOException
	 */
    public Rdv(String addr, String port) throws IOException {
    	dgSocket = new DatagramSocket(Integer.parseInt(port));
    	peers = new Hashtable<String, PeerInfo>();
    	fichiers = new ArrayList<Fichier>();
    }
    
    /**
     * Methode qui ecoute sur le port et qui redirige les message et renvoie la reponse
     * @throws IOException
     */
    private void serve() throws IOException {    	
    	while (true) {
    		DatagramPacket dgPacket = receive();
    		
    		String msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
    		System.out.println(msg);
    		InetAddress address = dgPacket.getAddress();
    		int port = dgPacket.getPort();
    		String answer = null;

    		
    		if ( msg.equals("RGTR") ) {
    			answer = register(address, port);
        		send(address, port, answer);
    		}
    		else {
    			String[] words = msg.split("[:]");
    			if ( words.length >= 2 ) {
    				switch (words[0]) {
    					case "RTRV" :
    						answer = retrieve(words[1].trim());
        					if ( ! answer.equals("ERROR") ) {
        						StringBuilder sizeAnswer = new StringBuilder("OK:");
        						sizeAnswer.append(answer.length());
        						System.out.println(sizeAnswer);
        						send(address, port, sizeAnswer.toString());
        					}
        		    		send(address, port, answer);
        		    		String fich="";
        		    		for (int i=0; i<this.fichiers.size(); i++) 		    			
        		    			fich+= this.fichiers.get(i).toString();
        		    		if (fich.length()>0)
        		    			fich.substring(0, fich.length()-1);
        		    		send(address, port, fich);
    						break;
    					case "NEWFILE" :
    						this.notifyPeersAddFiles(words[2], words[1]);
    						break;
    					case "RMVFILE" :
    						this.notifyPeersRemoveFiles(words[2], words[1]);
    						break;
    					case "QUIT" :
    						answer = quit(words[1].trim());
        		    		send(address, port, answer);
    						break;
    				}
    			}
    		}
    	}
    }
    
    /**
     * Methode permettant de recevoir les messages
     * @return un DatagramPacket contenant le message
     * @throws IOException
     */
    private DatagramPacket receive() throws IOException {
		buffer = new byte[_bfLength];
		DatagramPacket dgPacket = new DatagramPacket(buffer, _bfLength);
		dgSocket.receive(dgPacket);
		
		return dgPacket;
    }
    
    /**
     * Methode permettant d'envoyer un message
     * @param address L'adresse du client
     * @param port Le numero de port du client
     * @param msg Le message a envoyer au client
     * @throws IOException
     */
    private void send(InetAddress address, int port, String msg) throws IOException {
		buffer = msg.getBytes();
		DatagramPacket dgPacket = new DatagramPacket(buffer, 0, buffer.length, address, port);			
		dgSocket.send(dgPacket);
    }
    
    /**
     * Renvoie la liste des autres pairs (identifiant)
     * @param uuid Identifiant personnel du client
     * @return String des uuid|adresse|port de chaque client
     */
    private String retrieve(String uuid) {
    	System.out.println("ReTRieVing");
    	
    	if ( peers.containsKey(uuid) ) {
    		StringBuilder sb = new StringBuilder();
    		Enumeration<PeerInfo> p = peers.elements();
    		while ( p.hasMoreElements() ) {
    			PeerInfo peer = p.nextElement();
    			if ( ! peer.getUUID().toString().equals(uuid) ) {
    				sb.append(peer.toString()); 
    				sb.append("|");
    			}
    		}
    		if ( sb.length() != 0 ) {
    			sb.deleteCharAt(sb.length()-1);
    		}
    		
        	return sb.toString(); 
		}
    	return "ERROR";
	}
    
    /**
     * Methode qui notifie les autres pairs d'ajout de nouveau fichiers sur le reseau 
     * @param files les nouveaux fichiers
     * @param uuid l'identifiant du pair possedant les nouveau fichiers
     */
	private void notifyPeersAddFiles (String files, String uuid){
		String [] temp = files.split("[|]");
		ArrayList<Fichier> fich = new ArrayList<Fichier>();
		for (int i=0; i<temp.length; i+=2){
			fich.add(new Fichier(temp[i], Integer.parseInt(temp[i+1]), uuid));
			this.fichiers.add(new Fichier(temp[i], Integer.parseInt(temp[i+1]), uuid));
		}

		String msg = "NEWFILE:";
		for (int i=0; i<fich.size(); i++){
			msg+=fich.get(i).toString();
		}
		msg.substring(0, msg.length()-1);
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			if (! peer.getUUID().equals(uuid)){
				try {
					send(peer.getAddress(), peer.getPort(), msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
     * Methode qui notifie les autres pairs de la suppression fichiers sur le reseau et supprime les 
     * fichiers sur le serveur.
	 * @param files les fichiers supprimer
	 * @param uuid l'identifiant du pair possedant les fichier qui sont supprimer
	 */
	private void notifyPeersRemoveFiles (String files, String uuid){
		String [] temp = files.split("|");
		ArrayList<Fichier> fichiers = new ArrayList<Fichier>();
		for (int i=0; i<temp.length; i+=2){
			Fichier f = new Fichier(temp[i], Integer.parseInt(temp[i+1]), uuid);
			for (int j=0; j<this.fichiers.size(); j++){
				if (this.fichiers.get(j).compareTo(f)==0){
					fichiers.add(f);
					this.fichiers.remove(j);
				}
			}
		}
		String msg = "RMVFILE:";
		for (int i=0; i<fichiers.size(); i++){
			msg+=fichiers.get(i).toString();
		}
		msg.substring(0, msg.length()-1);
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			if (! peer.getUUID().equals(uuid)){
				try {
					send(peer.getAddress(), peer.getPort(), msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
	/**
	 * Methode notifiant les pairs qu'un pair s'est retirer du reseau
	 * @param uuid
	 */
	private void notifyPeersRemovePeer(String uuid){
		this.peers.remove(uuid);
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
				try {
					send(peer.getAddress(), peer.getPort(), "RMVPEER"+uuid);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Methode enlevant un pair de la liste
	 * @param uuid indentifiant du client
	 * @return OK s'il a bien ete enleve ERROR sinon
	 */
	private String quit(String uuid) {
    	System.out.println("QUITing");
    	String fileRemoving = "";
    	for (int i=0; i<this.fichiers.size(); i++){
    		if (this.fichiers.get(i).getUuid().equals(uuid))
    			fileRemoving+= this.fichiers.get(i).toString();
    	}
    	if (this.fichiers.size()!=0)
    		fileRemoving.substring(0, fileRemoving.length()-1);
    	this.notifyPeersRemoveFiles(fileRemoving, uuid);
    	this.notifyPeersRemovePeer(uuid);
    	if ( peers.remove(uuid) != null ) {
    		return "OK";
    	}
    	return "ERROR";
	}

	/**
	 * Methode enregistrant un nouveau pair
	 * @param address Adresse du client
	 * @param port Numero de port du client
	 * @return OK:uuid Identifiant du client
	 * @throws IOException
	 */
	private String register(InetAddress address, int port) throws IOException {
    	System.out.println("ReGisTeRing");
    	
		PeerInfo p = new PeerInfo(address, port);
		notifyAddPeers(p);
		peers.put(p.getUUID().toString(), p);
		
		StringBuilder sb = new StringBuilder();
		sb.append("OK:");
		sb.append(p.getUUID());

    	return sb.toString();    
    }
    
	/**
	 * Methode permettant de notifier aux clients l'ajout d'un nouveau client
	 * @param newPeer Le nouveau pair
	 * @throws IOException
	 */
	private void notifyAddPeers(PeerInfo newPeer) throws IOException {
		String msg = "NEWPEER:" + newPeer.toString();
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			send(peer.getAddress(), peer.getPort(), msg);
		}	
	}

	public static void main(String[] args) throws IOException {
		new Rdv("localhost", "5000").serve();
	}

}
