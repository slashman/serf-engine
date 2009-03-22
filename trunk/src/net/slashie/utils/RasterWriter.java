package net.slashie.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Hashtable;

public class RasterWriter {
	static Hashtable<String, String> charmap = new Hashtable<String, String>();
	static {
		/*charmap.put("R189G188B106", "x");
		charmap.put("R187G254B145", "y");
		charmap.put("R132G130B77", "z");
		charmap.put("R132G190B130", "2");
		charmap.put("R162G254B252", "1");*/
		charmap.put("132,130,77", "^"); // Very high mountains
		charmap.put("187,254,145", "."); // Grassland
		charmap.put("33,130,188", " "); // Deep Sea
		charmap.put("189,188,106", ","); // Plain
		charmap.put("149,190,235", " "); // Medium Sea
		charmap.put("162,254,252", "-"); // Shallow Sea
		charmap.put("132,190,130", "&"); // Forest
		charmap.put("162,254,252", " "); // Medium Sea
		charmap.put("41,190,250", "#"); // River
	}
	public static String handlesinglepixel(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red   = (pixel >> 16) & 0xff;
		int green = (pixel >>  8) & 0xff;
		int blue  = (pixel      ) & 0xff;
		// Deal with the pixel as necessary...
		String value = charmap.get(red+","+green+","+blue);
		if (value == null){
			System.err.println(red+","+green+","+blue+" not found.");
			System.exit(-1);
		}
		
		//System.out.print(value);
		return value;
	 }
	
	private static void flatRaster(String sourcefile,String destfile){
		try {
			BufferedWriter writer = FileUtil.getWriter(destfile);
			BufferedImage image = ImageUtils.createImage(sourcefile);
			int w = image.getWidth();
			int h = image.getHeight();
			
			int[] pixels = new int[w * h];
			PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				System.err.println("interrupted waiting for pixels!");
				return;
			}
			if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
				System.err.println("image fetch aborted or errored");
				return;
			}
			
			for (int j = 0; j < h; j++) {
				for (int i = 0; i < w; i++) {
					String charact = handlesinglepixel(pixels[j * w + i]);
					writer.write(charact);
				}
				writer.newLine();
			}
			writer.close();
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void gridRaster(String sourcefile,String destfile, int gridWidth, int gridHeight){
		try {
			BufferedWriter writer = FileUtil.getWriter(destfile);
			BufferedImage image = ImageUtils.createImage(sourcefile);
			int w = image.getWidth();
			int h = image.getHeight();
			
			int[] pixels = new int[w * h];
			PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				System.err.println("interrupted waiting for pixels!");
				return;
			}
			if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
				System.err.println("image fetch aborted or errored");
				return;
			}
			
			int squaresWidth = (int)Math.ceil((double)w/(double)gridWidth);
			int squaresHeight = (int)Math.ceil((double)h/(double)gridHeight);
			//array [x][y] = array [y*w+x]
			for (int j = 0; j < squaresHeight; j++) {
				for (int i = 0; i < squaresWidth; i++) {
					for (int yr = 0; yr < gridHeight; yr++){
						for (int xr = 0; xr < gridWidth; xr++){
							String charact = handlesinglepixel(pixels[(j * gridHeight+yr) * w + (i * gridWidth+xr)]);
							writer.write(charact);
						}
					}
				}
				//writer.newLine();
			}
			writer.close();
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		//flatRaster(args[0], args[1]);
		gridRaster(args[0], args[1],50,50);
	}
}
