package com.enron;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class BOW {
	public static String rawFile = "rawData.txt";
	public HashMap<String, String> emailId_ContentHashMap;
	public HashMap<String, String> emailId_StatusHashMap;
	public HashMap<String, Integer> unigram_FreqHashMap;
	public HashMap<String, Integer> bigram_FreqHashMap;
	public PorterStemmer ps;
	
	/**
	 * 
	 * @throws IOException
	 */
	public BOW() throws IOException {
		emailId_ContentHashMap = new HashMap<>();
		emailId_StatusHashMap = new HashMap<>();
		unigram_FreqHashMap = new HashMap<>();
		bigram_FreqHashMap = new HashMap<>();
		ps = new PorterStemmer();
		parseFile();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void parseFile() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(rawFile)));
		String line = null;
 		ArrayList<String> candidateContents = new ArrayList<>();
		while((line = br.readLine()) != null){
			String [] parts = line.split("\\$");
			if(parts.length != 3){
				System.err.println("Error line");
				continue;
			}
			String emailId = parts[0];
			String status = parts[1];
			String content = parts[2];
			emailId_ContentHashMap.put(emailId, content);
			emailId_StatusHashMap.put(emailId, status);
			candidateContents.add(content);
		}
		br.close();
		unigram_FreqHashMap = createUnigram(candidateContents);
		bigram_FreqHashMap = createBigram(candidateContents);
	}

	/**
	 * Create bigram dict
	 * @param candidateContents
	 */
	public HashMap<String, Integer> createBigram(ArrayList<String> candidateContents) {
		HashMap<String, Integer> result =  new HashMap<>();
		ArrayList<String> validWords = new ArrayList<>();
		for(String candidate: candidateContents){
			String [] words = candidate.split(" ");
			for(String word : words){
				if(word.matches("[^0-9a-zA-Z]"))
					continue;
				String item = ps.stem(word.toLowerCase());
				validWords.add(item);
			}
		}
		
		int length = validWords.size();
		for(int i = 0; i < length - 1; ++i){
			String bigram = validWords.get(i) + " " + validWords.get(i+1);
			if(result.containsKey(bigram)){
				result.put(bigram, result.get(bigram)+1);
			}else{
				result.put(bigram, 1);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param candidateContents
	 * @return
	 */
	public HashMap<String, Integer> createUnigram(ArrayList<String> candidateContents) {
		HashMap<String, Integer> result = new HashMap<>();
		for(String candidate : candidateContents){
			String [] words = candidate.split(" ");
			for(String word : words){
				if(word.matches("[^0-9a-zA-Z]"))
					continue;
				String item = ps.stem(word.toLowerCase());
				if(result.containsKey(item)){
					result.put(item, result.get(item)+1);
				}else{
					result.put(item, 1);
				}
			}
		}
		return result;
	}
}
