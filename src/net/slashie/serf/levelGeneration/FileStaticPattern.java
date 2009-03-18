package net.slashie.serf.levelGeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.slashie.utils.FileUtil;


public abstract class FileStaticPattern extends StaticPattern{
	public abstract String getFilename();
	
	public String[][] getCellMap(){
		FileUtil.extractZipFile(getFilename()+".zip");
		try {
			String[][] ret = new String[1][FileUtil.filasEnArchivo(getFilename()+".txt")];
            BufferedReader reader = FileUtil.getReader(getFilename()+".txt");
            String line = reader.readLine();
            int y = 0;
            while (line != null){
            	ret[0][y]=line;
            	line = reader.readLine();
            	y++;
            }

            reader.close();
            return ret;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
				

}
