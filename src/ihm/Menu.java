package ihm;

import javax.swing.JFrame;

// TODO: Auto-generated Javadoc
/**
 * The Class Menu.
 */
public class Menu extends JFrame{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new menu.
	 */
	public Menu(){
		this.getContentPane().add(new FenetreConnexion(this));
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
