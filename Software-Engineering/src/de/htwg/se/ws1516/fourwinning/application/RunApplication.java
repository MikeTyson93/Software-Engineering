package de.htwg.se.ws1516.fourwinning.application;

import de.htwg.se.ws1516.fourwinning.view.Tui;

public class RunApplication {

	/**
	 * @param args
	 */
	

	private RunApplication(){
		
	}

	
	
	public static void main(String[] args) {
		Tui textUI = new Tui();
		textUI.runApplication();
	}

}
