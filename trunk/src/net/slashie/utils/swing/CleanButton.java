package net.slashie.utils.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public class CleanButton extends JButton{
	private static final long serialVersionUID = 1L;
	private Image background;
	private Image face;
	private int faceX, faceY;
	private Image hover;
	private String popupText;

	private boolean onHover = false;
	
	private static JLabel legendLabel;
	private static SwingSystemInterface ssi;
	
	public static void init(JLabel legendLabel, SwingSystemInterface ssi){
		CleanButton.legendLabel = legendLabel;
		CleanButton.ssi = ssi;
	}
	
	public void setFace(Image face){
		this.face = face;
		if (face != null){
			faceX = (int)Math.floor((getWidth() - face.getWidth(null))/2.0d);
			faceY = (int)Math.floor((getHeight() - face.getHeight(null))/2.0d);
		}
	}
	
	public void setHover(Image hover){
		this.hover = hover;
		if (hover != null){
			this.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e) {
					onHover = true;
					Component b = (Component) e.getSource();
					String text = getPopupText();
					if (text == null)
						return;
					FontMetrics metrics = legendLabel.getFontMetrics(legendLabel.getFont());
					int textWidth = (int)(metrics.stringWidth(text));
					int xLocation = b.getLocationOnScreen().x+36-ssi.getScreenPosition().x;
					if (xLocation + textWidth + 30  > 800){
						int diff = xLocation + textWidth + 30 - 800;
						xLocation -= diff;
					}
					legendLabel.setText(getPopupText());
					legendLabel.setLocation(xLocation, b.getLocationOnScreen().y+getHeight()-ssi.getScreenPosition().y);
					legendLabel.setVisible(true);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					onHover = false;
					legendLabel.setVisible(false);
				}
			});
		}
	}
	
	public void setPopupText(String popupText) {
		this.popupText = popupText;
	}
	
	public void setBackground(Image background) {
		this.background = background;
	}
	
	public CleanButton(Image background, Image hover, Image face, Cursor c, String text){
		setBackground(background);
		if (background != null)
			setSize(background.getWidth(null), background.getHeight(null));
		else if (face != null){
			setSize(face.getWidth(null), face.getHeight(null));
		}
		setPreferredSize(getSize());
		setHover(hover);
		setFace(face);
		setCursor(c);
		setText(text);
		clean();
	}
	
	public CleanButton(Image background, Image hover, Image face, Cursor c){
		this(background, hover, face, c, "");
	}
	
	public CleanButton(Image background, Cursor c, String text){
		this(background, null, null, c, text);
	}
	
	public CleanButton(Image background, Cursor c){
		this(background, c, "");
	}
	
	public CleanButton(Image background){
		this(background, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (isVisible()){
			if (hover != null && onHover)
				g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
			else if (background != null)
				g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
			
			if (face != null)
				g.drawImage(face, faceX, faceY, this);	
		}
		super.paintComponent(g);
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
	
	@Override
	public void setVisible(boolean flag) {
		super.setVisible(flag);
		if (!flag){
			legendLabel.setVisible(false);
		}
	}
	
	
}
