package com.amituofo.xfs.plugin.fs.hdfs;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.hdfs.item.HadoopHDFSItemBase;
import com.amituofo.xfs.plugin.fs.hdfs.item.HadoopHDFSItemspace;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;

public class HadoopHDFileSystemEntry extends FileSystemEntryBase<HadoopHDFileSystemEntryConfig, HadoopHDFileSystemPreference, HadoopHDFSItemspace> {
	private FileSystem hadoopFileSystem;

	public HadoopHDFileSystemEntry(HadoopHDFileSystemEntryConfig entryConfig, HadoopHDFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected HadoopHDFSItemspace createDefaultItemspace() throws ServiceException {
		try {
			hadoopFileSystem = FileSystem.get(entryConfig.getHadoopConfiguration());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}

		HadoopHDFSItemspace root = new HadoopHDFSItemspace(this, "/");
		return root;
	}

	@Override
	protected List<HadoopHDFSItemspace> listAccessibleItemspaces() throws ServiceException {
		return null;
	}

	@Override
	public char getSeparatorChar() {
		return HadoopHDFSItemBase.SEPARATOR_CHAR;
	}

	@Override
	public void close() throws ServiceException {
		super.close();
		try {
			hadoopFileSystem.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean hasFeature(int featureId) {
		return false;
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void deleteItemSpace(String name) throws ServiceException {
	}

	public FileSystem getHadoopFileSystem() {
		return hadoopFileSystem;
	}

}
