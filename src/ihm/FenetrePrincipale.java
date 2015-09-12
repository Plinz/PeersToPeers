package ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import p2p.Client;
import p2p.Fichier;

public class FenetrePrincipale extends JPanel {


	private static final long serialVersionUID = 1L;

	JFrame parent;
	JList<String> fichiers;
	JScrollPane panCenter;
	JPanel panBas;
	JLabel name = new JLabel ("Name : ");
	JLabel hash = new JLabel ("Hash : ");
	JLabel uuid = new JLabel ("UUID : ");
	JLabel addr = new JLabel ("Adresse : ");
	Client client;


	public FenetrePrincipale(JFrame frame, Client cl) throws IOException {
		this.setLayout(new BorderLayout());
		this.client = cl;
		setPreferredSize(new Dimension(1200, 1200));
		this.parent = frame;
		DefaultListModel<String> model = new DefaultListModel<String>();
		this.fichiers = new JList<String>(model);
		for (int i=0; i<this.client.fichiers.size(); i++){
			model.addElement(this.client.fichiers.get(i).getName());
		}
		
		panBas = new JPanel();
		
		panBas.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.gridwidth = 1;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.BASELINE_LEADING;
		c.insets = new Insets(2, 10, 2, 10);
		c.weightx = 1;
		panBas.add(name, c);
		c.gridx=1;
		panBas.add(hash, c);
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridx=2;
		panBas.add(uuid, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx=3;
		panBas.add(addr, c);
		panBas.setBorder(new EtchedBorder());
		
		this.fichiers.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged (ListSelectionEvent e) { 
				if(!e.getValueIsAdjusting()){
			      Fichier f = client.fichiers.get(fichiers.getSelectedIndex());
			      name.setText("Name : "+f.getName());
			      hash.setText("Hash : "+f.getHashcode());
			      uuid.setText("UUID : "+f.getUuid());
			      addr.setText("Adresse : "+client.peers.get(f.getUuid()).getAddress()+":"+client.peers.get(f.getUuid()).getPort());
				}
			}
		});
		
		panCenter = new JScrollPane (this.fichiers);
		this.add(panCenter, BorderLayout.CENTER);
		this.add(panBas, BorderLayout.SOUTH);
		parent.setTitle("Peers To Peers");
		parent.add(this);
		parent.setVisible(false);
		parent.pack();
		Menu.setDefaultLookAndFeelDecorated(true);
		parent.setVisible(true);
		parent.setLocationRelativeTo(null);
		parent.setResizable(true);
		parent.setExtendedState(JFrame.MAXIMIZED_BOTH);
		parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}
