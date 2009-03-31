package net.slashie.serf.ui;

import java.util.*;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.utils.Debug;

/** 
 *  Shows the level
 *  Informs the Actions and Commands of the player.
 * 	Must be listening to a System Interface
 */

public abstract class UserInterface implements CommandListener/*, Runnable*/{
	//Attributes
	public abstract String getQuitMessage();
	
	//Status
	protected Vector monstersOnSight = new Vector();
	protected Vector featuresOnSight = new Vector();
	protected Vector itemsOnSight = new Vector();
	protected Action actionSelectedByCommand;
	
	//Components
	
	protected boolean eraseOnArrival; // Erase the buffer upon the arrival of a new msg
   	
	protected String lastMessage; 
	protected AbstractLevel level;
    // Relations
	protected Player player;

	// Setters
	/** Sets the object which will be informed of the player commands.
     * this corresponds to the Game object */
	
	
	//Getters
    public Player getPlayer() {
		return player;
	}

    // Smart Getters
	
    
    //  Final attributes
    

    private boolean [][] FOVMask;
    //Interactive Methods
    public abstract void doLook();
    
    public abstract void chat (String message);
    
    public abstract boolean promptChat (String message);

    // Drawing Methods
	public abstract void drawEffect(Effect what);
	
	public boolean isOnFOVMask(int x, int y){
		return FOVMask[x][y];
	}

	public abstract void addMessage(Message message);
	public abstract List<Message> getMessageBuffer();

	public void setPlayer(Player pPlayer){
		player = pPlayer;
		level = player.getLevel();
	}

	public void init(UserCommand[] gameCommands){
		//uiSelector = selector;
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
	
	public abstract boolean isDisplaying(Actor who);

    public void levelChange(){
		level = player.getLevel();
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

    
    /**
     * Prompts for Yes or NO
     */
    public abstract boolean prompt ();

	public abstract void refresh();


 	/**
     * Shows a message inmediately; useful for system
     * messages.
     *  
     * @param x the message to be shown
     */
	public abstract void showMessage(String x);

	public abstract void showImportantMessage(String x);
	
	/**
     * Shows a message inmediately; useful for system
     * messages. Waits for a key press or something.
     *  
     * @param x the message to be shown
     */
	public abstract void showSystemMessage(String x);
	
	public abstract void processQuit();
	
	public abstract void processSave();
	
	public abstract void processHelp();
	
	public abstract void onMusicOn();
	
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
	
	public abstract void setTargets(Action a) throws ActionCancelException;
	public abstract void showMessageHistory();
	public abstract void showInventory();
	public abstract int switchChat(String prompt, String... options);
	public abstract String inputBox(String prompt);
}