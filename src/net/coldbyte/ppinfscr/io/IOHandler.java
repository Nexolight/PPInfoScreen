package net.coldbyte.ppinfscr.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.coldbyte.ppinfscr.control.Output;
import net.coldbyte.ppinfscr.models.GUISettings;
import net.coldbyte.ppinfscr.models.PPTContainer;
import net.coldbyte.ppinfscr.settings.UserSettings;
import net.coldbyte.ppinfscr.settings.UserSettings.Settings;

/**
 *
 * (C) 2015 - Lucy von K�nel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class IOHandler {
	private Output out;
	
	/**
	 * Use this class to handle the files where this program accesses to
	 */
	public IOHandler(){
		this.out = Output.getInstance();
	}
	
	
	/**
	 * This will copy the given file into the temporary folder and return it's new Object
	 * @param file
	 * @param randomName
	 * @return
	 */
	public File copyToTmp(File file, boolean randomName){
		if(file.isFile()){	
			String filename = file.getName();
			if(randomName){
				filename = UUID.randomUUID().toString();
			}
			UserSettings uS = new UserSettings();
			File dst = new File(UserSettings.ppinfscrDatadir + filename);
			try {
				RandomAccessFile rf = new RandomAccessFile(file, "r");
				FileChannel bin = rf.getChannel();
				FileOutputStream dest = new FileOutputStream(dst);
				FileChannel bout = dest.getChannel();
				long size = bin.size();
				bin.transferTo(0, size, bout);
				bin.close();
				bout.close();
				rf.close();
				dest.close();
				return dst;
			} catch (IOException e2) {
				out.cWarn("Cannot create a shadow copy of the given file " + file.getAbsolutePath() + " - IOException",e2);
				e2.printStackTrace();
				return null;
			}
		}else{
			out.cWarn("The given File " + file.getAbsolutePath() + " is a folder - I will not copy");
			return null;
		}
	}
	
	/**
	 * This will remove all files from the temporary folder except the given one
	 * @param except
	 */
	public void cleanTmpExcept(File except){
		File tmp = new File(UserSettings.ppinfscrDatadir);
		if(tmp.exists()){
			File[] children = tmp.listFiles();
			for(File child : children){
				if(!child.equals(except)){
					removeAll(child);
				}
			}
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
				if(onefile.getName().matches(UserSettings.validPPTRegex)){
					pptfiles.add(onefile);
				}else{
					if(removeInvalid){
						out.cWarn("The format of the file " + onefile.getName() + " is not supported - remove");
						removeAll(onefile);
					}else{
						out.cWarn("The format of the file " + onefile.getName() + " is not supported");
					}
				}
			}else{
				if(removeInvalid){
					out.cWarn("The Directory " + onefile.getAbsolutePath() + " is not a file - remove");
					removeAll(onefile);
				}else{
					out.cWarn("The Directory " + onefile.getAbsolutePath() + " is not a file");
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
		UserSettings uS = new UserSettings();
		SimpleDateFormat datedfolderformat = new SimpleDateFormat(UserSettings.datedFoldersFormat);
		List<File> dirs = getAll(uS.getString(Settings.APPLICATION_ROOT));
		for(File dir : dirs){
			if(dir.isDirectory()){
				String dname = dir.getName();
				if(dname.matches(UserSettings.datedFoldersRegex)){
					try {
						Date date = datedfolderformat.parse(dname);
						PPTContainer pptc = new PPTContainer(dir, date);
						for(File f: pptc.getContainer().listFiles()){
							if(f.getName().matches(UserSettings.validPPTRegex)){
								validated.add(pptc);
								break; //contains at least one valid file
							}
						}
						
					} catch (ParseException e) {
						out.cWarn("Cannot parse foldername: " + dname, e);
						e.printStackTrace();
					}
				}else{
					out.cWarn("The name of the directory is wrong : " + dir.getName() + " correct is mm.dd.yyyy_hhmm");
				}
			}else{
				if(removeInvalid){
					out.cWarn("The file " + dir.getAbsolutePath() + " is not a directory - remove");
					removeAll(dir);
				}else{
					out.cWarn("The file " + dir.getAbsolutePath() + " is not a directory");
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
			if(dirsPath != null){
				for(String path : dirsPath){
					if(!path.isEmpty()){
						File onefile = new File(path);
						if(!onefile.isDirectory()){
							out.cInf("Folder: " + onefile + " does not exist - I will create it");
							onefile.mkdirs();
						}
					}
				}
			}
			if(filesPath != null){
				for(String path : filesPath){
					if(!path.isEmpty()){
						File onefile = new File(path);
						if(!onefile.isFile()){
							out.cInf("File: " + onefile + " does not exist - I will create it");
							onefile.createNewFile();
						}
					}
				}
			}
		} catch (IOException e) {
			out.cErr("Cannot create required file or folder - IOException", e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * This will extract the given file into the given location
	 * @param pathinjar
	 * @return
	 */
	public boolean extractTemplate(String pathinjar, String dst){
		InputStream in = getClass().getResourceAsStream(pathinjar);
		if(in == null){
			out.cWarn("Template file in jar "+ pathinjar + " does not exist - please contact the developer");
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
					out.cWarn("Cannot create / write " + dst, e);
					e.printStackTrace();
					return false;
				}
				out.cInf("Extracted template " + pathinjar + " to " + dst);
				return true;
			}else{
				return true;
			}
		}else{
			out.cErr("Required folder does not exist " + mydst.getParentFile().getPath());
			return false;
		}
	}
	
	/**
	 * read out the given setting from the defined settings file
	 * @param setting
	 * @return
	 */
	public String readSetting(Settings setting){
		File ini = new File(UserSettings.ppinfscrSetFile_OUT);
		if(ini.exists() && ini.isFile()){
			try {
				BufferedReader br = new BufferedReader(new FileReader(ini));
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if(line.matches("^"+setting.name()+".*?\\=.*$")){
						String[] splitline = line.split("=");
						if(splitline.length == 2){
							return splitline[1].trim();
						}else{
							return null;
						}
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				out.cWarn("Cannot read setting " + setting.name() + " FileNotFoundException", e);
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				out.cWarn("Cannot read setting " + setting.name() + " IOException", e);
				e.printStackTrace();
				return null;
			}
			return null;
		}else{
			return null;
		}
	}
	
	/**
	 * This will save the current settings to the depending file
	 * @param currentSettings
	 */
	public void saveSettings(GUISettings currentSettings){
		File settings = new File(UserSettings.ppinfscrSetFile_OUT);
		try {
			if(!settings.exists()){
				settings.createNewFile();
			}
			settings.delete();
			settings.createNewFile();
			try {
				UserSettings uS = new UserSettings();
				FileOutputStream fout = new FileOutputStream(settings);
				String newSettings = uS.getNewSettingsContent(currentSettings);
				fout.write(newSettings.getBytes());
				fout.close();
				out.cInf("Settings saved");
			} catch (FileNotFoundException e) {
				out.cWarn("Cannot write to settings file - FileNotFoundException",e);
				e.printStackTrace();
			}
		} catch (IOException e) {
			out.cWarn("Cannot save Settings - IOException",e);
			e.printStackTrace();
		}
	}
}
