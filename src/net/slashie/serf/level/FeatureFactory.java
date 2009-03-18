package net.slashie.serf.level;

import java.util.*;

import net.slashie.utils.Debug;

public class FeatureFactory {
	private static FeatureFactory singleton = new FeatureFactory();
	private Map<String, AbstractFeature> definitions;
	
	public AbstractFeature buildFeature (String id) {
		AbstractFeature x = definitions.get(id);
		if (x != null)
			return (AbstractFeature) x.clone();
		Debug.byebye("Feature "+id+" not found");
		return null;
	}

	public String getDescriptionForID(String id){
		AbstractFeature x = (AbstractFeature) definitions.get(id);
		if (x != null)
			return x.getDescription();
		else
		return "?";
	}

	public void addDefinition(AbstractFeature definition){
		definitions.put(definition.getClassifierID(), definition);
	}
	
	public void init(AbstractFeature[] defs) {
		for (int i = 0; i < defs.length; i++)
			definitions.put(defs[i].getClassifierID(), defs[i]);
	}

	public FeatureFactory(){
		definitions = new Hashtable<String, AbstractFeature>(40);
	}

	public static FeatureFactory getFactory(){
		return singleton;
	}
}