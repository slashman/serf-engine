package net.slashie.utils.swing;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;

import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Util;


public class MenuBox {
	
	private List<GFXMenuItem> items;
	private String title = "";

	//State Attributes
	private int currentPage;
	private int pages;
	
	//Components
	private int xpos, ypos, width, itemsPerPage;;
	private SwingSystemInterface si;
	private BufferedImage box;
	public MenuBox(SwingSystemInterface g, BufferedImage box){
		this.si = g;
		this.box = box;
	}
	
	private boolean showOptions;
	
	public void setPosition(int x, int y){
		xpos = x;
		ypos = y;
	}
	
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public void setItemsPerPage(int ipp){
		itemsPerPage = ipp;
	}
	public void setMenuItems(Vector items){
		this.items = items;
	}
	
	public void setMenuItems(List items){
		this.items = items;
	}

	private int gap = 24;
	
	public void setGap(int val){
		gap = val;
	}
	
	
	
	public void draw(){
		pages = (int)(Math.floor((items.size()-1) / (itemsPerPage)) +1);
		int fontSize = si.getDrawingGraphics().getFont().getSize();

		si.print(xpos, ypos, title, Color.BLUE);
		List<GFXMenuItem> shownItems = Util.page(items, itemsPerPage, currentPage);
		
		if (ordinal ){
			xpos-=2;
		}
		
		int i = 0;
		for (; i < shownItems.size(); i++){
			
			GFXMenuItem item = shownItems.get(i);
			if (!ordinal && isShowOptions()){
				si.printAtPixel(xpos*10, (ypos+1)*24+i*gap, ((char) (97 + i))+"." , Color.BLUE);
			}
			if (box != null){
				si.drawImage((xpos+2)*10+1, ypos*24+ i * gap + (int)(gap * 0.3D)-4, box);
			}
			if (item.getMenuImage() != null)
				si.drawImage((xpos+2)*10+5, ypos*24+ i * gap + (int)(gap * 0.3D), item.getMenuImage());
			String description = item.getMenuDescription();
			String detail = item.getMenuDetail();
			
			si.printAtPixel((xpos+6)*10, (ypos+1)*24 + i*gap, description, Color.WHITE);
			if (detail != null && !detail.equals("")){
				si.printAtPixel((xpos+6)*10, (ypos+1)*24 + i*gap + fontSize - 10, detail, Color.WHITE);
			}
		}
		ordinal = false;
		//si.print(inPosition.x, inPosition.y, inHeight+" "+pageElements+" "+pages);
		/*for (; i < inHeight-promptSize; i++){
			si.print(inPosition.x, inPosition.y+i+promptSize+1, spaces);
		}*/
		si.refresh();
	}

	public void setBounds(int x, int y, int width, int height){
		this.xpos = x;
		this.ypos = y;
		this.width = width;
		this.itemsPerPage = height;
	}
	
	public Object getSelection (){
		int pageElements = itemsPerPage;
		while (true){
			
			draw();
			List shownItems = Util.page(items, pageElements, currentPage);
			CharKey key = new CharKey(CharKey.NONE);
			while (key.code != CharKey.SPACE &&
				   key.code != CharKey.UARROW &&
				   key.code != CharKey.DARROW &&
				   key.code != CharKey.N8 &&
				   key.code != CharKey.N2 &&
				   (key.code < CharKey.A || key.code > CharKey.A + pageElements-1) &&
				   (key.code < CharKey.a || key.code > CharKey.a + pageElements-1)
				   )
			   key = si.inkey();
			if (key.code == CharKey.SPACE)
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
			si.loadLayer();

		}
	}
	
	public Object getUnpagedSelection (){
		int pageElements = itemsPerPage;
		draw();
		List shownItems = Util.page(items, pageElements, currentPage);
		CharKey key = new CharKey(CharKey.NONE);
		while (key.code != CharKey.SPACE &&
			   (key.code < CharKey.A || key.code > CharKey.A + pageElements-1) &&
			   (key.code < CharKey.a || key.code > CharKey.a + pageElements-1)
			   )
		   key = si.inkey();
		if (key.code == CharKey.SPACE)
			return null;
		if (key.code >= CharKey.A && key.code <= CharKey.A + shownItems.size()-1)
			return shownItems.get(key.code - CharKey.A);
		else
		if (key.code >= CharKey.a && key.code <= CharKey.a + shownItems.size()-1)
			return shownItems.get(key.code - CharKey.a);
		return null;

	}
	boolean ordinal = false;
	public Object getUnpagedOrdinalSelectionAKS (int[] keys) throws AdditionalKeysSignal{
		ordinal = true;
		draw();
		CharKey key = new CharKey(CharKey.NONE);
		while (key.code != CharKey.SPACE && !isOneOf(key.code, keys))
		   key = si.inkey();
		if (key.code == CharKey.SPACE)
			return null;
		if (isOneOf(key.code, keys))
			throw new AdditionalKeysSignal(key.code);
		return null;

	}
	
	protected boolean isOneOf(int value, int[] values){
		for (int i = 0; i < values.length; i++){
			if (value == values[i])
				return true;
		}
		return false;
	}
	
	public void setTitle(String s){
		title = s;
	}


	public boolean isShowOptions() {
		return showOptions;
	}


	public void setShowOptions(boolean showOptions) {
		this.showOptions = showOptions;
	}
}