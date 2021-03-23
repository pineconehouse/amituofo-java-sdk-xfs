package com.amituofo.xfs.service;

import java.io.InputStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;

public interface ContentWriter {
	void abort() throws ServiceException;

	void write(InputStream in, long length) throws ServiceException;

	void write(FileItem sourceItem) throws ServiceException;

	void write(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException;

	void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException;

}
