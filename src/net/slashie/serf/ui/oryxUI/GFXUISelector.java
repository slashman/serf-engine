package net.slashie.serf.ui.oryxUI;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import net.slashie.libjcsi.CharKey;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.UISelector;
import net.slashie.serf.ui.UserAction;
import net.slashie.utils.Debug;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Position;
import net.slashie.utils.PropertyFilters;

public class GFXUISelector extends UISelector implements ActionSelector, MouseListener, MouseMotionListener, Serializable{
	private static final long serialVersionUID = 1L;
	private transient SwingSystemInterface si;
	private boolean useMouse = false;
	
	private static final int DONOTHING1_KEY = CharKey.DOT;
	private static final int DONOTHING2_KEY = CharKey.DOT;
	
	private final int[] QDIRECTIONS = new int[]{
			Action.UPLEFT,
			Action.UP,
			Action.UPRIGHT,
			Action.LEFT,
			Action.SELF,
			Action.RIGHT,
			Action.DOWNLEFT,
			Action.DOWN,
			Action.DOWNRIGHT
		};
	private Cursor[] QCURSORS; 
	
	private void initializeCursors (String cursorsFile){
		QCURSORS = new Cursor[]{
			createCursor(cursorsFile, 1, 2),
			createCursor(cursorsFile, 1, 3),
			createCursor(cursorsFile, 2, 2),
			createCursor(cursorsFile, 4, 3),
			createCursor(cursorsFile, 3, 1),
			createCursor(cursorsFile, 2, 3),
			createCursor(cursorsFile, 4, 2),
			createCursor(cursorsFile, 3, 3),
			createCursor(cursorsFile, 3, 2)
		};
	}
	
	private static Cursor createCursor (String cursorsFile, int x, int y){
		Toolkit tk = Toolkit.getDefaultToolkit();
		try {
			Image cursorImage = ImageUtils.crearImagen(cursorsFile , x*24, y*24, 24, 24);
			Cursor c = tk.createCustomCursor(cursorImage, new Point(12,12), "gfxui-"+x+"-"+y);
			return c;
		} catch (IOException e) {
			SworeGame.crash("Error loading cursors", e);
			return null;
		}
		
	}
	
	private int mouseDirection = -1;
	private Position mousePosition;
	
	private boolean mouseMovementActive = false;
	
	public void setMouseMovementActive(boolean mouseMovementActive) {
		this.mouseMovementActive = mouseMovementActive;
	}

	public void init(SwingSystemInterface psi, UserAction[] gameActions, Properties UIProperties,
			Action advance, Action target, Action attack, GFXUserInterface ui, Properties keyBindings){
		super.init(gameActions, advance, target, attack, ui,keyBindings);
		this.si = psi;
		if (UIProperties.getProperty("useMouse").equals("true")){
			psi.addMouseListener(this);
			psi.addMouseMotionListener(this);
			useMouse = true;
		}
		initializeCursors(UIProperties.getProperty("IMG_CURSORS"));
		Rectangle r = PropertyFilters.getRectangle(UIProperties.getProperty("mouseQuadrant"));
		x1 = r.x;
		x2 = r.x + r.width;
		y1 = r.y;
		y2 = r.y + r.height;
		
	}
	
	int x1 = (int)Math.round((800.0/9.0)*4.0);
	int x2 = (int)Math.round((800.0/9.0)*5.0);
	int y1 = (int)Math.round((600.0/9.0)*4.0);
	int y2 = (int)Math.round((600.0/9.0)*5.0);
	
	public GFXUserInterface ui(){
		return (GFXUserInterface) getUI();
	}
	
	/** 
	 * Returns the Action that the player wants to perform.
     * It may also forward a command instead
     */
	public Action selectAction(Actor who){
    	Debug.enterMethod(this, "selectAction", who);
	    CharKey input = null;
	    Action ret = null;
	    while (ret == null){
	    	mouseMovementActive = true;
	    	if (ui().gameOver())
	    		return null;
			input = si.inkey(200);
			if (input.code == CharKey.NONE && !useMouse)
				continue;
			ret = ((GFXUserInterface)getUI()).selectCommand(input);
			if (ret != null){
				if (ret.canPerform(player))
            		return ret;
            	else 
            		return null;
			}
			if (input.code == DONOTHING1_KEY) {
				Debug.exitMethod("null");
				return null;
			}
			if (input.code == DONOTHING2_KEY) {
				Debug.exitMethod("null");
				return null;
			}
			
			if (useMouse && mousePosition != null){
				// mouseDirection = -1;
				if (level.isValidCoordinate(mousePosition)){
						ret = target;
		            	try {
		            		ret.setPerformer(player);
		            		if (ret.canPerform(player))
		            			ret.setPosition(mousePosition);
		            		else {
		            			level.addMessage(ret.getInvalidationMessage());
		            			throw new ActionCancelException();
		            		}
	                     	Debug.exitMethod(ret);
	                     	mousePosition = null;
	                    	return ret;
						}
						catch (ActionCancelException ace){
			 				ui().addMessage(new Message("- Cancelled", player.getPosition()));
			 				si.refresh();
							ret = null;
						}

				}
				mousePosition = null;
			}
			
			if (isArrow(input) || (useMouse && mousePosition == null && mouseDirection != -1)){
				int direction = -1;
				if (useMouse && mouseDirection != -1){
					direction = mouseDirection;
					//mouseDirection = -1;
				} else {
					direction = toIntDirection(input);
				}
				
				
				Actor vMonster = player.getLevel().getActorAt(Position.add(player.getPosition(), Action.directionToVariation(direction)));
				if (vMonster != null && vMonster.isHostile() && attack.canPerform(player)){
					attack.setDirection(direction);
					Debug.exitMethod(attack);
					return attack;
				} else {
					advance.setDirection(direction);
					Debug.exitMethod(advance);
					switch (direction){
					case Action.UPLEFT:
					case Action.LEFT:
					case Action.DOWNLEFT:
						ui().setFlipFacing(true);
						break;
					case Action.UPRIGHT:
					case Action.RIGHT:
					case Action.DOWNRIGHT:
						ui().setFlipFacing(false);
						break;
					}
					if (advance.canPerform(player)){
						return advance;
					} else {
						ui().addMessage(new Message(advance.getInvalidationMessage(), player.getPosition()));
						ret = null;
					}
				}
			} else {
            	ret = getRelatedAction(input.code);
            	try {
	            	if (ret != null){
	            		ret.setPerformer(player);
	            		if (ret.canPerform(player))
	            			ui().setTargets(ret);
	            		else {
	            			level.addMessage(ret.getInvalidationMessage());
		            		throw new ActionCancelException();
	            		}
                     	Debug.exitMethod(ret);
                    	return ret;
					}

				}
				catch (ActionCancelException ace){
					//si.cls();
	 				//refresh();
	 				ui().addMessage(new Message("- Cancelled", player.getPosition()));
					ret = null;
				}
				//refresh();
			}
		}
		Debug.exitMethod("null");
		return null;
	}
	

	public String getID(){
		return "UI";
	}
    
	public ActionSelector derive(){
 		return null;
 	}
	
	
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1){
			int quadrant = defineQuadrant(e.getPoint().x, e.getPoint().y);
			mouseDirection = QDIRECTIONS[quadrant-1];
		} else if (e.getButton() == MouseEvent.BUTTON3){
			translatePosition(e.getPoint().x, e.getPoint().y);
		}
	}

	public void mouseReleased(MouseEvent e) {
		mouseDirection = -1;
	}

	public void mouseDragged(MouseEvent e) {
		int newQuadrant = defineQuadrant(e.getPoint().x, e.getPoint().y);
		if (mouseDirection != -1 && mouseDirection != QDIRECTIONS[newQuadrant-1]){
			mouseDirection = QDIRECTIONS[newQuadrant-1];
		}
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e) {
		if (!mouseMovementActive)
			return;
		int newQuadrant = defineQuadrant(e.getPoint().x, e.getPoint().y);
		si.setCursor(QCURSORS[newQuadrant-1]);
		/*
		switch (newQuadrant){
		case 9:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			break;
		case 6:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			break;
		case 3:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			break;
		case 8:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			break;
		case 5:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			break;
		case 2:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			break;
		case 7:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			break;
		case 4:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			break;
		case 1:
			si.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			break;
		}
		/*if (isCursorEnabled && updateCursorPosition(e.getPoint().x, e.getPoint().y))
			drawCursor();*/
	}
	
	private int defineQuadrant(int x, int y){
		if (x > x2)
			if (y > y2)
				return 9;
			else if (y>y1)
				return 6;
			else
				return 3;
		else if (x > x1)
			if (y > y2)
				return 8;
			else if (y>y1)
				return 5;
			else
				return 2;
		else
			if (y > y2)
				return 7;
			else if (y>y1)
				return 4;
			else
				return 1;
	}
	private Position tempRel = new Position(0,0);
	private void translatePosition(int x, int y){
		int bigx = (int)Math.ceil(x/32.0);
		int bigy = (int)Math.ceil(y/32.0);
		tempRel.x = bigx-ui().PC_POS.x-1;
		tempRel.y = bigy-ui().PC_POS.y-1;
		mousePosition = Position.add(player.getPosition(), tempRel);
	}
	private Position tempCursorPosition = new Position(0,0);
	private Position tempCursorPositionScr = new Position(0,0);
	
	private boolean updateCursorPosition(int x, int y){
		int bigx = (int)Math.ceil(x/32.0);
		int bigy = (int)Math.ceil(y/32.0);
		tempRel.x = bigx-ui().PC_POS.x-1;
		tempRel.y = bigy-ui().PC_POS.y-1;
		if (tempCursorPosition != null){
			if (tempCursorPosition.x == player.getPosition().x + bigx-ui().PC_POS.x-1 && 
				tempCursorPosition.y == player.getPosition().y + bigy-ui().PC_POS.y-1){
				return false;
			}
			tempCursorPosition.x=player.getPosition().x + bigx-ui().PC_POS.x-1;
			tempCursorPosition.y=player.getPosition().y + bigy-ui().PC_POS.y-1;
		}
		if (tempCursorPositionScr != null){
			tempCursorPositionScr.x=tempRel.x;
			tempCursorPositionScr.y=tempRel.y;
		}
		return true;
	}
	
	public static int toIntDirection(Position what){
		switch (what.x()){
			case 1:
				switch (what.y()){
					case 1:
						return Action.DOWNRIGHT;
					case 0:
						return Action.RIGHT;
					case -1:
						return Action.UPRIGHT;
				}
			case 0:
				switch (what.y()){
					case 1:
						return Action.DOWN;
					case -1:
						return Action.UP;
				}
			case -1:
				switch (what.y()){
					case 1:
						return Action.DOWNLEFT;
					case 0:
						return Action.LEFT;
					case -1:
						return Action.UPLEFT;
				}
		}

		return -1;
	}
	
	
	

	
}