package com.amituofo.xfs.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemEntry;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemView;
import com.amituofo.xfs.plugin.fs.local.item.LocalItemBase;
import com.amituofo.xfs.service.FileItemIterator;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.util.FileSystemUtils;
import com.amituofo.xfs.util.ItemUtils;

public class MassLocalFileItemIterator<T extends Item> implements FileItemIterator<T> {

	private LocalFileSystemEntry fileSystemEntry;
//	private LocalFileSystemPreference preference;

	private String line;
	private BufferedReader bf;

	public MassLocalFileItemIterator(File fout) {
		try {
			fileSystemEntry = (LocalFileSystemEntry) new LocalFileSystemEntryConfig(
					LocalFileSystemEntryConfig.SYSTEM_NAME,
					FileSystemUtils.getDefaultAvailableRoot(),
					LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW).createFileSystemEntry().open();
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		preference = fileSystemEntry.getPreference();
		
		FileReader fr;
		try {
			fr = new FileReader(fout);
			bf = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			throw new ServiceException(e);
		}
	}

	@Override
	public boolean hasNext() {
		try {
			if ((line = bf.readLine()) != null) {
				return true;
			} else {
				release();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public T next() {
		return toItem(line);
	}

	private T toItem(String line) {
		if (line == null) {
			return null;
		}

		String[] fields = line.split(MassFileItemList.RS);
		// String field_LN = fields[0];
		String field_PT = fields[1].substring(3);
		String field_NM = fields[2].substring(3);
		String field_SZ = fields[3].substring(3);
		// String field_TP = fields[4].substring(3);
		// String field_UT = fields[5].substring(3);
		// String field_CT = fields[6].substring(3);
		// String field_SN = fields[7].substring(3);

		LocalItemBase item;
		// if (field_TP.equals(ItemType.Directory.name())) {
		// item = new LocalFolderItem();
		// } else {
//		item = new LocalFileItem(null, fileSystemEntry,preference, new File(field_PT));
		item = (LocalItemBase)ItemUtils.toLazyLocalItem(new File(field_PT));
		// }

		item.setName(field_NM);
		item.setSize(Long.parseLong(field_SZ));
		item.setPath(field_PT);
		// item.setData(file);
		// item.setParent((LocalFolderItem)toItem(file.getParentFile()));

		// XXX BUG
		return (T) item;
	}

	@Override
	public void release() {
		if (bf != null) {
			try {
				bf.close();
				bf = null;
			} catch (IOException e) {
			}
		}

	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
