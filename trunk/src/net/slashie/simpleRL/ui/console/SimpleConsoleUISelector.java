package net.slashie.simpleRL.ui.console;

import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.consoleUI.ConsoleUISelector;

public class SimpleConsoleUISelector extends ConsoleUISelector{
	@Override
	public int onActorStumble(Actor actor) {
		return 0;
	}

}
