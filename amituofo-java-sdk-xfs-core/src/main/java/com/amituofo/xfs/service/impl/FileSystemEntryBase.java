package com.amituofo.xfs.service.impl;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.value.Counter;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.SystemUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.EntryConfig;
import com.amituofo.xfs.config.RemoteEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.amituofo.xfs.service.FileSystemType;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.Itemspace;
import com.amituofo.xfs.service.ItemspaceFilter;

public abstract class FileSystemEntryBase<CONFIG extends EntryConfig, PREFERENCE extends FileSystemPreference, ITEMSPACE extends Itemspace> implements FileSystemEntry {
	protected final CONFIG entryConfig;
	protected final PREFERENCE preference;

	private ITEMSPACE defaultItemspace;
	private ITEMSPACE[] itemspaces;
	private Map<String, ITEMSPACE> itemspacesMap = new LinkedHashMap<String, ITEMSPACE>();

	public FileSystemEntryBase(CONFIG entryConfig) {
		super();
		this.entryConfig = entryConfig;
		this.preference = (PREFERENCE) entryConfig.createPreference();
	}

	public FileSystemEntryBase(CONFIG entryConfig, PREFERENCE preference) {
		super();
		this.entryConfig = entryConfig;

		if (preference == null) {
			this.preference = (PREFERENCE) entryConfig.createPreference();
		} else {
			this.preference = preference;
		}
	}

	protected abstract ITEMSPACE createDefaultItemspace() throws ServiceException;

	protected abstract List<ITEMSPACE> listAccessibleItemspaces() throws ServiceException;

	@Override
	public FileSystemEntry open() throws ServiceException {
		if (defaultItemspace == null) {
			defaultItemspace = createDefaultItemspace();
		}

		return this;
	}

	@Override
	public ITEMSPACE getDefaultItemspace() {
		return defaultItemspace;
	}

	@Override
	public Itemspace getItemspace(String spaceName) {
		if (StringUtils.isEmpty(spaceName)) {
			return this.getDefaultItemspace();
		}

		if (itemspacesMap.size() == 0) {
			try {
				this.getItemspaces();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}

		return itemspacesMap.get(spaceName);
	}

	@Override
	public FileSystemEntry refresh() throws ServiceException {
		itemspaces = null;
		itemspacesMap.clear();
		// defaultItemspace = createDefaultItemspace();

		return this;
	}

	@Override
	public ITEMSPACE[] getItemspaces(ItemspaceFilter filter) throws ServiceException {
		ITEMSPACE[] is = getItemspaces();
		if (filter == null) {
			return is;
		}

		List<ITEMSPACE> tmpItemspaceList = new ArrayList<ITEMSPACE>();
		for (ITEMSPACE itemspace : is) {
			if (filter.accept(itemspace)) {
				tmpItemspaceList.add(itemspace);
			}
		}

		return (ITEMSPACE[]) tmpItemspaceList.toArray(new Itemspace[tmpItemspaceList.size()]);
	}

	@Override
	public ITEMSPACE[] getItemspaces() throws ServiceException {
		if (itemspaces == null) {
			List<ITEMSPACE> tmpItemspaceList = new ArrayList<ITEMSPACE>();
			ITEMSPACE defaultis = getDefaultItemspace();

			List<ITEMSPACE> itemspaceList = null;
			try {
				// 列出itemspace可能因为权限原因失败
				itemspaceList = listAccessibleItemspaces();
			} catch (Exception e) {
				e.printStackTrace();
				itemspaceList = null;
			}

			if (defaultis != null) {
				if (itemspaceList != null && itemspaceList.size() > 0) {
					boolean foundDefault = false;
					for (ITEMSPACE itemspace : itemspaceList) {
						tmpItemspaceList.add(itemspace);
						// 找出与默认space相同的，并替换
						if (itemspace.getName().equals(defaultis.getName())) {
							defaultis = itemspace;
							foundDefault = true;
						}
					}

					// 如果没找到默认的，就把这个默认的加到列表里
					if (!foundDefault) {
						tmpItemspaceList.add(defaultis);
					}
				} else {
					tmpItemspaceList.add(defaultis);
				}
			}

			for (ITEMSPACE itemspace : tmpItemspaceList) {
				itemspacesMap.put(itemspace.getName(), itemspace);
			}

			itemspaces = (ITEMSPACE[]) tmpItemspaceList.toArray(new Itemspace[tmpItemspaceList.size()]);
		}

		return itemspaces;
	}

	@Override
	public void close() throws ServiceException {
		if (itemspaces != null) {
			defaultItemspace = null;
			itemspaces = null;
			itemspacesMap.clear();
		}
	}

	@Override
	public FolderItem getDefaultRoot() throws ServiceException {
		return this.getDefaultItemspace().getRootFolder();
	}

	// protected abstract String tidyPath(String path);
	// @Override
	// protected String tidyPath(String path) {
	// path = path.replace('\\', entryConfig.getPathSeparator());
	// if (path.charAt(path.length() - 1) != entryConfig.getPathSeparator()) {
	// path += entryConfig.getPathSeparator();
	// }
	// return path;
	// }

	// @Override
	// public FolderItem parsePath(FolderItem workingDirectory, String path) throws ServiceException {
	// FolderItem dir = workingDirectory == null ? getRoot() : (FolderItem) workingDirectory.getRoot().clone();
	//
	// path = tidyPath(path);
	//
	// ((ItemInnerFunc) dir).setName(URLUtils.getLastNameFromPath(path));
	// ((ItemInnerFunc) dir).setPath(path);
	// if (dir.exists()) {
	// return dir;
	// } else {
	// throw new ServiceException("Path not exist or format incorrect!");
	// }
	// }

	@Override
	public boolean isAvailable() {
		try {
			return getDefaultItemspace().getRootFolder().exists();
		} catch (ServiceException e) {
			return false;
		}
	}

	@Override
	public void test() throws ServiceException {
		if (entryConfig instanceof RemoteEntryConfig) {
			String host = ((RemoteEntryConfig) entryConfig).getHost();
			int port = ((RemoteEntryConfig) entryConfig).getPort();

			if (StringUtils.isNotEmpty(host) && port > 0) {
				try {
					ValidUtils.invalidHostReachable(host, 5000);
				} catch (ConnectException e1) {
					throw new ServiceException(
							"Unable to connect to " + host + ". " + e1.getMessage() + ". Makesure the host name is correct and firewall did not block connection!");
				}

				try {
					ValidUtils.invalidHostConnection(host, port, 5000);
				} catch (ConnectException e1) {
					throw new ServiceException(
							"Host "
									+ host
									+ " is reachable, But unable to connect to port "
									+ port
									+ ". "
									+ e1.getMessage()
									+ ". Makesure the port is correct and firewall did not blocked!");
				} catch (UnknownHostException e1) {
					String hostspath;
					if (SystemUtils.isWindows()) {
						hostspath = "C:\\Windows\\System32\\drivers\\etc\\hosts";
					} else {
						hostspath = "/etc/hosts";
					}
					throw new ServiceException(
							"Host name ["
									+ host
									+ "] unable be resolved, Please check the host name or dns, Also you can append mappings of IP addresses in your system [hosts] file, it's may located at "
									+ hostspath);
				}
			}
		}

		try {
			this.open();

			final List<Throwable> es = new ArrayList<Throwable>();
			final Counter c = new Counter();
			getItemspaces();
			getDefaultItemspace().getRootFolder().list(new ItemHandler() {

				@Override
				public void exceptionCaught(Item data, Throwable e) {
					es.add(e);
				}

				@Override
				public HandleFeedback handle(Integer meta, Item obj) {
					if (c.i++ > 10) {
						return HandleFeedback.interrupted;
					}

					return null;
				}
			});

			if (es.size() != 0) {
				Throwable e = es.get(0);
				if (e instanceof ServiceException) {
					throw (ServiceException) e;
				} else {
					throw new ServiceException(e);
				}
			}
		} finally {
			this.close();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		FileSystemEntryBase a = (FileSystemEntryBase) obj;
		FileSystemEntryBase b = this;

		if (!a.getPluginName().equals(b.getPluginName())) {
			return false;
		}

		if (!a.getRootPath().equals(b.getRootPath())) {
			return false;
		}

		if (!a.getFileSystemType().equals(b.getFileSystemType())) {
			return false;
		}

		if (!a.getEntryConfig().equals(b.getEntryConfig())) {
			return false;
		}

		return true;
	}

	public String getName() {
		return entryConfig.getName();
	}

	public String getRootPath() {
		return entryConfig.getRootPath();
	}

	public String getPluginName() {
		return entryConfig.getFileSystemId();
	}

	public FileSystemType getFileSystemType() {
		return entryConfig.getFileSystemType();
	}

	public CONFIG getEntryConfig() {
		return entryConfig;
	}

	public PREFERENCE getPreference() {
		return preference;
	}

}
