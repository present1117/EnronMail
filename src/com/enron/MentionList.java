package com.enron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MentionList {
	public HashSet<String> mentionListDict;
	
	public static void createHeader(){
		HashSet<String> mentionListWords = new HashSet<>();
		try {
			ArrayList<String> fileContents = FileFunctions.getLinesInFile(Configures.allMentionListsFile, 0, Integer.MAX_VALUE);
			for(String line : fileContents){
				String words = line.substring(line.indexOf('[')+1, line.indexOf(']')).replace(",", "").replace(".", "");
				ArrayList<String> list = BOW.getTokens(words);
				for(String word : list){
					mentionListWords.add(word);
				}
			}
			String output = "";
			for(String words : mentionListWords){
				if(output.length() != 0){
					output+=",";
				}
				output += words;
			}
			PrintWriter pr = new PrintWriter(new File("mentionList.txt"));
			pr.write(output);
			pr.close();
			System.out.println("done");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readinData(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(Configures.MentionListFile)));
			String input = br.readLine();
			br.close();
			String[] words = input.split(",");
			for(String word : words){
				mentionListDict.add(word);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param line format:   0:[a, b, c] : 7
	 * @return
	 */
	public static int evalueLine(String line, HashMap<String, Integer> words){
		System.out.println(line);
		String[] parts = line.split(":", 2);
		int label = Integer.parseInt(parts[0]);
		String candidates = parts[1].substring(parts[1].indexOf('[')+1, parts[1].indexOf(']')).replace(",", " ").replace(".", " ").replace("  ", " ");
		ArrayList<String> temp = BOW.getTokens(candidates);
		for(String word : temp){
			words.put(word, words.get(word)+1);
		}
		return label;
	}
	
}