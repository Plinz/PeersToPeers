package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FenetrePrincipale extends JPanel {


	private static final long serialVersionUID = 1L;

	JFrame parent;
	JPanel panelDroit;
	JPanel panelGauche;
	JPanel vide;
	JPanel vide1;
	JLabel sous = new JLabel("gold");

	public FenetrePrincipale(JFrame frame) {
		setPreferredSize(new Dimension(1200, 1200));
		this.parent = frame;
		panelDroit = new JPanel();
		panelDroit.setBackground(new Color(0, 0, 0, 1));
		panelGauche = new JPanel();
		panelGauche.setBackground(new Color(0, 0, 0, 1));

		sous.setForeground(Color.green);


		/** Colonne gauche **/
	
		panelGauche.setLayout(new BorderLayout());
		panelGauche.add(new JLabel("rien pour le moment"),BorderLayout.CENTER);
		this.add(panelGauche, BorderLayout.WEST);
		
		/**Colonne milieu**/

		/** Colonne Droite **/
		panelDroit.setLayout(new BorderLayout());
		panelDroit.add(sous,BorderLayout.LINE_END);
		this.add(panelDroit, BorderLayout.EAST);
		
		parent.add(this);
		parent.setVisible(false);
		parent.pack();
		Menu.setDefaultLookAndFeelDecorated(true);
		parent.setVisible(true);
		parent.setLocationRelativeTo(null);
		parent.setResizable(true);
		parent.setExtendedState(Frame.MAXIMIZED_BOTH);
	}


}
