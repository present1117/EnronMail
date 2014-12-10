package edu.columbia.ccls.utils;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileFunctions {
	public static File [] getFilesInDirectory(String path) throws IOException{
		File [] fileList = null;
		if(path==null){
			throw new NullPointerException("directory path is null");
		}
		if(!path.endsWith("/")){
			path += "/";
		}
		File inputFile = new File(path);
		if(inputFile!=null && inputFile.isDirectory()){
			fileList = inputFile.listFiles();
		}
		else{
			throw new IOException("directory does not exist:"+path);
		}
		return fileList;
	
	}
	
	public static ArrayList<String> getLinesInFile(String filePath, int begin, int numReadLines)
			throws NullPointerException, FileNotFoundException, IOException{

		if(filePath==null){
			throw new NullPointerException("filePath is null");
		}
		ArrayList<String>lines = null;
		File file = new File(filePath);
		FileInputStream fstream = null;
		fstream = new FileInputStream(file.getAbsolutePath());
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String sentenceFromFile=null;

		int numSkipLines = begin-0;
		
		//Skip lines
		while((numSkipLines>0) && (br.readLine())!=null){
			numSkipLines--;
		}
		//Read lines
		while((numReadLines>0) && (sentenceFromFile=br.readLine())!=null){
			numReadLines--;
			if(lines==null){
				lines = new ArrayList<String>();
			}
			lines.add(sentenceFromFile);
		}
		in.close();
		fstream.close();

		return lines;
	}
}
