package de.htwg.se.ws1516.fourwinning;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.htwg.se.ws1516.fourwinning.view.*;

public class FourWinning {
	private static final Logger LOGGER = Logger.getLogger(Tui.class.getName());

	private Tui TextUI = new Tui();
	private static FourWinning instance = null;
	
	public static FourWinning getInstance() {
		if (instance == null) {
			instance = new FourWinning();
		}
		return instance;
	}

	public Tui getTui() {
		return TextUI;
	}
	
	public static void main (String[] args){
		LOGGER.setLevel(Level.INFO);
		FourWinning game = getInstance();
		game.TextUI.createGameArea();
		String continu = "next round";
		while("next round" == continu){
			continu = game.TextUI.runGame();
		}
		LOGGER.info(continu);
		
	}
}
