package com.amituofo.xfs.config;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.DesUtils;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.ValidUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleConfiguration implements SimpleConfigurationEntry, Serializable {
	private String configId;

	public static final String CONFIG_ID = "_<_CONFIG_ID_>_";
	public static final String CONFIG_CLASS_NAME = "_<_CONFIG_CLASS_NAME_>_";

	private final Map<String, Object> configMap = new HashMap<String, Object>();
	private final byte[] desKey = new byte[] { 112, 111, 119, 101, 114, 101, 100, 45, 98, 121, 45, 114, 105, 115, 111, 110, 45, 104, 97, 110, 45, 97, 109, 116, 102, 57, 57 };// "powered-by-rison-han-amtf99";

	public SimpleConfiguration(Class cls) {
		generateNewID();
		set(CONFIG_CLASS_NAME, cls.getName());
	}

	public SimpleConfiguration() {
		generateNewID();
		set(CONFIG_CLASS_NAME, this.getClass().getName());
	}

	public void generateNewID() {
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString().replace("-", "").substring(0, 6);

		// set(CONFIG_ID, id);
		this.configId = id;
	}

	@Override
	public SimpleConfiguration getSimpleConfiguration() {
		return this;
	}

	@Override
	public void setSimpleConfiguration(Map<String, Object> configMap) {
		String id = getConfigurationID();
		String className = getConfigurationClassName();
		
		String oldid = (String) configMap.remove(CONFIG_ID);
		this.configId = oldid;

		this.configMap.clear();
		this.configMap.putAll(configMap);

		if (this.getConfigurationID() == null) {
			// set(CONFIG_ID, id);
			this.configId = id;
		}

		if (this.getConfigurationClassName() == null) {
			set(CONFIG_CLASS_NAME, className);
		}
	}

	@Override
	public void setSimpleConfiguration(SimpleConfiguration config) {
		String id = getConfigurationID();
		String className = getConfigurationClassName();
		
		configMap.remove(CONFIG_ID);
		this.configId = config.configId;

		this.configMap.clear();
		this.configMap.putAll(config.configMap);

		if (this.getConfigurationID() == null) {
			// set(CONFIG_ID, id);
			this.configId = id;
		}

		if (this.getConfigurationClassName() == null) {
			set(CONFIG_CLASS_NAME, className);
		}
	}

	@Override
	protected SimpleConfiguration clone() {
		SimpleConfiguration newsc = new SimpleConfiguration();
		newsc.setSimpleConfiguration(this);
		return newsc;
	}

	public String getConfigurationID() {
		return this.configId;
		// this.getString(CONFIG_ID);
	}

	public String getConfigurationClassName() {
		return this.getString(CONFIG_CLASS_NAME);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SimpleConfiguration)) {
			return false;
		}

		SimpleConfiguration a = (SimpleConfiguration) obj;
		SimpleConfiguration b = this;

		return a.configMap.equals(b.configMap);
	}

	public void validateConfig() throws InvalidParameterException {
		ValidUtils.invalidIfEmpty(getString(CONFIG_CLASS_NAME), "Configuration class name cound not be empty!");

		// Class<SimpleConfigurationEntry> cls;
		// try {
		// String clsName = getString(CONFIG_CLASS_NAME);
		// cls = (Class<SimpleConfigurationEntry>) Class.forName(clsName);
		// SimpleConfigurationEntry cc = cls.newInstance();
		// } catch (Exception e) {
		// throw new InvalidParameterException("Unable to create configuration instance.", e);
		// }
	}

	public void setSensitive(String key, String value) throws ServiceException {
		if (StringUtils.isNotEmpty(value)) {
			// byte[] x = desKey.getBytes();
			try {
				configMap.put(key, DesUtils.parseByte2HexStr(DesUtils.encrypt(value.getBytes(), desKey)));
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		} else {
			configMap.put(key, value);
		}
	}

	public void set(String key, String value) {
		configMap.put(key, value);
	}

	public void set(String key, String[] values) {
		configMap.put(key, values);
	}

	public void set(String key, boolean value) {
		configMap.put(key, Boolean.toString(value));
	}

	public void set(String key, int value) {
		configMap.put(key, Integer.toString(value));
	}

	public void set(String key, long value) {
		configMap.put(key, Long.toString(value));
	}

	public void set(String key, float value) {
		configMap.put(key, Float.toString(value));
	}

	public void set(String key, double value) {
		configMap.put(key, Double.toString(value));
	}

	public void set(String key, char value) {
		configMap.put(key, String.valueOf(value));
	}

	public boolean has(String key) {
		return configMap.get(key) != null;
	}

	// public boolean hasNotNull(String key) {
	// return configMap.get(key) != null;
	// }

	public String getSensitiveString(String key) throws ServiceException {
		String value = getString(key);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return new String(DesUtils.decrypt(DesUtils.parseHexStr2Byte(value), desKey));
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		return value;
	}

	public char getChar(String key) {
		String v = (String) configMap.get(key);
		if (v != null) {
			return v.charAt(0);
		}

		return 0;
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defaultValue) {
		Object val = configMap.get(key);
		if (val != null) {
			if (val instanceof String) {
				return (String) val;
			} else {
				return val.toString();
			}
		}

		return defaultValue;
	}

	public boolean getBoolean(String key) {
		String v = (String) configMap.get(key);
		if ("true".equalsIgnoreCase(v)) {
			return true;
		}

		return false;
	}

	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		String v = (String) configMap.get(key);
		if (StringUtils.isNotEmpty(v)) {
			return Integer.parseInt(v);
		}

		return defaultValue;
	}

	public Long getLong(String key) {
		return getLong(key, null);
	}

	public Long getLong(String key, Long defaultValue) {
		String v = (String) configMap.get(key);
		if (StringUtils.isNotEmpty(v)) {
			return Long.valueOf(v);
		}

		return defaultValue;
	}

	public Float getFloat(String key) {
		return getFloat(key, null);
	}

	public Float getFloat(String key, Float defaultValue) {
		String v = (String) configMap.get(key);
		if (StringUtils.isNotEmpty(v)) {
			return Float.parseFloat(v);
		}

		return defaultValue;
	}
	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	public Double getDouble(String key, Double defaultValue) {
		String v = (String) configMap.get(key);
		if (StringUtils.isNotEmpty(v)) {
			return Double.parseDouble(v);
		}

		return defaultValue;
	}

	public List<String> getStringList(String key) {
		Object o = configMap.get(key);
		if (o != null) {
			if (o instanceof List) {
				return (List<String>) o;
			}

			if (o instanceof String[]) {
				List<String> list = new ArrayList<String>();
				String[] vs = (String[]) o;
				for (String string : vs) {
					list.add(string);
				}

				return list;
			}
		}
		return null;
	}

	public static String toSerializableString(SimpleConfiguration simpleConfiguration) throws InvalidParameterException, JsonProcessingException, UnsupportedEncodingException {
		ObjectMapper objectMapper = new ObjectMapper();
		simpleConfiguration.validateConfig();
		// return objectMapper.writeValueAsString(simpleConfiguration.configMap);

		simpleConfiguration.configMap.put(CONFIG_ID, simpleConfiguration.configId);
		byte[] buf = objectMapper.writeValueAsBytes(simpleConfiguration.configMap);
		simpleConfiguration.configMap.remove(CONFIG_ID);

		return new String(buf, "utf-8");
	}

	public static Object parseSerializableString(
			String config) throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		ObjectMapper objectMapper = new ObjectMapper();
		HashMap<String, Object> configMap = objectMapper.readValue(config, new TypeReference<HashMap<String, Object>>() {
		});
		// Class<SimpleConfigurationEntry> cls = (Class<SimpleConfigurationEntry>) Class.forName((String) configMap.get(CONFIG_CLASS_NAME));
		// SimpleConfigurationEntry sc = cls.newInstance();


		SimpleConfiguration sc = new SimpleConfiguration();
		sc.setSimpleConfiguration(configMap);
		return sc;
	}

	public static Object parseSerializableString(HashMap<String, Object> configMap,
			Class<? extends SimpleConfigurationEntry> cls) throws InstantiationException, IllegalAccessException {
		SimpleConfigurationEntry sc = cls.newInstance();
		sc.setSimpleConfiguration(configMap);
		return sc;
	}

}
