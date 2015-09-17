package servTCP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ThreadServeur extends Thread {
	private ObjectInputStream in;
	private FileOutputStream out;

	public ThreadServeur(Socket s, File fichier) throws IOException {
		in = new ObjectInputStream(s.getInputStream());
		out = new FileOutputStream(fichier);
	}

	@Override
	public void run() {
		try {
			byte buf[] = new byte[1024];
			int n;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
