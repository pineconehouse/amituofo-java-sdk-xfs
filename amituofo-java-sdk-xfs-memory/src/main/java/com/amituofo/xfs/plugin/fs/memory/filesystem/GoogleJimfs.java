package com.amituofo.xfs.plugin.fs.memory.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.util.StreamUtils;
import com.amituofo.xfs.plugin.fs.memory.MemoryFileSystemEntry;
import com.amituofo.xfs.plugin.fs.memory.item.MemoryFileItem;
import com.amituofo.xfs.plugin.fs.memory.item.MemoryFolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.Property;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Configuration.Builder;
import com.google.common.jimfs.Jimfs;

public class GoogleJimfs extends MemoryFileSystemBase {
	private final static Map<String, FileSystem> fsCache = new HashMap<String, FileSystem>();
	private final static Map<String, Integer> fsInused = new HashMap<String, Integer>();
	private FileSystem fs;

	public GoogleJimfs(MemoryFileSystemEntry fileSystemEntry) {
		super(fileSystemEntry);
	}

	@Override
	public void init() throws MemoryFileSystemException {
		final String id = fileSystemEntry.getName();
		fs = fsCache.get(id);
		if (fs == null) {
			// Configuration config = Configuration.windows().toBuilder()
			// .setAttributeViews("basic", "owner", "dos", "acl", "user")
			// .setNameCanonicalNormalization(CASE_FOLD_UNICODE)
			// .setWorkingDirectory("C:\\Users\\Pinecone") // or "C:/Users/user"
			// .build();

			Long size = fileSystemEntry.getEntryConfig().getMemoryStorageSize();

			Builder builder = Configuration.unix().toBuilder()
					// .setAttributeViews("basic", "owner", "posix", "unix")
					.setWorkingDirectory("/data-will-be-lost-when-all-entry-closed");

			if (size != null && size > 0) {
				builder.setMaxSize(fileSystemEntry.getEntryConfig().getMemoryStorageSize());
			}
//			
			Configuration config = builder.build();

			fs = Jimfs.newFileSystem(config);
			fsCache.put(id, fs);
		}

		Integer connectionCount = fsInused.get(id);
		if (connectionCount == null) {
			connectionCount = 1;
		} else {
			connectionCount++;
		}
		fsInused.put(id, connectionCount);
	}

	@Override
	public void close() throws MemoryFileSystemException {
		try {
			final String id = fileSystemEntry.getName();
			Integer connectionCount = fsInused.get(id);
			if (connectionCount == 1) {
				fs.close();
				fs = null;
				fsCache.remove(id);
				fsInused.remove(id);
			} else {
				connectionCount--;
				fsInused.put(id, connectionCount);
			}
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public void newFile(String filepath) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			Files.createFile(foo);
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public void newDirectory(String filepath) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			Files.createFile(foo);
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public void newDirectorys(String filepath) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			Files.createDirectories(foo);
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public boolean delete(String filepath) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			Files.delete(foo);
			return true;
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public boolean rename(String filepath, String newfilepath) throws MemoryFileSystemException {
		try {
			Path source = fs.getPath(filepath);
			Path target = fs.getPath(newfilepath);
			Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
			return true;
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public boolean exist(String filepath) throws MemoryFileSystemException {
		Path foo = fs.getPath(filepath);
		return Files.exists(foo);
	}

	@Override
	public void list(String folderpath, ItemFilter filter, ItemHandler handler) throws MemoryFileSystemException {
		DirectoryStream<Path> stream = null;
		try {
			Path dir = fs.getPath(folderpath);
			stream = Files.newDirectoryStream(dir);
			for (Path path : stream) {
				// Long createTime = (Long) Files.getAttribute(path, "creationTime");
				// Long lastModifiedTime = (Long) Files.getAttribute(path, "lastModifiedTime");
				Long createTime = System.currentTimeMillis();
				Long lastModifiedTime = createTime;

				// 左侧文件名
				String fileName = path.getFileName().toString();
				// Type
				char type = Files.isDirectory(path) ? 'D' : 'F';
				// 左侧文件创建时间
				long fileCreateTime = createTime;
				// 左侧文件修改时间
				long fileLastUpdateTime = lastModifiedTime;

				// System.out.println(path);

				Item item;
				String filepath = path.toString();
				if (type == 'D') {
					item = new MemoryFolderItem(this, getItemspace(), filepath);
				} else {
					// 左侧文件大小
					Long fileSize = (Long) Files.getAttribute(path, "size");
					// 左侧文件
					// String fileContentHash = result.getString(6);

					item = new MemoryFileItem(this, getItemspace(), filepath);
					((ItemHiddenFunction) item).setSize(fileSize);
				}

				((ItemHiddenFunction) item).setName(fileName);
				((ItemHiddenFunction) item).setCreateTime(fileCreateTime);
				((ItemHiddenFunction) item).setLastUpdateTime(fileLastUpdateTime);

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
				if (ef == HandleFeedback.interrupted) {
					handler.handle(ItemEvent.EXEC_END, null);
					return;
				}
			}

		} catch (Exception e) {
			handler.exceptionCaught(null, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		// -------------------------------------------

		handler.handle(ItemEvent.EXEC_END, null);
	}

	@Override
	public InputStream read(String filepath, long offset, long length) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			return Files.newInputStream(foo);
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public long write(String filepath, InputStream in, long length) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			OutputStream out = Files.newOutputStream(foo);

			return StreamUtils.inputStream2OutputStream(in, true, out, true);
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public long getSize(String filepath) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			return Files.size(foo);
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

	@Override
	public long getLastModifiedTime(String filepath) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			FileTime lastModifiedTime = Files.getLastModifiedTime(foo);
			if (lastModifiedTime != null) {
				return lastModifiedTime.toMillis();
			}

		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
		return 0;
	}

	@Override
	public Property<Object>[] getSystemAttribute(String filepath) throws MemoryFileSystemException {
		try {
			Path foo = fs.getPath(filepath);
			Long size = Files.size(foo);
			FileTime lastModifiedTime = Files.getLastModifiedTime(foo);
			if (size != null && lastModifiedTime != null) {
				return new Property[] { new Property("size", size), new Property("lastModifiedTime", lastModifiedTime.toMillis()) };
			}

			if (size != null) {
				return new Property[] { new Property("size", size) };
			}

			if (lastModifiedTime != null) {
				return new Property[] { new Property("lastModifiedTime", lastModifiedTime.toMillis()) };
			}

			return null;
		} catch (IOException e) {
			throw new MemoryFileSystemException(e);
		}
	}

}
