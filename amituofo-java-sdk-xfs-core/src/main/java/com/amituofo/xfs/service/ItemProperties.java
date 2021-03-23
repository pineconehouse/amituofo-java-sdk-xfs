package com.amituofo.xfs.service;

import java.util.ArrayList;
import java.util.List;

public class ItemProperties {
	// protected final Map<String, Object> properties = new HashMap<String, Object>();
	protected final List<Property<Object>> properties = new ArrayList<Property<Object>>();

	public ItemProperties() {
	}

	// public void set(String name, Object value) {
	// properties.put(name, value);
	// }

	public void add(String name, Object value) {
		properties.add(new Property<Object>(name, value));
	}

	public Object get(String name) {
		// Object value = properties.get(name);
		//
		// if (value != null) {
		// if (value instanceof String) {
		// return (String) value;
		// } else {
		// return value.toString();
		// }
		// }
		// return "";
		for (Property<Object> property : properties) {
			if (property.getKey().equalsIgnoreCase(name)) {
				return property.getValue();
			}
		}
		return null;
	}

	public Property<Object>[] values() {
		// Set<String> keys = properties.keySet();
		// Property<Object>[] kvs = new Property[keys.size()];
		// int i = 0;
		//
		// for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
		// String name = (String) iterator.next();
		// Object value = properties.get(name);
		// kvs[i++] = new Property<Object>(name, value);
		// }
		//
		// return kvs;

		return properties.toArray(new Property[properties.size()]);
	}

	// public KeyValue<String>[] getPropertiesInString() {
	// Set<String> keys = properties.keySet();
	// KeyValue<Object>[] kvs = new KeyValue[keys.size()];
	// int i = 0;
	//
	// for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
	// String name = (String) iterator.next();
	// Object value = properties.get(name);
	// kvs[i++] = new KeyValue<Object>(name, value);
	// }
	//
	// return kvs;
	// }
}
