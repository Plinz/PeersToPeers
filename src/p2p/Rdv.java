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
	private static final  int _pSrv = 5001;
	private static final int _bfLength = 41;
	private Hashtable<String, PeerInfo> peers;
	private byte[] buffer = null; 
	private DatagramSocket dgSocket;
	private ArrayList<Fichier> fichiers;
	
    public Rdv() throws IOException {
    	dgSocket = new DatagramSocket(_pSrv);
    	peers = new Hashtable<String, PeerInfo>();
    }
    
    private void serve() throws IOException {    	
    	while (true) {
    		DatagramPacket dgPacket = receive();
    		
    		String msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
    		InetAddress address = dgPacket.getAddress();
    		int port = dgPacket.getPort();
    		String answer = null;

    		if ( msg.equals("RGTR") ) { // enlever le \n après avoir testé avec nc
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
    
    private DatagramPacket receive() throws IOException {
		buffer = new byte[_bfLength];
		DatagramPacket dgPacket = new DatagramPacket(buffer, _bfLength);
		dgSocket.receive(dgPacket);
		
		return dgPacket;
    }
    
    private void send(InetAddress address, int port, String msg) throws IOException {
		buffer = msg.getBytes();
		DatagramPacket dgPacket = new DatagramPacket(buffer, 0, buffer.length, address, port);			
		dgSocket.send(dgPacket);
    }
    
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
    			
    		}
    		return "OK";
    	}
    	return "ERROR";
	}
    
	private String quit(String uuid) {
    	System.out.println("QUITing");
    	
    	if ( peers.remove(uuid) != null ) {
    		return "OK";
    	}
    	return "ERROR";
	}

	private String register(InetAddress address, int port) throws IOException {
    	System.out.println("ReGisTeRing");
    	
		PeerInfo p = new PeerInfo(address, port);
		notifyPeers(p);
		peers.put(p.getUUID().toString(), p);
		
		StringBuilder sb = new StringBuilder();
		sb.append("OK:");
		sb.append(p.getUUID());

    	return sb.toString();    
    }
    
	private void notifyPeers(PeerInfo newPeer) throws IOException {
		String msg = "PUSH:" + newPeer.toString();
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
