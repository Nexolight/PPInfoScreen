package net.coldbyte.ppinfscr.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class IOHandler {
	
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
}
