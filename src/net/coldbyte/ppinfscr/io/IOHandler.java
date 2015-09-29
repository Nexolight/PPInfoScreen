package net.coldbyte.ppinfscr.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.coldbyte.ppinfscr.main.Main;
import net.coldbyte.ppinfscr.models.PPTContainer;
import net.coldbyte.ppinfscr.settings.DefaultSettings;
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
	 * This will return the File objects from each directory within the given basepath
	 * @param basepath
	 * @return
	 */
	public List<File> getDirs(String basepath){
		List<File> dirlist = new ArrayList<File>();
		File dir = new File(basepath);
		for(File oneDir : dir.listFiles()){
			if(oneDir.isDirectory()){
				dirlist.add(oneDir);
			}
		}
		return dirlist;
	}
	
	/**
	 * This will return the File objects from each file within the given basepath
	 * @param basepath
	 * @return
	 */
	public List<File> getFiles(String basepath){
		List<File> filelist = new ArrayList<File>();
		File dir = new File(basepath);
		for(File oneFile : dir.listFiles()){
			if(oneFile.isFile()){
				filelist.add(oneFile);
			}
		}
		return filelist;
	}
	
	/**
	 * This will return the File objects from each file and directory within the given basepath
	 * @param basepath
	 * @return
	 */
	public List<File> getAll(String basepath){
		List<File> list = new ArrayList<File>();
		File dir = new File(basepath);
		for(File obj : dir.listFiles()){
			if(obj.isFile() || obj.isDirectory()){
				list.add(obj);
			}
		}
		return list;
	}
	
	/**
	 * This will return all supported PowerPoint files within the given directory
	 * @param basedir
	 * @param removeInvalid
	 * @return
	 */
	public List<File> getPPTFiles(File basedir, boolean removeInvalid){
		List<File> allfiles = getAll(basedir.getAbsolutePath());
		List<File> pptfiles = new ArrayList<File>();
		for(File onefile : allfiles){
			if(onefile.isFile()){
				if(onefile.getName().matches(DefaultSettings.validPPTRegex)){
					pptfiles.add(onefile);
				}else{
					if(removeInvalid){
						out.cOut("The format of the file " + onefile.getName() + " is not supported - remove");
						onefile.delete();
					}else{
						out.cOut("The format of the file " + onefile.getName() + " is not supported");
					}
				}
			}else{
				if(removeInvalid){
					out.cOut("The Directory " + onefile.getAbsolutePath() + " is not a file - remove");
					onefile.delete();
				}else{
					out.cOut("The Directory " + onefile.getAbsolutePath() + " is not a file");
				}
			}
		}
		return pptfiles;
	}
	
	/**
	 * This will validate all PowerPoint source directories which exist
	 * and return them in their valid container format
	 * @param removeInvalid true if you want to delete any invalid file inside the depending directory
	 * @return
	 */
	public List<PPTContainer> getPPTContainers(boolean removeInvalid){
		List<PPTContainer> validated = new ArrayList<PPTContainer>();
		SimpleDateFormat datedfolderformat = new SimpleDateFormat(DefaultSettings.datedFoldersFormat);
		List<File> dirs = getAll(DefaultSettings.ppinfscrSources);
		for(File dir : dirs){
			if(dir.isDirectory()){
				String dname = dir.getName();
				if(dname.matches(DefaultSettings.datedFoldersRegex)){
					try {
						Date date = datedfolderformat.parse(dname);
						PPTContainer pptc = new PPTContainer(dir, date);
						validated.add(pptc);
					} catch (ParseException e) {
						if(removeInvalid){
							out.cOut("Cannot parse foldername: " + dname + " - remove directory from " + dir.getParentFile().getAbsolutePath());
							dir.delete();
						}else{
							out.cOut("Cannot parse foldername: " + dname);
						}
						e.printStackTrace();
					}
				}
			}else{
				if(removeInvalid){
					out.cOut("The file " + dir.getAbsolutePath() + " is not a directory - remove");
					dir.delete();
				}else{
					out.cOut("The file " + dir.getAbsolutePath() + " is not a directory");
				}
				
			}
		}
		return validated;
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
