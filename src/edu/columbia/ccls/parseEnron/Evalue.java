package edu.columbia.ccls.parseEnron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.columbia.ccls.utils.BOW;
import edu.columbia.ccls.utils.Configures;
import edu.columbia.ccls.utils.FileFunctions;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.NonSparseToSparse;

public class Evalue {
	public void createWekaData(){
		BOW dataStore = new BOW();
		dataStore.parseFile();
//		HashMap<Signature, String> unigram_StatusHashMap = new HashMap<>();
		FastVector attVals = new FastVector();
		Instances data = new Instances("Unigram", createWekaHeader(dataStore.unigram_FreqHashMap, attVals), 0);
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
			saver.setFile(new File("data/unigramTest.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public FastVector createWekaHeader(HashMap<String, Integer> freqHashMap, FastVector attVals){
		FastVector atts = new FastVector();
		for(String word : freqHashMap.keySet()){
			atts.addElement(new Attribute(word));
		}
		atts.addElement(new Attribute("NOfWords"));//number of words
		attVals.addElement("NP");
		attVals.addElement("P");
		atts.addElement(new Attribute("CLASS", attVals));//email class
		return atts;
	}
	
	public void evalueFileBatch(){
		FastVector attVals = new FastVector();
		Threads dummyThread = new Threads();
		dummyThread.readInData();
		Instances data = new Instances("Thread", createWekaHeader(dummyThread.file_FreqHashMap, attVals), 0);
		for (final File fileEntry : new File(Configures.dataFolder).listFiles()) {
			if (fileEntry.getName().startsWith("."))
				continue;
			String filename = Configures.dataFolder + fileEntry.getName();
			Threads emailThread = new Threads();
			emailThread.evalueFile(filename);
			createOneWekaData(data, fileEntry, emailThread, attVals);
		}
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		try {
			saver.setFile(new File("data/threadTest.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * for Thread to create one weka data.
	 * @param data
	 * @param fileEntry
	 * @param emailThread
	 * @param attVals
	 */
	private void createOneWekaData(Instances data, File fileEntry, Threads emailThread, FastVector attVals) {
		double[] vals = new double[data.numAttributes()];
		int index = 0;
		String status = "NP";//dataStore.emailId_StatusHashMap.get(id);
//		unigram_StatusHashMap.put(new Signature(input), status);
		for(String word : emailThread.file_FreqHashMap.keySet()){
			if(emailThread.file_FreqHashMap.get(word) >= 1){
				vals[index] = (double)emailThread.file_FreqHashMap.get(word);
			}else{
				vals[index] = 0.0;
			}
			index++;
		}
		vals[index] = (double) emailThread.wordsCount;
		index++;
		vals[index] = attVals.indexOf(status);
		data.add(new Instance(1.0, vals));
	}
	
	public static void createWekaDataForMentionList(){
		//create Header.
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(Configures.MentionListFile)));
			String[] input = br.readLine().split(",");
			br.close();
			HashMap<String, Integer> template = new HashMap<>();
			for(String word : input){
				template.put(word, 0);
			}
			FastVector atts = new FastVector();
			FastVector attVals = new FastVector();
			for(String word : input){
				atts.addElement(new Attribute(word));
			}
			atts.addElement(new Attribute("NOfWords"));//number of words
			attVals.addElement("L");
			attVals.addElement("NL");
//			attVals.addElement("CS");
			atts.addElement(new Attribute("CLASS", attVals));//email class
			ArrayList<String> lines = FileFunctions.getLinesInFile(Configures.allMentionListsFile, 0, 350);
			Instances data = new Instances("mentionData", atts, 0);
			for(String line : lines){
				HashMap<String, Integer> words = new HashMap<>(template);
				int label = MentionList.evalueLine(line, words);
				if(label == -1)
					continue;
				double[] vals = new double[data.numAttributes()];
				int index = 0;
				int wordCount = 0;
				for(String word : words.keySet()){
					int count = words.get(word);
					if(count > 0){
						vals[index] = count;
						wordCount++;
					}
					index++;
				}
				vals[index++] = wordCount;
				System.out.println("label: " + label);
				if(label == -1){
//					vals[index] = attVals.indexOf("CS");
				}else if (label == 0) {
					vals[index] = attVals.indexOf("NL");
				}else if(label == 1){
					vals[index] = attVals.indexOf("L");
				}else {
					System.out.println("Label error!");
				}
				data.add(new Instance(1.0, vals));
			}
			NonSparseToSparse nonSparseToSparseInstance = new NonSparseToSparse(); 
			nonSparseToSparseInstance.setInputFormat(data); 
			Instances sparseDataset = Filter.useFilter(data, nonSparseToSparseInstance);
			ArffSaver arffSaverInstance = new ArffSaver(); 
			arffSaverInstance.setInstances(sparseDataset); 
			arffSaverInstance.setFile(new File("data/sparseMention.arff")); 
			arffSaverInstance.writeBatch();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
//		Evalue eva = new Evalue();
//		eva.createWekaData();
//		Threads.createLegalTerms();
//		new Evalue().evalueFileBatch();
//		MentionList.createHeader();
		createWekaDataForMentionList();
	}
}