package com.amituofo.xfs.config;

import java.util.Map;

public interface SimpleConfigurationEntry {
	SimpleConfiguration getSimpleConfiguration();

	void setSimpleConfiguration(SimpleConfiguration config);

	void setSimpleConfiguration(Map<String, Object> configMap);
}
