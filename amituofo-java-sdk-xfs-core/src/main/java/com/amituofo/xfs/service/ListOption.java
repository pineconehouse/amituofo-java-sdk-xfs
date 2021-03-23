package com.amituofo.xfs.service;

public class ListOption {
	private ItemFilter filter;
	private String prefix;
	private boolean withSubDirectory;
	private boolean withVersion = false;

	public ListOption() {
	}

	public ListOption withFilter(ItemFilter filter) {
		this.filter = filter;
		return this;
	}

	public ListOption withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public ItemFilter getFilter() {
		return filter;
	}

	public String getPrefix() {
		return prefix;
	}

	public ListOption withSubDirectory(boolean withSubDirectory) {
		this.withSubDirectory = withSubDirectory;
		return this;
	}

	public boolean isWithSubDirectory() {
		return withSubDirectory;
	}

	public ListOption withVersion(boolean withVersion) {
		this.withVersion = withVersion;
		return this;
	}

	public boolean isWithVersion() {
		return withVersion;
	}
}
