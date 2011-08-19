package net.slashie.utils.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	protected String legend;
	private BufferedImage box;
	private Color foreColor = Color.WHITE;
	private Color titleColor = Color.WHITE;
	private int itemHeight;
	private int itemWidth;
	protected int gridX;
	protected int gridY;
	
	// Status Attributes
	protected List<? extends GFXMenuItem> items;
	private int currentPage;
	private int pages;
	protected List<GFXMenuItem> shownItems;
	private MouseMotionListener mml;
	protected boolean hoverDisabled;
	protected boolean wasJustOnHovered;
	
	private int usedBuffer = 0;
	
	protected SwingSystemInterface si;

	protected Integer preselectedCode;

	protected CleanButton closeButton;
	
	public class SelectedItem{
		public int selectedIndex;
		public int cursorX;
		public int cursorY;
	}
	
	/*UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT*/
	public BorderedGridBox(BufferedImage border1, BufferedImage border2,BufferedImage border3,BufferedImage border4, SwingSystemInterface g, Color backgroundColor, Color borderIn, Color borderOut, int borderWidth, int outsideBound, int inBound, int insideBound, 
			final int itemHeight, final int itemWidth, final int gridX, final int gridY, BufferedImage box, CleanButton closeButton){
		super (border1, border2, border3, border4, borderOut, borderIn, backgroundColor, borderWidth, outsideBound, inBound, insideBound );
		this.si = g;
		this.box = box;
		this.itemHeight = itemHeight;
		this.itemWidth = itemWidth;
		this.gridX = gridX;
		this.gridY = gridY;
		if (legend == null)
			legend = title;
		this.closeButton = closeButton;
		if (closeButton != null)
			si.add(closeButton);
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
				if (hoverDisabled)
					return;
				if (items == null)
					return;
				draw(true);
				
				SelectedItem selectedItem = getSelectedItemByClick(e.getPoint(), legendLines, lineHeight);
				if (selectedItem != null){
					if (wasJustOnHovered){
						si.restoreFromBuffer(usedBuffer, getDrawingLayer());
					} else {
						si.commitLayer(getDrawingLayer());
						si.backupInBuffer(usedBuffer, getDrawingLayer());
					}
					GFXMenuItem item = (GFXMenuItem) shownItems.get(selectedItem.selectedIndex);
					int xpos = selectedItem.cursorX * itemWidth + getLocation().x + getBorderWidth();
					int ypos = selectedItem.cursorY * itemHeight + getLocation().y + getBorderWidth() + (legendLines + 1) * lineHeight;
					if (item instanceof CustomGFXMenuItem){
						((CustomGFXMenuItem) item).drawMenuItem(si, xpos, ypos, selectedItem.selectedIndex, true);
						if (((CustomGFXMenuItem) item).showTooltip()){
							((CustomGFXMenuItem) item).drawTooltip(si, xpos, ypos, selectedItem.selectedIndex);
						}
					} else {
						defaultMenuItemPrint(item, 32, xpos, ypos, selectedItem.selectedIndex);
					}
					si.commitLayer(getDrawingLayer());
					si.setCursor(getHandCursor());
					wasJustOnHovered = true;
				} else {
					if (wasJustOnHovered){
						// No grid selected
						//si.loadAndDrawLayer(getDrawingLayer());
						si.restoreFromBuffer(usedBuffer, getDrawingLayer());
						si.commitLayer(getDrawingLayer());
						wasJustOnHovered = false;
					} else {
						si.commitLayer(getDrawingLayer());
						//si.setCursor(getDefaultCursor());
					}
				}
			}
		};
		si.addMouseMotionListener(mml);
	}
	
	protected Cursor getDefaultCursor() {
		return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	}

	protected Cursor getHandCursor() {
		return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}
	
	protected SelectedItem getSelectedItemByClick(Point point, int legendLines, int lineHeight) {
		int pixelX = point.x;
		int pixelY = point.y;
		
		pixelX -= getLocation().x + getBorderWidth();
		pixelY -= getLocation().y + getBorderWidth() + (legendLines + 2) * lineHeight;
		
		pixelY += legendLines * lineHeight;
		int cursorX = (int) Math.floor((double) pixelX / (double) itemWidth);
		int cursorY = (int) Math.floor((double) pixelY / (double) itemHeight);
		int selectedIndex =  cursorX * gridY + cursorY;
		if (cursorX >= 0 && cursorX < gridX && cursorY >= 0 && cursorY < gridY && selectedIndex >= 0 && selectedIndex < shownItems.size()){
			SelectedItem ret = new SelectedItem();
			ret.cursorX = cursorX;
			ret.cursorY = cursorY;
			ret.selectedIndex = selectedIndex;
			return ret;
		} else {
			return null;
		}
	}
	
	public void setMenuItems(List<? extends GFXMenuItem> items){
		currentPage = 0;
		this.items = items;
		int itemsPerPage = gridX * gridY;
		pages = (int)(Math.floor((items.size()-1) / (double)(itemsPerPage)) +1);
	}
	
	public int getDrawingLayer(){
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public void draw(boolean refresh){
		// Draw the frame
		int xpos = (int)getLocation().getX();
		int ypos = (int)getLocation().getY();
		super.paintAt(si.getDrawingGraphics(getDrawingLayer()), xpos, ypos);
		
		// Prepare some variables
		int fontSize = getFont().getSize();
		int lineHeight = (int)Math.ceil(fontSize*1.5);
		xpos+=getBorderWidth();
		ypos+=getBorderWidth();
		int itemsPerPage = gridX * gridY;
		pages = (int)(Math.floor((items.size()-1) / (double)(itemsPerPage)) +1);
		
		// Draw the title
		si.printAtPixel(getDrawingLayer(), xpos, ypos+fontSize, title, titleColor);

		// Draw the legend
		if (legend == null)
			legend = title;
		String[] legends = legend.split("XXX");
		for (int i = 0; i < legends.length; i++){
			ypos += lineHeight;
			si.printAtPixel(getDrawingLayer(), xpos, ypos + fontSize, legends[i], foreColor);
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
		
		// Draw the footer
		if (pages > 1){
			si.printAtPixel(getDrawingLayer(), (int)getLocation().getX()+getBorderWidth(), (int)getLocation().getY()+getHeight()-getBorderWidth()-lineHeight, "Page "+(currentPage+1)+"/"+pages, Color.WHITE);
		}
		if (refresh)
			si.commitLayer(getDrawingLayer());
	}

	private void defaultMenuItemPrint(GFXMenuItem item, int boxWidth, int xpos, int ypos, int i) {
		int fontSize = getFont().getSize();
		int lineHeight = (int)Math.round(fontSize*1.5);
		if (box != null){
			si.drawImage(getDrawingLayer(), xpos, ypos, box);
		}
		if (item.getMenuImage() != null)
			si.drawImage(getDrawingLayer(), xpos, ypos, item.getMenuImage());
		
		String description = item.getMenuDescription();
		String detail = item.getMenuDetail();
		si.printAtPixel(getDrawingLayer(), xpos + boxWidth, ypos + fontSize, ((char) (97 + i))+". " + description, foreColor);
		if (detail != null && !detail.equals("")){
			si.printAtPixel(getDrawingLayer(), xpos + boxWidth, ypos + lineHeight + fontSize, detail, foreColor);
		}
	}
	
	/**
	 * This is a blocking method that returns a selection for the grid box.
	 * 
	 * Since the gridbox supports mouse selection, this method syncs with the mouse
	 * listener in order to perform a blocking return.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GFXMenuItem getSelection (){
		int itemsPerPage = gridX * gridY;
		final int pageElements = itemsPerPage;
		BlockingQueue<Integer> selectionQueue = new LinkedBlockingQueue<Integer>(1);
		boolean preselected = false;
		// Preselection
		if (preselectedCode != null){
			preselected = true;
			try {
				selectionQueue.put(preselectedCode);
			} catch (InterruptedException e1) {}
			preselectedCode = null;
		}
		
		// Keyboard Selection

		CallbackKeyListener<Integer> cbkl = new CallbackKeyListener<Integer>(selectionQueue){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					int code = SwingSystemInterface.charCode(e);
					if (code != CharKey.SPACE &&
						code != CharKey.ENTER &&
						code != CharKey.ESC &&
						code != CharKey.UARROW &&
						code != CharKey.DARROW &&
						code != CharKey.N8 &&
						code != CharKey.N2 &&
						(code < CharKey.A || code > CharKey.A + pageElements-1) &&
						(code < CharKey.a || code > CharKey.a + pageElements-1)
						){
						
					} else {
						handler.put(code);
					}
				} catch (InterruptedException e1) {}
			}
		}; 
		
		CallbackMouseListener cbml = new CallbackMouseListener(selectionQueue){
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String[] legends = legend.split("XXX");
					int fontSize = getFont().getSize();
					final int lineHeight = (int)Math.round(fontSize*1.5);
					final int legendLines = legends.length > 0 ? legends.length: 1;
					SelectedItem selectedItem = getSelectedItemByClick(e.getPoint(), legendLines, lineHeight);
					if (selectedItem != null)
						handler.put(selectedItem.selectedIndex + CharKey.a);
					onItemSelected();
				} catch (InterruptedException e1) {}
			}
		};
		
		CallbackActionListener<Integer> cbal = new CallbackActionListener<Integer>(selectionQueue){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.put(CharKey.SPACE);
				} catch (InterruptedException e1) {}
			}
		};
		
		si.addKeyListener(cbkl);
		si.addMouseListener(cbml);
		if (closeButton != null)
			closeButton.addActionListener(cbal);
		GFXMenuItem selection = null;
		while (true){
			List<GFXMenuItem> shownItems = Util.page(items, pageElements, currentPage);
			if (!preselected)
				draw(true);
			Integer code = null;
  	  		while (code == null){
				try {
					code = selectionQueue.take();
				} catch (InterruptedException e1) {
				}
	  		}
			
			if (code == CharKey.SPACE || code == CharKey.ESC){
				selection = null;
				break;
			}
			if (code == CharKey.UARROW || code == CharKey.N8)
				if (currentPage > 0)
					currentPage --;
			if (code == CharKey.DARROW || code == CharKey.N2)
				if (currentPage < pages-1)
					currentPage ++;
			
			if (code >= CharKey.A && code <= CharKey.A + shownItems.size()-1){
				selection = shownItems.get(code - CharKey.A);
				break;
			}else
			if (code >= CharKey.a && code <= CharKey.a + shownItems.size()-1){
				selection = shownItems.get(code - CharKey.a);
				break;
			}
			si.commitLayer(getDrawingLayer());
		}
		si.removeKeyListener(cbkl);
		si.removeMouseListener(cbml);
		if (closeButton != null)
			closeButton.removeActionListener(cbal);
		wasJustOnHovered = false;
		return selection;
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
		if (closeButton != null)
			si.remove(closeButton);
	}
	
	public void onItemSelected() {
	}
	
	public void setHoverDisabled(boolean v){
		hoverDisabled = v;
	}
	
	public void rePag(){
		if (currentPage > 0)
			currentPage --;
	}
	
	public void avPag(){
		if (currentPage < pages-1)
			currentPage ++;
	}
	
	public int getCurrentPage(){
		return currentPage;
	}
	
	public int getItemsPerPage(){
		return gridX * gridY;
	}
	
	public boolean isValidPage(int page){
		return page >= 0 && page <= pages - 1;
	}
	
	public int getPages(){
		return pages;
	}
	
	public void setCurrentPage(int page){
		if (isValidPage(page))
			currentPage = page;
	}

	public void setUsedBuffer(int usedBuffer) {
		this.usedBuffer = usedBuffer;
	}
	
}