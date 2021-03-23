package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.FileUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.amituofo.xfs.service.ItemType;

public class LocalFolderItem extends LocalItemBase implements FolderItem {

	public LocalFolderItem(LocalDriver itemspace, File file) {
		super(itemspace, file);
	}

//	@Override
//	public Long getSize() {
//		return 0L;
//	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		DirectoryStream<Path> stream = null;
		try {
			Path dir = Paths.get(file.toURI());
			stream = Files.newDirectoryStream(dir);
			for (Path path : stream) {
				// path.toRealPath()
				File file = path.toFile();

				Item item = itemspace.newItemInstance(file);

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
				if (ef == HandleFeedback.interrupted) {
					handler.handle(ItemEvent.EXEC_END, null);
					return;
				}
			}

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			// throw new ServiceException(e);
			handler.exceptionCaught(this, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		// -------------------------------------------
	}

	// @Override
	// public boolean deleteEmptyFolder() throws ServiceException {
	// boolean deleted = file.delete();
	//
	// return deleted;
	// }

	@Override
	public boolean delete() {
		boolean deleted = file.delete();
		return deleted;
	}

	@Override
	public boolean delete(ItemHandler handler) {
		return deleteAllFiles(this, handler);
	}

	private boolean deleteAllFiles(LocalFolderItem fileItem, final ItemHandler handler) {
		// 递归查询本目录下的文件
		fileItem.list(new ItemHandler() {
			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (eventType == ItemEvent.ITEM_FOUND) {
					// 如果是文件夹递归查询
					if (data.isDirectory()) {
						deleteAllFiles((LocalFolderItem) data, handler);
					} else {
						// 如果是文件直接删除
						deleteFile((LocalFileItem) data, handler);
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				if (handler != null) {
					handler.exceptionCaught(data, e);
				}
			}
		});

		// 到达此处代表此目录下已没有文件，直接删除此目录，如果有文件会返回false
		boolean deleted = deleteFile(fileItem, handler);

		return deleted;
	}

	private boolean deleteFile(LocalItemBase fileItem, ItemHandler handler) {
		boolean deleted = fileItem.file.delete();

		if (handler != null) {
			handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, fileItem);
		}

		return deleted;
	}

	@Override
	public boolean createDirectory(String name) throws ServiceException {
		return new File(FileUtils.catPath(this.getPath(), name)).mkdirs();
	}

	@Override
	public boolean createDirectory() throws ServiceException {
		return file.mkdirs();
	}

	@Override
	public void rename(String newname) throws ServiceException {
		File target = new File(FileUtils.catPath(this.getParent().getPath(), newname));
		file.renameTo(target);
	}

	public FileItem linkFile(String fileName) throws ServiceException {
		String fullPath;
		// 是window的盘符全路径
		String realFileName;
		if (fileName.indexOf(':') == 1) {
			fullPath = fileName;
			// C:\\
			if (fullPath.length() == 3) {
				realFileName = fullPath;
				// C:
			} else if (fullPath.length() == 2) {
				fullPath += "\\";
				realFileName = fullPath;
			} else {
				realFileName = URLUtils.getLastNameFromPath(fileName);
			}
		} else {
			fullPath = URLUtils.catPath(this.getPath(), fileName, this.getPathSeparator());
			realFileName = URLUtils.getLastNameFromPath(fileName);
		}

		FileItem item = ((ItemInstanceCreator) itemspace).newFileItemInstance(fullPath);
		((ItemHiddenFunction) item).setName(realFileName);
		return item;
	}

	@Override
	public FolderItem linkFolder(String folderName) throws ServiceException {
		String fullPath;
		// 是window的盘符全路径
		String realFileName;
		if (folderName.indexOf(':') == 1) {
			fullPath = folderName;
			// C:\\
			if (fullPath.length() == 3) {
				realFileName = fullPath;
				// C:
			} else if (fullPath.length() == 2) {
				fullPath += "\\";
				realFileName = fullPath;
			} else {
				realFileName = URLUtils.getLastNameFromPath(folderName);
			}
		} else {
			fullPath = URLUtils.catPath(this.getPath(), folderName, this.getPathSeparator());
			realFileName = URLUtils.getLastNameFromPath(folderName);
		}
		FolderItem item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(fullPath);
		((ItemHiddenFunction) item).setName(realFileName);
		return item;
	}

	@Override
	public boolean isRoot() {
		String path = this.getPath();
		return "/".equals(path) || (path.lastIndexOf(":") == 1 && path.length() <= 3);
	}

}
