package com.amituofo.xfs.service;

import com.amituofo.common.define.DatetimeFormat;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;

public abstract class ItemBase<ITEMSPACE extends Itemspace> implements Item, ItemHiddenFunction {
	// protected final PREFERENCE preference;
	// protected final ENTRY fileSystemEntry;
	protected ITEMSPACE itemspace;

	// private FolderItem parent;
	protected String name;
	protected String actualPath;
	protected Long size;
	protected Long createTime;
	protected Long lastUpdateTime;
	protected Long lastAccessTime;

	protected Object data;
	protected ContentHash contentHash;
	// protected String catelog;

	public ItemBase(ITEMSPACE itemspace) {
		super();
		this.itemspace = itemspace;
	}
	// public abstract Item clone();

	@Override
	public FileSystemPreference getOperationPreference() {
		return itemspace.getFileSystemPreference();
	}

	@Override
	public FileSystemEntry getFileSystemEntry() {
		return itemspace.getFileSystemEntry();
	}

	@Override
	public FolderItem getRoot() {
		return itemspace.getRootFolder();
	}

	@Override
	public String toString() {
		return this.getPath();
	}

	// @Override
	// public URI toURI() {
	// String path = this.getPath();
	// if (path.length() == 0 || path.charAt(0) != '/') {
	// path = "/" + path;
	// }
	// String host = StringUtils.encodeBase64String(this.getFileSystemEntry().getName());
	// path = StringUtils.encodeBase64String(path);
	// String fragment = StringUtils.encodeBase64String(this.getSection().getName());
	//
	// if(this.isDirectory()) {
	// URLUtils.catPath(path, "/");
	// }
	//
	// try {
	// return new URI("xfsi", host, path, fragment);
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Item)) {
			return false;
		}

		Item a = this;
		Item b = (Item) obj;

		if (a.getType() != b.getType()) {
			return false;
		}

		if ((a.getPath() != null && b.getPath() != null) && !a.getPath().equals(b.getPath())) {
			return false;
		}

		if ((a.getName() != null && b.getName() != null) && !a.getName().equals(b.getName())) {
			return false;
		}

		if ((a.getSize() != null && b.getSize() != null) && !a.getSize().equals(b.getSize())) {
			return false;
		}

		if (!a.getSystemName().equals(b.getSystemName())) {
			return false;
		}

		// if (!a.getFileSystemEntry().getName().equals(b.getFileSystemEntry().getName())) {
		// return false;
		// }

		if (!a.getItemspace().equals(b.getItemspace())) {
			return false;
		}

		return true;
	}

	@Override
	public ITEMSPACE getItemspace() {
		return itemspace;
	}

	@Override
	public Itemspace[] getItemspaces() throws ServiceException {
		return this.getFileSystemEntry().getItemspaces();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameOnly() {
		if (name != null) {
			int i = name.lastIndexOf('.');
			if (i != -1) {
				return name.substring(0, i);
			}
		}

		return name;
	}

	public String getExt() {
		if (name != null) {
			int i = name.lastIndexOf('.');
			if (i != -1) {
				return name.substring(i + 1);
			}
		}

		return "";
	}

	// public void setName(String name) {
	// if (name != null) {
	// int i = name.lastIndexOf('.');
	// if (i != -1) {
	// this.ext = name.substring(i + 1);
	// this.name = name.substring(0, i);
	// } else {
	// this.name = name;
	// }
	// }
	// }
	//
	// public String getExt() {
	// return ext;
	// }

	// public String getCatelog() {
	// return catelog;
	// }
	//
	// public void setCatelog(String catelog) {
	// this.catelog = catelog;
	// }

	// public FolderItem getParent() {
	// return parent;
	// }

	// public void setParent(FolderItem parent) {
	// this.parent = parent;
	// }

	public String getPath() {
		return actualPath;
	}

	public void setPath(String path) {
		this.actualPath = path;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Long getSize() {
		return size;
	}

	@Override
	public int getStatus() {
		return 0;
	}

	public void setSize(Long size) {
		if (size != null) {
			this.size = Math.abs(size);
		} else {
			this.size = null;
		}
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public ContentHash getContentHash() {
		return contentHash;
	}

	public void setContentHash(ContentHash contentHash) {
		this.contentHash = contentHash;
	}

	@Override
	public FolderItem getParent() {
		if (this.isDirectory() && ((FolderItem) this).isRoot()) {
			return null;
		}
		// a
		// a/
		// /a
		// a/b/
		// /a/b
		// /a/b/
		String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), null);

		if (parentPath == null || parentPath.length() == 0) {
			// if (this.isFile()) {
			return this.getRoot();
			// } else {
			// return null;
			// }
		}

		String rootpath = this.getFileSystemEntry().getRootPath();
		if (rootpath.equals(parentPath)) {
			return itemspace.getRootFolder();
		}

		String name = URLUtils.getLastNameFromPath(parentPath);
		FolderItem parent = ((ItemInstanceCreator) itemspace).newFolderItemInstance(parentPath);
		((ItemHiddenFunction) parent).setName(name);
		// ((ItemInnerFunc) parent).setPath(parentPath);

		return parent;
	}

	@Override
	public Item clone() {
		Item item;
		if (this.isDirectory()) {
			item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(this.getPath());
			// clone.setCatelog(this.getCatelog());
			((ItemHiddenFunction) item).setName(this.getName());
			// clone.setPath(this.getPath());
			// clone.setData(this.getData());
			// clone.setSize(this.getSize());
			((ItemHiddenFunction) item).setCreateTime(this.getCreateTime());
			((ItemHiddenFunction) item).setLastUpdateTime(this.getLastUpdateTime());
		} else {
			item = ((ItemInstanceCreator) itemspace).newFileItemInstance(this.getPath());
			// clone.setCatelog(this.getCatelog());
			((ItemHiddenFunction) item).setName(this.getName());
			// clone.setPath(this.getPath());
			// clone.setData(this.getData());
			((ItemHiddenFunction) item).setSize(this.getSize());
			((ItemHiddenFunction) item).setCreateTime(this.getCreateTime());
			((ItemHiddenFunction) item).setLastUpdateTime(this.getLastUpdateTime());
		}

		return item;
	}

	@Override
	public ItemProperties getProperties() throws ServiceException {
		ItemProperties ip = new ItemProperties();
		ip.add("basic:Name", this.getName());
		if (this.isFile()) {
			ip.add("basic:IsFile", "Yes");
			ip.add("basic:Size", this.getSize());
		} else {
			ip.add("basic:IsFile", "No");
			// ip.add("basic:isSymbolicLink", this.getCreateTime());
			// ip.add("basic:isRegularFile", this.getCreateTime());
		}

		ip.add("basic:CreationTime", this.getCreateTime() != null ? DatetimeFormat.YYYY_MM_DD_HHMMSS.format(this.getCreateTime()) : "-");
		ip.add("basic:LastAccessTime", this.getLastAccessTime() != null ? DatetimeFormat.YYYY_MM_DD_HHMMSS.format(this.getLastAccessTime()) : "-");
		ip.add("basic:LastModifiedTime", this.getLastUpdateTime() != null ? DatetimeFormat.YYYY_MM_DD_HHMMSS.format(this.getLastUpdateTime()) : "-");

		return ip;
	}

}
