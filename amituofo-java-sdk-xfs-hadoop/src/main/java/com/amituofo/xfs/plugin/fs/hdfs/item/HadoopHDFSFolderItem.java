package com.amituofo.xfs.plugin.fs.hdfs.item;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.ItemspaceBase;

public class HadoopHDFSFolderItem extends HadoopHDFSItemBase implements FolderItem {
	public HadoopHDFSFolderItem(FileSystem hadoopfs, HadoopHDFSItemspace itemspace, String filepath) {
		super(itemspace, hadoopfs);
		this.setPath(filepath);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		try {
//			FileStatus[] files;
			// if (filter != null) {
			// files = memfs.listStatus(getHadoopPath(), new PathFilter() {
			//
			// @Override
			// public boolean accept(Path path) {
			// return false;
			// }
			// });
			// } else {
			// files = hadoopfs.listStatus(getFilePath());
			// }

			// for (FileStatus fileStatus : files) {
			//
			// Item item = toItem(fileStatus);
			//
			// if (filter != null && !filter.accept(item)) {
			// continue;
			// }
			//
			// handler.handle(ItemEvent.ITEM_FOUND, item);
			// }
//			RemoteIterator<FileStatus> it = hadoopfs.listStatusIterator(getFilePath());
			RemoteIterator<LocatedFileStatus> it = hadoopfs.listLocatedStatus(getFilePath());
			while (it.hasNext()) {
				FileStatus status = it.next();
				Item item = toItem(status);

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				handler.handle(ItemEvent.ITEM_FOUND, item);
			}

			handler.handle(ItemEvent.EXEC_END, null);

		} catch (IOException e) {
			e.printStackTrace();
			handler.exceptionCaught(null, e);
		}
	}

	private Item toItem(FileStatus fileStatus) {
		Item item;
		Path path = fileStatus.getPath();
		String fullpath = URLUtils.catPath(this.getPath(), path.getName(), getFileSystemEntry().getSeparatorChar());
		if (fileStatus.isDirectory()) {
			item = ((ItemspaceBase) itemspace).newFolderItemInstance(fullpath);
		} else {
			item = ((ItemspaceBase) itemspace).newFileItemInstance(fullpath);
		}

		// ((HadoopHDFSItemBase) item).setFileStatus(fileStatus);
		((HadoopHDFSItemBase) item).filepath = path;
		((ItemHiddenFunction) item).setName(path.getName());
		((ItemHiddenFunction) item).setSize(fileStatus.getLen());
		((ItemHiddenFunction) item).setLastUpdateTime(fileStatus.getModificationTime());
		((ItemHiddenFunction) item).setCreateTime(fileStatus.getModificationTime());
		((ItemHiddenFunction) item).setLastAccessTime(fileStatus.getAccessTime());

		return item;
	}

	private Path getHadoopPath(String folderPath) {
		return new Path(folderPath);
	}

	@Override
	public boolean exists() throws ServiceException {
		if ("/".equals(this.getPath())) {
			return true;
		}

		return super.exists();
	}


	@Override
	public boolean delete(ItemHandler handler) {
		boolean deleted=false;
		try {
			deleted = super.delete();
			if (handler != null) {
				handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, this);
			}
			return deleted;
		} catch (ServiceException e) {
//			e.printStackTrace();
			if (handler != null) {
				handler.exceptionCaught(this, e);
			}
			return false;
		}
	}

//	@Override
//	public boolean deleteEmptyFolder() throws ServiceException {
//		return delete(null);
//	}

//	@Override
//	public boolean createDirectory(String name) throws ServiceException {
//		try {
//			String folderPath = URLUtils.catPath(this.getPath(), name, itemspace.getFileSystemEntry().getSeparatorChar());
//			hadoopfs.mkdirs(this.getHadoopPath(folderPath));
//			return true;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

	@Override
	public boolean createDirectory() throws ServiceException {
		try {
			hadoopfs.mkdirs(this.getFilePath());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
