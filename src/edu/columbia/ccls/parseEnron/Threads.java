package edu.columbia.ccls.parseEnron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import edu.columbia.ccls.utils.BOW;
import edu.columbia.ccls.utils.Configures;

public class Threads {
	public static HashSet<String> LegalTerms;
	public static HashMap<String, Integer> unigram_FreqHashMap;
	public static PriorityQueue<WordCount> maxHeap;
	public HashMap<String, Integer> file_FreqHashMap;
	public int wordsCount = 0;
	
	public void readInData() {
		file_FreqHashMap = new HashMap<>();
		try {
			String line;
			//read in all the email BOW.
			BufferedReader br = new BufferedReader(new FileReader(new File(Configures.emailWordsFile)));
			while ((line = br.readLine()) != null){
				file_FreqHashMap.put(line.toLowerCase().trim(), 0);
			}
			br.close();
			//read in all the legal terms BOW.
			br = new BufferedReader(new FileReader(new File(Configures.legalTermsFile)));
			while ((line = br.readLine()) != null){
				file_FreqHashMap.put(line.toLowerCase().trim(), 0);
			}
			br.close();
			//read in all the nominals Terms BOW.
			br = new BufferedReader(new FileReader(new File(Configures.nominalsTermsFile)));
			while ((line = br.readLine()) != null){
				file_FreqHashMap.put(line.toLowerCase().trim(), 0);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void evalueFile(String filename){
		readInData();
		wordsCount = 0;
		ArrayList<String> words = new ArrayList<>();
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			while ((line = br.readLine()) != null){
				ArrayList<String> w = BOW.getTokens(line);
				words.addAll(w);
			}
			br.close();
			
			for(String word : words){
				if(file_FreqHashMap.containsKey(word)){
					file_FreqHashMap.put(word, file_FreqHashMap.get(word)+1);
				}else {
					System.out.println("Error case:" + word);
				}
			}
			wordsCount = words.size();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Create the legalTerms file.
	 */
	public static void createLegalTerms(){
		BOW LegalBow = new BOW();
		LegalTerms = LegalBow.saveLegalTerms(Configures.legalTermsFile);
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("data/legalTerms.txt")));
			for(String word : LegalTerms){
				pw.write(word+"\n");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Used to create file.
	 */
	public static void createDictBatch() {
		unigram_FreqHashMap = new HashMap<>();
		maxHeap = new PriorityQueue<>();
		int totalWords = 0;
		// for(String legalword: LegalTerms){//add all the legal words in the
		// unigram_Hashmap.
		// unigram_FreqHashMap.put(legalword, 0);
		// }
		for (final File fileEntry : new File(Configures.dataFolder).listFiles()) {
			if (fileEntry.getName().startsWith("."))
				continue;
			createDict(Configures.dataFolder + fileEntry.getName());
		}
		for (String word : unigram_FreqHashMap.keySet()) {
			int count = unigram_FreqHashMap.get(word);
			WordCount instance = new WordCount(word, count);
			maxHeap.add(instance);
		}
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("data/bow.txt")));
			while(!maxHeap.isEmpty()) {
				totalWords++;
				WordCount instance = maxHeap.poll();
//				System.out.println(instance.getWord() + " " + instance.count);
				pw.write(instance.getWord()+"\n");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(totalWords);
		System.out.println("Succeed!");
	}

	/**
	 * read in all the terms from the table and save them to a file
	 * 
	 * @param filename
	 */
	private static void createDict(String filename) {
		String line = null;
		ArrayList<String> candidateContents = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					filename)));
			while ((line = br.readLine()) != null)
				candidateContents.add(line);
			br.close();
			for (String candidate : candidateContents) {
				ArrayList<String> words = BOW.getTokens(candidate);
				for (String word : words) {
					if (unigram_FreqHashMap.containsKey(word))
						unigram_FreqHashMap.put(word,
								unigram_FreqHashMap.get(word) + 1);
					else
						unigram_FreqHashMap.put(word, 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class WordCount implements Comparable<WordCount> {
	String word;
	Integer count;
	public WordCount(String word, int count) {
		this.word = word;
		this.count = count;
	}

	public String getWord() {
		return this.word;
	}

	public Integer getCount() {
		return count;
	}
	
	@Override
	public int compareTo(WordCount o) {
		return o.count - this.count;
	}
}