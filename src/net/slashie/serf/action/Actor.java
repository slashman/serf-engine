package net.slashie.serf.action;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.sound.SFXManager;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.utils.Debug;
import net.slashie.utils.Position;
import net.slashie.utils.PriorityEnqueable;

public abstract class Actor implements Cloneable, java.io.Serializable, PriorityEnqueable{
	static final long serialVersionUID = 1L; 
	
	protected /*transient*/ int positionx, positiony, positionz;
	private transient Appearance appearance;
	private String appearanceId;
	
	protected ActionSelector selector;
	private /*transient*/ Position position = new Position(0,0,0);
	private /*transient*/ int nextTime=10;
	
	private boolean wasSeen;
	
	public int getCost(){
		//Debug.say("Cost of "+getDescription()+" "+ nextTime);
		return nextTime;
	}
	
	public void reduceCost(int value){
		//Debug.say("Reducing cost of "+getDescription()+"by"+value+" (from "+nextTime+")");
		nextTime = nextTime - value;
	}
	
	public void setNextTime(int value){
		//Debug.say("Next time for "+getDescription()+" "+ value);
		nextTime = value;
	}

	protected AbstractLevel level;

	//CallBack
	public void counterFinished(String counterId) {};
	
	public void updateStatus(){
		wasSeen = false;
		Set<String> counters = hashCounters.keySet();
		for (String key: counters){
			Integer counter = (Integer)hashCounters.get(key);
			if (counter.intValue() == 0){
				counterFinished(key);
				hashCounters.remove(key);
			} else {
				hashCounters.put(key, new Integer(counter.intValue()-1));
			}
		}
	}

	public abstract String getDescription();

	/**
	 * Makes the actor try to perform the chosen action. 
	 * @param x The action to be performed
	 * @return false if the actor could not perform the action, true otherwise
	 */
	public boolean execute(Action x){
		if (x != null){
        	x.setPerformer(this);
        	if (x.canPerform(this)){
	        	if (x.getSFX() != null)
	        		SFXManager.play(x.getSFX());
				x.execute();
				setNextTime(x.getCost());
				updateStatus();
				return true;
        	} else {
        		return false;
        	}
		} else {
			//Null action, do nothing
			setNextTime(0);
			return true;
		}
		
	}
	public boolean act(){
		if (getSelector() == null)
			setSelector(new NullSelector());
		Action x = getSelector().selectAction(this);
		return execute(x);
	}

	public void setPosition (int x, int y, int z){
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public void die(){
		/** Request to be removed from any dispatcher or structure */
		aWannaDie = true;
	}

	public boolean wannaDie(){
		return aWannaDie;
	}

	private boolean aWannaDie;


	public void setPosition (Position p){
		position = p;
	}

	public Position getPosition(){
		return position;
	}

	public void setLevel(AbstractLevel what){
		level = what;
	}

	public AbstractLevel getLevel(){
		return level;
	}

	public ActionSelector getSelector() {
		return selector;
	}

	public void setSelector(ActionSelector value) {
		selector = value;
	}

	public Appearance getAppearance() {
		if (appearance == null)
			appearance = AppearanceFactory.getAppearanceFactory().getAppearance(appearanceId);
		return appearance;
	}

	public void setAppearanceId(String appearanceId) {
		this.appearanceId = appearanceId;
	}

	public Object clone(){
		try {
			Actor x = (Actor) super.clone();
			if (position != null)
				x.setPosition(new Position(position.x, position.y, position.z));
			return x;
		} catch (CloneNotSupportedException cnse){
			Debug.doAssert(false, "failed class cast, Feature.clone()");
		}
		return null;
	}


	public void message(String mess){
	}
	
	protected Map<String, Integer> hashCounters = new Hashtable<String, Integer>();
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
	
	private Map<String, Boolean> hashFlags = new Hashtable<String, Boolean>();
	public void setFlag(String flagID, boolean value){
		hashFlags.put(flagID, new Boolean(value));
	}
	
	public boolean getFlag(String flagID){
		Boolean val =(Boolean)hashFlags.get(flagID); 
		return val != null && val.booleanValue();
	}

	public boolean wasSeen() {
		return wasSeen;
	}

	public void setWasSeen(boolean wasSeen) {
		this.wasSeen = wasSeen;
	}

	public abstract String getClassifierID();
	
	/**
	 * Determines if the User Interface can show a detailed info view
	 * @return
	 */
	public boolean extendedInfoAvailable(){
		return false;
	}
	
	/**
	 * Determines if the player must be shown to the user (May have additional gameplay effects)
	 * @return
	 */
	public boolean isInvisible() {
		return false;
	}

	public boolean isHostile() {
		return false;
	}
}