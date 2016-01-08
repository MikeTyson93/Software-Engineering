/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwg.se.ws1516.fourwinning.view.GUI;

import de.htwg.se.ws1516.fourwinning.controller.impl.GameController;
import de.htwg.se.ws1516.fourwinning.controller.impl.GameOverEvent;
import de.htwg.se.ws1516.fourwinning.controller.impl.GameRunningState;
import de.htwg.se.ws1516.fourwinning.controller.impl.PlayerBuildState;
import de.htwg.se.ws1516.fourwinning.controller.impl.PlayerChangeEvent;
import de.htwg.se.ws1516.fourwinning.controller.impl.PlayerChangeState;
import de.htwg.se.ws1516.fourwinning.models.Feld;
import de.htwg.se.ws1516.fourwinning.models.Player;
import de.htwg.util.observer.Event;
import de.htwg.util.observer.IObserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Gui extends JFrame implements ActionListener, IObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8169521783214478709L;
	JButton[] einwerfen;
	JPanel einwerfenPanel;
	JTextField lSpieler;
	JMenuBar menueBar;
	JMenu datei;
	JMenuItem close;
	JMenu info;
	JMenuItem autor;
	JMenuItem newGame;
	JMenu zug;
	JMenuItem zugUndo;
	JMenuItem zugRedo;
	private GameMatrix gm;
	private int rows;
	private int columns;
	private GameController spiel;
	private Player eins;
	private Player zwei;
	private Player aktiv;
	private Feld[][] spielfeld;
	int waechsel = 0;

	// static int[] hoehe;


	public Gui(GameController spiel) throws IOException{
		createGameArea(spiel);
		spiel.addObserver(this);
	}

	public void createGameArea(GameController spiel) throws IOException {
		// Anzahl Spalten und Reihen und GUI bauen
		try {
			this.spiel=spiel;
			this.rows = Integer.parseInt(JOptionPane.showInputDialog("Anzahl Reihen"));
			this.columns = Integer.parseInt(JOptionPane.showInputDialog("Anzahl Spalten"));
			einwerfenPanel = new JPanel();
			spiel.setRows(rows);
			spiel.setColumns(columns);
			spiel.baueSpielfeld(rows, columns);
			menueBar = new JMenuBar();
			datei = new JMenu();
			datei.setText("Datei");
			menueBar.add(datei);
			close = new JMenuItem("Beenden");
			close.addActionListener(this);
			newGame = new JMenuItem("Neues Spiel");
			newGame.addActionListener(this);
			datei.add(newGame);
			datei.add(close);
			zug = new JMenu("Zug");
			menueBar.add(zug);
			zugUndo = new JMenuItem("Zug rückgängig machen");
			zugUndo.addActionListener(this);
			zug.add(zugUndo);
			zugRedo = new JMenuItem("Zug wiederholen");
			zugRedo.addActionListener(this);
			zug.add(zugRedo);
			info = new JMenu();
			info.setText("Info");
			menueBar.add(info);
			autor = new JMenuItem("Autor");
			autor.addActionListener(this);
			info.add(autor);
			setJMenuBar(menueBar);
			einwerfen = new JButton[columns];
			setTitle("4 gewinnt");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setLayout(new BorderLayout());
			this.setLocationRelativeTo(null);
		} catch (Exception x) {
			JOptionPane.showMessageDialog(null, "Ungültige Spielparameter eingegeben", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		createPlayers();
	}

	public void createPlayers() {
		try {
			String one = JOptionPane.showInputDialog("Name des ersten Spielers angeben.");
			String two = JOptionPane.showInputDialog("Name des zweiten Spielers angeben.");
			spiel.createPlayers(one, two);
			this.eins = spiel.getPlayerOne();
			this.zwei = spiel.getPlayerTwo();
			eins.setActive(true);
			zwei.setActive(false);
			einwerfenPanel.setLayout(new GridLayout(1, columns));
			this.add(einwerfenPanel, BorderLayout.NORTH);
			for (int i = 0; i < columns; i++) {
				einwerfen[i] = new JButton();
				einwerfen[i].setText(Integer.toString(i + 1));
				einwerfen[i].addActionListener(this);
				einwerfenPanel.add(einwerfen[i]);
			}

			this.gm = new GameMatrix(rows, columns);

			this.add(gm, BorderLayout.CENTER);
			lSpieler = new JTextField(spiel.aktiverSpieler().getName() + " ist am Zug!");

			this.add(lSpieler, BorderLayout.SOUTH);
			this.setResizable(true);
			this.setResizable(false);
			this.pack();

			setVisible(true);
		} catch (Exception x) {
			JOptionPane.showMessageDialog(null, "Ungültige Spielparameter eingegeben", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/*
	 * public void clearFeld() { for (int i = 0; i < rows; i++) { for (int j =
	 * 0; j < columns; j++) { this.gm.setLeer(i, j); }
	 * 
	 * } }
	 */

	public void setGelb(int rows, int columns) {
		this.gm.setGelb(rows, columns);
	}

	public void setRot(int rows, int columns) {
		this.gm.setRot(rows, columns);
	}

	public void setLeer(int rows, int columns) {
		this.gm.setLeer(rows, columns);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object quelle = e.getSource();
		playGame(quelle);
	}

	public void playGame(Object quelle) {
		if (quelle == zugUndo) {
			// aktiv = spiel.aktiverSpieler();
			spiel.undo();
			spielfeld = spiel.update();
			return;
		}

		if (quelle == zugRedo) {
			spiel.redo();
			spielfeld = spiel.update();
			ausgabe(spielfeld, rows, columns, eins, zwei);
			return;
		}

		if (quelle == close) {
			System.exit(0);
		}

		if (quelle == autor) {
			JOptionPane.showMessageDialog(null, "Sebastian Gerstmeier & Michael Merkle", "Autor",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		for (int i = 0; i < einwerfen.length; i++) {
			if (quelle == einwerfen[i]) {
				
				spielfeld = spiel.update();
				
				aktiv = spiel.aktiverSpieler();
				spiel.zug(i, aktiv);
				

				String whoHasWon = "";
				if (spiel.spielGewonnen(spielfeld, aktiv)) {
					whoHasWon = String.format("%n%s hat das Spiel gewonnen!%n", aktiv.getName());

					JOptionPane.showMessageDialog(null, whoHasWon, "Gewinner: " + aktiv.getName(),
							JOptionPane.INFORMATION_MESSAGE);
					
					return;
				}
				String draw;
				if (spiel.spielDraw(spielfeld)) {
					draw = "Unentschieden";
					JOptionPane.showMessageDialog(null, draw, "Unentschieden", JOptionPane.INFORMATION_MESSAGE);
					
					return;
				}
				
				spielerwaechsel();
			
				lSpieler.setText(spiel.aktiverSpieler().getName() + " ist am Zug!");

			}
			spiel.notifyObservers(new PlayerChangeEvent());
		}

	}

	public void spielerwaechsel() {
		
		//spiel.getState().nextState(spiel);
	}
	
	@Override
	public void update(Event e) {
		if(e == null){
			ausgabe(spielfeld, rows, columns, eins, zwei);
		} 
	}
	

	public void ausgabe(Feld[][] feld, int rows, int columns, Player eins, Player zwei) {
		feld = spiel.update();
		for (int k = 0; k < rows; k++) {
			for (int l = 0; l < columns; l++) {
				if (feld[k][l].getSet()) {
					if (feld[k][l].getOwner().getName().equals(eins.getName())) {
						this.setGelb(k, l);
						gm.repaint();
					} else if (feld[k][l].getOwner().getName().equals(zwei.getName())) {
						this.setRot(k, l);
						gm.repaint();
					}
				} else {
					this.setLeer(k, l);
					gm.repaint();
				}
			}

		}

	}
}
