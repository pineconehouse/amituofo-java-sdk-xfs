package com.amituofo.xfs.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amituofo.xfs.service.FileItemIterator;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemList;
import com.amituofo.xfs.util.TemporaryDir;

public class MassFileItemList<T extends Item> implements ItemList<T> {

	private final File file;
	private BufferedWriter bw;

	private long lineNumber = 0;

	public final static String RS = String.valueOf((char) (30));

	private List<FileItemIterator<T>> its = new ArrayList<FileItemIterator<T>>();

	private StringBuilder buf = new StringBuilder();

	public MassFileItemList(String id) {
		this(TemporaryDir.INSTANCE.getTempWorkingFolder("MFIL"), id);
	}

	public MassFileItemList(File workingDir, String id) {
		file = new File(workingDir.getPath() + File.separator + id + "FIL.found");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public MassFileItemList() {
	// file = TemporaryDir.INSTANCE.getTempWorkingFile("ITEMS.found");
	// FileOutputStream fos;
	// try {
	// fos = new FileOutputStream(file);
	// bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	public void add(T item) {
		try {
			buf.setLength(0);
			buf.append(lineNumber);
			buf.append(RS);
			buf.append("PT:").append(item.getPath());
			buf.append(RS);
			buf.append("NM:").append(item.getName());
			buf.append(RS);
			buf.append("SZ:").append(item.getSize());
			buf.append(RS);
			buf.append("TP:").append(item.getType().name());
			// buf.append(RS);
			// buf.append("UT:").append(item.getLastUpdateTime());
			// buf.append(RS);
			// buf.append("CT:").append(item.getCreateTime());
			// buf.append(RS);
			// buf.append("SN:").append(item.getSystemName());
			bw.write(buf.toString());
			bw.newLine();

			lineNumber++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long size() {
		return lineNumber;
	}

	@Override
	public Iterator<T> iterator() {
		try {
			bw.close();
			bw = null;
		} catch (IOException e) {
			e.printStackTrace();
			bw = null;
		}

		MassLocalFileItemIterator<T> it = new MassLocalFileItemIterator<T>(file);
		its.add(it);
		return it;
	}

	@Override
	public synchronized void updateStatus(Item item, int status) {

	}

	@Override
	public void release() {
		for (FileItemIterator<T> fileItemIterator : its) {
			fileItemIterator.release();
		}

		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
			}
			bw = null;
		}

		file.delete();
	}

	@Override
	public void clear() {
		if (file.exists()) {
			if (file.delete()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

//	@Override
//	public List<T> toList() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
