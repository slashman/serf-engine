package net.slashie.simpleRL.ui.console;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.textcomponents.MenuBox;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.libjcsi.textcomponents.TextBox;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.simpleRL.domain.entities.Adventurer;
import net.slashie.simpleRL.domain.entities.Monster;
import net.slashie.simpleRL.domain.world.Level;
import net.slashie.simpleRL.ui.SimpleRLUserInterface;

public class CharSimpleRLUserInterface extends ConsoleUserInterface implements SimpleRLUserInterface {
	private ConsoleSystemInterface csi;
	
	public CharSimpleRLUserInterface(ConsoleSystemInterface csi) {
		this.csi = csi;
	}
	
	@Override
	public void onMusicOn() {
		Level level = (Level) getPlayer().getLevel();
		if (level.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(level.getMusicKey());
	}
	
	@Override
	public String getQuitPrompt() {
		return "Do you want to quit?";
	}
	
	@Override
	public void drawStatus() {
		Adventurer a = (Adventurer) getPlayer();
		csi.print(2, 1, a.getName(), ConsoleSystemInterface.BLUE);
		csi.print(2, 2, "HP: ", ConsoleSystemInterface.RED);
		csi.print(6, 2, a.getHp()+"   ");
		csi.print(30, 1, "                      ", ConsoleSystemInterface.YELLOW);
		csi.print(30, 1, a.getLevel().getDescription(), ConsoleSystemInterface.YELLOW);
	}

	@Override
	public void showDetailedInfo(Actor a) {
		csi.saveBuffer();
		csi.cls();
		if (a instanceof Monster){
			Monster m = (Monster)a;
			csi.print(2, 2, m.getDescription());
			csi.print(2, 4, m.getPowers());
		}
		csi.print(5, 4, "Press Space");
		csi.waitKey(CharKey.SPACE);
		csi.restore();
	}
	
	@Override
	public void showInventory() {
		Equipment.eqMode = true;
		int xpos = 1, ypos = 0;
  		MenuBox menuBox = new MenuBox(csi);
  		menuBox.setHeight(17);
  		menuBox.setWidth(50);
  		menuBox.setPosition(1,5);
  		menuBox.setBorder(false);
  		TextBox itemDescription = new TextBox(csi);
  		itemDescription.setBounds(52,9,25,5);
  		csi.saveBuffer();
  		csi.cls();
  		csi.print(xpos,ypos,    "------------------------------------------------------------------------", ConsoleSystemInterface.BLUE);
  		csi.print(xpos,ypos+1,  "Inventory", ConsoleSystemInterface.WHITE);
  		csi.print(xpos,ypos+2,    "------------------------------------------------------------------------", ConsoleSystemInterface.BLUE);
  		csi.print(xpos,24,  "[Space] to continue, Up and Down to browse");
  		int choice = 0;
  		while (true){
  	  		List<Equipment> inventory = getPlayer().getInventory();
  	  		Vector menuItems = new Vector();
  	  		for (Equipment item: inventory){
  	  			menuItems.add(item);
  	  		}
  	  		menuBox.setMenuItems(menuItems);
  	  		menuBox.draw();
  	  		csi.refresh();
  	  		
	  		CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.SPACE && !x.isArrow())
				x = csi.inkey();
			if (x.code == CharKey.SPACE || x.code == CharKey.ESC){
				break;
			}
  		}
 		
		csi.restore();
		csi.refresh();
		Equipment.eqMode = false;	
	}
	
	@Override
	public int switchChat(String title, String prompt, String... options) {
		MenuBox selectionBox = new MenuBox(csi);
		selectionBox.setPosition(20,2);
		selectionBox.setWidth(31);
		selectionBox.setHeight(8);
  		Vector<MenuItem> menuItems = new Vector<MenuItem>();
  		int i = 0;
  		for (String option: options){
  			menuItems.add(new SimpleItem(i,option));
  			i++;
  		}
  		selectionBox.setMenuItems(menuItems);
  		selectionBox.setPromptSize(2);
  		selectionBox.setBorder(true);
  		selectionBox.setPrompt(prompt);
  		selectionBox.setTitle(title);
  		selectionBox.draw();
  		
		while (true) {
			csi.refresh();
			SimpleItem itemChoice = ((SimpleItem)selectionBox.getSelection());
			if (itemChoice == null)
				break;
			return itemChoice.getValue();
		}
		return -1;	
	}
}

class SimpleItem implements MenuItem{
	private String text;
	private int value;

	SimpleItem (int value, String text){
		this.text = text;
		this.value = value;
	}
	
	public char getMenuChar() {
		return '*';
	}
	
	public int getMenuColor() {
		return ConsoleSystemInterface.WHITE;
	}
	
	public String getMenuDescription() {
		return text;
	}
	
	public int getValue(){
		return value;
	}
}