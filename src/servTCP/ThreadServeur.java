package servTCP;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadServeur extends Thread {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private p2p.Client client;

	public ThreadServeur(Socket s, p2p.Client c) throws IOException {
		in = new ObjectInputStream(s.getInputStream());
		out = new ObjectOutputStream(s.getOutputStream());
		this.client = c;
	}

	@Override
	public void run() {
		try {
			byte buf[] = new byte[1024];
			int n;
			while ((n = in.readInt()) == -1) {
			}
			System.out.println("in : "+n);
			in.close();
			String reponse = new String(buf, "UTF-8");

			for (int i = 0; i < this.client.ownFichiers.size(); i++) {
				if (this.client.ownFichiers.get(i).getHashcode() == Integer
						.parseInt(reponse)) {
					File f = this.client.ownFichiers.get(i).getFile();
					this.client.view.runDownload(i);
					byte[] byout = new byte[(int) f.length()];
					FileInputStream fis = new FileInputStream(f);
					BufferedInputStream bis = new BufferedInputStream(fis);
					bis.read(byout, 0, byout.length);
					out.write(byout, 0, byout.length);
					out.flush();
		            if (fis!=null) fis.close();
		            this.client.view.stopDownload(i);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
        finally {
				try {
		            if (in != null)  in.close();
		            if (out != null) out.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
          }
	}
}
