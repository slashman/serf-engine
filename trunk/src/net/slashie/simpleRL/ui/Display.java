package net.slashie.simpleRL.ui;

import java.io.File;

import net.slashie.simpleRL.domain.entities.Adventurer;
import net.slashie.utils.OutParameter;

public abstract class Display {
	public static Display thus;
	
	public abstract void createPlayer(OutParameter name);
	public abstract int showTitleScreen();
	public abstract int showSavedGames(File[] saves);
	public abstract void showIntro(Adventurer adventurer);
	public abstract void showWin(Adventurer adventurer);
	public abstract void showHelp();
}
