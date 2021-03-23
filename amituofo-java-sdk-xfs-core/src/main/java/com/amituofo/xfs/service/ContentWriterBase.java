package com.amituofo.xfs.service;

public abstract class ContentWriterBase<FILE extends FileItem> implements ContentWriter {
	protected final FILE fileitem;

	public ContentWriterBase(FILE file) {
		this.fileitem = file;
	}
	
}
