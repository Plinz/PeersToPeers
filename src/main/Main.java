package main;

import ihm.Menu;

public class Main {
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Menu();
			}
		});
	}
}
