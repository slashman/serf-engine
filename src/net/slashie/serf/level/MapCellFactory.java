package net.slashie.serf.level;

import java.util.*;

import net.slashie.serf.SworeException;

public class MapCellFactory {
	private static MapCellFactory singleton = new MapCellFactory();
	private Map<String, AbstractCell> definitions;

	public static MapCellFactory getMapCellFactory(){
		return singleton;
    }

	public AbstractCell getMapCell (String id) throws SworeException{
		AbstractCell ret = (AbstractCell) definitions.get(id);
		if (ret != null)
			if (ret.cloneRequired())
				return ret.clone();
			else
				return ret;
		throw new SworeException("MapCellID " +id +" not found");
	}

	public void addDefinition(AbstractCell definition){
		definitions.put(definition.getID(), definition);
	}

	public MapCellFactory(){
		definitions = new Hashtable<String, AbstractCell>(40);
	}

	public void init(AbstractCell[] defs) {
		for (int i = 0; i < defs.length; i++)
			definitions.put(defs[i].getID(), defs[i]);
	}
	
	public void init(List<AbstractCell> defs) {
		for (int i = 0; i < defs.size(); i++)
			definitions.put(defs.get(i).getID(), defs.get(i));
	}

}