package com.amituofo.xfs.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amituofo.common.api.ExceptionHandler;
import com.amituofo.common.api.ObjectHandler;
import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.SystemUtils;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemEntry;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemPreference;
import com.amituofo.xfs.plugin.fs.local.item.LazyLocalFileItem;
import com.amituofo.xfs.plugin.fs.local.item.LazyLocalFolderItem;
import com.amituofo.xfs.plugin.fs.local.item.LocalDriver;
import com.amituofo.xfs.plugin.fs.local.item.LocalFolderItem;
import com.amituofo.xfs.plugin.fs.local.item.LocalItemBase;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.HandleableItemCouple;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemList;

public class ItemUtils {
	public static LocalFileSystemEntry LOCAL_FILE_SYSTEM_ENTRY = (LocalFileSystemEntry) ((LocalFileSystemEntry) LocalFileSystemEntryConfig.DEFAULT_VIEW_DRIVER1
			.createFileSystemEntry()).open();
	public static LocalFileSystemPreference LOCAL_FILE_SYSTEM_PREFERENCE = LOCAL_FILE_SYSTEM_ENTRY.getPreference();

	// public final static ItemFilter defaultFilter = new ItemFilter().withShowHidden(true);
	public final static ItemFilter ALL_FILE_FILTER = new ItemFilter() {

		@Override
		public boolean accept(Item item) {
			return item.isFile();
		}
	};// new ItemFilter().withShowHidden(true).withType(ItemType.File);

	// public static FolderSummary sum(Item item) throws ServiceException {
	// final FolderSummary sum = new FolderSummary();
	//
	// if (item.isFile()) {
	// sum.sum(item);
	// } else if (item.isDirectory()) {
	// sum.sum(item);
	//
	// item.list(defaultFilter, new EventHandler<EventType, Item>() {
	//
	// @Override
	// public EventFeedback statusChanged(EventType eventType, Item item) throws ServiceException {
	// if (EventType.ITEM_FOUND == eventType) {
	// sum.sum(item);
	//
	// if (item.isDirectory()) {
	// sum.sum(sum(item));
	// }
	// }
	//
	// return null;
	// }
	// });
	// }
	//
	// return sum;
	// }

	// public static void listAllFiles(Item item, EventHandler<EventType, Item> handler) throws ServiceException {
	// if (item.isFile()) {
	// handler.statusChanged(EventType.ITEM_FOUND, item);
	// } else if (item.isDirectory()) {
	// listAllFilesInDir(item, handler);
	// }
	// }
	//
	// public static void listAllFilesInDir(Item item, EventHandler<EventType, Item> handler) throws ServiceException {
	//
	// if (item.isDirectory()) {
	// item.list(ALL_FILE_FILTER, new EventHandler<EventType, Item>() {
	//
	// @Override
	// public EventFeedback statusChanged(EventType eventType, Item item) throws ServiceException {
	// if (EventType.ITEM_FOUND == eventType) {
	// if (item.isDirectory()) {
	// listAllFilesInDir(item, handler);
	// } else {
	// handler.statusChanged(EventType.ITEM_FOUND, item);
	// }
	// }
	//
	// return null;
	// }
	// });
	// }
	// }

	public static ItemStatistic sumFiles(Item item) throws ServiceException {
		final ItemStatistic sum = new ItemStatistic();

		if (item.isFile()) {
			sum.sum(item);
		} else if (item.isDirectory()) {
			sum.sum(sumFilesInDir((FolderItem) item));
		}

		return sum;
	}

	public static ItemStatistic sumFilesInDir(FolderItem item) {
		final ItemStatistic sum = new ItemStatistic();

		if (item.isDirectory()) {
			// if(item instanceof LocalFolderItem) {
			// ((LocalFolderItem)item).getOperationPreference().isShowHidden();
			// }
			item.list(ALL_FILE_FILTER, new ItemHandler() {

				@Override
				public HandleFeedback handle(Integer eventType, Item item) {
					if (item != null) {// ItemEvent.ITEM_FOUND == eventType) {
						if (item.isDirectory()) {
							sum.sum(sumFilesInDir((FolderItem) item));
						} else {
							sum.sum(item);
						}
					}

					return null;
				}

				@Override
				public void exceptionCaught(Item data, Throwable e) {

				}
			});
		}

		return sum;
	}

	public static ItemStatistic sumAll(Item item) throws ServiceException {
		final ItemStatistic sum = new ItemStatistic();

		if (item.isFile()) {
			sum.sum(item);
		} else if (item.isDirectory()) {
			// sum.sum(item);

			sum.sum(sumDir((FolderItem) item));
		}

		return sum;
	}

	public static ItemStatistic sumDir(FolderItem item) {
		final ItemStatistic sum = new ItemStatistic();

		if (item.isDirectory()) {
			item.list(new ItemHandler() {

				@Override
				public HandleFeedback handle(Integer eventType, Item item) {
					if (ItemEvent.ITEM_FOUND == eventType) {
						sum.sum(item);

						if (item.isDirectory()) {
							sum.sum(sumDir((FolderItem) item));
						}
					}

					return null;
				}

				@Override
				public void exceptionCaught(Item data, Throwable e) {
				}
			});
		}

		return sum;
	}

	// public static void listAll(Item item, EventHandler<ItemEvent, Item> event) throws ServiceException {
	// if (item.isFile()) {
	// event.statusChanged(ItemEvent.ITEM_FOUND, item);
	// } else if (item.isDirectory()) {
	// ((FolderItem) item).list(null, new EventHandler<ItemEvent, Item>() {
	//
	// @Override
	// public EventFeedback statusChanged(Integer eventType, Item subitem) throws ServiceException {
	// if (ItemEvent.ITEM_FOUND == eventType) {
	// if (subitem.isDirectory()) {
	// listAll(subitem, event);
	// return event.statusChanged(ItemEvent.ITEM_FOUND, subitem);
	// } else {
	// return event.statusChanged(ItemEvent.ITEM_FOUND, subitem);
	// }
	// }
	//
	// return null;
	// }
	// });
	// }
	// }

	public static void listAllItems(Item item, ItemFilter filter, boolean subFolder, ItemHandler handler) {
		if (item.isFile()) {
			if (filter == null) {
				handler.handle(ItemEvent.ITEM_FOUND, item);
			}

			if (filter.accept(item)) {
				handler.handle(ItemEvent.ITEM_FOUND, item);
			}
		} else if (item.isDirectory()) {
			listAllItems((FolderItem) item, filter, subFolder, handler);
		}
	}

	public static void listAllItems(final FolderItem folderItem, final String prefix, final ItemFilter filter, final boolean subFolder, final ItemHandler handler) {
		if (StringUtils.isEmpty(prefix)) {
			listAllItems(folderItem, filter, subFolder, handler);
			return;
		}

		final List<FolderItem> folders = new ArrayList<FolderItem>();
		folderItem.list(new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer event, Item subitem) {
				if (ItemEvent.ITEM_FOUND == event) {
					if (subitem.getPath().startsWith(prefix)) {
						if (subFolder && subitem.isDirectory()) {
							folders.add((FolderItem) subitem);
						}

						if (filter == null) {
							return handler.handle(ItemEvent.ITEM_FOUND, subitem);
						}

						if (filter.accept(subitem)) {
							return handler.handle(ItemEvent.ITEM_FOUND, subitem);
						}
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				handler.exceptionCaught(data, e);
			}
		});

		if (subFolder) {
			for (FolderItem folder : folders) {
				listAllItems(folder, filter, true, handler);
			}
		}
	}

	public static void listAllItems(final FolderItem folderItem, final ItemFilter filter, final boolean subFolder, final ItemHandler handler) {
		final List<FolderItem> folders = new ArrayList<FolderItem>();
		folderItem.list(new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer event, Item subitem) {
				if (ItemEvent.ITEM_FOUND == event) {
					if (subFolder && subitem.isDirectory()) {
						folders.add((FolderItem) subitem);
					}

					if (filter == null) {
						return handler.handle(ItemEvent.ITEM_FOUND, subitem);
					}

					if (filter.accept(subitem)) {
						return handler.handle(ItemEvent.ITEM_FOUND, subitem);
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				handler.exceptionCaught(data, e);
			}
		});

		if (subFolder) {
			for (FolderItem folder : folders) {
				listAllItems(folder, filter, true, handler);
			}
		}
	}

	public static void listAllFileItems(final FolderItem folderItem, final ItemFilter filter, final boolean subFolder, final ItemHandler handler) {
		final List<FolderItem> folders = new ArrayList<FolderItem>();
		folderItem.list(new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer event, Item subitem) {
				if (ItemEvent.ITEM_FOUND == event) {
					if (subFolder && subitem.isDirectory()) {
						folders.add((FolderItem) subitem);
						return null;
					}

					if (filter == null) {
						return handler.handle(ItemEvent.ITEM_FOUND, subitem);
					}

					if (filter.accept(subitem)) {
						return handler.handle(ItemEvent.ITEM_FOUND, subitem);
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				handler.exceptionCaught(data, e);
			}
		});

		if (subFolder) {
			for (FolderItem folder : folders) {
				listAllItems(folder, filter, true, handler);
			}
		}
	}

	// public static void listEachFolderFileItems(final FolderItem folderItem, final ItemFilter filter, final boolean subFolder, final
	// FolderItemsHandler handler, final Interrupter interrupter) {
	// // List<FolderItem> foldersTobeList = new ArrayList<FolderItem>();
	// // final List<FileItem> fileItems = new ArrayList<FileItem>();
	//
	// final List<FolderItem> foldersTobeList = new LinkedList<FolderItem>();
	// final List<FileItem> fileItems = new LinkedList<FileItem>();
	// folderItem.list(new ItemHandler() {
	//
	// @Override
	// public HandleFeedback handle(Integer event, Item subitem) {
	// if (ItemEvent.ITEM_FOUND == event) {
	// if (subitem.isDirectory()) {
	// if (subFolder) {
	// foldersTobeList.add((FolderItem) subitem);
	// }
	// return null;
	// }
	//
	// if (filter == null) {
	// fileItems.add((FileItem) subitem);
	// } else if (filter.accept(subitem)) {
	// fileItems.add((FileItem) subitem);
	// }
	// }
	//
	// if (interrupter != null && interrupter.isInterrupted()) {
	// return HandleFeedback.interrupted;
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public void exceptionCaught(Item data, Throwable e) {
	// handler.exceptionCaught(data, e);
	// }
	// });
	//
	// HandleFeedback feedBack = handler.handle(ItemEvent.ITEM_FOUND, folderItem, fileItems);
	// if (HandleFeedback.interrupted == feedBack || HandleFeedback.failed == feedBack ||(interrupter != null && interrupter.isInterrupted())) {
	// return;
	// }
	//
	// if (subFolder) {
	// for (FolderItem folder : foldersTobeList) {
	// listEachFolderFileItems(folder, filter, true, handler, interrupter);
	// }
	// }
	// }

	// public static void main(String[] x) {
	// FolderItem folder = (FolderItem)ItemUtils.toLazyLocalFolderItem(new
	// File("E:\\WORKSPACE1\\hitachivantara-java-project-fi-express\\release\\Hsergate-linux-x86-64-1.0.21"));
	// ItemUtils.listEachFolderFileItems(folder, null, true, new FolderItemsHandler() {
	//
	// @Override
	// public HandleFeedback handle(ItemEvent event, FolderItem folder, List<FileItem> fileItems) {
	// // TODO Auto-generated method stub
	// System.out.println(folder.getActualPath());
	// for (FileItem file : fileItems) {
	// System.out.println("->"+file.getActualPath());
	// }
	// return null;
	// }
	//
	// @Override
	// public void exceptionCaught(Item data, Throwable e) {
	// // TODO Auto-generated method stub
	//
	// }
	// });
	// }

	public static void listAllFolderItems(final FolderItem folderItem, final ItemFilter filter, final boolean subFolder, final ItemHandler handler) {
		final List<FolderItem> folders = new ArrayList<FolderItem>();
		folderItem.list(new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer event, Item subitem) {
				if (ItemEvent.ITEM_FOUND == event) {
					if (subitem.isDirectory()) {
						if (subFolder) {
							folders.add((FolderItem) subitem);
						}

						if (filter == null) {
							return handler.handle(ItemEvent.ITEM_FOUND, subitem);
						}

						if (filter.accept(subitem)) {
							return handler.handle(ItemEvent.ITEM_FOUND, subitem);
						}
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				handler.exceptionCaught(data, e);
			}
		});

		if (subFolder) {
			for (FolderItem folder : folders) {
				listAllFolderItems(folder, filter, true, handler);
			}
		}
	}

	public static void listItems(Item item, ItemHandler handler) {
		listAllItems(item, null, false, handler);
	}

	public static void listItems(Item item, ItemFilter filter, ItemHandler handler) {
		listAllItems(item, filter, false, handler);
	}

	public static void listAllItems(Item item, ItemHandler handler) {
		listAllItems(item, null, true, handler);
	}

	public static void listAllItems(Item item, ItemFilter filter, ItemHandler handler) {
		listAllItems(item, filter, true, handler);
	}

	public static void listFileItems(Item item, ItemHandler handler) {
		listAllItems(item, ALL_FILE_FILTER, false, handler);
	}

	//
	public static void listItems(FolderItem item, ItemHandler handler) {
		listAllItems(item, null, false, handler);
	}

	public static void listItems(FolderItem item, ItemFilter filter, ItemHandler handler) {
		listAllItems(item, filter, false, handler);
	}

	public static void listAllItems(FolderItem item, ItemHandler handler) {
		listAllItems(item, null, true, handler);
	}

	public static void listAllItems(FolderItem item, ItemFilter filter, ItemHandler handler) {
		listAllItems(item, filter, true, handler);
	}

	public static void listFileItems(FolderItem item, ItemHandler handler) {
		listAllItems(item, ALL_FILE_FILTER, false, handler);
	}

	// public static void listItems(Item item, ItemFilter filter, boolean subFolder, ItemHandler handler) {
	// if (item.isFile()) {
	// handler.handle(ItemEvent.ITEM_FOUND, item);
	// } else if (item.isDirectory()) {
	// ((FolderItem) item).list(filter, new ItemHandler() {
	//
	// @Override
	// public HandleFeedback handle(Integer eventType, Item subitem) {
	// if (ItemEvent.ITEM_FOUND == eventType) {
	// if (subFolder && subitem.isDirectory()) {
	// listItems(subitem, filter, subFolder, handler);
	// } else {
	// handler.handle(ItemEvent.ITEM_FOUND, subitem);
	// }
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public void exceptionCaught(Item data, Throwable e) {
	// handler.exceptionCaught(data, e);
	// }
	// });
	// }
	// }
	//
	// public static void listAllFile(Item item, ItemHandler event) {
	// listItems(item, ALL_FILE_FILTER, event);
	// }

	// public static void listFile(FolderItem item, ItemFilter filter, ItemHandler event) {
	// if (item.isFile()) {
	// event.handle(ItemEvent.ITEM_FOUND, item);
	// } else if (item.isDirectory()) {
	// ((FolderItem) item).list(filter, new ItemHandler() {
	//
	// @Override
	// public HandleFeedback handle(Integer eventType, Item subitem) {
	// if (ItemEvent.ITEM_FOUND == eventType) {
	// if (subitem.isFile()) {
	// event.handle(ItemEvent.ITEM_FOUND, subitem);
	// }
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public void exceptionCaught(Item data, Throwable e) {
	// event.exceptionCaught(data, e);
	// }
	// });
	// }
	// }
	//
	// public static void listFile(Item item, ItemHandler event) {
	// if (item.isFile()) {
	// event.handle(ItemEvent.ITEM_FOUND, item);
	// } else if (item.isDirectory()) {
	// ((FolderItem) item).list(ALL_FILE_FILTER, new ItemHandler() {
	//
	// @Override
	// public HandleFeedback handle(Integer eventType, Item subitem) {
	// if (ItemEvent.ITEM_FOUND == eventType) {
	// if (subitem.isFile()) {
	// event.handle(ItemEvent.ITEM_FOUND, subitem);
	// }
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public void exceptionCaught(Item data, Throwable e) {
	// event.exceptionCaught(data, e);
	// }
	// });
	// }
	// }

	public static Item getFirstFile(Item[] items) {
		if (items != null && items.length > 0) {
			for (Item item : items) {
				if (item.isFile()) {
					return item;
				}
			}
		}

		return null;
	}

	public static Item getFirstFile(List<Item> items) {
		if (items != null && items.size() > 0) {
			for (Item item : items) {
				if (item.isFile()) {
					return item;
				}
			}
		}

		return null;
	}

	public static FolderItem getFirstFolder(List<Item> items) {
		if (items != null && items.size() > 0) {
			for (Item item : items) {
				if (item.isDirectory()) {
					return (FolderItem) item;
				}
			}
		}

		return null;
	}

	public static boolean isOnly1Folder(List<? extends Item> selectedItems) {
		if (selectedItems.size() == 1) {
			Item selectedItem = selectedItems.get(0);
			if (selectedItem.isDirectory()) {
				return true;
			}
		}

		return false;
	}

	public static boolean isEmpty(List<? extends Item> selectedItems) {
		return selectedItems == null || selectedItems.size() == 0;
	}

	public static boolean isOnlyFoldes(List<? extends Item> selectedItems) {
		for (Item item : selectedItems) {
			if (item.isFile()) {
				return false;
			}
		}

		return true;
	}

	public static boolean isOnlyFiles(List<? extends Item> selectedItems) {
		for (Item item : selectedItems) {
			if (item.isDirectory()) {
				return false;
			}
		}

		return true;
	}

	public static void statisticsItemCouple(FileItem srcItem, ItemList<HandleableItemCouple> fileItems, ItemStatistic totalFound) {
		// 统计需要删除的项目数量
		// if (srcItem.isFile()) {
		synchronized (fileItems) {
			fileItems.add(new HandleableItemCouple((FileItem) srcItem));
			totalFound.sum(srcItem);
		}
		// }
	}

	public static void statisticsItemCouple(final FolderItem folderItem,
			final ItemList<HandleableItemCouple> fileItems,
			final List<FolderItem> folderItems,
			final ItemStatistic totalFound,
			final AbortHandler abortHandler,
			final ExceptionHandler<Item> exHandler) {

		synchronized (folderItems) {
			folderItems.add(folderItem);
		}

		// 遍历目录或文件
		ItemUtils.listAllItems(folderItem, new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (abortHandler.isAbort()) {
					return HandleFeedback.interrupted;
				}

				if (eventType == ItemEvent.ITEM_FOUND) {
					if (data.isDirectory()) {
						synchronized (folderItems) {
							folderItems.add((FolderItem) data);
						}
					} else {
						synchronized (fileItems) {
							fileItems.add(new HandleableItemCouple((FileItem) data));
							// for (int i = 0; i < 1000000; i++) {
							// fileItems.add(new HandleableItemCouple((FileItem) data));
							// }
						}
					}

					synchronized (totalFound) {
						totalFound.sum(data);
						// for (int i = 0; i < 1000000; i++) {
						// totalFound.sum(data);
						// }
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				if (exHandler != null) {
					exHandler.exceptionCaught(data, e);
				}
			}
		});

		// 统计这个目录本身
		synchronized (totalFound) {
			totalFound.sum(folderItem);
		}
	}

	public static void statisticsItemCouple(final FolderItem folderItem,
			final ItemList<HandleableItemCouple> fileItems,
			final ItemStatistic totalFound,
			final AbortHandler abortHandler,
			final ExceptionHandler<Item> exHandler) {

		// 遍历目录或文件
		ItemUtils.listAllItems(folderItem, new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (abortHandler.isAbort()) {
					return HandleFeedback.interrupted;
				}

				if (eventType == ItemEvent.ITEM_FOUND) {
					if (data.isDirectory()) {
					} else {
						synchronized (fileItems) {
							fileItems.add(new HandleableItemCouple((FileItem) data));
						}
					}

					synchronized (totalFound) {
						totalFound.sum(data);
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				if (exHandler != null) {
					exHandler.exceptionCaught(data, e);
				}
			}
		});

		// 统计这个目录本身
		synchronized (totalFound) {
			totalFound.sum(folderItem);
		}
	}

	public static void statistics(FileItem srcItem, ItemList<FileItem> fileItems, ItemStatistic totalFound) {
		// 统计需要删除的项目数量
		// if (srcItem.isFile()) {
		synchronized (fileItems) {
			fileItems.add((FileItem) srcItem);
			totalFound.sum(srcItem);
		}
		// }
	}

	public static void statistics(final FolderItem folderItem,
			final ItemList<FileItem> fileItems,
			final List<FolderItem> folderItems,
			final ItemStatistic totalFound,
			final AbortHandler abortHandler,
			final ExceptionHandler<Item> exHandler) {

		if (folderItems != null) {
			synchronized (folderItems) {
				folderItems.add(folderItem);
			}
		}

		// 遍历目录或文件
		ItemUtils.listAllItems(folderItem, new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (abortHandler.isAbort()) {
					return HandleFeedback.interrupted;
				}

				if (eventType == ItemEvent.ITEM_FOUND) {
					if (data.isDirectory() && folderItems != null) {
						synchronized (folderItems) {
							folderItems.add((FolderItem) data);
						}
					} else {
						synchronized (fileItems) {
							fileItems.add((FileItem) data);
						}
					}

					synchronized (totalFound) {
						totalFound.sum(data);
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				if (exHandler != null) {
					exHandler.exceptionCaught(data, e);
				}
			}
		});

		// 统计这个目录本身
		synchronized (totalFound) {
			totalFound.sum(folderItem);
		}
	}

	public static void statistics(Item srcItem, final ItemList fileItems, ItemStatistic totalFound, AbortHandler abortHandler, ExceptionHandler<Item> exHandler) {

		// 统计需要删除的项目数量
		if (srcItem.isFile()) {
			synchronized (fileItems) {
				fileItems.add((FileItem) srcItem);
				totalFound.sum(srcItem);
			}
		} else {
			// 遍历目录或文件
			statistics((FolderItem) srcItem, fileItems, totalFound, abortHandler, exHandler);
			// ItemUtils.listAll(srcItem, new EventHandler<EventType, Item>() {
			//
			// @Override
			// public EventFeedback statusChanged(EventType eventType, Item data) throws ServiceException {
			// if (eventType == EventType.ITEM_FOUND) {
			// if (data.isFile()) {
			// synchronized (fileItems) {
			// fileItems.add((FileItem)data);
			// }
			// }
			//
			// synchronized (totalFound) {
			// totalFound.sum(data);
			// }
			// }
			//
			// return null;
			// }
			// });
			//
			// // 统计这个目录本身
			// synchronized (totalFound) {
			// totalFound.sum(srcItem);
			// }
		}
	}

	public static void statistics(final List<Item> fileItems, boolean statisticsFolder, final ItemStatistic totalFound, final ExceptionHandler<Item> exHandler) {

		// 统计项目
		for (Item fileItem : fileItems) {

			totalFound.sum(fileItem);

			if (statisticsFolder && fileItem.isDirectory()) {
				// 遍历目录或文件
				ItemUtils.listAllItems((FolderItem) fileItem, new ItemHandler() {

					@Override
					public HandleFeedback handle(Integer eventType, Item data) {
						if (eventType == ItemEvent.ITEM_FOUND) {
							// if (data.isFile()) {
							// }
							synchronized (totalFound) {
								totalFound.sum(data);
							}
						}

						return null;
					}

					@Override
					public void exceptionCaught(Item data, Throwable e) {
						if (exHandler != null) {
							exHandler.exceptionCaught(data, e);
						}
					}
				});
			}
		}
	}

	public static void statistics(Item srcItem, final List<FileItem> fileItems, final ItemStatistic totalFound, final ExceptionHandler<Item> exHandler) {

		// 统计需要删除的项目数量
		if (srcItem.isFile()) {
			synchronized (fileItems) {
				fileItems.add((FileItem) srcItem);
				totalFound.sum(srcItem);
			}
		} else {
			// 遍历目录或文件
			ItemUtils.listAllItems((FolderItem) srcItem, new ItemHandler() {

				@Override
				public HandleFeedback handle(Integer eventType, Item data) {
					if (eventType == ItemEvent.ITEM_FOUND) {
						if (data.isFile()) {
							synchronized (fileItems) {
								fileItems.add((FileItem) data);
							}
						}

						synchronized (totalFound) {
							totalFound.sum(data);
						}
					}

					return null;
				}

				@Override
				public void exceptionCaught(Item data, Throwable e) {
					if (exHandler != null) {
						exHandler.exceptionCaught(data, e);
					}
				}
			});

			// 统计这个目录本身
			synchronized (totalFound) {
				totalFound.sum(srcItem);
			}
		}
	}

	public static ItemStatistic statistics(final Item[] srcItems, final ObjectHandler<Integer, ItemStatistic> handler) {
		final ItemStatistic summary = new ItemStatistic();
		// 统计需要删除的项目数量
		for (Item srcItem : srcItems) {
			if (srcItem.isFile()) {
				summary.sum(srcItem);
				HandleFeedback feedBack = handler.handle(ItemEvent.VALUE_CHANGED, summary);
				if (feedBack == HandleFeedback.interrupted) {
					return summary;
				}
			} else {
				// 遍历目录或文件
				ItemUtils.listAllItems((FolderItem) srcItem, new ItemHandler() {

					@Override
					public HandleFeedback handle(Integer eventType, Item data) {
						if (eventType == ItemEvent.ITEM_FOUND) {
							summary.sum(data);
							return handler.handle(ItemEvent.VALUE_CHANGED, summary);
						}

						return null;
					}

					@Override
					public void exceptionCaught(Item data, Throwable e) {
						handler.exceptionCaught(summary, e);
					}
				});

				// 统计这个目录本身
				summary.sum(srcItem);
				HandleFeedback feedBack = handler.handle(ItemEvent.VALUE_CHANGED, summary);
				if (feedBack == HandleFeedback.interrupted) {
					return summary;
				}
			}
		}

		handler.handle(ItemEvent.EXEC_END, summary);
		return summary;
	}

	public static ItemStatistic statisticsAndUpdateFolderSize(final Item[] items,
			final ObjectHandler<Integer, ItemStatistic> eachItemHandler,
			final ObjectHandler<FolderItem, ItemStatistic> folderItemHandler) {
		final ItemStatistic allSummary = new ItemStatistic();

		for (Item item : items) {
			if (item.isDirectory()) {
				statisticsAndUpdateFolderSize((FolderItem) item, allSummary, eachItemHandler, folderItemHandler);
			} else {
				allSummary.sum(item);
				eachItemHandler.handle(ItemEvent.VALUE_CHANGED, allSummary);
			}
		}

		eachItemHandler.handle(ItemEvent.EXEC_END, allSummary);
		return allSummary;
	}

	public static ItemStatistic statisticsAndUpdateFolderSize(final FolderItem folderItem,
			final ItemStatistic totalItemSummary,
			final ObjectHandler<Integer, ItemStatistic> eachItemHandler,
			final ObjectHandler<FolderItem, ItemStatistic> folderItemHandler) {
		final ItemStatistic thisFolderSummary = new ItemStatistic();
		folderItem.list(new ItemHandler() {
			@Override
			public HandleFeedback handle(Integer event, Item item) {
				if (ItemEvent.ITEM_FOUND == event) {
					totalItemSummary.sum(item);
					HandleFeedback continuelist = eachItemHandler.handle(ItemEvent.VALUE_CHANGED, totalItemSummary);
					thisFolderSummary.sum(item);
					if (item.isDirectory()) {
						ItemStatistic subFolderSummary = statisticsAndUpdateFolderSize((FolderItem) item, totalItemSummary, eachItemHandler, folderItemHandler);
						if (subFolderSummary == null) {
							return HandleFeedback.interrupted;
						}
						thisFolderSummary.sum(subFolderSummary);
					}
					return continuelist;
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				eachItemHandler.exceptionCaught(thisFolderSummary, e);
			}
		});

		((ItemHiddenFunction) folderItem).setSize(thisFolderSummary.getTotalSize());
		HandleFeedback feedback = folderItemHandler.handle(folderItem, thisFolderSummary);
		if (feedback == HandleFeedback.interrupted) {
			return null;
		}
		return thisFolderSummary;
	}

	public static void statisticsAndUpdateFolderSize(final FolderItem[] items, final ObjectHandler<FolderItem, ItemStatistic> folderItemHandler) {
		for (FolderItem item : items) {
			statisticsAndUpdateFolderSize((FolderItem) item, folderItemHandler);
		}
	}

	public static void statisticsAndUpdateFolderSize(final List<FolderItem> items, final ObjectHandler<FolderItem, ItemStatistic> folderItemHandler) {
		for (FolderItem item : items) {
			statisticsAndUpdateFolderSize((FolderItem) item, folderItemHandler);
		}
	}

	public static ItemStatistic statisticsAndUpdateFolderSize(final FolderItem folderItem, final ObjectHandler<FolderItem, ItemStatistic> folderItemHandler) {
		final ItemStatistic thisFolderSummary = new ItemStatistic();
		folderItem.list(new ItemHandler() {
			@Override
			public HandleFeedback handle(Integer event, Item item) {
				if (ItemEvent.ITEM_FOUND == event) {
					thisFolderSummary.sum(item);
					if (item.isDirectory()) {
						ItemStatistic subFolderSummary = statisticsAndUpdateFolderSize((FolderItem) item, folderItemHandler);
						if (subFolderSummary == null) {
							return HandleFeedback.interrupted;
						}
						thisFolderSummary.sum(subFolderSummary);
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
			}
		});

		((ItemHiddenFunction) folderItem).setSize(thisFolderSummary.getTotalSize());
		HandleFeedback feedback = folderItemHandler.handle(folderItem, thisFolderSummary);
		if (feedback == HandleFeedback.interrupted) {
			return null;
		}
		return thisFolderSummary;
	}

	// public static RootItem toSection(String name) {
	// return new DefaultSection(name);
	// }

	// public static RootItem toSection(File file, boolean isRoot) {
	// if (isRoot) {
	// return new DefaultSection(file.getPath());
	// }
	//
	// if (SystemUtils.isWindows()) {
	// String path = file.getPath();
	// if (path.length() > 2) {
	// return new DefaultSection(path.substring(0, 3));
	// } else {
	// return new DefaultSection(path);
	// }
	// } else {
	// return new DefaultSection("/");
	// }
	// }

	public static File getFileRoot(File file) {
		if (SystemUtils.isWindows()) {
			String path = file.getPath();
			if (path.length() > 2) {
				return new File(path.substring(0, 3));
			} else {
				return new File(path);
			}
		} else {
			return new File("/");
		}
	}

	public static Item toLazyLocalItem(File file) {
		if (file == null) {
			return null;
		}

		Item item;
		if (file.isDirectory()) {
			item = toLazyLocalFolderItem(file);
		} else {
			item = toLazyLocalFileItem(file);
		}

		return item;
	}

	public static FileItem toLazyLocalFileItem(File file) {
		if (file == null) {
			return null;
		}

		File rootFile = getFileRoot(file);
		LocalDriver driver = new LocalDriver(LOCAL_FILE_SYSTEM_ENTRY, LOCAL_FILE_SYSTEM_PREFERENCE, rootFile);
		FileItem item = new LazyLocalFileItem(driver, file);

		return item;
	}

	public static FileItem toLazyLocalFileItem(String filepath) {
		if (filepath == null) {
			return null;
		}
		File file = new File(filepath);

		return toLazyLocalFileItem(file);
	}

	public static FolderItem toLazyLocalFolderItem(String filepath) {
		return toLazyLocalFolderItem(new File(filepath));
	}

	public static FolderItem toLazyLocalFolderItem(File file) {
		if (file == null) {
			return null;
		}
		File rootFile = getFileRoot(file);
		LocalDriver driver = new LocalDriver(LOCAL_FILE_SYSTEM_ENTRY, LOCAL_FILE_SYSTEM_PREFERENCE, rootFile);
		FolderItem item = new LazyLocalFolderItem(driver, file);

		return item;
	}

	// public static Item toLocalItem(File file) {
	// if (file == null) {
	// return null;
	// }
	//
	// LocalItemBase item;
	// if (file.isDirectory()) {
	// item = new LocalFolderItem(file.getPath(), FileSystemUtils.LOCAL_FILE_SYSTEM_PREFERENCE);
	// } else {
	// item = new LocalFileItem(file, FileSystemUtils.LOCAL_FILE_SYSTEM_PREFERENCE);
	// }
	//
	// return item;
	// }

	// public static FileItem targetFile(FileItem sourceFileItem) {
	// // 计算并生成目标文件路径
	// // String targetFolderPath = source.getParent().getActualPath().substring(cutpoint);
	// String srcItemPath = sourceFileItem.getActualPath();
	//
	// String targetFolderPath;
	// int endIndex = srcItemPath.length() - sourceFileItem.getName().length();
	// if (endIndex >= cutpoint) {
	// targetFolderPath = srcItemPath.substring(cutpoint, endIndex);
	// } else {
	// targetFolderPath = "";
	// }
	// targetFolderPath = targetFolderPath.replace(sourceFileItem.getPathSeparator(), targetWorkingFolderItem.getPathSeparator());
	// FolderItem targetFolderItem = (FolderItem) targetWorkingFolderItem.clone();
	// targetFolderItem.setActualPath(URLUtils.catPath(baseTargetFolder, targetFolderPath, targetWorkingFolderItem.getPathSeparator()));
	//
	// }

	// public static FolderItem mappingFolderPath(int sourcePathCutpoint, FolderItem sourceFolderItem, String targetHome, FolderItem
	// targetWorkingFolderItem) {
	// // 计算并生成目标文件路径
	// // String targetFolderPath = source.getParent().getActualPath().substring(cutpoint);
	// String srcItemPath = sourceFolderItem.getPath();
	//
	// String targetFolderPath = srcItemPath.substring(sourcePathCutpoint);
	//
	// targetFolderPath = targetFolderPath.replace(sourceFolderItem.getPathSeparator(), targetWorkingFolderItem.getPathSeparator());
	// FolderItem targetFolderItem = (FolderItem) targetWorkingFolderItem.clone();
	// ((ItemInnerFunc)targetFolderItem).setPath(URLUtils.catPath(targetHome, targetFolderPath, targetWorkingFolderItem.getPathSeparator()));
	//
	// return targetFolderItem;
	// }

	// public static FolderItem mappingFolderPath(int sourcePathCutpoint, FileItem sourceFileItem, String targetHome, FolderItem
	// targetWorkingFolderItem) {
	// // 计算并生成目标文件路径
	// // String targetFolderPath = source.getParent().getActualPath().substring(cutpoint);
	// String srcItemPath = sourceFileItem.getPath();
	//
	// String targetFolderPath;
	// int endIndex = srcItemPath.length() - sourceFileItem.getName().length();
	// if (endIndex >= sourcePathCutpoint) {
	// targetFolderPath = srcItemPath.substring(sourcePathCutpoint, endIndex);
	// } else {
	// targetFolderPath = "";
	// }
	//
	// targetFolderPath = targetFolderPath.replace(sourceFileItem.getPathSeparator(), targetWorkingFolderItem.getPathSeparator());
	// FolderItem targetFolderItem = (FolderItem) targetWorkingFolderItem.clone();
	// ((ItemInnerFunc)targetFolderItem).setPath(URLUtils.catPath(targetHome, targetFolderPath, targetWorkingFolderItem.getPathSeparator()));
	//
	// return targetFolderItem;
	// }

	// public static FileItem mappingFileItem(int sourcePathCutpoint, FileItem sourceFileItem, FolderItem targetWorkingFolderItem) throws
	// ServiceException {
	// // 计算并生成目标文件路径
	// // String targetFolderPath = source.getParent().getActualPath().substring(cutpoint);
	// String srcItemPath = sourceFileItem.getPath();
	//
	// String targetItemPath = srcItemPath.substring(sourcePathCutpoint);
	//
	// targetItemPath = targetItemPath.replace(sourceFileItem.getPathSeparator(), targetWorkingFolderItem.getPathSeparator());
	//
	// return targetWorkingFolderItem.linkFile(targetItemPath);
	// }

	public static void copyToSystemClipboard(final Item[] items, ObjectHandler<List<File>, Item> transferToLocalFile) {
		if (items == null || items.length == 0) {
			return;
		}

		// if (localTempFolder == null || localTempFolder.isFile()) {
		// localTempFolder = new File(System.getProperty("java.io.tmpdir"));
		// }

		final List<File> files = new ArrayList<File>();
		for (Item item : items) {
			if (item instanceof LocalItemBase) {
				files.add(((LocalItemBase) item).getFile());
			} else {
				if (transferToLocalFile != null) {
					HandleFeedback feedback = transferToLocalFile.handle(files, item);
					if (HandleFeedback.interrupted == feedback) {
						return;
					}
				}
			}
		}

		if (files.size() == 0) {
			return;
		}

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		Transferable contents = new Transferable() {
			final DataFlavor[] dataFlavors = new DataFlavor[] { DataFlavor.javaFileListFlavor };

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				return files;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return dataFlavors;
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				for (int i = 0; i < dataFlavors.length; i++) {
					if (dataFlavors[i].equals(flavor)) {
						return true;
					}
				}
				return false;
			}
		};

		clipboard.setContents(contents, null);
	}

	// https://docs.oracle.com/javase/jndi/tutorial/objects/storing/remote.html
	public static List<Item> getItemsFromSystemClipboard() throws UnsupportedFlavorException, IOException {
		List<Item> items = new ArrayList<Item>();
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable t = clipboard.getContents(null); // 获取粘贴板内数据传输对象
		if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {// 类型是否匹配为文件
			List<File> filelist = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);// 拿出粘贴板内文件对象列表
			for (int i = 0; i < filelist.size(); i++) { // 遍历文件列表并复制
				File file = filelist.get(i);
				if (file.canRead()) {
					Item item = ItemUtils.toLazyLocalItem(file);
					items.add(item);
				}
			}
		}
		return items;
	}

	public static Map<String, List<Item>> getDuplicateNameItems(Item[] items, List<Item> withoutDuplicateItems) {
		Map<String, Item> itemMap = new HashMap<String, Item>();
		Map<String, List<Item>> duplicateItemMap = new HashMap<String, List<Item>>();
		for (Item item : items) {
			String name = item.getName().toLowerCase();
			if (itemMap.containsKey(name)) {
				List<Item> duplicateItems = duplicateItemMap.get(name);
				if (duplicateItems == null) {
					duplicateItems = new ArrayList<Item>();
					duplicateItemMap.put(name, duplicateItems);
					duplicateItems.add(itemMap.get(name));
				}
				duplicateItems.add(item);
			} else {
				itemMap.put(name, item);
				if (withoutDuplicateItems != null) {
					withoutDuplicateItems.add(item);
				}
			}
		}

		return duplicateItemMap;
	}

	public static ItemFilter createSkipHiddenLocalFolderFilter(Item sample) {
		if (sample != null && sample instanceof LocalFolderItem) {
			return new ItemFilter() {

				@Override
				public boolean accept(Item item) {
					if (item == null) {
						return false;
					}

					File file = ((LocalItemBase) item).getFile();
					return !file.isHidden();
				}
			};
		}
		return null;
	}

	public static boolean containsFolderItem(Item[] items) {
		if (items == null || items.length == 0) {
			return false;
		}
		for (Item item : items) {
			if (item.isDirectory()) {
				return true;
			}
		}
		return false;
	}

	public static String toSummaryItemListString(Item[] items, int max) {
		String showItemsMsg = "";
		if (items != null && items.length != 0) {
			int count = Math.min(items.length, max);
			// showItemsMsg = "\n";
			for (int i = 0; i < count; i++) {
				Item item = items[i];
				showItemsMsg += "   " + (i + 1) + ") " + item.getPath() + "\n";
			}

			if (items.length > count) {
				showItemsMsg += "   " + " ...\n";
			}
		}

		return showItemsMsg;
	}

	public static List<Item> toList(FileItem item) {
		List<Item> items = new ArrayList<Item>();
		items.add(item);
		return items;
	}

	public static List<FolderItem> getFolderItems(List<Item> selectedItems) {
		List<FolderItem> folders = new ArrayList<FolderItem>();
		for (Item item : selectedItems) {
			if (item instanceof FolderItem) {
				folders.add((FolderItem) item);
			}
		}
		return folders;
	}

	public static List<FolderItem> getFolderItems(Item[] selectedItems) {
		List<FolderItem> folders = new ArrayList<FolderItem>();
		for (Item item : selectedItems) {
			if (item instanceof FolderItem) {
				folders.add((FolderItem) item);
			}
		}
		return folders;
	}

	// public static Item[] toArray(List<OSDVersionFileItem> itemlist) {
	// if(itemlist!=null) {
	// Item[] items = new Item[itemlist.size()];
	// }
	// // TODO Auto-generated method stub
	// return null;
	// }

}
