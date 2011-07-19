package net.slashie.utils.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Util;
import net.slashie.utils.swing.BorderedGridBox.SelectedItem;

public class BorderedMenuBox extends AddornedBorderPanel {
	
	//Configurable properties
	private String title = "";
	private String legend;
	private BufferedImage box;
	private Color foreColor = Color.WHITE;
	private Color titleColor = Color.WHITE;

	private int itemHeight;
	
	//State Attributes
	private List items;
	private int currentPage;
	private int pages;
	private int itemsPerPage;
	private SwingSystemInterface si;
	private List<GFXMenuItem> shownItems;
	private MouseMotionAdapter mml;
	
	
	/*UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT*/
	public BorderedMenuBox(BufferedImage border1, BufferedImage border2,BufferedImage border3,BufferedImage border4, SwingSystemInterface g, Color backgroundColor, Color borderIn, Color borderOut, int borderWidth, int outsideBound, int inBound, int insideBound, int itemHeight, BufferedImage box){
		super (border1, border2, border3, border4, borderOut, borderIn, backgroundColor, borderWidth, outsideBound, inBound, insideBound );
		this.si = g;
		this.box = box;
		this.itemHeight = itemHeight;
	}
	
	public void setItemsPerPage(int ipp){
		itemsPerPage = ipp;
		setSize(getWidth(), (itemsPerPage+1)*itemHeight);
	}
	public void setMenuItems(List items){
		this.items = items;
	}
	
	protected int getSelectedItemByClick(Point point) {
		String[] legends = legend.split("XXX");
		int fontSize = getFont().getSize();
		int legendLines = legends.length > 0 ? legends.length: 1;
		int pixelY = point.y;
		
		pixelY -= (getLocation().y + getBorderWidth()); // Header
		pixelY -= (fontSize + legendLines * itemHeight);
		pixelY -= itemHeight;
		
		int selectedIndex = (int) Math.floor((double) pixelY / (double) itemHeight);
		selectedIndex ++;
		if (selectedIndex >= 0 && selectedIndex < shownItems.size()){
			return selectedIndex;
		} else {
			return -1;
		}
	}
	
	public void draw(){
		int xpos = (int)getLocation().getX();
		int ypos = (int)getLocation().getY();
		int fontSize = getFont().getSize();
		super.paintAt(si.getGraphics2D(), xpos, ypos);
		xpos+=getBorderWidth();
		ypos+=getBorderWidth();
		pages = (int)(Math.floor((items.size()-1) / (double)(itemsPerPage)) +1);
		si.printAtPixel(xpos, ypos+fontSize, title, titleColor);
		if (legend == null)
			legend = title;
		String[] legends = legend.split("XXX");
		int legendLines = legends.length;
		for (int i = 0; i < legends.length; i++){
			si.printAtPixel(xpos, ypos+fontSize+(i+1)*itemHeight, legends[i], foreColor);
		}
		
		shownItems = Util.page(items, itemsPerPage, currentPage);
		
		ypos+=itemHeight;
		
		
		int i = 0;
		for (; i < shownItems.size(); i++){
			
			GFXMenuItem item = (GFXMenuItem) shownItems.get(i);
			si.printAtPixel(xpos, ypos+(i+legendLines)*itemHeight+fontSize, ((char) (97 + i))+"." , foreColor );
			if (box != null){
				si.drawImage(xpos + itemHeight, ypos+ (i+legendLines) * itemHeight, box);
			}
			if (item.getMenuImage() != null)
				si.drawImage(xpos+itemHeight, ypos+ (i+legendLines) * itemHeight, item.getMenuImage());
			String description = item.getMenuDescription();
			
			String detail = item.getMenuDetail();
			
			si.printAtPixel(xpos + 2*itemHeight, ypos+ (i+legendLines)*itemHeight+fontSize, description, foreColor);
			if (detail != null && !detail.equals("")){
				si.printAtPixel(xpos+2*itemHeight, ypos+ (i+legendLines)*itemHeight+ 2* fontSize + 2, detail, foreColor);
			}
		}
		si.refresh();
	}

	@SuppressWarnings("unchecked")
	public Object getSelection (){
		final int pageElements = itemsPerPage;
		BlockingQueue<Integer> selectionQueue = new LinkedBlockingQueue<Integer>(1);
		// Keyboard Selection

		CallbackKeyListener<Integer> cbkl = new CallbackKeyListener<Integer>(selectionQueue){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					int code = SwingSystemInterface.charCode(e);
					if (code != CharKey.SPACE &&
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
		
		CallbackMouseListener<Integer> cbml = new CallbackMouseListener<Integer>(selectionQueue){
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					int selectedItem = getSelectedItemByClick(e.getPoint());
					if (selectedItem != -1)
						handler.put(selectedItem + CharKey.a);
				} catch (InterruptedException e1) {}
			}
		};
		
		final BorderedMenuBox this_ = this;
		mml = new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				draw();
				String[] legends = legend.split("XXX");
				int fontSize = getFont().getSize();
				int legendLines = legends.length > 0 ? legends.length: 1;
				

				int selectedItem = getSelectedItemByClick(e.getPoint());
				if (selectedItem != -1){
					int xpos = getLocation().x + getBorderWidth() - 4;
					int ypos = 5 + selectedItem * this_.itemHeight + getLocation().y + getBorderWidth() + (fontSize + legendLines * this_.itemHeight);
					si.setColor(titleColor);
					si.getGraphics2D().drawRect(xpos, ypos, getWidth() - 15 - getBorderWidth(), this_.itemHeight);
					si.getGraphics2D().drawRect(xpos+1, ypos+1, getWidth() - 15 - getBorderWidth() - 2, this_.itemHeight - 2);
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
		si.addKeyListener(cbkl);
		si.addMouseListener(cbml);
		
		GFXMenuItem selection = null;

		while (true){
			
			draw();
			List<GFXMenuItem> shownItems = Util.page(items, pageElements, currentPage);
			
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
			}
			else
			if (code >= CharKey.a && code <= CharKey.a + shownItems.size()-1){
				selection = shownItems.get(code - CharKey.a);
				break;
			}
			si.restore();
		}
		si.removeKeyListener(cbkl);
		si.removeMouseListener(cbml);
		si.removeMouseMotionListener(mml);
		return selection;
	}
	
	public Object getSelection(CharKey key) {
		List shownItems = Util.page(items, itemsPerPage, currentPage);
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
		int pageElements = itemsPerPage;
		while (true){
			
			draw();
			List shownItems = Util.page(items, pageElements, currentPage);
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

	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}


	


}