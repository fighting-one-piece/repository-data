package org.project.modules.association.similar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.project.modules.association.similar.data.DataLoader;
import org.project.modules.association.similar.data.DataSet;
import org.project.modules.association.similar.data.Document;
import org.project.modules.association.similar.data.DocumentHelper;
import org.project.modules.association.similar.data.DocumentSimilarity;
import org.project.utils.DistanceUtils;
import org.project.utils.WordUtils;

public class DocumentOperation {
	
	/**
	 * 计算TFIDF
	 * TF计算是词频除以总词数
	 * @param documents
	 */
	public static void calculateTFIDF(List<Document> documents) {
		int docTotalCount = documents.size();
		for (Document document : documents) {
			Map<String, Double> tfidfWords = document.getTfidfWords();
			int wordTotalCount = document.getWords().length;
			Map<String, Integer> docWords = DocumentHelper.docWordsStatistics(document);
			for (String word : docWords.keySet()) {
				double wordCount = docWords.get(word);
				double tf = wordCount / wordTotalCount;
				double docCount = DocumentHelper.wordInDocsStatistics(word, documents) + 1;
				double idf = Math.log(docTotalCount / docCount);
				double tfidf = tf * idf;
				tfidfWords.put(word, tfidf);
			}
			System.out.println("doc " + document.getName() + " finish");
		}
	}
	
	/**
	 * 计算TFIDF
	 * TF计算是词频除以词频最高数
	 * @param documents
	 */
	public static void calculateTFIDF_1(List<Document> documents) {
		int docTotalCount = documents.size();
		for (Document document : documents) {
			Map<String, Double> tfidfWords = document.getTfidfWords();
			List<Map.Entry<String, Double>> list = 
					new ArrayList<Map.Entry<String, Double>>(tfidfWords.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
				@Override
				public int compare(Entry<String, Double> o1,
						Entry<String, Double> o2) {
					return -o1.getValue().compareTo(o2.getValue());
				}
			});
			if (list.size() == 0) continue; 
			double wordTotalCount = list.get(0).getValue();
			Map<String, Integer> docWords = DocumentHelper.docWordsStatistics(document);
			for (String word : docWords.keySet()) {
				double wordCount = docWords.get(word);
				double tf = wordCount / wordTotalCount;
				double docCount = DocumentHelper.wordInDocsStatistics(word, documents) + 1;
				double idf = Math.log(docTotalCount / docCount);
				double tfidf = tf * idf;
				tfidfWords.put(word, tfidf);
			}
			System.out.println("doc " + document.getName() + " finish");
		}
	}
	
	/**
	 * 计算相似度
	 * @param documents
	 */
	public static void calculateSimilarity(List<Document> documents) {
		for (Document document : documents) {
			String[] topWords = DocumentHelper.docTopNWords(document, 20);
			for (Document odocument : documents) {
				String[] otopWords = DocumentHelper.docTopNWords(odocument, 20);
				String[] allWords = WordUtils.mergeAndRemoveRepeat(topWords, otopWords);
				double[] v1 = DocumentHelper.docWordsVector(document, allWords);
				double[] v2 = DocumentHelper.docWordsVector(odocument, allWords);
				double cosine = DistanceUtils.cosine(v1, v2);
				DocumentSimilarity docSimilarity = new DocumentSimilarity();
				docSimilarity.setDocName1(document.getName());
				docSimilarity.setDocName2(odocument.getName());
				docSimilarity.setVector1(v1);
				docSimilarity.setVector2(v2);
				docSimilarity.setCosine(cosine);
				document.getSimilarities().add(docSimilarity);
			}
			for (DocumentSimilarity similarity : document.getSimilarities()) {
				System.out.println(similarity);
			}
		}
	}

	public static void main(String[] args) {
		String path = "D:\\resources\\01-news-18828";
		DataSet dataSet = DataLoader.load(path);
		calculateTFIDF(dataSet.getDocuments());
		calculateSimilarity(dataSet.getDocuments());
	}
	
}
