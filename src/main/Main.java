package main;

import ihm.Menu;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 */
public class Main {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Menu();
			}
		});
	}
}
