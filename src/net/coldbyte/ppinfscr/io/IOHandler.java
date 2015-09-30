package net.coldbyte.ppinfscr.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.coldbyte.ppinfscr.main.Main;
import net.coldbyte.ppinfscr.models.PPTContainer;
import net.coldbyte.ppinfscr.settings.DefaultSettings;
import net.coldbyte.ppinfscr.ui.Output;

/**
 *
 * (C) 2015 - Lucy von K�nel
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
	 * This will copy the given file into the temporary folder and return it's new Object
	 * @param file
	 * @param randomName
	 * @return
	 */
	public File copyToTmp(File file, boolean randomName){
		boolean ok = false;
		if(file.isFile()){	
			String filename = file.getName();
			if(randomName){
				filename = UUID.randomUUID().toString();
			}
			File dst = new File(DefaultSettings.ppinfscrSysdir + filename);
			
			
			InputStream is = null;
		    OutputStream os = null;
			if(dst.exists()){
				removeAll(dst);
			}
			try {
				dst.createNewFile();
			} catch (IOException e1) {
				out.cOut("Cannot create empty file "+dst.getAbsolutePath()+" to write into");
				e1.printStackTrace();
				return null;
			}
			try{
		        try {
		        	is = new FileInputStream(file);
					os = new FileOutputStream(dst);
			        byte[] buffer = new byte[1024];
			        int length;
			        while ((length = is.read(buffer)) > 0) {
			            os.write(buffer, 0, length);
			        }
		        } catch (FileNotFoundException e) {
					out.cOut("Cannot copy file one not found - source: " + file.getAbsolutePath() + " destination: " + dst.getAbsolutePath());
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					out.cOut("Cannot copy file from source: " + file.getAbsolutePath() + " to destination: " + dst.getAbsolutePath() + " IOException");
					e.printStackTrace();
					return null;
				}
			}finally{
				try {
					is.close();
					os.close();
					ok = true;
				} catch (IOException e) {
					out.cOut("Cannot close FileInput or FileOutput stream - IOException");
					e.printStackTrace();
					return null;
				}
			}
			if(ok){
				return dst;
			}else{
				return null;
			}
		}else{
			out.cOut("The given File " + file.getAbsolutePath() + " is a folder - I will not copy");
			return null;
		}
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
	 * This will delete all files recursively or just the file if it's not a directory
	 * @param directory
	 */
	public void removeAll(File base){
		if(base.isFile()){
			base.delete();
		}
		if(base.isDirectory()){
			for(File f : base.listFiles()){
				if(f.isFile()){
					f.delete();
				}else{
					removeAll(f);
				}
			}
			base.delete();
		}
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
						removeAll(onefile);
					}else{
						out.cOut("The format of the file " + onefile.getName() + " is not supported");
					}
				}
			}else{
				if(removeInvalid){
					out.cOut("The Directory " + onefile.getAbsolutePath() + " is not a file - remove");
					removeAll(onefile);
				}else{
					out.cOut("The Directory " + onefile.getAbsolutePath() + " is not a file");
				}
			}
		}
		return pptfiles;
	}
	
	/**
	 * This will validate all PowerPoint source directories which exist
	 * and return them in their valid container format it will ignore any container with invalid
	 * structure
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
						for(File f: pptc.getContainer().listFiles()){
							if(f.getName().matches(DefaultSettings.validPPTRegex)){
								validated.add(pptc);
								break; //contains at least one valid file
							}
						}
						
					} catch (ParseException e) {
						out.cOut("Cannot parse foldername: " + dname);
						e.printStackTrace();
					}
				}else{
					out.cOut("The name of the directory is wrong : " + dir.getName() + " correct is mm.dd.yyyy_hhmm");
				}
			}else{
				if(removeInvalid){
					out.cOut("The file " + dir.getAbsolutePath() + " is not a directory - remove");
					removeAll(dir);
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
