package net.slashie.utils.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.*;

import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Util;

/**
 * Clickable grid with an option for on hover popups
 *
 */
public class BorderedGridBox extends AddornedBorderPanel {
	private static final long serialVersionUID = 1L;
	
	//Configurable properties
	private String title = "";
	private String legend;
	private BufferedImage box;
	private Color foreColor = Color.WHITE;
	private Color titleColor = Color.WHITE;
	private int itemHeight;
	private int itemWidth;
	private int gridX;
	private int gridY;
	
	// Status Attributes
	private List<? extends GFXMenuItem> items;
	private int currentPage;
	private int pages;
	private List<GFXMenuItem> shownItems;
	private MouseMotionListener mml;
	
	protected SwingSystemInterface si;
	
	
	/*UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT*/
	public BorderedGridBox(BufferedImage border1, BufferedImage border2,BufferedImage border3,BufferedImage border4, SwingSystemInterface g, Color backgroundColor, Color borderIn, Color borderOut, int borderWidth, int outsideBound, int inBound, int insideBound, 
			final int itemHeight, final int itemWidth, final int gridX, final int gridY, BufferedImage box){
		super (border1, border2, border3, border4, borderOut, borderIn, backgroundColor, borderWidth, outsideBound, inBound, insideBound );
		this.si = g;
		this.box = box;
		this.itemHeight = itemHeight;
		this.itemWidth = itemWidth;
		this.gridX = gridX;
		this.gridY = gridY;
		if (legend == null)
			legend = title;
		
		// Calculate legend height
		String[] legends = legend.split("XXX");
		int fontSize = getFont().getSize();
		final int lineHeight = (int)Math.round(fontSize*1.5);
		final int legendLines = legends.length > 0 ? legends.length: 1;
		
		setSize(gridX * itemWidth + borderWidth * 2, gridY * itemHeight + borderWidth * 2 + legendLines * lineHeight);
		// Mouse things
		mml = new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				draw(true);
				int pixelX = e.getPoint().x;
				int pixelY = e.getPoint().y;
				
				pixelX -= getLocation().x + getBorderWidth();
				pixelY -= getLocation().y + getBorderWidth() + (legendLines + 2) * lineHeight;
				
				pixelY += legendLines * lineHeight;
				int cursorX = (int) Math.floor((double) pixelX / (double) itemWidth);
				int cursorY = (int) Math.floor((double) pixelY / (double) itemHeight);
				int selectedIndex =  cursorX * gridY + cursorY;
				if (cursorX >= 0 && cursorX < gridX && cursorY >= 0 && cursorY < gridY && selectedIndex >= 0 && selectedIndex < shownItems.size()){
					GFXMenuItem item = (GFXMenuItem) shownItems.get(selectedIndex);
					int xpos = cursorX * itemWidth + getLocation().x + getBorderWidth();
					int ypos = cursorY * itemHeight + getLocation().y + getBorderWidth() + (legendLines + 1) * lineHeight;
					if (item instanceof CustomGFXMenuItem){
						((CustomGFXMenuItem) item).drawMenuItem(si, xpos, ypos, selectedIndex, true);
						if (((CustomGFXMenuItem) item).showTooltip()){
							((CustomGFXMenuItem) item).drawTooltip(si, xpos, ypos);
						}
					} else {
						defaultMenuItemPrint(item, 32, xpos, ypos, selectedIndex);
					}
					
					si.refresh();
					si.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					// No grid selected
					si.refresh();
					si.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				}
			}
		};
		si.addMouseMotionListener(mml);
	}
	
	public void setMenuItems(List<? extends GFXMenuItem> items){
		this.items = items;
	}
	
	@SuppressWarnings("unchecked")
	public void draw(boolean refresh){
		// Draw the frame
		int xpos = (int)getLocation().getX();
		int ypos = (int)getLocation().getY();
		super.paintAt(si.getGraphics2D(), xpos, ypos);
		
		// Prepare some variables
		int fontSize = getFont().getSize();
		int lineHeight = (int)Math.ceil(fontSize*1.5);
		xpos+=getBorderWidth();
		ypos+=getBorderWidth();
		int itemsPerPage = gridX * gridY;
		pages = (int)(Math.floor((items.size()-1) / (double)(itemsPerPage)) +1);
		
		// Draw the title
		si.printAtPixel(xpos, ypos+fontSize, title, titleColor);

		// Draw the legend
		if (legend == null)
			legend = title;
		String[] legends = legend.split("XXX");
		for (int i = 0; i < legends.length; i++){
			ypos += lineHeight;
			si.printAtPixel(xpos, ypos + fontSize, legends[i], foreColor);
		}
		
		if (legends.length > 0)
			ypos += lineHeight;
		
		// Calculate shown items from all collection
		shownItems = Util.page(items, itemsPerPage, currentPage);
		
		// Draw the items
		int startingY = ypos;
		int startingX = xpos;
		int xCursor = -1;
		int yCursor = gridY-1;
		
		for (int i = 0; i < shownItems.size(); i++){
			if (yCursor == gridY-1){
				yCursor = -1;
				xCursor ++;
			}
			yCursor++;
			xpos = startingX + xCursor * itemWidth;
			ypos = startingY + yCursor * itemHeight;
			
			GFXMenuItem item = (GFXMenuItem) shownItems.get(i);
			if (item instanceof CustomGFXMenuItem){
				((CustomGFXMenuItem) item).drawMenuItem(si, xpos, ypos, i, false);
			} else {
				defaultMenuItemPrint(item, 32, xpos, ypos, i);
			}
		}
		if (refresh)
			si.refresh();
	}

	private void defaultMenuItemPrint(GFXMenuItem item, int boxWidth, int xpos, int ypos, int i) {
		int fontSize = getFont().getSize();
		int lineHeight = (int)Math.round(fontSize*1.5);
		if (box != null){
			si.drawImage(xpos, ypos, box);
		}
		if (item.getMenuImage() != null)
			si.drawImage(xpos, ypos, item.getMenuImage());
		
		String description = item.getMenuDescription();
		String detail = item.getMenuDetail();
		si.printAtPixel(xpos + boxWidth, ypos + fontSize, ((char) (97 + i))+". " + description, foreColor);
		if (detail != null && !detail.equals("")){
			si.printAtPixel(xpos + boxWidth, ypos + lineHeight + fontSize, detail, foreColor);
		}
	}

	public Object getSelection (){
		int itemsPerPage = gridX * gridY;
		int pageElements = itemsPerPage;
		while (true){
			draw(true);
			@SuppressWarnings("unchecked")
			List<GFXMenuItem> shownItems = Util.page(items, pageElements, currentPage);
			CharKey key = new CharKey(CharKey.NONE);
			while (key.code != CharKey.SPACE &&
					key.code != CharKey.ESC &&
				   key.code != CharKey.UARROW &&
				   key.code != CharKey.DARROW &&
				   key.code != CharKey.N8 &&
				   key.code != CharKey.N2 &&
				   (key.code < CharKey.A || key.code > CharKey.A + pageElements-1) &&
				   (key.code < CharKey.a || key.code > CharKey.a + pageElements-1)
				   )
			   key = si.inkey();
			if (key.code == CharKey.SPACE || key.code == CharKey.ESC)
				return null;
			if (key.code == CharKey.UARROW || key.code == CharKey.N8)
				if (currentPage > 0)
					currentPage --;
			if (key.code == CharKey.DARROW || key.code == CharKey.N2)
				if (currentPage < pages-1)
					currentPage ++;
			
			if (key.code >= CharKey.A && key.code <= CharKey.A + shownItems.size()-1)
				return shownItems.get(key.code - CharKey.A);
			else
			if (key.code >= CharKey.a && key.code <= CharKey.a + shownItems.size()-1)
				return shownItems.get(key.code - CharKey.a);
			si.restore();
		}
	}
	
	public Object getSelection(CharKey key) {
		int itemsPerPage = gridX * gridY;
		@SuppressWarnings("unchecked")
		List<GFXMenuItem> shownItems = Util.page(items, itemsPerPage, currentPage);
		if (key.code == CharKey.SPACE || key.code == CharKey.ESC)
			return null;
		if (key.code == CharKey.UARROW || key.code == CharKey.N8)
			if (currentPage > 0)
				currentPage --;
		if (key.code == CharKey.DARROW || key.code == CharKey.N2)
			if (currentPage < pages-1)
				currentPage ++;
		
		if (key.code >= CharKey.A && key.code <= CharKey.A + shownItems.size()-1)
			return shownItems.get(key.code - CharKey.A);
		else
		if (key.code >= CharKey.a && key.code <= CharKey.a + shownItems.size()-1)
			return shownItems.get(key.code - CharKey.a);
		return null;
	}
	
	public Object getSelectionAKS (int[] keys) throws AdditionalKeysSignal{
		int itemsPerPage = gridX * gridY;
		int pageElements = itemsPerPage;
		while (true){
			draw(true);
			@SuppressWarnings("unchecked")
			List<GFXMenuItem> shownItems = Util.page(items, pageElements, currentPage);
			CharKey key = new CharKey(CharKey.NONE);
			while (key.code != CharKey.SPACE &&
					key.code != CharKey.ESC &&
				   key.code != CharKey.UARROW &&
				   key.code != CharKey.DARROW &&
				   key.code != CharKey.N8 &&
				   key.code != CharKey.N2 &&
				   (key.code < CharKey.A || key.code > CharKey.A + pageElements-1) &&
				   (key.code < CharKey.a || key.code > CharKey.a + pageElements-1) &&
				   !isOneOf(key.code, keys)
				   )
			   key = si.inkey();
			if (key.code == CharKey.SPACE || key.code == CharKey.ESC)
				return null;
			if (key.code == CharKey.UARROW || key.code == CharKey.N8)
				if (currentPage > 0)
					currentPage --;
			if (key.code == CharKey.DARROW || key.code == CharKey.N2)
				if (currentPage < pages-1)
					currentPage ++;
			
			if (key.code >= CharKey.A && key.code <= CharKey.A + shownItems.size()-1)
				return shownItems.get(key.code - CharKey.A);
			else
			if (key.code >= CharKey.a && key.code <= CharKey.a + shownItems.size()-1)
				return shownItems.get(key.code - CharKey.a);
			if (isOneOf(key.code, keys))
				throw new AdditionalKeysSignal(key.code);
			si.restore();

		}
	}
	
	public void setTitle(String s){
		title = s;
	}
	
	protected boolean isOneOf(int value, int[] values){
		for (int i = 0; i < values.length; i++){
			if (value == values[i])
				return true;
		}
		return false;
	}
	
	public void setForeColor(Color color) {
		foreColor = color;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}
	
	public void kill(){
		si.removeMouseMotionListener(mml);
	}
}