package com.enron;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class Evalue {
	class Signature{
		public HashMap<String, Integer> n_gram_ValueHashMap;
		public Signature(HashMap<String, Integer> input) {
			n_gram_ValueHashMap = input;
		}
	}
	
	public void createWekaData(){
		BOW dataStore = new BOW();
		dataStore.parseFile();
//		HashMap<Signature, String> unigram_StatusHashMap = new HashMap<>();
			FastVector atts = new FastVector();
			FastVector attVals = new FastVector();
			for(String word : dataStore.unigram_FreqHashMap.keySet()){
				atts.addElement(new Attribute(word));
			}
			atts.addElement(new Attribute("NOfWords"));//number of words
			attVals.addElement("NP");
			attVals.addElement("P");
			atts.addElement(new Attribute("CLASS", attVals));//email class
			Instances data = new Instances("Unigram", atts, 0);
			for(String id : dataStore.emailId_ContentHashMap.keySet()){
				double[] vals = new double[data.numAttributes()];
				int index = 0;
				String content = dataStore.emailId_ContentHashMap.get(id);
				ArrayList<String> container = new ArrayList<>();
				container.add(content);
				HashMap<String, Integer> input = dataStore.createUnigram(container);
				String status = dataStore.emailId_StatusHashMap.get(id);
	//			unigram_StatusHashMap.put(new Signature(input), status);
				for(String word : dataStore.unigram_FreqHashMap.keySet()){
					if(input.containsKey(word)){
						vals[index] = 1.0;
					}else{
						vals[index] = 0.0;
					}
					index++;
				}
				vals[index] = (double) input.size();
				index++;
				vals[index] = attVals.indexOf(status);
				data.add(new Instance(1.0, vals));
			}
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		try {
			saver.setFile(new File("unigramTest.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
//		Evalue eva = new Evalue();
//		eva.createWekaData();
		Thread.ModelCreation();
	}
}
