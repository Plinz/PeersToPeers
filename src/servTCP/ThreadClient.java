package servTCP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadClient extends Thread {
	Socket s;
	FileInputStream inf;
	ObjectOutputStream out;

	public ThreadClient(Socket s, File fichier) throws IOException {
		this.s = s;
		inf = new FileInputStream(fichier);
		out = new ObjectOutputStream(s.getOutputStream());
	}

	@Override
	public void run() {
		try {
			byte buf[] = new byte[1024];
			int n;
			while ((n = inf.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			inf.close();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
