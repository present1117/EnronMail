package edu.columbia.ccls.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;


public class BOW {
	
	public HashMap<String, String> emailId_ContentHashMap;
	public HashMap<String, String> emailId_StatusHashMap;
	public HashMap<String, Integer> unigram_FreqHashMap;
	public HashMap<String, Integer> bigram_FreqHashMap;
	public static PorterStemmer ps=new PorterStemmer();
	public static Stopwords stop=new Stopwords();
	
	/**
	 * 
	 */
	public BOW(){
		emailId_ContentHashMap = new HashMap<>();
		emailId_StatusHashMap = new HashMap<>();
		unigram_FreqHashMap = new HashMap<>();
		bigram_FreqHashMap = new HashMap<>();
	}
	
	/**
	 * Input is a string, and output would be all the valid tokens for this tokens.
	 * @param str
	 * @return
	 */
	public static ArrayList<String> getTokens(String str){
		if(str==null)
			throw new NullPointerException("String received for breaking down is Null");
		ArrayList<String> tokens = new ArrayList<String>();
		if(str.length() <= 0)
			return tokens;
		Reader r = new StringReader(str);
		Tokenizer<Word> tk = PTBTokenizerFactory.newWordTokenizerFactory("americanize=false").getTokenizer(r);
        List<Word> tokenized = tk.tokenize();
        for (Word w: tokenized) {
        	String word = w.word();
			if(word.matches("[^a-zA-Z]"))
				continue;
			if(stop.checkStop(word.toLowerCase()))
				continue;
			String item = ps.stem(word.toLowerCase());
			if(item.contains("Invalid term") || item.contains("No term entered"))
				continue;
			if(item.length() > 20)
				continue;
			tokens.add(item);
        }
		return tokens;
	}
	
	/**
	 * use Random number to separate line, deprecate
	 */
	public void parseFile(){
		String line = null;
 		ArrayList<String> candidateContents = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(Configures.defaultFile)));
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
	public HashSet<String> saveLegalTerms(String fileName){
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
		return findValidWords(candidateContents);
	}

	private HashSet<String> findValidWords(ArrayList<String> candidateContents) {
		HashSet<String> set = new HashSet<>();
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
				set.add(item);
			}
		}
		return set;
	}

	/**
	 * Create bigram dict
	 * @param candidateContents
	 */
	public HashMap<String, Integer> createBigram(ArrayList<String> candidateContents) {
		HashMap<String, Integer> result =  new HashMap<>();
		ArrayList<String> words=null;
		for(String candidate: candidateContents){
			words = getTokens(candidate);
		}
		int length = words.size();
		for(int i = 0; i < length - 1; ++i){
			String bigram = words.get(i) + " " + words.get(i+1);
			if(result.containsKey(bigram))
				result.put(bigram, result.get(bigram)+1);
			else
				result.put(bigram, 1);
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
				if(result.containsKey(word))
					result.put(word, result.get(word)+1);
				else
					result.put(word, 1);
			}
		}
		return result;
	}
}