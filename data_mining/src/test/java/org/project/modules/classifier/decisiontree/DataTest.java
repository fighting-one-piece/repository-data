package org.project.modules.classifier.decisiontree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.data.DataSplit;
import org.project.modules.classifier.decisiontree.data.DataSplitItem;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.utils.FileUtils;
import org.project.utils.JSONUtils;
import org.project.utils.ShowUtils;

public class DataTest {

	@Test
	public void a() {
		String path = "d:\\trainset_10000_l.txt";
		Data data = DataLoader.loadWithId(path);
		Map<String, Map<Object, Integer>> a = 
				new HashMap<String, Map<Object, Integer>>();
		for (Instance instance : data.getInstances()) {
			Map<String, Object> attrs = instance.getAttributes();
			for (Map.Entry<String, Object> entry : attrs.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				Map<Object, Integer> b = a.get(key);
				if (null == b) {
					b = new HashMap<Object, Integer>();
					a.put(key, b);
				}
				Integer c = b.get(value);
				b.put(value, null == c ? 1 : c + 1);
			}
		}
		for (Map.Entry<String, Map<Object, Integer>> e : a.entrySet()) {
			System.out.print(e.getKey() + "-->");
			for (Map.Entry<Object, Integer> f : e.getValue().entrySet()) {
				System.out.print(f.getKey() + "--" + f.getValue() + ":");
			}
			System.out.println();
		}
		System.out.println(a.size());
		System.out.println(data.getAttributes().length);
	}
	
	@Test
	public void load() {
		String path = "d:\\trainset_5000_l.txt";
		Data data = DataLoader.loadWithId(path);
		System.out.println(data.getAttributes().length);
		System.out.println(data.getInstances().size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void jsonInstance() {
		Instance instance = new Instance();
//		instance.setId(1L);
		instance.setCategory(1);
		instance.setAttribute("1", 1);
		instance.setAttribute("2", 2);
		instance.setAttribute("3", 3);
		String jsonData = JSONUtils.object2json(instance);
		System.out.println(jsonData);
//		Instance i = (Instance) JSONUtils.json2Object(jsonData, Instance.class);
//		System.out.println(i.getId());
//		System.out.println(i.getCategory());
//		i.print();
		JSONObject jsonObject = JSONUtils.json2Object(jsonData);
		Instance i = new Instance();
		i.setId(jsonObject.getLong("id"));
		i.setCategory(jsonObject.get("category"));
		i.setAttributes(jsonObject.getJSONObject("attributes"));
		i.print();
	}
	
	@Test
	public void addLineNum() throws Exception {
		FileUtils.addLineNum("D:\\trainset_5000.txt", "D:\\trainset_5000_l.txt");
	}
	
	private Set<String> calculateAttribute(String input) {
		Set<String> attributes = new HashSet<String>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				line = reader.readLine();
				tokenizer.nextToken();
				tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					attributes.add(entry[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return attributes;
	}
	
	private Set<String> getAttribute(String input, int n) {
		Set<String> attributes = new HashSet<String>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				line = reader.readLine();
				tokenizer.nextToken();
				tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					if (attributes.size() != n) {
						attributes.add(entry[0]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		return attributes;
	}
	
	@Test
	public void viewAttributeNum() {
		String input = "D:\\trainset_extract_1_l.txt";
		System.out.println(calculateAttribute(input).size());
		input = "D:\\trainset_extract_10_l.txt";
		System.out.println(calculateAttribute(input).size());
		input = "D:\\trainset_10000_l.txt";
		System.out.println(calculateAttribute(input).size());
	}
	
	@Test
	public void generateDataFile() {
		String input = "D:\\trainset_10000_l.txt";
		String output = "D:\\attribute_100_r_10000.txt";
		int attributeNum = 100;
		Set<String> attributes = getAttribute(input, attributeNum);
		System.out.println(attributes.size());
		List<Instance> instances = new ArrayList<Instance>();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(new File(input));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				Instance instance = new Instance();
				instance.setId(Long.parseLong(tokenizer.nextToken()));
				instance.setCategory(tokenizer.nextToken());
				int index = 0;
				while (tokenizer.hasMoreTokens() && index < attributeNum) {
					String value = tokenizer.nextToken();
					String[] entry = value.split(":");
					if (attributes.contains(entry[0])) {
						instance.setAttribute(entry[0], entry[1]);
						System.out.println("token: " + index);
						index++;
					}
				}
				Iterator<String> iter = attributes.iterator();
				while (iter.hasNext() && index < attributeNum) {
					String attribute = iter.next();
					Object value = instance.getAttribute(attribute);
					if (null == value) {
						instance.setAttribute(attribute, "1.0");
						System.out.println("add: " + index);
						index++;
					}
				}
				instances.add(instance);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
		DataHandler.writeData(output, new Data(
				attributes.toArray(new String[0]), instances));
	}
	
	@Test
	public void compute() {
		Map<Object, Integer> takeValues = new HashMap<Object, Integer>();
		Map<Object, Integer> values = new HashMap<Object, Integer>();
		values.put("a", 3);
		values.put("b", 4);
		values.put("c", 5);
		double valuesCount = 0;
		for (int count : values.values()) {
			valuesCount += count;
		}
		int k = values.keySet().size();
		int temp = 90;
		for (Map.Entry<Object, Integer> entry : values.entrySet()) {
			int value = entry.getValue();
			Double p = value / valuesCount * 90;
			if (--k > 0) {
				takeValues.put(entry.getKey(), p.intValue());
				temp = temp - p.intValue(); 
			} else {
				takeValues.put(entry.getKey(), temp);
			}
		}
		ShowUtils.print(takeValues);
	}
	
	@Test
	public void compute1() {
		String path = "d:\\trainset_extract_10.txt";
		Data data = DataLoader.load(path);
		System.out.println("data attributes:　" + data.getAttributes().length);
		String p = "d:\\trainset_extract_1.txt";
		Data testData = DataLoader.load(p);
		System.out.println("testdata attributes:　" + testData.getAttributes().length);
		DataHandler.computeFill(testData, data, 1.0);
		DataHandler.writeData("d:\\a.txt", testData);
	}
	
	@Test
	public void split() {
		String path = "d:\\trains14_id.txt";
		Data data = DataLoader.loadWithId(path);
		data.setSplitAttribute("age");
		data.setSplitPoints(new String[]{"middle_aged",
				"youth,senior"});
		DataSplit ds = DataHandler.split(data);
		System.out.println("~~~~~~~");
		for(Instance i : data.getInstances()) {
			i.print();
		}
		System.out.println("~~~~~~~");
		for (DataSplitItem item : ds.getItems()) {
			System.out.println("---");
			for(Instance i : item.getInstances()) {
				i.print();
			}
			System.out.println("---");
		}
	}
	
}
