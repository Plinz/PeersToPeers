package ihm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import p2p.Client;

public class FenetreConnexion extends JPanel implements ActionListener {


	private static final long serialVersionUID = 1L;

	JFrame parent;
	
	JLabel serveur = new JLabel("Indentifiant du serveur :");
	JLabel address = new JLabel("Adresse :");
	JLabel port = new JLabel("Port :");
	JLabel error = new JLabel("");
	JTextField ip = new JTextField();
	JTextField po = new JTextField();
	JButton ok = new JButton("Connexion");

	public FenetreConnexion(JFrame frame) {
		setPreferredSize(new Dimension(300, 150));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5, 10, 5, 10);
		this.add(serveur, c);
		c.gridy=1;
		c.gridwidth=1;
		c.anchor = GridBagConstraints.BASELINE_LEADING;
		this.add(address, c);
		c.gridx=1;
		c.weightx=2;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		this.add(ip, c);
		c.gridx=0;
		c.gridy=2;
		c.weightx=0;
		c.gridwidth=1;
		c.anchor = GridBagConstraints.BASELINE_LEADING;
		c.fill = GridBagConstraints.NONE;
		this.add(port, c);
		c.gridx=1;
		c.weightx=2;
		c.fill = GridBagConstraints.BOTH;
		this.add(po, c);
		c.gridx=0;
		c.gridy=3;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.BASELINE_TRAILING;
		ok.addActionListener(this);
		this.add(ok, c);
		c.gridx=0;
		c.gridy=4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.CENTER;
		this.add(error, c);
		this.error.setForeground(Color.RED);
		this.error.setVisible(false);
		this.parent = frame;
		
		parent.setTitle("Connexion");
		parent.add(this);
		parent.setVisible(false);
		parent.pack();
		parent.setVisible(true);
		parent.setLocationRelativeTo(null);
		parent.setResizable(true);
		parent.setExtendedState(Frame.MAXIMIZED_BOTH);
		parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(ip.getText());
			Client c = new Client(addr, Integer.parseInt(po.getText()));
			try {
				int r = c.initialisation();
				if (r==0){
					this.error.setText("Connexion Impossible");
					this.error.setVisible(true);					
				}
				else{
					this.setVisible(false);
					this.parent.add(new FenetrePrincipale(this.parent, c));
					this.parent.revalidate();
				}
			} catch (IOException e2) {
				this.error.setText("Connexion Impossible");
				this.error.setVisible(true);
			}
		} catch (UnknownHostException e1) {
			this.error.setText("Impossible de trouver le serveur");
			this.error.setVisible(true);
		}

	}


}
