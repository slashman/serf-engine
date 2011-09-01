package net.slashie.simpleRL.controller;

import java.util.Calendar;

import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Player;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.LevelMetaData;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.CommandListener;
import net.slashie.serf.ui.UserInterface;
import net.slashie.simpleRL.domain.entities.Adventurer;
import net.slashie.simpleRL.domain.world.Level;
import net.slashie.simpleRL.ui.Display;
import net.slashie.utils.OutParameter;

public class SimpleRL extends SworeGame{
	private static SimpleRL currentGame;
	
	@Override
	public AbstractLevel createLevel(LevelMetaData metadata) {
		return LevelMaster.createLevel(metadata, getPlayer());
	}

	@Override
	public Player generatePlayer(int gameType, SworeGame game) {
		OutParameter name = new OutParameter();
		Display.thus.createPlayer(name);
		return AdventurerGenerator.getAdventurerObject((String)name.getObject());
	}

	@Override
	public String getDeathMessage() {
		return "Your Die..";
	}

	@Override
	public String getFirstMessage(Actor player) {
		return "Go now, Champion!";
	}

	@Override
	public void onGameResume() {
		currentGame = this;
		Level expeditionLevel = (Level)getPlayer().getLevel();
		if (expeditionLevel.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(expeditionLevel.getMusicKey());
	}

	@Override
	public void onGameStart(int gameType) {
		currentGame = this;
		Display.thus.showIntro((Adventurer)getPlayer());
		loadMetadata();
		loadLevel("TOWN");
		currentGame.setPlayer(getPlayer());
	}

	private void loadMetadata() {
		LevelMetaData md = null;
		
		md = new LevelMetaData("TOWN");
		addMetaData("TOWN", md);
		
		md = new LevelMetaData("LEVEL_1");
		md.addExits("TOWN","_BACK");
		md.addExits("LEVEL_2","_NEXT");
		addMetaData("LEVEL_1", md);
		
		md = new LevelMetaData("LEVEL_2");
		md.addExits("LEVEL_1","_BACK");
		md.addExits("AMULET_ROOM","_NEXT");
		addMetaData("LEVEL_2", md);
		
		md = new LevelMetaData("AMULET_ROOM");
		md.addExits("LEVEL_2","_BACK");
		addMetaData("AMULET_ROOM", md);
	}

	@Override
	public void onGameWon() {
		Display.thus.showWin((Adventurer)getPlayer());
		System.exit(0);
	}

	@Override
	public void onLevelLoad(AbstractLevel aLevel) {
		Level level = (Level)aLevel;
		if (level.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(level.getMusicKey());
	}
	
	public static SimpleRL getCurrentGame() {
		return currentGame;
	}

	public void commandSelected (Command commandCode){
		super.commandSelected(commandCode);
		switch (commandCode){
		case HELP:
			Display.thus.showHelp();
			break;
		}
	}
	


}
