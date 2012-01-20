package com.sargant.runnit;

import com.sargant.runnit.gui.MainWindow;

public class Runnit {
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			RunnitApp app = new RunnitApp();
			MainWindow window = new MainWindow(app);
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
