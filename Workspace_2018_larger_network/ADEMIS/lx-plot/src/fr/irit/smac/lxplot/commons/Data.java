package fr.irit.smac.lxplot.commons;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Helper class aiming at storing data in files
 * 
 * @author Alexandre Perles
 *
 */
public class Data {
	private static Map<String, DataStore> stores = new LinkedHashMap<String, DataStore>();

	public static String get(String _storeKey, String _name, Supplier<String> supplierFunction) {
		return getDatastore(_storeKey).get(_name, supplierFunction);
	}

	public static void set(String _storeKey, String _name, String _value) {
		getDatastore(_storeKey).set(_name, _value);
	}

	private static DataStore getDatastore(String _name) {
		if (!stores.containsKey(_name)) {
			stores.put(_name, new DataStore(_name));
		}
		return stores.get(_name);
	}

	public static void clear(String string) {
		getDatastore(string).clear();
	}
}
