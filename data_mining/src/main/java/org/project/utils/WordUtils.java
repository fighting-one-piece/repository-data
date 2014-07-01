package org.project.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.tartarus.snowball.SnowballProgram;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;

public class WordUtils {
	
	public static Logger logger = Logger.getLogger(WordUtils.class);
	
	/* 分隔符的集合 */
	public static final String delimiters = " \t\n\r\f~!@#$%^&*()_+|`-=\\{}[]:\";'<>?,./'";
	
	/**
	 * 分词
	 * @param input 输入文本
	 * @param seg 分词器
	 * @return
	 */
	public static String[] split(String input, Seg seg) {
		return split(new StringReader(input), seg);
	}
	
	public static String[] split(Reader reader, Seg seg) {
		List<String> words = new ArrayList<String>();
		MMSeg mmSeg = new MMSeg(reader, seg);
		Word word = null;
		try {
			while ((word = mmSeg.next()) != null) {
				words.add(word.getString());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return removeStopWords(words.toArray(new String[0]));
	}
	
	/**
	 * 分词
	 * @param input 输入文本
	 * @param language 语言
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] split(String input, String language) {
		/* 根据分隔符分词 */
		StringTokenizer stringTokenizer = new StringTokenizer(input, delimiters);
		/* 所有的词 */
		Vector vector = new Vector();
		/* 全大写的词 -- 不用提词干所以单独处理 */
		Vector vectorForAllUpperCase = new Vector();
		/* 根据大写字母分词 */
		flag0: while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			/* 全大写的词单独处理 */
			boolean allUpperCase = true;
			for (int i = 0; i < token.length(); i++) {
				if (!Character.isUpperCase(token.charAt(i))) {
					allUpperCase = false;
				}
			}
			if (allUpperCase) {
				vectorForAllUpperCase.addElement(token);
				continue flag0;
			}
			/* 非全大写的词 */
			int index = 0;
			flag1: while (index < token.length()) {
				flag2: while (true) {
					index++;
					if ((index == token.length())
							|| !Character.isLowerCase(token.charAt(index))) {
						break flag2;
					}
				}
				vector.addElement(token.substring(0, index).toLowerCase());
				token = token.substring(index);
				index = 0;
				continue flag1;
			}
		}
		/* 提词干 */
		try {
			Class stemClass = Class.forName("org.tartarus.snowball.ext."
					+ language + "Stemmer");
			SnowballProgram stemmer = (SnowballProgram) stemClass.newInstance();
			Method stemMethod = stemClass.getMethod("stem", new Class[0]);
			Object[] emptyArgs = new Object[0];
			for (int i = 0; i < vector.size(); i++) {
				stemmer.setCurrent((String) vector.elementAt(i));
				stemMethod.invoke(stemmer, emptyArgs);
				vector.setElementAt(stemmer.getCurrent(), i);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		/* 合并全大写的词 */
		for (int i = 0; i < vectorForAllUpperCase.size(); i++) {
			vector.addElement(vectorForAllUpperCase.elementAt(i));
		}
		/* 转为数组形式 */
		String[] array = new String[vector.size()];
		Enumeration enumeration = vector.elements();
		int index = 0;
		while (enumeration.hasMoreElements()) {
			array[index] = (String) enumeration.nextElement();
			index++;
		}
		/* 打印显示 */
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		return removeStopWords(array);
	}
	
	/**
	 * 英文分词
	 * @param input 输入文本
	 * @return
	 */
	public static String[] splitEN(String input) {
		return split(input, "english");
	}
	
	/**
	 * 文件分词
	 * @param path
	 * @param seg
	 * @return
	 */
	public static String[] splitFile(String path, Seg seg) {
		File file = new File(path);
		if (!file.exists()) {
			logger.error("file not exists");
		}
		return splitFile(file, seg);
	}
	
	/**
	 * 文件分词
	 * @param path
	 * @param seg
	 * @return
	 */
	public static String[] splitFile(File file, Seg seg) {
		String[] words = null;
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(in));
			words = split(reader, seg);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return removeStopWords(words);
	}
	
	/**
	 * 目录分词
	 * @param path
	 * @param seg
	 * @return
	 */
	public static String[] splitDirectory(String path, Seg seg) {
		File directory = new File(path);
		if (!directory.exists()) {
			logger.error("directory not exists");
		}
		String[] all_words = null;
		for (File file : directory.listFiles()) {
			String[] words = splitFile(file, seg);
			if (null == all_words) {
				all_words = words;
				continue;
			}
			String[] new_words = new String[all_words.length + words.length];
			System.arraycopy(all_words, 0, new_words, 0, all_words.length);
			System.arraycopy(words, 0, new_words, all_words.length, words.length);
			all_words = new_words;
		}
		return all_words;
	}
	
	
	public static String[] removeStopWords(String[] words) {
		Set<String> stopWords = new HashSet<String>();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = WordUtils.class.getClassLoader().getResourceAsStream("dic/stopwords/baidu.dic");
			br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (null != line && !"".equals(line)) {
				stopWords.add(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(br);
		}
		List<String> filterWords = new ArrayList<String>();
		for (String word : words) {
			if (!stopWords.contains(word)) {
				filterWords.add(word);
			}
		}
		return filterWords.toArray(new String[0]);
	}
	
	/**
	 * 合并并且去重
	 * @param w1
	 * @param w2
	 * @return
	 */
	public static String[] mergeAndRemoveRepeat(String[] w1, String[] w2) {
		Set<String> words = new HashSet<String>();
		for (String word : w1) {
			words.add(word);
		}
		for (String word : w2) {
			words.add(word);
		}
		return words.toArray(new String[0]);
	}
	
	public static void main(String[] args) {
		String path = "d:\\resources\\a.txt";
		String[] words = splitFile(path, new ComplexSeg(Dictionary.getInstance()));
		ShowUtils.print(words);
		words = removeStopWords(words);
		ShowUtils.print(words);
		String p = WordUtils.class.getClassLoader().getResource("dic/sougou/sougou.dic").getPath();
		System.out.println(p);
		words = splitFile(path, new ComplexSeg(Dictionary.getInstance(p)));
		ShowUtils.print(words);
	}
}
