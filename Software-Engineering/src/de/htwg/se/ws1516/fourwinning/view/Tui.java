package de.htwg.se.ws1516.fourwinning.view;

import de.htwg.se.ws1516.fourwinning.controller.impl.*;
import de.htwg.se.ws1516.fourwinning.controller.*;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.htwg.se.ws1516.fourwinning.models.*;

public class Tui {
	
	
	private static final Logger LOGGER = Logger.getLogger(Tui.class.getName());
	private static Scanner eingabe;
	GameController spiel;
	Player eins;
	Player zwei;
	Player aktiv = eins;
	Feld[][] spielfeld;
	int rows;
	int columns;

	public Tui(){	
		
	}

	public void ausgabe(Feld[][] feld, int rows, int columns, Player eins, Player zwei) {
		LOGGER.setLevel(Level.INFO);
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
	
	public void createGameArea(){
		eingabe = new Scanner(System.in);
		LOGGER.info("Reihen angeben");
		rows = eingabe.nextInt();
		LOGGER.info("Spalten angeben");
		columns = eingabe.nextInt();
		spiel = new GameController(rows, columns);
		LOGGER.info(spiel.getStatusText());
		spiel.baueSpielfeld(rows, columns);
		LOGGER.info(spiel.getStatusText());
		
		
	}
	
	
	public void createPlayers(){
		LOGGER.info("Name des ersten Spielers angeben");
		String one = eingabe.next();
		LOGGER.info("Name des zweiten Spielers angeben");
		String two = eingabe.next();
		spiel.createPlayers(one, two);
		LOGGER.info(spiel.getStatusText());
		eins = spiel.getPlayerOne();
		zwei = spiel.getPlayerTwo();
		eins.setActive(true);
		zwei.setActive(false);
	}
	
	public String playGame(){

		aktiv = spiel.aktiverSpieler();
		LOGGER.info(spiel.getStatusText());
		
		String rowExplain = String.format(
				"%nMachen sie Ihren Zug, geben sie dafuer die Column an, zwischen 0 und %d%n", columns - 1);
		LOGGER.info(rowExplain);
		spielfeld = spiel.update();
		System.out.println("Um den letzten Zug zu widerholen, geben sie redo ein");
		String currentColumnString = eingabe.next();
		if ("redo".equals(currentColumnString)){
			spiel.redo();
		} else {
			int currentColumn = Integer.parseInt(currentColumnString);
			LOGGER.info(spiel.zug(currentColumn, aktiv));
		}
			ausgabe(spielfeld, rows, columns, eins, zwei);
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
		if ("undo".equals(undo)){
			spiel.undo();
			spielfeld = spiel.update();
			return "next round";
		}
		return "next round";
	}
	
	public void spielerwaechsel(Player eins, Player zwei){
		spiel.changePlayer(eins, zwei);
	}
	
	public String runGame(){

		if(spiel.getState() instanceof PlayerBuildState){
			createPlayers();
			spiel.getState().nextState(spiel);
			return "next round";
		} else if(spiel.getState() instanceof GameRunningState){
			spiel.getState().nextState(spiel);
			return playGame();
		} else if(spiel.getState() instanceof PlayerChangeState){
			spielerwaechsel(eins,zwei);
			spiel.getState().nextState(spiel);
			return "next round";
		}
		return null;
	}
}
	
	

