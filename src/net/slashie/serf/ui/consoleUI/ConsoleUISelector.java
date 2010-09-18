package net.slashie.serf.ui.consoleUI;

import java.util.Properties;

import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.UISelector;
import net.slashie.serf.ui.UserAction;
import net.slashie.utils.Position;

public class ConsoleUISelector extends UISelector {
	public static final int STUMBLE_NOTHING = 0;
	public static final int STUMBLE_ATTACK = 1;
	public static final int STUMBLE_WALK = 2;
	private ConsoleSystemInterface si;
	public ConsoleUserInterface ui(){
		return (ConsoleUserInterface) getUI();
	}
	
	public void init(ConsoleSystemInterface csi, UserAction[] gameActions, Action advance, Action target, Action attack, ConsoleUserInterface ui, Properties keybindings){
		super.init(gameActions, advance, target, attack, ui, keybindings);
		this.si = csi;
	}
	
	/** 
	 * Returns the Action that the player wants to perform.
     * It may also forward a command instead
     * 
     */
	public Action selectAction(Actor who){
	    CharKey input = null;
	    Action ret = null;
	    while (ret == null){
	    	if (ui().gameOver())
	    		return null;
			input = si.inkey();
			ret = ui().selectCommand(input);
			if (ret != null)
				return ret;
			if (input.code == CharKey.DOT) {
				
				return null;
			}
			if (input.code == CharKey.DELETE) {
				
				return null;
			}
			/*if (cheatConsole(input)){
				continue;
			}*/
			if (input.isArrow()){
				int direction = Action.toIntDirection(input);
				Actor vMonster = player.getLevel().getActorAt(Position.add(player.getPosition(), Action.directionToVariation(direction)));
				if (vMonster != null && vMonster.isHostile() && attack.canPerform(player)){
					advance.setDirection(direction);
					if (advance.canPerform(player)){
						return advance;
					} else {
						level.addMessage(advance.getInvalidationMessage());
						si.refresh();
						return null;
					}
				} else {
					switch (onActorStumble(vMonster)){
					case STUMBLE_ATTACK: //Attack the actor
						if (attack.canPerform(player)){
							attack.setDirection(direction);
							return attack;
						} else {
							level.addMessage(attack.getInvalidationMessage());
							si.refresh();
						}
						break;
					case STUMBLE_WALK: //Walk into the actor
						advance.setDirection(direction);
						return advance;
					}
				}
			} else if (input.code == WEAPONCODE){
				//Not clear what to do here...
			}else{
            	ret = getRelatedAction(input.code);
            	try {
	            	if (ret != null){
	            		ret.setPerformer(player);
	            		if (ret.canPerform(player))
	            			ui().setTargets(ret);
	            		else {
	            			level.addMessage(ret.getInvalidationMessage());
	            			si.refresh();
	            			throw new ActionCancelException();
	            		}
                    	return ret;
					}

				}
				catch (ActionCancelException ace){
					ui().addMessage(new Message("Cancelled", player.getPosition()));
					ret = null;
				}
			}
		}
		return null;
	}
	
	public String getID(){
		return "UI";
	}
    
	public ActionSelector derive(){
 		return null;
 	}
	
	private static final int WEAPONCODE = CharKey.SPACE;
	
	public int onActorStumble(Actor actor){return STUMBLE_WALK;};
	
}
