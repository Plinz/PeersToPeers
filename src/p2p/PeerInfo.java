package p2p;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * The Class PeerInfo.
 */
public class PeerInfo {
	
	/** The uuid. */
	private UUID uuid;
	
	/** The address. */
	private InetAddress address;
	
	/** The port. */
	private int port;
	
	/**
	 * Instantiates a new peer info.
	 *
	 * @param address the address
	 * @param port the port
	 */
	public PeerInfo(InetAddress address, int port) {
		this.uuid = UUID.randomUUID();
		this.address = address;
		this.port = port;
	}
	
	/**
	 * Instantiates a new peer info.
	 *
	 * @param uuidString the uuid string
	 * @param address the address
	 * @param port the port
	 */
	public PeerInfo(String uuidString, String address, String port){
		try {
			this.address = InetAddress.getByName(address.substring(1));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.uuid = UUID.fromString(uuidString);
		this.port = Integer.parseInt(port);
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return uuid + ":" + address + ":" + port;
	}
	
	/**
	 * To string with out uuid.
	 *
	 * @return the string
	 */
	public String toStringWithOutUuid(){
		return address + "|" + port;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerInfo other = (PeerInfo) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
