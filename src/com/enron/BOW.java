package com.enron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;


public class BOW {
	public static String rawFile = "rawData.txt";
	public HashMap<String, String> emailId_ContentHashMap;
	public HashMap<String, String> emailId_StatusHashMap;
	public HashMap<String, Integer> unigram_FreqHashMap;
	public HashMap<String, Integer> bigram_FreqHashMap;
	public PorterStemmer ps;
	public Stopwords stop;
	
	/**
	 * 
	 */
	public BOW(){
		emailId_ContentHashMap = new HashMap<>();
		emailId_StatusHashMap = new HashMap<>();
		unigram_FreqHashMap = new HashMap<>();
		bigram_FreqHashMap = new HashMap<>();
		ps = new PorterStemmer();
		stop = new Stopwords();
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public ArrayList<String> getTokens(String str){
		if(str==null)
			throw new NullPointerException("String received for breaking down is Null");
		
		ArrayList<String> tokens = new ArrayList<String>();
		Reader r = new StringReader(str);
		Tokenizer<Word> tk = PTBTokenizerFactory.newWordTokenizerFactory("americanize=false").getTokenizer(r);
        List<Word> tokenized = tk.tokenize();
        for (Word w: tokenized) {
            tokens.add(w.word());
        }
		return tokens;
	}
	
	/**
	 * 
	 */
	public void parseFile(){
		String line = null;
 		ArrayList<String> candidateContents = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(rawFile)));
			while((line = br.readLine()) != null){
				String [] parts = line.split("40578");
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		unigram_FreqHashMap = createUnigram(candidateContents);
		bigram_FreqHashMap = createBigram(candidateContents);
	}

	/**
	 * 
	 */
	public void parseFile(String fileName){
		String line = null;
 		ArrayList<String> candidateContents = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			while((line = br.readLine()) != null){
				String content = line.trim();
				if(content.length() != 0)
					candidateContents.add(content);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			ArrayList<String> words = getTokens(candidate);
			for(String word : words){
				if(word.matches("[^a-zA-Z]"))
					continue;
				if(stop.checkStop(word.toLowerCase()))
					continue;
				String item = ps.stem(word.toLowerCase());
				if(item.contains("Invalid term") || item.contains("No term entered"))
					continue;
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
			ArrayList<String> words = getTokens(candidate);
			for(String word : words){
				if(word.matches("[^a-zA-Z]"))
					continue;
				if(stop.checkStop(word.toLowerCase()))
					continue;
				String item = ps.stem(word.toLowerCase());
				if(item.contains("Invalid term") || item.contains("No term entered"))
					continue;
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
