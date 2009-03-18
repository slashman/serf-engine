package net.slashie.serf.ui;

import net.slashie.utils.Position;

public abstract class EffectFactory {
	private static EffectFactory singleton;
	public static void setSingleton(EffectFactory ef){
		singleton = ef;
	}
	public static EffectFactory getSingleton(){
		return singleton;
	}
	
	public abstract Effect createDirectedEffect(Position start, Position end, String ID, int length);
	public abstract Effect createDirectionalEffect(Position start, int direction, int depth, String ID);
	public abstract Effect createLocatedEffect(Position location, String ID);
	
}
