package net.slashie.serf.ui.oryxUI;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.slashie.serf.game.SworeGame;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.oryxUI.AnimatedGFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Position;
import net.slashie.utils.PropertyFilters;

public class UIAssetsLoader {
	private static Map<String, Image> images = new Hashtable<String, Image>();
	
	public static AnimatedGFXAppearance createAnimatedAppearance(String filename, int width, int height, String ID, int delay, Position... positions){
		BufferedImage bigImage = (BufferedImage) images.get(filename);
		if (bigImage == null){
			try {
				bigImage = ImageUtils.createImage(filename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+filename, e);
			}
			images.put(filename, bigImage);
		}
		
		Image[] frames = new Image[positions.length];
		int i = 0;
		for (Position position: positions){
			int xpos = position.x - 1;
			int ypos = position.y - 1;
			frames[i] = ImageUtils.crearImagen(bigImage,  xpos*width, ypos*height, width, height);
			i++;
		}
		AnimatedGFXAppearance ret = new AnimatedGFXAppearance(ID, frames, 0,0, delay);
		
		return ret;
	}
	
	public static GFXAppearance createAppearance(String filename, int width, int height, String ID, int xpos, int ypos){
		xpos--;
		ypos--;
		BufferedImage bigImage = (BufferedImage) images.get(filename);
		if (bigImage == null){
			try {
				bigImage = ImageUtils.createImage(filename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+filename, e);
			}
			images.put(filename, bigImage);
		}
		try {
			BufferedImage img = ImageUtils.crearImagen(bigImage, xpos*width, ypos*height, width, height);
			GFXAppearance ret = new GFXAppearance(ID, img, 0,0);
			return ret;
		} catch (Exception e){
			SworeGame.crash("Error loading image "+filename, e);
		}
		return null;
	}
	
	public static GFXAppearance sumAppearances(String filename, int width, int height, String ID, int xpos1, int ypos1, int xpos2, int ypos2){
		xpos1--;
		ypos1--;
		xpos2--;
		ypos2--;
		BufferedImage bigImage = (BufferedImage) images.get(filename);
		if (bigImage == null){
			try {
				bigImage = ImageUtils.createImage(filename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+filename, e);
			}
			images.put(filename, bigImage);
		}
		try {
			BufferedImage img1 = ImageUtils.crearImagen(bigImage,  xpos1*width, ypos1*height, width, height);
			BufferedImage img2 = ImageUtils.crearImagen(bigImage,  xpos2*width, ypos2*height, width, height);
			BufferedImage imgSum = ImageUtils.overlay(img1, img2, 0, 0);
			GFXAppearance ret = new GFXAppearance(ID, imgSum, 0,0);
			return ret;
		} catch (Exception e){
			SworeGame.crash("Error loading image "+filename, e);
		}
		return null;
		
	}

	public static Assets getAssets(String graphicsPackPath, String assetsXMLFile){
		Assets ret = new Assets(); 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(new File(graphicsPackPath+"/"+assetsXMLFile));
			NodeList baseNodes = document.getChildNodes();
			for (int i = 0; i < baseNodes.getLength(); i++){
				Node baseNode = baseNodes.item(i);
				if (baseNode.getNodeType() == Node.ELEMENT_NODE){
					Element baseElement = (Element) baseNode;
					if (baseElement.getTagName().equals("uiAssets")){
						processUiAssets(graphicsPackPath, baseElement, ret);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private static void processUiAssets (String graphicsPackPath, Element graphicsPackElement, Assets assets) throws IOException, FontFormatException{
		NodeList baseNodes = graphicsPackElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("image")){
					Image image = processImage(graphicsPackPath, baseElement, assets);
					assets.addImageAsset(baseElement.getAttribute("id"), image);
				} else if (baseElement.getTagName().equals("font")){
					Font font = processFont(graphicsPackPath, baseElement, assets);
					assets.addFontAsset(baseElement.getAttribute("id"), font);
				} else if (baseElement.getTagName().equals("cursor")){
					Cursor cursor = processCursor(graphicsPackPath, baseElement, assets);
					assets.addCursorAsset(baseElement.getAttribute("id"), cursor);
				} else if (baseElement.getTagName().equals("tileSet")){
					processTileSet(graphicsPackPath, baseElement, assets);
				}
			}
		}
	}
	
	private static void processTileSet (String graphicsPackPath, Element tileSetElement, Assets assets) throws IOException{
		NodeList baseNodes = tileSetElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("image")){
					Image image = processTilesetImage(graphicsPackPath, tileSetElement.getAttribute("file"), baseElement, assets);
					assets.addImageAsset(baseElement.getAttribute("id"), image);
				}
			}
		}
	}
	private static Image processTilesetImage(String graphicsPackPath, String filename, Element imageElement, Assets assets) throws IOException {
		String strBounds = imageElement.getAttribute("bounds");
		try {
			if (strBounds == null || strBounds.equals("")){
				return ImageUtils.createImage(graphicsPackPath+"/"+filename);
			} else {
				return PropertyFilters.getImage(graphicsPackPath+"/"+filename, strBounds);
			}
		} catch (IOException e) {
			System.err.println("Error loading image "+filename+" "+strBounds);
			throw e;
		}	
	}

	private static Cursor processCursor(String graphicsPackPath, Element cursorElement, Assets assets) throws IOException {
		String filename = cursorElement.getAttribute("file");
		int width = Integer.parseInt(cursorElement.getAttribute("width"));
		int height = Integer.parseInt(cursorElement.getAttribute("height"));
		int x = Integer.parseInt(cursorElement.getAttribute("x"));
		int y = Integer.parseInt(cursorElement.getAttribute("y"));
		int hotX = Integer.parseInt(cursorElement.getAttribute("hotX"));
		int hotY = Integer.parseInt(cursorElement.getAttribute("hotY"));
		return createCursor(graphicsPackPath+"/"+filename, x, y, width, height, hotX, hotY);
	}

	private static Font processFont(String graphicsPackPath, Element fontElement, Assets assets) throws FileNotFoundException, FontFormatException, IOException {
		return PropertyFilters.getFont(graphicsPackPath+"/"+fontElement.getAttribute("file"), fontElement.getAttribute("size"));
	}

	private static Image processImage(String graphicsPackPath, Element imageElement, Assets assets) throws IOException {
		String strBounds = imageElement.getAttribute("bounds");
		String filename = imageElement.getAttribute("file");
		try {
			if (strBounds == null || strBounds.equals("")){
				return ImageUtils.createImage(graphicsPackPath+"/"+filename);
			} else {
				return PropertyFilters.getImage(graphicsPackPath+"/"+filename, strBounds);
			}
		} catch (IOException e) {
			System.err.println("Error loading "+filename);
			throw e;
		}
	}
	
	public static Cursor createCursor (String cursorsFile, int x, int y, int width, int height, int hotX, int hotY) throws IOException{
		Toolkit tk = Toolkit.getDefaultToolkit();
		BufferedImage cursorImage = ImageUtils.createImage(cursorsFile , x, y, width, height);
		Dimension d = tk.getBestCursorSize(width, height);
		BufferedImage emptyImage = ImageUtils.createEmptyImage(d.width, d.height);
		Image compositeImage = ImageUtils.overlay(emptyImage, cursorImage, 0, 0);
		Cursor c = tk.createCustomCursor(compositeImage, new Point(hotX, hotY), "gfxui-"+x+"-"+y);
		return c;
	}
}
