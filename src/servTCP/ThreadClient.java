package servTCP;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ThreadClient extends Thread {
	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;
	int hash;
	String pathname;

	public ThreadClient(InetAddress ip, int port, int hash, String pathname)
			throws IOException {
		System.out.println("port ==>" + port);
		this.s = new Socket(ip, port);
		this.pathname = pathname;
		this.hash = hash;
	}

	@Override
	public void run() {
		try {
			in = new ObjectInputStream(s.getInputStream());
			out = new ObjectOutputStream(s.getOutputStream());
			System.out.println("test 5");
			out.writeInt(this.hash);

			byte[] byin = new byte[1024];
			FileOutputStream fos = new FileOutputStream(new File(this.pathname));
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = in.read(byin, 0, byin.length);
			int current = bytesRead;
			System.out.println("test 6");
			do {
				bytesRead = in.read(byin, current, (byin.length - current));
				if (bytesRead >= 0)
					current += bytesRead;
			} while (bytesRead > -1);
			System.out.println("test 7");
			bos.write(byin, 0, current);
			bos.flush();
			fos.close();
			bos.close();
			in.close();
			out.close();
			System.out.println("test 8");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
