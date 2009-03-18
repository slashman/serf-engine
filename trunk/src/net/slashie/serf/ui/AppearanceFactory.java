package net.slashie.serf.ui;

import java.util.*;
import net.slashie.utils.*;

public class AppearanceFactory {
	private Hashtable<String, Appearance> definitions;
	private static AppearanceFactory singleton = new AppearanceFactory();

	public static AppearanceFactory getAppearanceFactory(){
		return singleton;
	}

	public Appearance getAppearance (String id){
		Appearance ret = (Appearance) definitions.get(id);
		Debug.doAssert(ret != null, "Couldnt find the appearance "+id);
		return ret;
	}

	public void addDefinition(Appearance definition){
		definitions.put(definition.getID(), definition);
	}

	public AppearanceFactory(){
		definitions = new Hashtable<String, Appearance>(40);
	}

}