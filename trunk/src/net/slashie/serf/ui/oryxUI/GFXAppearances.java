package net.slashie.serf.ui.oryxUI;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
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

public class GFXAppearances {
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

	public static List<Appearance> getGFXAppearances(String graphicsPackPath, String graphicsPackXMLFile){
		List<Appearance> ret = new ArrayList<Appearance>(); 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(new File(graphicsPackPath+"/"+graphicsPackXMLFile));
			NodeList baseNodes = document.getChildNodes();
			for (int i = 0; i < baseNodes.getLength(); i++){
				Node baseNode = baseNodes.item(i);
				if (baseNode.getNodeType() == Node.ELEMENT_NODE){
					Element baseElement = (Element) baseNode;
					if (baseElement.getTagName().equals("graphicsPack")){
						processGraphicsPack(graphicsPackPath, baseElement, ret);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private static void processGraphicsPack (String graphicsPackPath, Element graphicsPackElement, List<Appearance> appearancesList){
		NodeList baseNodes = graphicsPackElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("appearances")){
					processAppearances(graphicsPackPath, baseElement, appearancesList);
				}
			}
		}
	}

	private static void processAppearances (String graphicsPackPath, Element appearancesElement, List<Appearance> appearancesList){
		NodeList baseNodes = appearancesElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("appearance")){
					appearancesList.add(processAppearance(graphicsPackPath, appearancesElement, baseElement));
				} else if (baseElement.getTagName().equals("animatedAppearance")){
					appearancesList.add(processAnimatedAppearance(graphicsPackPath, appearancesElement, baseElement));
				} else if (baseElement.getTagName().equals("compositeAppearance")){
					appearancesList.add(processCompositeAppearance(graphicsPackPath, appearancesElement, baseElement));
				} else {
					System.out.println("Error: Invalid appearance tag "+baseElement.getTagName());
				}
			}
		}
	}

	private static Appearance processAppearance(String graphicsPackPath, Element appearancesElement, Element baseElement) {
		String filename = appearancesElement.getAttribute("file");
		int tileWidth = Integer.parseInt(appearancesElement.getAttribute("tileWidth"));
		int tileHeight = Integer.parseInt(appearancesElement.getAttribute("tileHeight"));
		String ID = baseElement.getAttribute("id");
		int xpos = Integer.parseInt(baseElement.getAttribute("x"));
		int ypos = Integer.parseInt(baseElement.getAttribute("y"));
		return createAppearance(graphicsPackPath+"/"+filename, tileWidth, tileHeight, ID, xpos, ypos);
	}
	
	private static Appearance processAnimatedAppearance(String graphicsPackPath, Element appearancesElement, Element baseElement) {
		String filename = appearancesElement.getAttribute("file");
		int tileWidth = Integer.parseInt(appearancesElement.getAttribute("tileWidth"));
		int tileHeight = Integer.parseInt(appearancesElement.getAttribute("tileHeight"));
		String ID = baseElement.getAttribute("id");
		String[] xStrings = baseElement.getAttribute("x").split(",");
		String[] yStrings = baseElement.getAttribute("y").split(",");
		int delay = Integer.parseInt(baseElement.getAttribute("delay"));
		Position[] positions = new Position[xStrings.length];
		for (int i = 0; i < xStrings.length; i++){
			positions[i] = new Position(Integer.parseInt(xStrings[i]), Integer.parseInt(yStrings[i]));
		}
		return createAnimatedAppearance(graphicsPackPath+"/"+filename, tileWidth, tileHeight, ID, delay, positions);
	}

	private static Appearance processCompositeAppearance(String graphicsPackPath, Element appearancesElement, Element baseElement) {
		String filename = appearancesElement.getAttribute("file");
		int tileWidth = Integer.parseInt(appearancesElement.getAttribute("tileWidth"));
		int tileHeight = Integer.parseInt(appearancesElement.getAttribute("tileHeight"));
		String ID = baseElement.getAttribute("id");
		int xpos1 = Integer.parseInt(baseElement.getAttribute("x1"));
		int ypos1 = Integer.parseInt(baseElement.getAttribute("y1"));
		int xpos2 = Integer.parseInt(baseElement.getAttribute("x2"));
		int ypos2 = Integer.parseInt(baseElement.getAttribute("y2"));
		return sumAppearances(graphicsPackPath+"/"+filename, tileWidth, tileHeight, ID, xpos1, ypos1, xpos2, ypos2);
	}
}
