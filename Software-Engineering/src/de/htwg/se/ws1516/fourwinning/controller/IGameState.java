package de.htwg.se.ws1516.fourwinning.controller;

import de.htwg.se.ws1516.fourwinning.controller.impl.*;

public interface IGameState {
	void nextState(GameController game);
}
