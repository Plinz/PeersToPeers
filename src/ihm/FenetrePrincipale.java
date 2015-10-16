package ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import p2p.Client;
import p2p.Fichier;
import p2p.PeerInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class FenetrePrincipale.
 */
public class FenetrePrincipale extends JPanel implements Observer{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The parent. */
	JFrame parent;

	/** The pan center. */
	private JScrollPane panCenter;
	
	/** The pan left. */
	private JScrollPane panLeft;
	
	/** The pan bas. */
	private JPanel panBas;
	
	/** The pan right. */
	private JScrollPane panRight;
	
	/** The file chooser. */
	private JFileChooser fileChooser;
	
	/** The repertory chooser. */
	private JFileChooser repertoryChooser;
	
	/** The fichiers other. */
	private JList<String> fichiersOther;
	
	/** The model other. */
	private DefaultListModel<String> modelOther;
	
	/** The fichiers own. */
	private JList<String> fichiersOwn;
	
	/** The model own. */
	private DefaultListModel<String> modelOwn;
	
	/** The download. */
	private JList<String> download;
	
	/** The model download. */
	private DefaultListModel<String> modelDownload;
	
	/** The menu bar. */
	private JMenuBar menuBar;
	
	/** The importation. */
	private JMenu importation;
	
	/** The aide. */
	private JMenu aide;
	
	/** The import file. */
	private JMenuItem importFile;
	
	/** The import repertory. */
	private JMenuItem importRepertory;
	
	/** The name. */
	private JLabel name = new JLabel("Name : ");
	
	/** The hash. */
	private JLabel hash = new JLabel("Hash : ");
	
	/** The uuid. */
	private JLabel uuid = new JLabel("UUID : ");
	
	/** The addr. */
	private JLabel addr = new JLabel("Adresse : ");
	
	/** The client. */
	private Client client;

	/**
	 * Instantiates a new fenetre principale.
	 *
	 * @param frame the frame
	 * @param cl the cl
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public FenetrePrincipale(JFrame frame, Client cl) throws IOException {
		cl.receiveChange(this);
		this.setLayout(new BorderLayout());
		this.client = cl;
		this.client.view = this;
		this.setPreferredSize(new Dimension(1200, 1200));
		this.parent = frame;
		
		this.modelOther = new DefaultListModel<String>();
		this.fichiersOther = new JList<String>(this.modelOther);
		for (int i=0; i<this.client.otherFichiers.size(); i++){
			this.modelOther.addElement(this.client.otherFichiers.get(i).getName());
		}
		
		this.modelOwn = new DefaultListModel<String>();
		this.fichiersOwn = new JList<String>(this.modelOwn);
		for (int i=0; i<this.client.ownFichiers.size(); i++){
			this.modelOwn.addElement(this.client.ownFichiers.get(i).getName());
		}
		
		this.modelDownload = new DefaultListModel<String>();
		this.download = new JList<String>(this.modelDownload);
		
		
		//Menu NORTH
		this.menuBar = new JMenuBar();
		this.importation = new JMenu("Partager");
		this.aide = new JMenu("Aide");
		this.importFile = new JMenuItem ("Importer des fichiers");
		this.importRepertory = new JMenuItem ("Importer des dossiers");
		this.importRepertory.setVisible(false);
		this.importation.add(this.importFile);
		this.importation.add(this.importRepertory);
		this.menuBar.add(this.importation);
		this.menuBar.add(this.aide);
		this.parent.setJMenuBar(this.menuBar);
		
		//Chooser pour file
		this.importFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
				fileChooser.showOpenDialog(null);
				File f = fileChooser.getSelectedFile();
				ArrayList<File> file = new ArrayList<File>();
				file.add(f);
		        try {
					client.sendNewFiles(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        modelOwn.removeAllElements();
				for (int i=0; i<client.ownFichiers.size(); i++){
					modelOwn.addElement(client.ownFichiers.get(i).getName());
				}			
			}
		});
		
		//Chooser pour repertory
		this.importRepertory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				repertoryChooser = new JFileChooser();
				repertoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
				repertoryChooser.showOpenDialog(null);
				File f = repertoryChooser.getSelectedFile();
				ArrayList<File> file = new ArrayList<File>();
				Stack<File> s = new Stack<File>();
				s.push(f);
				while(!s.isEmpty()){
					File tmp = s.pop();
					File [] all = tmp.listFiles();
					for (int i=0; i<all.length; i++){
						if (all[i].isDirectory())
							s.push(all[i]);
						else
							file.add(all[i]);
					}
				}
		        try {
					client.sendNewFiles(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        modelOwn.removeAllElements();
				for (int i=0; i<client.ownFichiers.size(); i++){
					modelOwn.addElement(client.ownFichiers.get(i).getName());
				}
			}
			
		});
		
		//JList des ownfichiers que l'on partage  WEST
		this.panLeft = new JScrollPane(this.fichiersOwn);
		
		//JList des otherfichiers que l'on peut telecharger  CENTER
		this.panCenter = new JScrollPane (this.fichiersOther);
		
		//JList des telechargement en cours ou fait
		this.panRight = new JScrollPane(this.download);
		
		//Mise à jour de la barre d'etat
		this.fichiersOther.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged (ListSelectionEvent e) {
				int i;
				if(!e.getValueIsAdjusting() && (i = fichiersOther.getSelectedIndex())>-1){
			      Fichier f = client.otherFichiers.get(i);
			      name.setText("Name : "+f.getName());
			      hash.setText("Hash : "+f.getHashcode());
			      uuid.setText("UUID : "+f.getUuid());
			      addr.setText("Adresse : "+client.peers.get(f.getUuid()).getAddress()+":"+client.peers.get(f.getUuid()).getPort());
				}
			}
		});
		
		//Double click pour Telechargement
		this.fichiersOther.addMouseListener(new MouseAdapter(){
		    @Override
		    public void mouseClicked(MouseEvent e){
	        	System.out.println("test 0");
		        if(e.getClickCount()==2){
		        	Fichier f = client.otherFichiers.get(fichiersOther.getSelectedIndex());
		        	PeerInfo p = client.peers.get(f.getUuid());
		        	modelDownload.addElement(f.getName());
		        	System.out.println("test 1");
		        	try {
			        	System.out.println("test 2");
						servTCP.ThreadClient clientTCP = new servTCP.ThreadClient(p.getAddress(), 5002, f.getHashcode(), "/");	
			        	System.out.println("test 3");
						clientTCP.run();
			        	System.out.println("test 4");
		        	} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        	modelDownload.removeElement(f.getName());
		        }
		        else{
		        	System.out.println("pas click");
		        }
		    }
		});
		
		//Barre d'etat SOUTH
		panBas = new JPanel(new GridBagLayout());
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
		
		
		
		
		this.add(panCenter, BorderLayout.CENTER);
		this.add(panBas, BorderLayout.SOUTH);
		this.add(panLeft, BorderLayout.WEST);
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
	
	/**
	 * Update other files.
	 */
	public void updateOtherFiles (){
		this.modelOther.removeAllElements();
		for (int i=0; i<this.client.otherFichiers.size(); i++){
			this.modelOther.addElement(this.client.otherFichiers.get(i).getName());
		}
		this.revalidate();
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable obs, Object txt) {
		if (obs instanceof p2p.ThreadClient){
			if (txt.toString().equals("file")){
				this.updateOtherFiles();
			}
			else{
				System.out.println("update affichage peers");
			}
		}
	}
	
	
	
	/**
	 * Run download.
	 *
	 * @param range the range
	 */
	public void runDownload(int range){
		this.modelOwn.setElementAt(this.modelOwn.get(range)+" - Download", range);
	}
	
	/**
	 * Stop download.
	 *
	 * @param range the range
	 */
	public void stopDownload(int range){
		this.modelOwn.setElementAt(this.client.ownFichiers.get(range).getName(), range);
	}

}
