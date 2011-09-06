package net.slashie.serf.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.utils.Position;

public class EnvironmentInfo {
	private AbstractCell[][] cellsAround;
	private Map<String, List<AbstractFeature>> featuresAround = new HashMap<String, List<AbstractFeature>>();
	private Map<String, AbstractItem> itemsAround = new HashMap<String, AbstractItem>();
	private Map<String, Actor> actorsAround = new HashMap<String, Actor>();
	
	public AbstractCell[][] getCellsAround() {
		return cellsAround;
	}
	public void setCellsAround(AbstractCell[][] cellsAround) {
		this.cellsAround = cellsAround;
	}
	
	public void addFeature(int relativeX, int relativeY, List<AbstractFeature> features) {
		featuresAround.put(relativeX+"-"+relativeY, features);
	}
	public void addItem(int relativeX, int relativeY, AbstractItem item) {
		itemsAround.put(relativeX+"-"+relativeY, item);
	}
	public void addActor(int relativeX, int relativeY, Actor actor) {
		actorsAround.put(relativeX+"-"+relativeY, actor);
	}
	
	public List<AbstractFeature> getFeaturesAt(Position runner) {
		return featuresAround.get(runner.x+"-"+runner.y);
	}
	
	public AbstractItem getItemAt(Position runner) {
		return itemsAround.get(runner.x+"-"+runner.y);
	}
	public Actor getActorAt(Position runner) {
		return actorsAround.get(runner.x+"-"+runner.y);
	}
	
	
}
