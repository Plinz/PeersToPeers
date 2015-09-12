package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;

public class Rdv {
	
	/**
	 * Port du Serveur
	 */
	private static final  int _pSrv = 5001;
	
	/**
	 * Longeur d'un buffer
	 */
	private static final int _bfLength = 41;
	
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
    public Rdv() throws IOException {
    	dgSocket = new DatagramSocket(_pSrv);
    	peers = new Hashtable<String, PeerInfo>();
    }
    
    /**
     * Methode qui ecoute sur le port et qui redirige les message et renvoie la reponse
     * @throws IOException
     */
    private void serve() throws IOException {    	
    	while (true) {
    		DatagramPacket dgPacket = receive();
    		
    		String msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
    		InetAddress address = dgPacket.getAddress();
    		int port = dgPacket.getPort();
    		String answer = null;

    		if ( msg.equals("RGTR") ) {
    			answer = register(address, port);
    		}
    		else {
    			String[] words = msg.split(":");
    			if ( words.length == 2 ) {
    				if ( words[0].equals("RTRV") ) {
    					answer = retrieve(words[1].trim());
    					if ( ! answer.equals("ERROR") ) {
    						StringBuilder sizeAnswer = new StringBuilder("OK:");
    						sizeAnswer.append(answer.length());
    						System.out.println(sizeAnswer);
    						send(address, port, sizeAnswer.toString());
    					}	
    				} else {
    					if ( words[0].equals("FILE") ) {
    						answer =  miseAJour(words[1].trim(), words[2].trim());
    					}
    					else {
    						if ( words[0].equals("QUIT") ) {
        						answer = quit(words[1].trim());
        					}
        					else {
        						System.out.println("ERROR");
        						answer = "ERROR";
        					}
    					}
    				}
    			}
    		}
    		send(address, port, answer);
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
     * Methode mettant a jour la liste des fichier et qui la renvoie à tous
     * @param uuid Du client faisant une modification
     * @param sfiles Concatenation des information des fichier à ajouter
     * @return OK si la mise c'est bien passé ERROR sinon
     */
/*
    private String miseAJour(String uuid, String sfiles) {
    	System.out.println("Mise A Jour");
    	if (peers.containsKey(uuid)){
    		String [] files = sfiles.split("|");
    		fichiers = new ArrayList<Fichier>();
    		for (int i=0; i<files.length-1; i+=2){
    			fichiers.add(new Fichier(files[i], Integer.parseInt(files[i+1]), uuid));
    		}
    		Enumeration<PeerInfo> p = peers.elements();
    		while ( p.hasMoreElements() ) {
    			PeerInfo peer = p.nextElement();
    			try {
					this.send(peer.getAddress(), peer.getPort(), this.getMessageFichier());
				} catch (IOException e) {
					e.printStackTrace();
					return "ERROR";
				}
    		}
    		return "OK";
    	}
    	return "ERROR";
	}
*/

    /**
     * Methode qui notifie les autres pairs d'ajout de nouveau fichiers sur le reseau 
     * @param files les nouveaux fichiers
     * @param uuid l'identifiant du pair possedant les nouveau fichiers
     */
	private void notifyPeersAddFiles (ArrayList<Fichier> files, Integer uuid){
		String msg = "PUSHNEWFILES:";
		for (int i=0; i<files.size(); i++){
			msg+=files.get(i).toString();
		}
		msg+="END";
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
     * Methode qui notifie les autres pairs de la suppression fichiers sur le reseau 
	 * @param files les fichiers supprimer
	 * @param uuid l'identifiant du pair possedant les fichier qui sont supprimer
	 */
	private void notifyPeersRemoveFiles (ArrayList<Fichier> files, Integer uuid){
		String msg = "PUSHREMOVEFILES:";
		for (int i=0; i<files.size(); i++){
			msg+=files.get(i).toString();
		}
		msg+="END";
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
	 * Methode permettant de mettre en String les information de la liste des fichiers
	 * @return les information de la liste des fichiers
	 */
	private String getMessageFichier(){
		String msg ="";
		for (int i=0; i<this.fichiers.size(); i++){
			Fichier f = this.fichiers.get(i);
			msg+=f.getName()+"|"+f.getHashcode()+"|"+f.getUuid()+"|";
		}
		msg+="END";
		return msg;
	}
    
	/**
	 * Methode enlevant un pair de la liste
	 * @param uuid indentifiant du client
	 * @return OK s'il a bien ete enleve ERROR sinon
	 */
	private String quit(String uuid) {
    	System.out.println("QUITing");
    	
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
		String msg = "PUSHNEWPEERS:" + newPeer.toString();
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			send(peer.getAddress(), peer.getPort(), msg);
		}	
	}

	public static void main(String[] args) throws IOException {
		new Rdv().serve();
	}

}
