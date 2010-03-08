package net.slashie.simpleRL.ui.console;

import java.io.File;

import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.simpleRL.domain.entities.Adventurer;
import net.slashie.simpleRL.ui.Display;
import net.slashie.utils.OutParameter;

public class CharDisplay extends Display{
	private ConsoleSystemInterface csi;
	public CharDisplay(ConsoleSystemInterface si) {
		this.csi = si;
	}
	
	@Override
	public void createPlayer(OutParameter nameOut) {
		csi.cls();
		csi.print(2, 2, "How should we call you, champion");
		csi.locateCaret(3, 3);
		csi.refresh();
		String name = csi.input(10);
		nameOut.setObject(name);
	}
	
	@Override
	public int showTitleScreen() {
		csi.cls();
		csi.print(2, 2, "SIMPLERL!!!!1!1!");
		csi.print(10, 4, "a. New Game");
		csi.print(10, 5, "b. Journey Onward");
		csi.print(10, 6, "c. Exit");
		csi.refresh();
    	STMusicManagerNew.thus.playKey("TITLE");
    	CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.A && x.code != CharKey.a &&
				x.code != CharKey.B && x.code != CharKey.b &&
				x.code != CharKey.C && x.code != CharKey.c)
			x = csi.inkey();
		csi.cls();
		switch (x.code){
		case CharKey.A: case CharKey.a:
			return 0;
		case CharKey.B: case CharKey.b:
			return 1;
		case CharKey.C: case CharKey.c:
			return 2;
		}
		return 0;
	}
	
	@Override
	public int showSavedGames(File[] saveFiles) {
		csi.cls();
		if (saveFiles == null || saveFiles.length == 0){
			csi.print(3,6, "No save files");
			csi.print(4,8, "[Space to Cancel]");
			csi.refresh();
			csi.waitKey(CharKey.SPACE);
			return -1;
		}
			
		csi.print(3,6, "Pick an save file");
		for (int i = 0; i < saveFiles.length; i++){
			csi.print(5,7+i, (char)(CharKey.a+i+1)+ " - "+ saveFiles[i].getName());
		}
		csi.print(3,9+saveFiles.length, "[Space to Cancel]");
		csi.refresh();
		CharKey x = csi.inkey();
		while ((x.code < CharKey.a || x.code > CharKey.a+saveFiles.length) && x.code != CharKey.SPACE){
			x = csi.inkey();
		}
		csi.cls();
		if (x.code == CharKey.SPACE)
			return -1;
		else
			return x.code - CharKey.a;
	}
	
	@Override
	public void showIntro(Adventurer adventurer) {
		csi.cls();
		csi.print(2,2, "Full of anger, "+adventurer.getName()+" ventures into the dungeon");
		csi.print(2,4, "Press space to continue", ConsoleSystemInterface.RED);
		csi.refresh();
		csi.waitKey(CharKey.SPACE);
		csi.cls();
	}
	
	@Override
	public void showWin(Adventurer adventurer) {
		csi.cls();
		csi.print(2,2, adventurer.getName()+" is crowned, etc.");
		csi.print(2,4, "Press space to continue", ConsoleSystemInterface.RED);
		csi.refresh();
		csi.waitKey(CharKey.SPACE);
	}
	
	@Override
	public void showHelp() {
		csi.saveBuffer();
		csi.cls();
		csi.print(2,4, "Just run around, dont bump into spikes. Find the Amulet of Vendor", ConsoleSystemInterface.RED);
		csi.refresh();
		csi.waitKey(CharKey.SPACE);
		csi.restore();
	}
}
