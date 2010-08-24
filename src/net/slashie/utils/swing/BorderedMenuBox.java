package net.slashie.utils.swing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;

import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Util;


public class BorderedMenuBox extends AddornedBorderPanel {
	
	//Configurable properties
	private String title = "";
	private String legend;
	private BufferedImage box;
	private Color foreColor = Color.WHITE;
	private int itemHeight;
	
	//State Attributes
	private List items;
	private int currentPage;
	private int pages;
	private int itemsPerPage;
	private SwingSystemInterface si;
	
	
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
	
	
	public void draw(){
		int xpos = (int)getLocation().getX();
		int ypos = (int)getLocation().getY();
		int fontSize = getFont().getSize();
		super.paintAt(si.getGraphics2D(), xpos, ypos);
		xpos+=getBorderWidth();
		ypos+=getBorderWidth();
		pages = (int)(Math.floor((items.size()-1) / (double)(itemsPerPage)) +1);
		si.printAtPixel(xpos, ypos+fontSize, title, foreColor);
		if (legend == null)
			legend = title;
		String[] legends = legend.split("XXX");
		int legendLines = legends.length;
		for (int i = 0; i < legends.length; i++){
			si.printAtPixel(xpos, ypos+fontSize+(i+1)*itemHeight, legends[i], foreColor);
		}
		
		List shownItems = Util.page(items, itemsPerPage, currentPage);
		
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
				si.printAtPixel(xpos+2*itemHeight, (int) (ypos+ (i+legendLines)* itemHeight + Math.round(itemHeight/2d))+fontSize, detail, foreColor);
			}
		}
		si.refresh();
	}

	public Object getSelection (){
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


}