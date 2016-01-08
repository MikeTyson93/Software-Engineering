package de.htwg.se.ws1516.fourwinning.view.TUI;

import de.htwg.se.ws1516.fourwinning.controller.impl.*;
import de.htwg.se.ws1516.fourwinning.controller.*;
import de.htwg.util.observer.IObserver;
import de.htwg.util.observer.Event;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.htwg.se.ws1516.fourwinning.models.*;

public class Tui implements IObserver {

	GameController spiel;

	private static final Logger LOGGER = Logger.getLogger(Tui.class.getName());
	private static Scanner eingabe;
	Player eins;
	Player zwei;
	Player aktiv = eins;
	Feld[][] spielfeld;
	int rows;
	int columns;
	String zugerfolgreich;
	

	public Tui(GameController spiel) {
		this.spiel = spiel;
		spiel.addObserver(this);
		
	}

	public void ausgabe(Feld[][] feld, int rows, int columns, Player eins, Player zwei) {
		LOGGER.setLevel(Level.INFO);
		System.out.println("Ausgabe");
		for (int k = 0; k < rows; k++) {
			for (int l = 0; l < columns; l++) {
				if (feld[k][l].getSet()) {
					if (feld[k][l].getOwner().getName().equals(eins.getName())) {
						String fieldOfP1 = "[X]\t";
						System.out.printf(fieldOfP1);
					} else if (feld[k][l].getOwner().getName().equals(zwei.getName())) {
						String fieldOfP2 = "[O]\t";
						System.out.printf(fieldOfP2);
					}
				} else {
					String noSetField = "[ ]\t";
					System.out.printf(noSetField);
				}
			}
			String seperator = "%n";
			System.out.printf(seperator);

		}

	}

	public void createGameArea() {
		eingabe = new Scanner(System.in);
		LOGGER.info("Rows angeben");
		rows = spiel.getRows();
		LOGGER.info("Columns angeben");
		columns = spiel.getColumns();
		spiel.setRows(rows);
		spiel.setColumns(columns);

		spiel.baueSpielfeld(rows, columns);
		LOGGER.info(spiel.getStatusText());
		LOGGER.info(spiel.getStatusText());
		

	}

	public void createPlayers() {
		eingabe = new Scanner(System.in);
		LOGGER.info("Name Spieler eins angeben");
		String one = spiel.getPlayerOne().getName();
		LOGGER.info("Name Spieler zwei angeben");
		String two = spiel.getPlayerTwo().getName();
		spiel.createPlayers(one, two);
		LOGGER.info(spiel.getStatusText());
		eins = spiel.getPlayerOne();
		zwei = spiel.getPlayerTwo();
		eins.setActive(true);
		zwei.setActive(false);
	}

	public String playGame() {
		spielfeld = spiel.update();
		eingabe = new Scanner(System.in);
		aktiv = spiel.aktiverSpieler();
		LOGGER.info(spiel.getStatusText());

		String rowExplain = String.format("%nMachen sie Ihren Zug, geben sie dafuer die Column an, zwischen 0 und %d%n",
				columns - 1);
		LOGGER.info(rowExplain);

		System.out.println("Um den letzten Zug zu widerholen, geben sie redo ein");
		String currentColumnString = eingabe.next();
		if ("redo".equals(currentColumnString)) {
			spiel.redo();
		} else {
			spielfeld = spiel.update();
			int currentColumn = Integer.parseInt(currentColumnString);
			zugerfolgreich = (spiel.zug(currentColumn, aktiv));
			LOGGER.info(zugerfolgreich);
		}
		spiel.notifyObservers();
		String whoHasWon = "";
		if (spiel.spielGewonnen(spielfeld, aktiv)) {
			whoHasWon = String.format("%n%s hat das Spiel gewonnen!%n", aktiv.getName());

			return whoHasWon;
		}
		LOGGER.info(spiel.getStatusText());
		LOGGER.info(whoHasWon);
		String draw = "";
		if (spiel.spielDraw(spielfeld)) {
			draw = "Unentschieden";
			return draw;
		}
		LOGGER.info(spiel.getStatusText());
		LOGGER.info(draw);
		LOGGER.info("%n%n Schreibe undo, um den Zug rueckgaengig zu machen, ansonsten beliebige taste %n%n");
		String undo = eingabe.next();
		if ("undo".equals(undo)) {
			spiel.undo();
			spielfeld = spiel.update();
			return "next round";
		}
		spiel.notifyObservers(new PlayerChangeEvent());
		return "next round";
	}

	public void spielerwaechsel(Player eins, Player zwei) {
		spiel.changePlayer(eins, zwei);
	}

	public String runGame() {

		if (spiel.getState() instanceof PlayerBuildState) {
			createPlayers();
			spiel.getState().nextState(spiel);
			return "next round";
		} else if (spiel.getState() instanceof GameRunningState) {
			spiel.getState().nextState(spiel);
			String rueck = playGame();
			spielerwaechsel(eins,zwei);
			return rueck;
		} else if (spiel.getState() instanceof PlayerChangeState) {

			spiel.getState().nextState(spiel);
			return "next round";
		}
		return null;
	}

	@Override
	public void update(Event e) {
		if(e == null){
			ausgabe(spielfeld, rows, columns, eins, zwei);
			spiel.changePlayer(eins, zwei);	
		}else if(e instanceof PlayerChangeEvent){
			//spiel.changePlayer(eins, zwei);	
		}
		}

	
}
