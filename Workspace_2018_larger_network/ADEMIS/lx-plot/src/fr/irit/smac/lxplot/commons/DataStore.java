package fr.irit.smac.lxplot.commons;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class comes with the Data class and is here to help the storage of non
 * sensitive data on the disk
 * 
 * @author Alexandre Perles
 *
 */
public class DataStore {

	private final String dir = "cache/";
	private String filename;
	private File file;
	private boolean need_reload = true;
	private Map<String, String> cache = new LinkedHashMap<String, String>();

	public DataStore(String _name) {
		this.filename = dir + _name + ".dat";
	}

	public String get(String _name, Supplier<String> supplierFunction) {

		if (need_reload)
			reload();

		if (cache.containsKey(_name))
			return cache.get(_name);
		else {
			String value = supplierFunction.get();
			set(_name, value);
			return value;
		}
	}

	private File getFile() {
		if (file == null) {
			new File(dir).mkdirs();
			file = new File(filename);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			need_reload = true;
		}
		return file;
	}

	public void set(String key, String value) {
		if (need_reload)
			reload();
		cache.put(key, value);
		writeCacheToFile();
	}

	private void writeCacheToFile() {
		PrintWriter pw;
		try {
			pw = new PrintWriter(getFile());
			for (Entry<String, String> e : cache.entrySet()) {
				pw.println(e.getKey() + "=" + e.getValue().replace("\n", "\\n"));
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}

	}

	private void reload() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getFile())));
			Pattern p = Pattern.compile("^([^=]+)=(.*)$");

			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					// System.out.println("'"+m.group(1)+"' '"+m.group(2)+"'");
					cache.put(m.group(1), m.group(2).replace("\\n", "\n"));
				}
			}
			need_reload = false;
			br.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void clear() {
		cache.clear();
		writeCacheToFile();
	}
}
