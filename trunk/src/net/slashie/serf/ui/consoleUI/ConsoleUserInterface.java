package net.slashie.serf.ui.consoleUI;

import java.util.*;


import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.EnvironmentInfo;
import net.slashie.serf.action.Message;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.GameSessionInfo;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.CommandListener;
import net.slashie.serf.ui.Effect;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.consoleUI.effects.CharEffect;
import net.slashie.utils.Line;
import net.slashie.utils.Position;

import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.textcomponents.BasicListItem;
import net.slashie.libjcsi.textcomponents.ListBox;
import net.slashie.libjcsi.textcomponents.MenuBox;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.libjcsi.textcomponents.TextBox;
import net.slashie.libjcsi.textcomponents.TextInformBox;

/** 
 *  Shows the level using characters.
 *  Informs the Actions and Commands of the player.
 * 	Must be listening to a System Interface
 */

public abstract class ConsoleUserInterface extends UserInterface implements CommandListener, Runnable{
	
	//Attributes
	protected int xrange = 8;
	protected int yrange = 8;
	
	public Position
		VP_START = new Position(1,3),
		VP_END = new Position (51,21),
		PC_POS = new Position (25,12);
	
	//Components
	protected TextInformBox messageBox;
	protected ListBox idList;
	
	private boolean eraseOnArrival; // Erase the buffer upon the arrival of a new msg
	
	private Map<String, BasicListItem> sightListItems = new Hashtable<String, BasicListItem>();
	// Relations

 	private transient ConsoleSystemInterface si;

	// Setters
	/** Sets the object which will be informed of the player commands.
     * this corresponds to the Game object */
	
	//Getters

    // Smart Getters
    public Position getAbsolutePosition(Position insideLevel){
    	Position relative = Position.subs(insideLevel, player.getPosition());
		return Position.add(PC_POS, relative);
	}

	

    private boolean [][] FOVMask;
    //Interactive Methods
    public void doLook(){
		Position offset = new Position (0,0);
		messageBox.setForeColor(ConsoleSystemInterface.WHITE);
		refresh();
		si.saveBuffer();
		Actor lookedActor = null;
		while (true){
			Position browser = Position.add(player.getPosition(), offset);
			String looked = "";
			if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]){
				AbstractCell choosen = level.getMapCell(browser);
				List<AbstractFeature> feats = level.getFeaturesAt(browser);
				List<AbstractItem> items = level.getItemsAt(browser);
				AbstractItem item = null;
				if (items != null) {
					item = items.get(0);
				}
				Actor actor = level.getActorAt(browser);
				if (choosen != null)
					looked += choosen.getDescription();
				if (feats != null){
					for (AbstractFeature feat: feats){
						looked += ", "+ feat.getDescription();
					}
				}
					
				if (item != null)
					if (items.size() == 1)
						looked += ", "+ item.getDescription();
					else
						looked += ", "+ item.getDescription()+" and some items";
				if (actor != null) {
					if (actor.extendedInfoAvailable()){
						looked += ", "+ actor.getDescription()+" ['m' for extended info]";
						lookedActor = actor;
					} else{
						looked += ", "+ actor.getDescription();
					}
				}
			}
			si.restore();
			
			messageBox.setText(looked);
			messageBox.draw();
			si.print(PC_POS.x + offset.x, PC_POS.y + offset.y, '_', ConsoleSystemInterface.WHITE);
			si.refresh();
			CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.ENTER && x.code != CharKey.SPACE && x.code != CharKey.m && x.code != CharKey.ESC &&
				   ! x.isArrow())
				x = si.inkey();
			if (x.code == CharKey.ENTER || x.code == CharKey.SPACE || x.code == CharKey.ESC){
				si.restore();
				break;
			}
			if (x.code == CharKey.m){
				if (lookedActor != null)
					showDetailedInfo(lookedActor);
			} else {
				offset.add(Action.directionToVariation(Action.toIntDirection(x)));
	
				if (offset.x >= xrange) offset.x = xrange;
				if (offset.x <= -xrange) offset.x = -xrange;
				if (offset.y >= yrange) offset.y = yrange;
				if (offset.y <= -yrange) offset.y = -yrange;
			}
			si.refresh();
     	}
		messageBox.setText("Look mode off");
		refresh();
	}

    public abstract void showDetailedInfo(Actor a);
    
    public void beforeSeenListCompilation(){}
    
    // Drawing Methods
    @Override
	public void drawEffect(Effect what){
		//Debug.enterMethod(this, "drawEffect", what);
		if (what == null)
			return;
		//drawLevel();
		if (insideViewPort(getAbsolutePosition(what.getPosition()))){
			si.refresh();
			si.setAutoRefresh(true);
			((CharEffect)what).drawEffect(this, si);
			si.setAutoRefresh(false);
		}
		//Debug.exitMethod();
	}
	
	public boolean isOnFOVMask(int x, int y){
		return FOVMask[x][y];
	}

	private void drawLevel(){
		//Cell[] [] cells = level.getCellsAround(player.getPosition().x,player.getPosition().y, player.getPosition().z, range);
		AbstractCell[] [] rcells = level.getMemoryCellsAround(player.getPosition().x,player.getPosition().y, player.getPosition().z, xrange,yrange);
		EnvironmentInfo environmentInfo = level.getEnvironmentAroundActor(player, player.getPosition().x,player.getPosition().y, player.getPosition().z, xrange,yrange);
		
		Position runner = new Position(0,0);
		monstersOnSight.removeAllElements();
		featuresOnSight.removeAllElements();
		itemsOnSight.removeAllElements();

		for (int x = 0; x < rcells.length; x++){
			for (int y=0; y<rcells[0].length; y++){
				if (rcells[x][y] != null && !rcells[x][y].getAppearance().getID().equals("NOTHING")){
					CharAppearance app = (CharAppearance)rcells[x][y].getAppearance(); 
					char cellChar = app.getChar();
					if (environmentInfo.getCellsAround()[x][y] == null)
						si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y, cellChar, ConsoleSystemInterface.GRAY);
				} else if (environmentInfo.getCellsAround()[x][y] == null || environmentInfo.getCellsAround()[x][y].getID().equals("AIR")){
					si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y, CharAppearance.getVoidAppearance().getChar(), CharAppearance.BLACK);
					
					
				}
			}
		}
		
		
	
		
		for (int x = 0; x < environmentInfo.getCellsAround().length; x++){
			runner.x = x - xrange;
			for (int y=0; y<environmentInfo.getCellsAround()[0].length; y++){
				runner.y = y - yrange;
				FOVMask[PC_POS.x-xrange+x][PC_POS.y-yrange+y] = false;
				if (environmentInfo.getCellsAround()[x][y] != null){
					FOVMask[PC_POS.x-xrange+x][PC_POS.y-yrange+y] = true;
					
					CharAppearance cellApp = (CharAppearance)environmentInfo.getCellsAround()[x][y].getAppearance();
					int cellColor = cellApp.getColor();
					char cellChar = cellApp.getChar();
					
					if (player.isInvisible() || x!=xrange || y != yrange)
						si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y, cellChar, cellColor);
					List<AbstractFeature> feats = environmentInfo.getFeaturesAt(runner);
					if (feats != null){
						for (AbstractFeature feat: feats){
							if (feat.isVisible()) {
								BasicListItem li = sightListItems.get(feat.getClassifierID());
								if (li == null){
									sightListItems.put(feat.getClassifierID(), new BasicListItem(((CharAppearance)feat.getAppearance()).getChar(), ((CharAppearance)feat.getAppearance()).getColor(), feat.getDescription()));
									li = (BasicListItem)sightListItems.get(feat.getClassifierID());
								}
								if (feat.isRelevant() && !featuresOnSight.contains(li)) 
									featuresOnSight.add(li);
								CharAppearance featApp = (CharAppearance)feat.getAppearance();
								si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y, featApp.getChar(), featApp.getColor());
							}
						}
					}
					
					drawAfterCells(runner,PC_POS.x-xrange+x,PC_POS.y-yrange+y);
					
					AbstractItem item = environmentInfo.getItemAt(runner);
					if (item != null){
						if (item.isVisible()){
							CharAppearance itemApp = (CharAppearance)item.getAppearance();
							si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y, itemApp.getChar(), itemApp.getColor());
							BasicListItem li = sightListItems.get(item.getFullID());
							if (li == null){
								//Debug.say("Adding "+item.getDefinition().getID()+" to the hashtable");
								sightListItems.put(item.getFullID(), new BasicListItem(((CharAppearance)item.getAppearance()).getChar(), ((CharAppearance)item.getAppearance()).getColor(), item.getDescription()));
								li = sightListItems.get(item.getFullID());
							}
							if (!itemsOnSight.contains(li))
								itemsOnSight.add(li);
						}
					}
					
					Actor monster = environmentInfo.getActorAt(runner);
					if (monster != null && !monster.isInvisible()){
						BasicListItem li = sightListItems.get(monster.getClassifierID());
						if (li == null){
							CharAppearance monsterApp = (CharAppearance)monster.getAppearance();
							sightListItems.put(monster.getClassifierID(), new BasicListItem(monsterApp.getChar(), monsterApp.getColor(), monster.getDescription()));
							li = (BasicListItem)sightListItems.get(monster.getClassifierID());
						}
						
						if (!monstersOnSight.contains(li))
							monstersOnSight.add(li);
						
						CharAppearance monsterApp = (CharAppearance) monster.getAppearance();
						si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y, monsterApp.getChar(), monsterApp.getColor());
					}
					
					if (!player.isInvisible()){
						si.print(PC_POS.x,PC_POS.y, ((CharAppearance)player.getAppearance()).getChar(), ((CharAppearance)player.getAppearance()).getColor());
					} else {
						si.print(PC_POS.x,PC_POS.y, ((CharAppearance)AppearanceFactory.getAppearanceFactory().getAppearance("SHADOW")).getChar(), ((CharAppearance)AppearanceFactory.getAppearanceFactory().getAppearance("SHADOW")).getColor());
					}
				} else {
					
				}
			}
		}
		
		idList.clear();
		beforeSeenListCompilation();
		idList.addElements(monstersOnSight);
		idList.addElements(itemsOnSight);
		idList.addElements(featuresOnSight);
	}
	
	public void drawAfterCells(Position runner, int x, int y) {
		
	}

	public void resetMessages(){
 		messageBox.clear();
	}

	private Vector messageHistory = new Vector(20,10);
	@Override
	public void addMessage(Message message){
		if (eraseOnArrival){
	 		messageBox.clear();
	 		messageBox.setForeColor(ConsoleSystemInterface.WHITE);
	 		eraseOnArrival = false;
		}
		if ((player != null && player.getPosition() != null && message.getLocation().z != player.getPosition().z) || (message.getLocation() != null && !insideViewPort(getAbsolutePosition(message.getLocation())))){
			return;
		}
		messageHistory.add(message.getText());
		if (messageHistory.size()>100)
			messageHistory.removeElementAt(0);
		messageBox.addText(message.getText()+" / ");
		
		messageBox.draw();
	}

    public abstract void drawStatus();
    
    private String fill(String str, int limit){
    	if (str.length() > limit)
    		return str.substring(0,limit);
    	else
    		return str+spaces(limit-str.length());
    }

    private Hashtable hashSpaces =  new Hashtable();
    private String spaces(int n){
    	String ret = (String)hashSpaces.get(n+"");
    	if (ret != null)
    		return ret;
    	ret = "";
    	for (int i = 0; i < n; i++)
    		ret +=" ";
    	hashSpaces.put(n+"", ret);
    	return ret;
    }
    private Action target;
	public void init(ConsoleSystemInterface psi, UserCommand[] gameCommands, Action target){
		this.target = target;
		super.init(gameCommands);
		messageBox = new TextInformBox(psi);
		idList = new ListBox(psi);
		
		messageBox.setPosition(1,22);
		messageBox.setWidth(78);
		messageBox.setHeight(2);
		messageBox.setForeColor(ConsoleSystemInterface.WHITE);
		
		/*monstersList.setPosition(2, 4);
		monstersList.setWidth(27);
		monstersList.setHeight(10);*/
		
		idList.setPosition(52,4);
		idList.setWidth(27);
		idList.setHeight(18);
		
		si = psi;
		FOVMask = new boolean[80][25];
	}

	/** 
	 * Checks if the point, relative to the console coordinates, is inside the
	 * ViewPort 
	 */
	public boolean insideViewPort(int x, int y){
    	//return (x>=VP_START.x && x <= VP_END.x && y >= VP_START.y && y <= VP_END.y);
		return (x>=0 && x < FOVMask.length && y >= 0 && y < FOVMask[0].length) && FOVMask[x][y];
    }

	public boolean insideViewPort(Position what){
    	return insideViewPort(what.x, what.y);
    }
	
	@Override
	public boolean isDisplaying(Actor who){
    	return insideViewPort(getAbsolutePosition(who.getPosition()));
    }

	protected int POSITION_PICKER_TEXT_COLOR = ConsoleSystemInterface.BLUE; 
	protected int POSITION_PICKER_COLOR = ConsoleSystemInterface.DARK_BLUE;
	protected int POSITION_PICKER_TIP_COLOR = ConsoleSystemInterface.BLUE;
    private Position pickPosition(String prompt, int fireKeyCode) throws ActionCancelException{
    	messageBox.setForeColor(POSITION_PICKER_TEXT_COLOR);
    	messageBox.setText(prompt);
		messageBox.draw();
		si.refresh();
		si.saveBuffer();
		
		Position defaultTarget = null; 
   		Position nearest = getNearestActorPosition();
   		if (nearest != null){
   			defaultTarget = nearest;
   		} else {
   			defaultTarget = null;
   		}
    	
    	Position browser = null;
    	Position offset = new Position (0,0);
    	if (!insideViewPort(PC_POS.x + offset.x,PC_POS.y + offset.y)){
    		offset = new Position (0,0);
    	}
    	
    	if (defaultTarget == null) {
    		offset = new Position (0,0);
    	} else{
			offset = new Position(defaultTarget.x - player.getPosition().x, defaultTarget.y - player.getPosition().y);
		}
    	while (true){
			si.restore();
			String looked = "";
			browser = Position.add(player.getPosition(), offset);
			
			/*if (PC_POS.x + offset.x < 0 || PC_POS.x + offset.x >= FOVMask.length || PC_POS.y + offset.y < 0 || PC_POS.y + offset.y >=FOVMask[0].length){
				offset = new Position (0,0);
				browser = Position.add(player.getPosition(), offset);
			}*/
				
			if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]){
				AbstractCell choosen = level.getMapCell(browser);
				List<AbstractFeature> feats = level.getFeaturesAt(browser);
				List<AbstractItem>items = level.getItemsAt(browser);
				AbstractItem item = null;
				if (items != null) {
					item = items.get(0);
				}
				Actor actor = level.getActorAt(browser);
				si.restore();
				
				if (choosen != null)
					looked += choosen.getDescription();
				if (feats != null){
					for (AbstractFeature feat: feats){
						looked += ", "+ feat.getDescription();
					}
				}
				if (actor != null)
					looked += ", "+ actor.getDescription();
				if (item != null)
					looked += ", "+ item.getDescription();
			}
			messageBox.setText(prompt+" "+looked);
			messageBox.draw();
			//si.print(PC_POS.x + offset.x, PC_POS.y + offset.y, '_', ConsoleSystemInterface.BLUE);
			drawLineTo(PC_POS.x + offset.x, PC_POS.y + offset.y, '*', POSITION_PICKER_COLOR);
			si.print(PC_POS.x + offset.x, PC_POS.y + offset.y, 'X', POSITION_PICKER_TIP_COLOR);
			si.refresh();
			CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.ENTER && x.code != CharKey.SPACE && x.code != CharKey.ESC && x.code != fireKeyCode &&
				   ! x.isArrow())
				x = si.inkey();
			if (x.code == CharKey.ESC){
				si.restore();
				throw new ActionCancelException();
			} 
			if (x.code == CharKey.ENTER || x.code == CharKey.SPACE || x.code == fireKeyCode){
				si.restore();
				return browser;
			}
			offset.add(Action.directionToVariation(Action.toIntDirection(x)));

			if (offset.x >= xrange) offset.x = xrange;
			if (offset.x <= -xrange) offset.x = -xrange;
			if (offset.y >= yrange) offset.y = yrange;
			if (offset.y <= -yrange) offset.y = -yrange;
     	}
		
		
    }

	private int pickDirection(String prompt) throws ActionCancelException{
		//refresh();
		messageBox.setText(prompt);
		messageBox.draw();
		si.refresh();
		//refresh();

		CharKey x = new CharKey(CharKey.NONE);
		while (x.code == CharKey.NONE)
			x = si.inkey();
		if (x.isArrow() || x.code == CharKey.N5){
			int ret = Action.toIntDirection(x);
        	return ret;
		} else {
			ActionCancelException ret = new ActionCancelException(); 

			si.refresh();
        	throw ret; 
		}
	}

	private AbstractItem pickEquipedItem(String prompt) throws ActionCancelException{
		List<? extends AbstractItem> equipped = player.getEquippedItems();
  		MenuBox menuBox = new MenuBox(si);
  		//menuBox.setBounds(26,6,30,11);
  		menuBox.setBounds(10,3,60,18);
  		menuBox.setPromptSize(2);
  		menuBox.setMenuItems(new Vector(equipped));
  		menuBox.setPrompt(prompt);
  		menuBox.setForeColor(ConsoleSystemInterface.WHITE);
  		menuBox.setBorder(true);
  		si.saveBuffer();
  		menuBox.draw();
		AbstractItem equiped = (AbstractItem) menuBox.getSelection();
		si.restore();
		if (equiped == null){
			ActionCancelException ret = new ActionCancelException();
			throw ret;
		}
		return equiped;
	}
	
	private AbstractItem pickItem(String prompt) throws ActionCancelException{
  		List<Equipment> inventory = player.getInventory();
  		List<MenuItem> inventoryMenuItems = new ArrayList<MenuItem>();
  		for (Equipment e: inventory){
  			inventoryMenuItems.add(getMenuItemForPicking(e));
  		}
  		Comparator<MenuItem> comparator = getMenuItemComparator();
  		if (comparator != null)
  			Collections.sort(inventoryMenuItems, comparator);
  		MenuBox menuBox = new MenuBox(si);
  		menuBox.setBounds(10,3,60,18);
  		menuBox.setPromptSize(2);
  		menuBox.setMenuItems(inventoryMenuItems);
  		menuBox.setPrompt(prompt);
  		menuBox.setForeColor(ConsoleSystemInterface.WHITE);
  		menuBox.setBorder(true);
  		si.saveBuffer();
  		menuBox.draw();
  		
		EquipmentMenuItem equipmentMenuItem = ((EquipmentMenuItem)menuBox.getSelection());
		si.restore();
		si.refresh();
		if (equipmentMenuItem == null){
			ActionCancelException ret = new ActionCancelException();
			throw ret;
		}
		Equipment equipment = equipmentMenuItem.getEquipment();
		if (equipment == null){
			ActionCancelException ret = new ActionCancelException();
			throw ret;
		}
		return equipment.getItem();
	}
	
	public Comparator<MenuItem> getMenuItemComparator() {
		return null;
	}

	public MenuItem getMenuItemForPicking(Equipment e) {
		return new EquipmentMenuItem(e);
	}

	private AbstractItem pickUnderlyingItem(String prompt) throws ActionCancelException{
  		List<AbstractItem> items = level.getItemsAt(player.getPosition());
  		if (items == null)
  			return null;
  		if (items.size() == 1)
  			return items.get(0);
  		MenuBox menuBox = new MenuBox(si);
  		menuBox.setBounds(10,3,60,18);
  		menuBox.setPromptSize(2);
  		menuBox.setMenuItems(new Vector(items));
  		menuBox.setPrompt(prompt);
  		menuBox.setForeColor(ConsoleSystemInterface.WHITE);
  		menuBox.setBorder(true);
  		si.saveBuffer();
  		menuBox.draw();
		AbstractItem item = (AbstractItem)menuBox.getSelection();
		si.restore();
		if (item == null){
			ActionCancelException ret = new ActionCancelException();
			throw ret;
		}
		return item;
	}
	
	private List<AbstractItem> pickMultiItems(String prompt) {
		Equipment.eqMode = true;
		List<Equipment> inventory = player.getInventory();
  		MenuBox menuBox = new MenuBox(si);
  		menuBox.setBounds(25,3,40,18);
  		menuBox.setPromptSize(2);
  		menuBox.setMenuItems(new Vector(inventory));
  		menuBox.setPrompt(prompt);
  		menuBox.setForeColor(ConsoleSystemInterface.WHITE);
  		menuBox.setBorder(true);
  		Vector ret = new Vector();
  		MenuBox selectedBox = new MenuBox(si);
  		selectedBox.setBounds(5,3,20,18);
  		selectedBox.setPromptSize(2);
  		selectedBox.setPrompt("Selected Items");
  		selectedBox.setMenuItems(ret);
  		selectedBox.setForeColor(ConsoleSystemInterface.WHITE);
  		selectedBox.setBorder(true);
  		
  		si.saveBuffer();
  		
		while (true){
			selectedBox.draw();
			menuBox.draw();
			Equipment equipment = (Equipment)menuBox.getSelection();
			if (equipment == null)
				break;
			if (!ret.contains(equipment.getItem()))
				ret.add(equipment.getItem());
		}
		si.restore();
		Equipment.eqMode = false;
		return ret;
	}
	
	public abstract String getQuitPrompt();
	
	public void processQuit(){
		messageBox.setForeColor(ConsoleSystemInterface.WHITE);
		messageBox.setText(getQuitPrompt()+" (y/n)");
		messageBox.draw();
		si.refresh();
		if (prompt()){
			messageBox.setText("[Press Space to continue]");
			messageBox.draw();
			si.refresh();
			si.waitKeys(CharKey.ENTER, CharKey.SPACE, CharKey.ESC);
			player.getGameSessionInfo().setDeathCause(GameSessionInfo.QUIT);
			informPlayerCommand(CommandListener.Command.QUIT);
		}
		messageBox.draw();
		messageBox.clear();
		si.refresh();
	}
	
	public void processSave(){
		if (!player.getGame().canSave()){
			level.addMessage("You cannot save your game here!");
			return;
		}
		messageBox.setForeColor(ConsoleSystemInterface.WHITE);
		messageBox.setText("Save your game? (y/n)");
		messageBox.draw();
		si.refresh();
		if (prompt()){
			messageBox.setText("Saving... [Press Space to continue]");
			messageBox.draw();
			si.refresh();
			si.waitKeys(CharKey.ENTER, CharKey.SPACE, CharKey.ESC);
			informPlayerCommand(CommandListener.Command.SAVE);
		}
		messageBox.draw();
		messageBox.clear();
		si.refresh();
	}

	public boolean prompt (){
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.Y && x.code != CharKey.y && x.code != CharKey.N && x.code != CharKey.n)
			x = si.inkey();
		return (x.code == CharKey.Y || x.code == CharKey.y);
	}

	public void refresh(){
		//cleanViewPort();
		beforeDrawStatus();
		drawStatus();
	 	drawLevel();
	 	sightListItems.clear();
	 	if (drawIdList())
	 		idList.draw();
		beforeRefresh();
		si.refresh();
		messageBox.draw();
	  	messageBox.setForeColor(ConsoleSystemInterface.GRAY);
	  	if (!player.getFlag("KEEPMESSAGES"))
	  		eraseOnArrival = true;
	  	
    }
	
	protected void beforeDrawStatus() {
		
	}

	public boolean drawIdList() {
		return true;
	}
	

	@Override
	public void setTargets(Action a) throws ActionCancelException{
		if (a.needsItem())
			a.setItem(pickItem(a.getPromptItem()));
		if (a.needsDirection()){
			a.setDirection(pickDirection(a.getPromptDirection()));
		}
		if (a.needsPosition()){
			if (a == target){
				a.setPosition(pickPosition(a.getPromptPosition(), CharKey.f));
			} else {
				a.setPosition(pickPosition(a.getPromptPosition(), CharKey.SPACE));
			}
		}
		if (a.needsEquipedItem())
			a.setEquipedItem(pickEquipedItem(a.getPromptEquipedItem()));
		if (a.needsMultiItems()){
			a.setMultiItems(pickMultiItems(a.getPromptMultiItems()));
		}
		if (a.needsUnderlyingItem()){
			a.setItem(pickUnderlyingItem(a.getPrompUnderlyingItem()));
		}
	}

	private int [] additionalKeys = new int[]{
				CharKey.N1, CharKey.N2, CharKey.N3, CharKey.N4,
		};
	
	private int [] itemUsageAdditionalKeys = new int[]{
				CharKey.u, CharKey.e, CharKey.d, CharKey.t,
		};
	

 	/**
     * Shows a message inmediately; useful for system
     * messages.
     *  
     * @param x the message to be shown
     */
	public void showMessage(String x){
		messageBox.setForeColor(ConsoleSystemInterface.WHITE);
		messageBox.addText(x);
		messageBox.draw();
		si.refresh();
	}
	
	public void showImportantMessage(String x){
		showMessage(x);
		si.waitKeys(CharKey.ENTER, CharKey.SPACE, CharKey.ESC);

	}
	
	public void showSystemMessage(String x){
		si.waitKeys(CharKey.ENTER, CharKey.SPACE, CharKey.ESC);
		messageBox.setForeColor(ConsoleSystemInterface.WHITE);
		messageBox.setText(x);
		messageBox.draw();
		si.refresh();
		si.waitKeys(CharKey.ENTER, CharKey.SPACE, CharKey.ESC);

	}
	
	
	public void showMessageHistory(){
		si.saveBuffer();
		si.cls();
		si.print(1, 0, "Message Buffer", CharAppearance.GRAY);
		for (int i = 0; i < 22; i++){
			if (i >= messageHistory.size())
				break;
			si.print(1,i+2, (String)messageHistory.elementAt(messageHistory.size()-1-i), CharAppearance.WHITE);
		}
		
		si.print(55, 24, "[ Space to Continue ]");
		si.waitKeys(CharKey.ENTER, CharKey.SPACE, CharKey.ESC);

		si.restore();
	}

	

    public Action selectCommand (CharKey input){
		Command com = getRelatedCommand(input.code);
		informPlayerCommand(com);
		Action ret = actionSelectedByCommand;
		actionSelectedByCommand = null;
		return ret;
	}
	
//	Runnable interface
	public void run (){}
	
//	IO Utility    
	public void waitKey (){
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code == CharKey.NONE)
			x = si.inkey();
	}


	private void drawLineTo(int x, int y, char icon, int color){
		Position target = new Position(x,y);
		Line line = new Line(PC_POS, target);
		Position tmp = line.next();
		while (!tmp.equals(target)){
			tmp = line.next();
			si.print(tmp.x, tmp.y, icon, color);
		}
		
	}
	


	public Vector getMessageBuffer() {
		if (messageHistory.size()>20)
			return new Vector(messageHistory.subList(messageHistory.size()-21,messageHistory.size()));
		else
			return messageHistory;
	}
	
	public void chat(String message) {
		// TODO Auto-generated method stub
		
	}

	public String getQuitMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processHelp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean promptChat(String message) {
		return promptChat(message, 20,8,40,5);
	}
	
	public void beforeRefresh(){
		
	}
	
	public boolean promptChat (String text, int x, int y, int w, int h){
		si.saveBuffer();
		TextBox chatBox = new TextBox(si);
		chatBox.setHeight(h);
		chatBox.setWidth(w);
		chatBox.setPosition(x, y);
		chatBox.setBorder(true);
		chatBox.setForeColor(ConsoleSystemInterface.WHITE);
		chatBox.setBorderColor(ConsoleSystemInterface.WHITE);
		chatBox.setText(text);
		chatBox.draw();
		si.refresh();
		boolean ret = prompt();
		si.restore();
		return ret;
	}
	
	public String inputBox(String prompt, int x, int y, int w, int h, int xp, int yp, int length){
		si.saveBuffer();
		TextBox chatBox = new TextBox(si);
		chatBox.setHeight(h);
		chatBox.setWidth(w);
		chatBox.setPosition(x, y);
		chatBox.setBorder(true);
		chatBox.setForeColor(ConsoleSystemInterface.WHITE);
		chatBox.setBorderColor(ConsoleSystemInterface.WHITE);
		chatBox.setText(prompt);
		chatBox.draw();
		si.refresh();
		si.locateCaret(xp, yp);
		String ret = si.input(length);
		si.restore();
		return ret;
	}
	
	public String inputBox(String prompt){
		return inputBox(prompt, 20, 2, 31, 8, 22, 6,20);
	}
	
	public void showTextBox(String text, int x, int y, int w, int h, CSIColor borderColor){
		printTextBox(text, x, y, w, h, borderColor);
		si.waitKeys(CharKey.ENTER, CharKey.SPACE, CharKey.ESC);
		si.restore();
	}
	
	public void printTextBox(String text, int x, int y, int w, int h, CSIColor borderColor){
		si.saveBuffer();
		TextBox chatBox = new TextBox(si);
		chatBox.setHeight(h);
		chatBox.setWidth(w);
		chatBox.setPosition(x, y);
		chatBox.setBorder(true);
		chatBox.setForeColor(ConsoleSystemInterface.WHITE);
		chatBox.setBorderColor(borderColor);
		chatBox.setText(text);
		chatBox.draw();
		si.refresh();
	}
	
	@Override
	public void reset() {
		messageBox.setText("");
		messageHistory.clear();
	}
}