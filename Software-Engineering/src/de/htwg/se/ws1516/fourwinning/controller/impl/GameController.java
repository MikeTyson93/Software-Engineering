package de.htwg.se.ws1516.fourwinning.controller.impl;

import de.htwg.se.ws1516.fourwinning.models.*;
import de.htwg.util.*;
import de.htwg.se.ws1516.fourwinning.controller.*;



public class GameController implements GameInterface {
	private GameStates status = GameStates.WELCOME;
	private String statusText = "Welcome";
	private PlayArea spielfeld;
	private Player one;
	private Player two;
	int mengeOne;
	int mengeTwo;
	RuleController regeln;
	private int rows;
	private int columns;
	int currentColumn;
	int currentRow;
	boolean spielGewonnen;
	CreateCommand commands;
	private IGameState state;
	
	
	public GameController(int rows, int columns) {
		this.setRows(rows);
		this.setColumns(columns);
		commands = new CreateCommand();
		state = new PlayerBuildState();
	}
	
	public PlayArea getSpielfeld(){
		return spielfeld;
	}
	
	public void setSpielfeld(PlayArea spielfeld){
		this.spielfeld = spielfeld;
	}
	
	@Override
	public void baueSpielfeld(int rows, int columns) {
		
		this.setRows(rows);
		this.setColumns(columns);
		spielfeld = new PlayArea(rows, columns);
		mengeOne = (rows * columns / 2 + 1);
		mengeTwo = (rows * columns / 2);
		regeln = new RuleController(rows, columns);
		status = GameStates.CREATE_AREA;
		statusText = "Area created";
	}

	@Override
	public void createPlayers(String nameOne, String nameTwo) {
		one = new Player(nameOne, mengeOne);
		two = new Player(nameTwo, mengeTwo);
		one.setActive(true);
		two.setActive(false);
		status = GameStates.CREATE_PLAYERS;
		statusText = "Players created";
	}

	/*
	 * liefert momentan aktiven Spieler zurueck
	 */
	@Override
	public Player aktiverSpieler() {
		if (one.getActive()){
			status = GameStates.PLAYER_ONE_TURN;
			statusText = "It's the turn of Player one";
			return one;
		} else{
			status = GameStates.PLAYER_TWO_TURN;
			statusText = "It's the turn of Player two";
			return two;
		}
	}

	@Override
	public Player getPlayerOne() {
		return one;
	}

	@Override
	public Player getPlayerTwo() {
		return two;
	}

	/*
	 * Wechselt aktiven Spieler und liefert den aktiven Spieler zurueck
	 */
	@Override
	public Player changePlayer(Player one, Player two) {
		if (one.getActive()) {
			one.setActive(false);
			two.setActive(true);
			return two;
        }
        one.setActive(true);
        two.setActive(false);
		return one;
		
	}

	/*
	 * Macht den Zug und gibt zurueck ob dieser funktioniert hat
	 */
	@Override
	public String zug(int column, Player p) {
		save(spielfeld.getFeld(), column);
		currentColumn = column;
		int statuszug = spielfeld.setChip(column, p);
		if (statuszug == -2)
			return "Zug fehlgeschlagen";
		currentRow = statuszug;
		return "Zug erfolgreich";
	}

	/*
	 * gibt aktuelles Spielfeld zurueck
	 */
	@Override
	public Feld[][] update() {
		return spielfeld.getFeld();
	}

	@Override
	public boolean spielGewonnen(Feld[][] feld, Player p) {
		this.spielGewonnen = regeln.getWin(feld, p, currentRow, currentColumn);
		status = GameStates.CHECK_WIN;
		statusText = "Regeln werden auf Gewinner ueberprueft";
		return spielGewonnen;
	}

	@Override
	public boolean spielDraw(Feld[][] feld) {
		status = GameStates.CHECK_DRAW;
		statusText = "Regeln werden auf Unentschieden ueberprueft";
		return regeln.getDraw(feld);
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public void undo(){
		Feld[][] ersatzfeld = commands.undoCommand();
		spielfeld.setFeld(ersatzfeld);
	}
	
	public void save(Feld[][] spielfeld, int column){
		commands.doCommand(spielfeld, column);
	}
	
	public void redo(){
		int spalte = commands.redoCommand();
		zug(spalte, aktiverSpieler());
	}

	public GameStates getStatus(){
		return status;
	}

	public String getStatusText(){
		return statusText;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	public void setState(IGameState state) {
        this.state = state;
    }
	
	public IGameState getState(){
		return state;
	}
}
