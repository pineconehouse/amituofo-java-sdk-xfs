package com.amituofo.xfs.service.impl;

import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.ItemspaceConfigBuilder;

public abstract class DefaultItemspaceConfigBuilder<T extends ItemspaceConfig> implements ItemspaceConfigBuilder<T> {

	private String itemspaceName;

	public DefaultItemspaceConfigBuilder(String itemspaceName) {
		this.itemspaceName = itemspaceName;
	}

	public String getItemspaceName() {
		return itemspaceName;
	}

	public DefaultItemspaceConfigBuilder<T> withItemspaceName(String itemspaceName) {
		this.itemspaceName = itemspaceName;
		return this;
	}

}
