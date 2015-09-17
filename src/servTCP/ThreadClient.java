package servTCP;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadClient extends Thread {
	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;
	int hash;
	String pathname;

	public ThreadClient(Socket s, int hash, String pathname) throws IOException {
		this.pathname = pathname;
		this.s = s;
		this.hash = hash;
		in = new ObjectInputStream(s.getInputStream());
		out = new ObjectOutputStream(s.getOutputStream());
	}

	@Override
	public void run() {
		try {
			out.writeInt(this.hash);

			byte[] byin = new byte[1024];
			FileOutputStream fos = new FileOutputStream(new File(this.pathname));
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = in.read(byin, 0, byin.length);
			int current = bytesRead;

			do {
				bytesRead = in.read(byin, current,
						(byin.length - current));
				if (bytesRead >= 0)
					current += bytesRead;
			} while (bytesRead > -1);

			bos.write(byin, 0, current);
			bos.flush();
			fos.close();
			bos.close();
			in.close();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
