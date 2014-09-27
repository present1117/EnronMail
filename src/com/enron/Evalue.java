package com.enron;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Evalue {
	
	class Signature{
		public HashMap<String, Integer> n_gram_ValueHashMap;
		public Signature(HashMap<String, Integer> input) {
			n_gram_ValueHashMap = input;
		}
	}
	
	public void createWekaData(){
		BOW dataStore = new BOW();		
//		HashMap<Signature, String> unigram_StatusHashMap = new HashMap<>();
		String outputline = "";
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(new File("data.csv"), false));
			for(String word : dataStore.unigram_FreqHashMap.keySet()){
				outputline += word+",";
			}
			outputline += "NOfWords";//number of words
			outputline += "CLASS";   //email class
			pw.println(outputline);
			pw.flush();
			pw.close();
			for(String id : dataStore.emailId_ContentHashMap.keySet()){
				outputline = "";
				pw = new PrintWriter(new FileWriter(new File("data.csv"), true));
				String content = dataStore.emailId_ContentHashMap.get(id);
				ArrayList<String> container = new ArrayList<>();
				container.add(content);
				HashMap<String, Integer> input = dataStore.createUnigram(container);
				String status = dataStore.emailId_StatusHashMap.get(id);
	//			unigram_StatusHashMap.put(new Signature(input), status);
				for(String word : dataStore.unigram_FreqHashMap.keySet()){
					if(input.containsKey(word)){
						outputline += "1,";
					}else{
						outputline += "0,";
					}
				}
				outputline += input.size()+",";
				outputline += status;
				pw.println(outputline);
				pw.flush();
				pw.close();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		Evalue eva = new Evalue();
		eva.createWekaData();
	}
}
