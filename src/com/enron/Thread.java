package com.enron;

public class Thread {
	public static String fileName = "LegalTerms.txt";
	public static void ModelCreation(){
		BOW dataSource = new BOW();
		dataSource.parseFile(fileName);
		for(String name : dataSource.unigram_FreqHashMap.keySet()){
			
		}
	}
}
