package de.htwg.se.ws1516.fourwinning.controller;

import de.htwg.se.ws1516.fourwinning.models.Feld;
import de.htwg.se.ws1516.fourwinning.models.Player;

public interface GameInterface {
	void baueSpielfeld(int rows, int columns);
	void createPlayers(String nameOne, String nameTwo);
	Player aktiverSpieler();
	Player getPlayerOne();
	Player getPlayerTwo();
	Player changePlayer(Player one, Player two);
	String zug(int column, Player p);
	Feld[][] update();
	boolean spielGewonnen(Feld[][] feld, Player p);
	boolean spielDraw(Feld[][] feld);
	int getRows();
	void setRows(int rows);
}
