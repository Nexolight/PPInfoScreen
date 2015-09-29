package net.coldbyte.ppinfscr.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.coldbyte.ppinfscr.main.Main;
import net.coldbyte.ppinfscr.ui.Output;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class IOHandler {
	
	private Output out = new Output(this.getClass().getName());
	
	/**
	 * Use this class to handle the files where this program accesses to
	 */
	public IOHandler(){
		
	}
	
	/**
	 * This will return the absolute path from each directory within the given one
	 * @param basepath
	 * @return
	 */
	public List<File> getDirs(String basepath){
		List<File> dirlist = new ArrayList<File>();
		File myFile = new File(basepath);
		for(File oneDir : myFile.listFiles()){
			if(oneDir.isDirectory()){
				dirlist.add(oneDir);
			}
		}
		return dirlist;
	}
	
	/**
	 * This will check the existance of the file at the given path
	 * @param fullpath
	 * @return
	 */
	public boolean checkExistence(String fullpath){
		File myFile = new File(fullpath);
		if(myFile.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Use this to create every file you need if it doesn't exist
	 * it will return false if there was any error
	 * @param fullpaths
	 * @return
	 */
	public boolean createRequired(String[] dirsPath, String[] filesPath){
		try {
			for(String path : dirsPath){
				File onefile = new File(path);
				if(!onefile.isDirectory()){
					out.cOut("Folder: " + onefile + " does not exist - I will create it");
					onefile.mkdirs();
				}
			}
			for(String path : filesPath){
				File onefile = new File(path);
				if(!onefile.isFile()){
					out.cOut("File: " + onefile + " does not exist - I will create it");
					onefile.createNewFile();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * This will extract the given file into the given location
	 * @param filename
	 * @return
	 */
	public boolean extractTemplate(String filename, String dst){
		InputStream in = getClass().getResourceAsStream("/templates/"+filename);
		
		if(in == null){
			out.cOut("Template file in jar /templates/"+filename + " does not exist");
			return false;
		}
		File mydst = new File(dst);
		if(mydst.getParentFile().exists()){
			if(!mydst.exists()){
				try {
					OutputStream bout;
					bout = new FileOutputStream(dst);
					byte[] buffer = new byte[1024];
					int len = in.read(buffer);
					while(len != -1){
						bout.write(buffer, 0, len);
						len = in.read(buffer);
					}
					in.close();
					bout.close();
				} catch (IOException e) {
					out.cOut("Cannot create / write " + dst);
					e.printStackTrace();
					return false;
				}
				out.cOut("Extracted template " + filename + " to " + dst);
				return true;
			}else{
				return true;
			}
		}else{
			out.cOut("Required folder does not exist " + mydst.getParentFile().getPath());
			return false;
		}
	}
}
