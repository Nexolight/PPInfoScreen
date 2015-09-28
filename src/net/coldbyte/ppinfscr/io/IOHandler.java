package net.coldbyte.ppinfscr.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	
}
