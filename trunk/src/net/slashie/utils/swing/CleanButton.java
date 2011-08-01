package net.slashie.utils.swing;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class CleanButton extends JButton{
	private static final long serialVersionUID = 1L;
	private Image backgroundImage;
	private String popupText;
	
	public void setPopupText(String popupText) {
		this.popupText = popupText;
	}
	
	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	
	public CleanButton(ImageIcon icon, Cursor c){
		this.backgroundImage = icon.getImage();
		clean();
		setCursor(c);
	}
	
	public CleanButton(Cursor c){
		super();
		clean();
		setCursor(c);
	}
	
	public CleanButton(ImageIcon icon){
		this(icon, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	public CleanButton(Image image){
		this (new ImageIcon(image));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (isVisible() && backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
		super.paintComponent(g);
	}
	
	public CleanButton(Image image, Cursor c){
		this (new ImageIcon(image), c);
	}
	
	public CleanButton(){
		this(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	private void clean(){
		setContentAreaFilled(false);
		setOpaque(false);
		setFocusable(false);
		setBorder(null);		
	}

	public String getPopupText() {
		return popupText;
	}
}
