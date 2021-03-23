package com.amituofo.xfs.util;

import java.nio.file.attribute.FileAttribute;

public class DefaultFileAttribute<T> implements FileAttribute<T> {
	private String name;
	private T value;

	public DefaultFileAttribute(String name, T length) {
		super();
		this.name = name;
		this.value = length;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public T value() {
		return value;
	}
}