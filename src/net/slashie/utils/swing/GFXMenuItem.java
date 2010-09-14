package net.slashie.utils.swing;

import java.awt.Image;

public interface GFXMenuItem extends java.io.Serializable{
	public Image getMenuImage();
	public String getMenuDescription();
	public String getMenuDetail();
	public String getGroupClassifier();

}