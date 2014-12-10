package edu.columbia.ccls.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Stopwords {
	public static HashSet<String> dict;
	public Stopwords() {
		dict = new HashSet<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(Configures.StopwordFile)));
			String line = null;
			while((line = br.readLine()) != null){
				dict.add(line.replace(" ", ""));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Check if the input word is a stopword.
	 * @param word
	 * @return True if it's a stopword, False otherwise.
	 */
	public boolean checkStop(String word){
		if(dict.contains(word))
			return true;
		return false;
	}
}
