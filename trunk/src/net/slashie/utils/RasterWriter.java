package net.slashie.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;

public class RasterWriter {
	private Hashtable<String, String> charmap = new Hashtable<String, String>();
	
	
	
	public RasterWriter(Hashtable<String, String> charmap) {
		super();
		this.charmap = charmap;
	}

	private String handlesinglepixel(int pixel) {
		//int alpha = (pixel >> 24) & 0xff;
		int red   = (pixel >> 16) & 0xff;
		int green = (pixel >>  8) & 0xff;
		int blue  = (pixel      ) & 0xff;
		// Deal with the pixel as necessary...
		String value = charmap.get(red+","+green+","+blue);
		if (value == null){
			System.err.println(red+","+green+","+blue+" not found.");
			System.exit(-1);
		}
		
		return value;
	 }
	
	public void flatRaster(String sourcefile,String destfile){
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
			e.printStackTrace();
		}
	}
	
	public void gridRaster(String sourcefile,String destfile, int gridWidth, int gridHeight){
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
							int xpixel = i * gridWidth + xr;
							int ypixel = j * gridHeight + yr;
							if (xpixel >= w || ypixel >= h){
								writer.write(" ");
							} else {
								String charact = handlesinglepixel(pixels[ypixel * w + xpixel]);
								writer.write(charact);
							}
						}
					}
				}
				//writer.newLine();
			}
			writer.close();
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
