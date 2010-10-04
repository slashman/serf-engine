package net.slashie.serf.ui.oryxUI;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.slashie.libjcsi.CharKey;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.GameSessionInfo;
import net.slashie.serf.game.Player;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.level.BufferedLevel;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.CommandListener;
import net.slashie.serf.ui.Effect;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.effects.GFXEffect;
import net.slashie.utils.Debug;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Line;
import net.slashie.utils.Position;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.swing.BorderedMenuBox;


/** 
 *  Shows the level using characters.
 *  Informs the Actions and Commands of the player.
 * 	Must be listening to a System Interface
 */

public abstract class GFXUserInterface extends UserInterface implements Runnable {
	//Attributes
	private int xrange = 11;
	private int yrange = 8;
	
	//Components
	public SwingInformBox messageBox;
	public AddornedBorderTextArea persistantMessageBox;
	private Action target;
	
	private boolean eraseOnArrival; // Erase the buffer upon the arrival of a new msg
	private boolean flipFacing;
	private Vector messageHistory = new Vector(10);
	
	// Relations

 	protected transient SwingSystemInterface si;

 	private Font FNT_MESSAGEBOX;
 	private Font FNT_PERSISTANTMESSAGEBOX;
 	protected int tileSize;
	private BufferedImage 
		TILE_LINE_STEPS, 
		TILE_LINE_AIM,
		TILE_SCAN;
	protected BufferedImage BORDER1, BORDER2, BORDER3, BORDER4, IMG_STATUSSCR_BGROUND, IMG_BORDERS, IMG_ICON;
	private int GADGETSIZE;
	protected Color COLOR_BORDER_OUT, COLOR_BORDER_IN, COLOR_WINDOW_BACKGROUND, COLOR_BOLD;
	private Color
		COLOR_LAST_MESSAGE = Color.WHITE,
		COLOR_OLD_MESSAGE = Color.GRAY;


	public static Font FNT_TEXT;
	public static Font FNT_TITLE;
	public static Font FNT_DIALOGUEIN;
	public static Font FNT_MONO;
	
	// Setters
	/** Sets the object which will be informed of the player commands.
     * this corresponds to the Game object */
	
	//Getters

    // Smart Getters
    public Position getAbsolutePosition(Position insideLevel){
    	Position relative = Position.subs(insideLevel, player.getPosition());
		return Position.add(PC_POS, relative);
	}

	public Position VP_START = new Position(0,0),
				VP_END = new Position (31,18),
				PC_POS = new Position (12,9);
	private boolean flipEnabled = true;
	
    public void setFlipFacing(boolean val){
    	if (flipEnabled)
    		flipFacing = val;
    }

    private boolean [][] FOVMask;
    
    private Color TRANSPARENT_GRAY = new Color(20,20,20,180);
    private Color MAP_NOSOLID_LOS = new Color(98,96,85,150);
    private Color MAP_NOSOLID = new Color(86,77,65,150);
    private Color MAP_SOLID = new Color(83,83,83);
    private void examineLevelMap(){
		messageBox.setVisible(false);
		isCursorEnabled = false;
		si.saveBuffer();
		int lw = level.getWidth();
		int lh = level.getHeight();
		int remnantx = (int)((740 - (lw * 3))/2.0d); 
		int remnanty = (int)((480 - (lh * 3))/2.0d);
		Graphics2D g = si.getGraphics2D();
		g.setColor(TRANSPARENT_GRAY);
		g.fillRect(0,0,800,600);
		Color cellColor = null;
		Position runner = new Position(0,0,player.getPosition().z);
		boolean isBufferedLevel = level instanceof BufferedLevel;
		for (int x = 0; x < level.getWidth(); x++, runner.x++, runner.y = 0)
			for (int y = 0; y < level.getHeight(); y++, runner.y++){
				if (isBufferedLevel && !((BufferedLevel)level).remembers(x,y,runner.z)) 
					//cellColor = Color.BLACK;
					continue;
				else {
					AbstractCell current = level.getMapCell(runner);
					if (level.isVisible(x,y,runner.z)){
						if (current == null)
							//cellColor = Color.BLACK;
							continue;
						else if (level.getExitOn(runner) != null)
							cellColor = Color.RED;
						else if (current.isSolid())
							cellColor = MAP_SOLID;
						else 
							cellColor = MAP_NOSOLID_LOS;
						
					} else {
						if (current == null)
							//cellColor = Color.BLACK;
							continue;
						else if (level.getExitOn(runner) != null)
							cellColor = Color.RED;
						else if (current.isSolid())
							cellColor = MAP_SOLID;
						else  
							cellColor = MAP_NOSOLID;
					}
					if (player.getPosition().x == x && player.getPosition().y == y)
						cellColor = Color.RED;
				}
				g.setColor(cellColor);
				//g.fillOval(30+remnantx+x*5, 30+remnanty+y*5, 5,5);
				g.fillRect(30+remnantx+x*3, 30+remnanty+y*3, 3,3);
			}
			si.refresh();	
		
		
		si.waitKeys(CharKey.SPACE, CharKey.ESC);
		messageBox.setVisible(true);
		isCursorEnabled = true;
		si.restore();
		si.refresh();
		
	}
    
    private void enterScreen(){
    	messageBox.setVisible(false);
    	isCursorEnabled = false;
    }
    
    private void leaveScreen(){
    	messageBox.setVisible(true);
    	isCursorEnabled = true;
    }
    
    public void showMessageHistory(){
    	enterScreen();
		si.saveBuffer();
		si.drawImage(IMG_STATUSSCR_BGROUND);
		si.print(1, 1, "Message Buffer", COLOR_BOLD);
		for (int i = 0; i < 22; i++){
			if (i >= messageHistory.size())
				break;
			si.print(1,i+2, (String)messageHistory.elementAt(messageHistory.size()-1-i), Color.WHITE);
		}
		
		si.print(55, 24, "[ Space to Continue ]", Color.WHITE);
		si.refresh();
		si.waitKeys(CharKey.SPACE, CharKey.ESC);
		si.restore();
		si.refresh();
		leaveScreen();
	}
    
    //Interactive Methods
    public void doLook(){
		Position offset = new Position (0,0);
		
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		si.saveBuffer();
		Actor lookedMonster = null;
		while (true){
			int cellHeight = 0;
			Position browser = Position.add(player.getPosition(), offset);
			String looked = "";
			si.restore();
			if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]){
				AbstractCell choosen = level.getMapCell(browser);
				if (choosen != null)
					cellHeight = choosen.getHeight();
				List<AbstractFeature> feats = level.getFeaturesAt(browser);
				List<AbstractItem> items = level.getItemsAt(browser);
				AbstractItem item = null;
				if (items != null) {
					item = items.get(0);
				}
				lookedMonster = null;
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
					looked += ", "+ actor.getDescription();
				}
			}
			messageBox.setText(looked);
			si.drawImage((PC_POS.x + offset.x)*tileSize, ((PC_POS.y + offset.y)*tileSize), TILE_SCAN);
			si.refresh();
			CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.SPACE && x.code != CharKey.m && x.code != CharKey.ESC &&
				   ! x.isArrow())
				x = si.inkey();
			if (x.code == CharKey.SPACE || x.code == CharKey.ESC){
				si.restore();
				break;
			}
			if (x.code == CharKey.m){
				if (lookedMonster != null)
					showDetailedInfo(lookedMonster);
			} else {
				offset.add(Action.directionToVariation(GFXUISelector.toIntDirection(x)));
	
				if (offset.x >= xrange) offset.x = xrange;
				if (offset.x <= -xrange) offset.x = -xrange;
				if (offset.y >= yrange) offset.y = yrange;
				if (offset.y <= -yrange) offset.y = -yrange;
			}
     	}
		messageBox.setText("Look mode off");
		si.restore();
		si.refresh();
		
	
	}

    public abstract void showDetailedInfo(Actor a);


    
   public void chat (String message){
	   si.saveBuffer();
	   showTextBox(message, 280, 30, 330, 170);
	   si.refresh();
	   //waitKey();
	   si.restore();
	}
   
	public void showTextBox(String text, int x, int y, int w, int h){
		printTextBox(text, x, y, w, h);
		si.waitKeys(CharKey.SPACE, CharKey.ESC);
		clearTextBox();
	}
	
	public void printTextBox(String text, int x, int y, int w, int h){
		addornedTextArea.setBounds(x, y, w, h);
		addornedTextArea.setText(text);
		addornedTextArea.setVisible(true);
	}
	
	public void clearTextBox(){
		addornedTextArea.setVisible(false);
	}
	
	protected AddornedBorderTextArea addornedTextArea;

	public boolean showTextBoxPrompt(String text, int consoleX, int consoleY, int consoleW, int consoleH){
		addornedTextArea.setBounds(consoleX, consoleY, consoleW, consoleH);
		addornedTextArea.setText(text);
		addornedTextArea.setVisible(true);
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.Y && x.code != CharKey.y && x.code != CharKey.N && x.code != CharKey.n)
			x = si.inkey();
		boolean ret = (x.code == CharKey.Y || x.code == CharKey.y);
		addornedTextArea.setVisible(false);
		return ret;
	}
   
   public boolean promptChat (String text, int x, int y, int w, int h){
	   si.saveBuffer();
	   boolean ret = showTextBoxPrompt(text, x, y, w, h);
	   si.refresh();
	   //waitKey();
	   si.restore();
	   return ret;
	}

    // Drawing Methods
	public void drawEffect(Effect what){
		if (what == null)
			return;
		if (insideViewPort(getAbsolutePosition(what.getPosition()))){
			((GFXEffect)what).drawEffect(this, si);
		}
	}
	
	public boolean isOnFOVMask(int x, int y){
		return FOVMask[x][y];
	}

	private void drawLevel(){
		Debug.enterMethod(this, "drawLevel");
		//Cell[] [] cells = level.getCellsAround(player.getPosition().x,player.getPosition().y, player.getPosition().z, range);
		AbstractCell[] [] rcells = level.getMemoryCellsAround(player.getPosition().x,player.getPosition().y, player.getPosition().z, xrange,yrange);
		AbstractCell[] [] vcells = level.getVisibleCellsAround(player.getPosition().x,player.getPosition().y, player.getPosition().z, xrange,yrange);
		
		Position runner = new Position(player.getPosition().x - xrange, player.getPosition().y-yrange, player.getPosition().z);
		
		monstersOnSight.removeAllElements();
		featuresOnSight.removeAllElements();
		itemsOnSight.removeAllElements();
		
		/*for (int x = 0; x < vcells.length; x++){
			for (int y=0; y<vcells[0].length; y++){*/
		for (int y = 0; y < vcells[0].length; y++){
			for (int x=0; x<vcells.length; x++){
				FOVMask[PC_POS.x-xrange+x][PC_POS.y-yrange+y] = false;
				int cellHeight = 0;
				if (vcells[x][y] == null || vcells[x][y].getID().equals("AIR")){
					if (rcells[x][y] != null && !rcells[x][y].getAppearance().getID().equals("NOTHING")){
						GFXAppearance app = (GFXAppearance)rcells[x][y].getAppearance();
						try {
							si.drawImage((PC_POS.x-xrange+x)*tileSize,(PC_POS.y-yrange+y)*tileSize-app.getSuperHeight(), app.getDarkImage());
						} catch (NullPointerException npe){
							Color c = si.getGraphics2D().getColor();
							si.getGraphics2D().setColor(Color.RED);
							si.getGraphics2D().fillRect((PC_POS.x-xrange+x)*tileSize,(PC_POS.y-yrange+y)*tileSize-app.getSuperHeight(), tileSize,49);
							si.getGraphics2D().setColor(c);
						}
					} else {
						//Draw nothing
						//si.drawImage((PC_POS.x-xrange+x)*tileSize,(PC_POS.y-yrange+y)*tileSize, "gfx/black.gif");
						//si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y, CharAppearance.getVoidAppearance().getChar(), CharAppearance.getVoidAppearance().BLACK);
					}
				} else {
					cellHeight = vcells[x][y].getHeight();
					FOVMask[PC_POS.x-xrange+x][PC_POS.y-yrange+y] = true;
					GFXAppearance cellApp = (GFXAppearance)vcells[x][y].getAppearance();
					si.drawImage((PC_POS.x-xrange+x)*tileSize,(PC_POS.y-yrange+y)*tileSize-cellApp.getSuperHeight(), cellApp.getImage());
				}
				runner.x++;
			}
			runner.x = player.getPosition().x-xrange;
			for (int x=0; x<vcells.length; x++){
				int cellHeight = 0;
				if (vcells[x][y] != null){
					cellHeight = vcells[x][y].getHeight();
					List<AbstractFeature> feats = level.getFeaturesAt(runner);
					if (feats != null){
						for (AbstractFeature feat: feats){
							if (feat.isVisible()) {
								GFXAppearance featApp = (GFXAppearance)feat.getAppearance();
								si.drawImage((PC_POS.x-xrange+x)*tileSize-featApp.getSuperWidth(),(PC_POS.y-yrange+y)*tileSize-4*cellHeight-featApp.getSuperHeight(), featApp.getImage());
							}
						}
					}
					
					
					List<AbstractItem> items = level.getItemsAt(runner);
					AbstractItem item = null;
					if (items != null){
						item = items.get(0);
					}
					if (item != null){
						if (item.isVisible()){
							GFXAppearance itemApp = (GFXAppearance)item.getAppearance();
							si.drawImage((PC_POS.x-xrange+x)*tileSize-itemApp.getSuperWidth(),(PC_POS.y-yrange+y)*tileSize-4*cellHeight -itemApp.getSuperHeight(), itemApp.getImage());
						}
					}
					
					if (yrange == y && x == xrange){
						if (player.isInvisible()){
							si.drawImage(PC_POS.x*tileSize,PC_POS.y*tileSize-4*cellHeight, ((GFXAppearance)AppearanceFactory.getAppearanceFactory().getAppearance("SHADOW")).getImage());
						}else{
							GFXAppearance playerAppearance = (GFXAppearance)player.getAppearance();
							BufferedImage playerImage = (BufferedImage)playerAppearance.getImage();
							if (flipEnabled && flipFacing){
								playerImage = ImageUtils.vFlip(playerImage);
								//flipFacing = false;
							}
							if (level.getMapCell(player.getPosition())!= null && level.getMapCell(player.getPosition()).isShallowWater())
								//si.drawImage(PC_POS.x*tileSize-playerAppearance.getSuperWidth(),PC_POS.y*tileSize-4*cellHeight-playerAppearance.getSuperHeight()+16/, playerImage);
								si.drawImage(PC_POS.x*tileSize-playerAppearance.getSuperWidth(),PC_POS.y*tileSize-playerAppearance.getSuperHeight()+16, playerImage);
							else
								//si.drawImage(PC_POS.x*tileSize-playerAppearance.getSuperWidth(),PC_POS.y*tileSize-4*cellHeight-playerAppearance.getSuperHeight(), playerImage);
								si.drawImage(PC_POS.x*tileSize-playerAppearance.getSuperWidth(),PC_POS.y*tileSize-playerAppearance.getSuperHeight(), playerImage);
						}
					}
					Actor monster = level.getActorAt(runner);
					
					if (monster != null && !monster.isInvisible()){
						GFXAppearance monsterApp = (GFXAppearance) monster.getAppearance();
						si.drawImage((PC_POS.x-xrange+x)*tileSize-monsterApp.getSuperWidth(),(PC_POS.y-yrange+y)*tileSize-4*cellHeight-monsterApp.getSuperHeight(), monsterApp.getImage());
					}
				}
				//runner.y++;
				runner.x++;
			} 
			/*runner.y = player.getPosition().y-yrange;
			runner.x ++;*/
			runner.x = player.getPosition().x-xrange;
			runner.y ++;
		}
		
		Debug.exitMethod();
	}
	
	public void addMessage(Message message){
		Debug.enterMethod(this, "addMessage", message);
		if (eraseOnArrival){
	 		messageBox.clear();
	 		messageBox.setForeground(COLOR_LAST_MESSAGE);
	 		eraseOnArrival = false;
		}
		if (message.getLocation().z != player.getPosition().z || !insideViewPort(getAbsolutePosition(message.getLocation()))){
			Debug.exitMethod();
			return;
		}
		messageHistory.add(message.getText());
		if (messageHistory.size()>500)
			messageHistory.removeElementAt(0);
		messageBox.addText(message.getText());
		dimMsg = 0;
		Debug.exitMethod();
	}

	/*private void drawCursor(){
		/*if (isCursorEnabled){
			si.restore();
			Cell underlying = player.getLevel().getMapCell(tempCursorPosition);
			si.drawImage((PC_POS.x+tempCursorPositionScr.x)*tileSize,(PC_POS.y+tempCursorPositionScr.y)*tileSize-4*underlying.getHeight(), TILE_SCAN);
			si.refresh();
		}
	}*/
	
	private boolean isCursorEnabled = false;
	
	private void initProperties(Properties p){
		xrange = PropertyFilters.inte(p.getProperty("XRANGE"));
		yrange = PropertyFilters.inte(p.getProperty("YRANGE"));
		
		//POS_LEVELDESC_X = PropertyFilters.inte(p.getProperty("POS_LEVELDESC_X"));
		//POS_LEVELDESC_Y = PropertyFilters.inte(p.getProperty("POS_LEVELDESC_Y"));
		
		//UPLEFTBORDER = PropertyFilters.inte(p.getProperty("UPLEFTBORDER"));
		PC_POS = PropertyFilters.getPosition(p.getProperty("PC_POS"));
		
		VP_START = Position.add(PC_POS, new Position (-xrange,-yrange));
		VP_END = Position.add(PC_POS, new Position (xrange,yrange));
		
		
		/*TILESIZE = PropertyFilters.inte(p.getProperty("TILESIZE"));*/
		COLOR_WINDOW_BACKGROUND = PropertyFilters.getColor(p.getProperty("COLOR_WINDOW_BACKGROUND"));
		COLOR_BORDER_IN = PropertyFilters.getColor(p.getProperty("COLOR_BORDER_IN"));
		COLOR_BORDER_OUT = PropertyFilters.getColor(p.getProperty("COLOR_BORDER_OUT"));
		/*COLOR_MSGBOX_ACTIVE = PropertyFilters.getColor(p.getProperty("COLOR_MSGBOX_ACTIVE"));
		COLOR_MSGBOX_INACTIVE = PropertyFilters.getColor(p.getProperty("COLOR_MSGBOX_INACTIVE"));*/
		try {
			FNT_MESSAGEBOX = PropertyFilters.getFont(p.getProperty("FNT_MESSAGEBOX"),p.getProperty("FNT_MESSAGEBOX_SIZE"));
			FNT_PERSISTANTMESSAGEBOX = PropertyFilters.getFont(p.getProperty("FNT_PERSISTANTMESSAGEBOX"),p.getProperty("FNT_PERSISTANTMESSAGEBOX_SIZE"));
			
		} catch (FontFormatException ffe){
			SworeGame.crash("Error loading the font", ffe);
		} catch (IOException ioe){
			SworeGame.crash("Error loading the font", ioe);
		}
		
		/*-- Load UI Images */
		try {
			IMG_STATUSSCR_BGROUND = ImageUtils.createImage(p.getProperty("IMG_STATUSSCR_BGROUND"));
			GADGETSIZE = PropertyFilters.inte(p.getProperty("GADGETSIZE"));
			BufferedImage IMG_GADGETS = PropertyFilters.getImage(p.getProperty("IMG_GADGETS"), p.getProperty("IMG_GADGETS_BOUNDS"));
			TILE_LINE_AIM  = ImageUtils.crearImagen(IMG_GADGETS, 0, 0, GADGETSIZE, GADGETSIZE);
			TILE_SCAN  = ImageUtils.crearImagen(IMG_GADGETS, GADGETSIZE, 0, GADGETSIZE, GADGETSIZE);
			TILE_LINE_STEPS  = ImageUtils.crearImagen(IMG_GADGETS, GADGETSIZE*2, 0, GADGETSIZE, GADGETSIZE);
			
			IMG_ICON = ImageUtils.createImage(p.getProperty("IMG_ICON"));
			COLOR_BOLD = PropertyFilters.getColor(p.getProperty("COLOR_BOLD"));
			IMG_BORDERS = PropertyFilters.getImage(p.getProperty("IMG_BORDERS"), p.getProperty("IMG_BORDERS_BOUNDS"));
			
			BORDER1 = ImageUtils.crearImagen(IMG_BORDERS, tileSize,0,tileSize,tileSize);
			BORDER2 = ImageUtils.crearImagen(IMG_BORDERS, 0,0,tileSize,tileSize);
			BORDER3 = ImageUtils.crearImagen(IMG_BORDERS, tileSize*3,0,tileSize,tileSize);
			BORDER4 = ImageUtils.crearImagen(IMG_BORDERS, tileSize*2,0, tileSize,tileSize);
			
			FNT_TITLE = PropertyFilters.getFont(p.getProperty("FNT_TITLE"), p.getProperty("FNT_TITLE_SIZE"));
			FNT_TEXT = PropertyFilters.getFont(p.getProperty("FNT_TEXT"), p.getProperty("FNT_TEXT_SIZE"));
			FNT_DIALOGUEIN  = FNT_TEXT;
			FNT_MONO = PropertyFilters.getFont(p.getProperty("FNT_MONO"), p.getProperty("FNT_MONO_SIZE"));
			
		} catch (Exception e){
			SworeGame.crash(e.getMessage(),e);
		}
		
		
		
		
	}
    
	public void init(SwingSystemInterface psi, String title, UserCommand[] gameCommands, Properties UIProperties, Action target){
		Debug.enterMethod(this, "init");
		super.init(gameCommands);
		this.target = target;
		tileSize = PropertyFilters.inte(UIProperties.getProperty("TILE_SIZE"));

		initProperties(UIProperties);
		//GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setDisplayMode(new DisplayMode(800,600,8, DisplayMode.REFRESH_RATE_UNKNOWN));
		
		try {
			addornedTextArea = new AddornedBorderTextArea(
					BORDER1,
					BORDER2,
					BORDER3,
					BORDER4,
					COLOR_BORDER_OUT,
					COLOR_BORDER_IN,
					COLOR_WINDOW_BACKGROUND,
					tileSize,
					6,9,12 );
		} catch (Exception e){
			e.printStackTrace();
		}
		
		addornedTextArea.setVisible(false);
		addornedTextArea.setEnabled(false);
		addornedTextArea.setForeground(Color.WHITE);
		addornedTextArea.setBackground(Color.BLACK);
		addornedTextArea.setFont(FNT_DIALOGUEIN);
		addornedTextArea.setOpaque(false);
		addornedTextArea.setForeground(new Color(244,226,108));
		psi.add(addornedTextArea);
		
		/*-- Assign values */
		si = psi;
		FOVMask = new boolean[80][25];
		si.getGraphics2D().setColor(Color.BLACK);
		si.getGraphics2D().fillRect(0,0,800,600);
		si.refresh();
		
		si.setIcon(IMG_ICON);
		si.setTitle(title);
		/*-- Init Components*/
		messageBox = new SwingInformBox();
		/*idList = new ListBox(psi);*/
		messageBox.setBounds(1*10,22*24,78*10,2*24);
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setBackground(Color.BLACK);
		messageBox.setFont(FNT_MESSAGEBOX);
		messageBox.setEditable(false);
		messageBox.setVisible(false);
		messageBox.setOpaque(false);
		messageBox.setLineWrap(true);
		messageBox.setWrapStyleWord(true);
		
		psi.add(messageBox);
		
		persistantMessageBox = new AddornedBorderTextArea(BORDER1, BORDER2, BORDER3, BORDER4, 
				new Color(52,42,20),
				new Color(164,138,68),
				new Color(232,253,77),
				tileSize,
				6,9,12);
		persistantMessageBox.setBounds(520,90,260,400);
		persistantMessageBox.setVisible(false);
		persistantMessageBox.setFont(FNT_PERSISTANTMESSAGEBOX);
		persistantMessageBox.setForeground(Color.WHITE);
		psi.add(persistantMessageBox);

		
		si.setVisible(true);
		
		Debug.exitMethod();
	}
	
	public void setPersistantMessage(String description) {
		persistantMessageBox.setText(description);
		persistantMessageBox.setVisible(true);
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

	public boolean isDisplaying(Actor who){
    	return insideViewPort(getAbsolutePosition(who.getPosition()));
    }

    private Position pickPosition(String prompt, int fireKeyCode) throws ActionCancelException{
    	Debug.enterMethod(this, "pickPosition");
    	messageBox.setForeground(COLOR_LAST_MESSAGE);
    	messageBox.setText(prompt);
    	Position defaultTarget = null; 
   		Position nearest = getNearestActorPosition();
   		if (nearest != null){
   			defaultTarget = nearest;
   		} else {
   			defaultTarget = null;
   		}
    	
    	Position browser = null;
    	Position offset = new Position (0,0);
    	    	
    	if (defaultTarget == null) {
    		offset = new Position (0,0);
    	} else{
			offset = new Position(defaultTarget.x - player.getPosition().x, defaultTarget.y - player.getPosition().y);
		}
    	
    	if (!insideViewPort(PC_POS.x + offset.x,PC_POS.y + offset.y)){
    		offset = new Position (0,0);
    	}
    	
    	/*if (!insideViewPort(offset))
    		offset = new Position (0,0);*/
    	
		si.refresh();
		si.saveBuffer();
		
		while (true){
			si.restore();
			int cellHeight = 0;
			browser = Position.add(player.getPosition(), offset);
			String looked = "";
			
			if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]){
				AbstractCell choosen = level.getMapCell(browser);
				List<AbstractFeature> feats = level.getFeaturesAt(browser);
				List<AbstractItem> items = level.getItemsAt(browser);
				if (choosen != null)
					cellHeight = choosen.getHeight();
				AbstractItem item = null;
				if (items != null) {
					item = (AbstractItem) items.get(0);
				}
				Actor actor = level.getActorAt(browser);
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
			//si.print(PC_POS.x + offset.x, PC_POS.y + offset.y, '_', ConsoleSystemInterface.RED);
			drawStepsTo(PC_POS.x + offset.x, (PC_POS.y + offset.y), TILE_LINE_STEPS, cellHeight);
			
			si.drawImage((PC_POS.x + offset.x)*tileSize-2, ((PC_POS.y + offset.y)*tileSize-2) -4*cellHeight, TILE_LINE_AIM);
			si.refresh();
			CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.SPACE && x.code != CharKey.ESC && x.code != fireKeyCode &&
				   ! x.isArrow())
				x = si.inkey();
			if (x.code == CharKey.ESC){
				si.restore();
				si.refresh();
				throw new ActionCancelException();
			}
			if (x.code == CharKey.SPACE || x.code == fireKeyCode){
				si.restore();
				return browser;
			}
			offset.add(Action.directionToVariation(GFXUISelector.toIntDirection(x)));

			if (offset.x >= xrange) offset.x = xrange;
			if (offset.x <= -xrange) offset.x = -xrange;
			if (offset.y >= yrange) offset.y = yrange;
			if (offset.y <= -yrange) offset.y = -yrange;
     	}
		
		
    }

	private int pickDirection(String prompt) throws ActionCancelException{
		Debug.enterMethod(this, "pickDirection");
		//refresh();
		leaveScreen();
		messageBox.setText(prompt);
		//si.refresh();
		//refresh();

		CharKey x = new CharKey(CharKey.NONE);
		while (x.code == CharKey.NONE)
			x = si.inkey();
		if (x.isArrow()){
			int ret = GFXUISelector.toIntDirection(x);
        	Debug.exitMethod(ret);
        	return ret;
		} else {
			ActionCancelException ret = new ActionCancelException(); 
			Debug.exitExceptionally(ret);
			si.refresh();
			throw ret; 
		}
	}

	private AbstractItem pickEquipedItem(String prompt) throws ActionCancelException{
		enterScreen();

		List<? extends AbstractItem> equipped = player.getEquippedItems();

		if (equipped.size() == 0){
  			level.addMessage("Nothing equipped");
  			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			throw ret;
  		}
  		
  		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize,null);
  		
  		//menuBox.setBounds(26,6,30,11);
  		menuBox.setBounds(6,4,70,12);
  		menuBox.setMenuItems(equipped);
  		menuBox.setTitle(prompt);
  		si.saveBuffer();
  		//menuBox.draw();
  		AbstractItem equiped = (AbstractItem)menuBox.getSelection();
		if (equiped == null){
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			si.restore();
			si.refresh();
			throw ret;
		}
		si.restore();
		si.refresh();
		leaveScreen();
		return equiped;
	}
	
	private AbstractItem pickItem(String prompt) throws ActionCancelException{
		enterScreen();
  		List inventory = player.getInventory();
  		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize,null);
  		
  		menuBox.setBounds(20,20,400,500);
  		menuBox.setItemsPerPage(12);
  		menuBox.setMenuItems(inventory);
  		menuBox.setTitle(prompt);
  		si.saveBuffer();
  		//menuBox.draw();
		Equipment equipment = (Equipment)menuBox.getSelection();
		si.restore();
		if (equipment == null){
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			si.restore();
			si.refresh();
			leaveScreen();
			throw ret;
		}
		si.restore();
		si.refresh();
		leaveScreen();
		return equipment.getItem();
	}
	
	
	private Vector pickMultiItems(String prompt) throws ActionCancelException{
		//Equipment.eqMode = true;
		List inventory = player.getInventory();
		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+3,null);
  		menuBox.setBounds(25,3,40,18);
  		//menuBox.setPromptSize(2);
  		menuBox.setMenuItems(inventory);
  		menuBox.setTitle(prompt);
  		//menuBox.setForeColor(ConsoleSystemInterface.RED);
  		//menuBox.setBorder(true);
  		Vector ret = new Vector();
  		BorderedMenuBox selectedBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize, null);
  		selectedBox.setBounds(5,3,20,18);
  		//selectedBox.setPromptSize(2);
  		selectedBox.setTitle("Selected Items");
  		selectedBox.setMenuItems(ret);
  		//selectedBox.setForeColor(ConsoleSystemInterface.RED);
  		
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
		//Equipment.eqMode = false;
		return ret;
	}

	public void processQuit(){
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText(getQuitMessage()+" (y/n)");
		si.refresh();
		if (prompt()){
			enterScreen();
			//si.refresh();
			player.getGameSessionInfo().setDeathCause(GameSessionInfo.QUIT);
			informPlayerCommand(CommandListener.QUIT);
		}
		messageBox.clear();
		si.refresh();
	}
	
	public void processSave(){
		if (!player.getGame().canSave()){
			level.addMessage("You cannot save your game here!");
			return;
		}
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText("Save your game? (y/n)");
		si.refresh();
		if (prompt()){
			messageBox.setText("Saving... ");
			si.refresh();
			enterScreen();
			informPlayerCommand(CommandListener.SAVE);
		}
		messageBox.clear();
		si.refresh();
	}

	public boolean prompt (){
		
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.Y && x.code != CharKey.y && x.code != CharKey.N && x.code != CharKey.n)
			x = si.inkey();
		return (x.code == CharKey.Y || x.code == CharKey.y);
	}

	private int dimMsg = 0;
	public void refresh(){
		si.cls();
		beforeDrawLevel();
	 	drawLevel();
		beforeRefresh();
		si.refresh();
		leaveScreen();
		if (dimMsg == 3){
			messageBox.setForeground(COLOR_OLD_MESSAGE);
			dimMsg = 0;
		}
		dimMsg++;
	  	if (!player.getFlag("KEEPMESSAGES"))
	  		eraseOnArrival = true;
	  	si.saveBuffer(); //sz040507
	  	
    }
	
	public void beforeDrawLevel(){
		
	}
	
	public void beforeRefresh(){
		
	}

	public void setTargets(Action a) throws ActionCancelException{
		if (a.needsItem())
			a.setItem(pickItem(a.getPromptItem()));
		if (a.needsDirection()){
			a.setDirection(pickDirection(a.getPromptDirection()));
		}
		if (a.needsPosition()){
			if (a == target)
				a.setPosition(pickPosition(a.getPromptPosition(), CharKey.f));
			else
				a.setPosition(pickPosition(a.getPromptPosition(), CharKey.SPACE));
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
	
	private AbstractItem pickUnderlyingItem(String prompt) throws ActionCancelException{
		enterScreen();
  		List items = level.getItemsAt(player.getPosition());
  		if (items == null)
  			return null;
  		if (items.size() == 1)
  			return (AbstractItem) items.get(0);
  		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize, null);
  		menuBox.setBounds(6,4,70,12);
  		menuBox.setMenuItems(items);
  		menuBox.setTitle(prompt);
  		si.saveBuffer();
  		//menuBox.draw();
		AbstractItem item = (AbstractItem)menuBox.getSelection();
		
		if (item == null){
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			si.restore();
			si.refresh();
			leaveScreen();
			throw ret;
		}
		si.restore();
		si.refresh();
		leaveScreen();
		return item;
	}
	

	
	private int [] additionalKeys = new int[]{
				CharKey.N1, CharKey.N2, CharKey.N3, CharKey.N4,
		};
	
	private int [] itemUsageKeys = new int[]{
				CharKey.u, CharKey.e, CharKey.d, CharKey.t,
		};
	
	

 	/**
     * Shows a message inmediately; useful for system
     * messages.
     *  
     * @param x the message to be shown
     */
	public void showMessage(String x){
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText(x);
		//si.refresh();
	}
	
	public void showImportantMessage(String x){
		showMessage(x);
		si.waitKeys(CharKey.SPACE, CharKey.ESC);
	}
	
	public void showSystemMessage(String x){
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText(x);
		//si.refresh();
		si.waitKeys(CharKey.SPACE, CharKey.ESC);
	}
	
	

	public void setPlayer(Player pPlayer) {
		super.setPlayer(pPlayer);
		flipFacing = false;
	}
	
    
	
	
	
	

//	Runnable interface
	public void run (){}
	
//	IO Utility    
	public void waitKey (){
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code == CharKey.NONE)
			x = si.inkey();
	}


	private void drawStepsTo(int x, int y, Image tile, int cellHeight){
		Position target = new Position(x,y);
		Line line = new Line(PC_POS, target);
		Position tmp = line.next();
		while (!tmp.equals(target)){
			tmp = line.next();
			si.drawImage(tmp.x*tileSize+13, (tmp.y*tileSize+13)-4*cellHeight, tile);
		}
		
	}
	
	
	
	
	
	public Vector getMessageBuffer() {
		//return new Vector(messageHistory.subList(0,21));
		if (messageHistory.size()>20)
			return new Vector(messageHistory.subList(messageHistory.size()-21,messageHistory.size()));
		else
			return messageHistory;
	}
	
	public Action selectCommand (CharKey input){
		Debug.enterMethod(this, "selectCommand", input);
		int com = getRelatedCommand(input.code);
		informPlayerCommand(com);
		Action ret = actionSelectedByCommand;
		actionSelectedByCommand = null;
		Debug.exitMethod(ret);
		return ret;
	}

	public abstract String getQuitMessage();

	@Override
	public boolean promptChat(String message) {
		return promptChat(message, 20,20,200,100);
	}

	@Override
	public int switchChat(String title, String prompt, String... options) {
		return 0;
	}

	@Override
	public String inputBox(String prompt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processHelp() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMusicOn() {
		// TODO Auto-generated method stub
	}

	public void setFlipEnabled(boolean flipEnabled) {
		this.flipEnabled = flipEnabled;
	}
	
	@Override
	public void reset() {
		messageBox.setText("");
		messageHistory.clear();
	}
}



