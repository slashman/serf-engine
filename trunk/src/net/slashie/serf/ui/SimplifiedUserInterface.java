package net.slashie.serf.ui;

import java.util.*;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.utils.Debug;
import net.slashie.utils.Position;

/** 
 *  Shows the level
 *  Informs the Actions and Commands of the player.
 * 	Must be listening to a System Interface
 */

public abstract class SimplifiedUserInterface implements CommandListener {
	// Interactive functionalities
	public abstract void doLook();
    
	// Game message handling
	public abstract void addMessage(Message message);
	public abstract List<Message> getMessageBuffer();
	
	// Simple Input/Output methods
	public abstract boolean confirm (String message);
    public abstract int select (String prompt, String... options);
    public abstract String read(String prompt);
    public abstract String show(String message);
    
	//FOVMask provided as convenience for rendering (?)
    private boolean [][] FOVMask;
	public boolean isOnFOVMask(int x, int y){
		return FOVMask[x][y];
	}
	
	public void init(UserCommand[] gameCommands){
		FOVMask = new boolean[80][25];
		for (int i = 0; i < gameCommands.length; i++)
			this.gameCommands.put(gameCommands[i].getKeyCode()+"", gameCommands[i]);
		addCommandListener(this);
	}

	protected int getRelatedCommand(int keyCode){
		Debug.enterMethod(this, "getRelatedCommand", keyCode+"");
    	UserCommand uc = (UserCommand ) gameCommands.get(keyCode+"");
    	if (uc == null){
    		Debug.exitMethod(CommandListener.NONE);
    		return CommandListener.NONE;
    	}

    	int ret = uc.getCommand();
    	Debug.exitMethod(ret+"");
    	return ret;
	}
	
	protected void informPlayerCommand(int command) {
	    Debug.enterMethod(this, "informPlayerCommand", command+"");
	    for (int i =0; i < commandListeners.size(); i++){
	    	commandListeners.get(i).commandSelected(command);
	    }
		Debug.exitMethod();
    }
	
	public void addCommandListener(CommandListener pCl) {
		commandListeners.add(pCl);
    }
	
	public void removeCommandListener(CommandListener pCl){
		commandListeners.remove(pCl);
	}
	
	protected Map<String, UserCommand> gameCommands = new Hashtable<String, UserCommand>();
	
	private List<CommandListener> commandListeners = new ArrayList<CommandListener>(5);

	//Command Listener Implementation
	public void commandSelected (int commandCode){
		switch (commandCode){
			case CommandListener.PROMPTQUIT:
				processQuit();
				break;
			case CommandListener.PROMPTSAVE:
				processSave();
				break;
			case CommandListener.HELP:
				processHelp();
				break;
			case CommandListener.LOOK:
				doLook();
				break;
			case CommandListener.SHOWINVEN:
				showInventory();
				break;
			case CommandListener.SWITCHMUSIC:
				boolean enabled = STMusicManagerNew.thus.isEnabled();
				if (enabled){
					showMessage("Turn off music");
					STMusicManagerNew.thus.stopMusic();
					STMusicManagerNew.thus.setEnabled(false);
				} else {
					showMessage("Turn on music");
					STMusicManagerNew.thus.setEnabled(true);
					onMusicOn();
				}
				break;
		}
	}
	

	//TODO: Replace this with Game.isOver();
	private boolean gameOver;
	
	public void setGameOver(boolean bal){
		gameOver = bal;
	}
	
	public boolean gameOver(){
		return gameOver;
	}
	
	//	 Singleton
	private static UserInterface singleton;
	
	public static void setSingleton(UserInterface ui){
		singleton = ui;
	}
	public static UserInterface getUI (){
		return singleton;
	}
}