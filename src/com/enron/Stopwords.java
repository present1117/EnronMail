package com.enron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Stopwords {
	public static String StopwordFile = "stopwords.txt";
	public static HashSet<String> dict;
	public Stopwords() {
		dict = new HashSet<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(StopwordFile)));
			String line = null;
			while((line = br.readLine()) != null){
				dict.add(line.replace(" ", ""));
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean checkStop(String word){
		if(dict.contains(word))
			return true;
		return false;
	}
}
