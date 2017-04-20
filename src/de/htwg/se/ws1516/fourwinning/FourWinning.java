package de.htwg.se.ws1516.fourwinning;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Logger;

import de.htwg.se.ws1516.fourwinning.view.gui.*;
import de.htwg.se.ws1516.fourwinning.view.tui.*;
import de.htwg.se.ws1516.fourwinning.controller.IGameController;
import de.htwg.se.ws1516.fourwinning.models.Player;

public class FourWinning {
	private static final Logger LOGGER = Logger.getLogger(Tui.class.getName());
	private Tui textUI;
	private Gui graphicUI;
	static protected IGameController controller;
	private static FourWinning instance;
	int rows;
	int columns;

	private FourWinning() throws IOException {
		Injector injector = Guice.createInjector(new FourWinningModule());
		controller = injector.getInstance(IGameController.class);
		
		textUI = new Tui(controller);
		graphicUI = new Gui(controller);
		buildGameArea();
		buildPlayers();
		graphicUI.createGameArea();
		textUI.createGameArea();
		textUI.createPlayers();
		graphicUI.createPlayers();
		instance = null;
	}

	public static IGameController getController(){
		return controller;
	}

	public static FourWinning getInstance() throws IOException {
		if (instance == null) {
			instance = new FourWinning();
		}
		return instance;
	}

	public Tui getTui() {
		return textUI;
	}
	
	public void buildGameArea(){
		controller.setRows(6);
		controller.setColumns(7);
		controller.baueSpielfeld(6, 7);
	}

	public void buildPlayers(){
		String one = "Michael";
		String two = "Stephan";
		controller.createPlayers(one, two);
		Player eins = controller.getPlayerOne();
		Player zwei = controller.getPlayerTwo();
		eins.setActive(true);
		zwei.setActive(false);
	}
	
	public static void main(String[] args) {
		try {
			LOGGER.setLevel(Level.INFO);
			FourWinning game = getInstance();

			String continu = "next round";
			while ("next round" == continu) {
				continu = game.textUI.runGame();
			}
			LOGGER.info(continu);
		} catch (java.io.IOException e) {
			LOGGER.log(Level.SEVERE, "exception", e);
		}
	}
}
