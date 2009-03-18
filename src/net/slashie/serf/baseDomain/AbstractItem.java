package net.slashie.serf.baseDomain;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.serf.game.Player;
import net.slashie.serf.ui.Appearance;


public abstract class AbstractItem implements Serializable, MenuItem{

	public abstract boolean isVisible();
	
	public abstract String getDescription();
	
	public abstract Appearance getAppearance();

	public abstract String getFullID();
	
	protected Hashtable<String, Integer> hashCounters = new Hashtable<String, Integer>();
	public void setCounter(String counterID, int turns){
		hashCounters.put(counterID, new Integer(turns));
	}
	
	public int getCounter(String counterID){
		Integer val = (Integer)hashCounters.get(counterID);
		if (val == null)
			return -1;
		else
			return val.intValue();
	}
	
	public boolean hasCounter(String counterID){
		return getCounter(counterID) > 0;
	}
	
	public void reduceCounters(Player p){
		Enumeration<String> countersKeys = hashCounters.keys();
		while (countersKeys.hasMoreElements()){
			String key = (String) countersKeys.nextElement();
			Integer counter = (Integer)hashCounters.get(key);
			if (counter.intValue() == 0){
				onItemCounterZeroed(key);
				hashCounters.remove(key);
			} else {
				hashCounters.put(key, new Integer(counter.intValue()-1));
			}
		}
	}
	
	public abstract void onItemCounterZeroed(String key);
	
	public boolean hasCounters(){
		return hashCounters.size() > 0;
	}

}
