package com.enron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import weka.core.Instance;

public class Evalue {
	
	class Signature{
		public HashMap<String, Integer> n_gram_ValueHashMap;
		public Signature(HashMap<String, Integer> input) {
			n_gram_ValueHashMap = input;
		}
	}
	
	public void createWekaData() throws IOException{
		BOW dataStore = new BOW();
		HashMap<Signature, String> n_gram_StatusHashMap = new HashMap<>();
		
		for(String id : dataStore.emailId_ContentHashMap.keySet()){
			String content = dataStore.emailId_ContentHashMap.get(id);
			ArrayList<String> container = new ArrayList<>();
			container.add(content);
			HashMap<String, Integer> input = dataStore.createUnigram(container);
			String status = dataStore.emailId_StatusHashMap.get(id);
			n_gram_StatusHashMap.put(new Signature(input), status);
		}
	}
}
