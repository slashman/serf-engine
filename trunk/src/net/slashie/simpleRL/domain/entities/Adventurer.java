package net.slashie.simpleRL.domain.entities;

import java.util.List;

import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.simpleRL.domain.item.DungeonItem;
import net.slashie.simpleRL.domain.world.LevelCell;

public class Adventurer extends Player{
	
	public Adventurer() {
		setAppearanceId("ADVENTURER");
	}
	
	private final static int MAX_ITEMS = 10;
	private String name;
	private List<DungeonItem> equipedItems;
	private int hp;
	private int speed;

	@Override
	public String getStatusString() {
		return "HP: "+hp;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean canCarry(AbstractItem item, int quantity) {
		return getInventory().size() < MAX_ITEMS;
	}
	
	@Override
	public String getSaveFilename() {
		return name;
	}
	
	@Override
	public String getClassifierID() {
		// Used to identify this as an actor
		return "PLAYER_ADVENTURER";
	}
	
	@Override
	public int getDarkSightRange() {
		// The dark sight is the extended area of the LOS where the player can perceive other light sources
		return 10;
	}
	
	@Override
	public int getSightRangeInCells() {
		return 5;
	}
	
	@Override
	public int getSightRangeInDots() {
		return 5;
	}
	
	@Override
	public List<? extends AbstractItem> getEquippedItems() {
		return equipedItems;
	}
	
	@Override
	public void onCellStep(AbstractCell acell) {
		LevelCell cell = (LevelCell)acell;
		if (cell.isSpike()){
			damage(1);
		}
	}
	
	private void damage(int d){
		this.hp -= d;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getHp() {
		return hp;
	}
	
}
