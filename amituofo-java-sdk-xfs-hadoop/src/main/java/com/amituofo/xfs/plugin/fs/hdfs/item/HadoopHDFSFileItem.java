package com.amituofo.xfs.plugin.fs.hdfs.item;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;

public class HadoopHDFSFileItem extends HadoopHDFSItemBase implements FileItem {
	public HadoopHDFSFileItem(FileSystem hadoopfs, HadoopHDFSItemspace itemspace, String filepath) {
		super(itemspace, hadoopfs);
		this.setPath(filepath);
	}

	@Override
	public void upateProperties() {
		try {
//			super.setFileStatus(hadoopfs.getFileStatus(this.getFilePath()));
			FileStatus fileStatus = hadoopfs.getFileStatus(this.getFilePath());
			((ItemHiddenFunction) this).setSize(fileStatus.getLen());
			((ItemHiddenFunction) this).setLastUpdateTime(fileStatus.getModificationTime());
			((ItemHiddenFunction) this).setCreateTime(fileStatus.getModificationTime());
			((ItemHiddenFunction) this).setLastAccessTime(fileStatus.getAccessTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public InputStream getContent() throws ServiceException {
		try {
			return hadoopfs.open(this.getFilePath());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public ContentWriter getContentWriter() {
		return new HadoopHDFSContentWriter(this);
	}

}
