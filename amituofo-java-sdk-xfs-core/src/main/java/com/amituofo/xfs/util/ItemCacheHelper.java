package com.amituofo.xfs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.filter.AndFilter;
import com.amituofo.xfs.service.filter.FileTypeFilter;

public class ItemCacheHelper {
	private final Map<String, List<Item>> cache;

	public ItemCacheHelper() {
		this.cache = new HashMap<String, List<Item>>();
	}

	public ItemCacheHelper(Map<String, List<Item>> cache) {
		this.cache = cache;
	}

	// public List<Item> getList(FolderItem folder) {
	// if (folder == null) {
	// return null;
	// }
	//
	// String id = folder.getPath();
	// List<Item> itemlist = cache.get(id);
	//
	// return itemlist;
	// }

	public void listFolder(FolderItem folder, ItemFilter filter, ItemHandler handler) {
		AndFilter andfilter = new AndFilter();
		andfilter.add(filter);
		andfilter.add(new FileTypeFilter(ItemType.Directory));
		list(folder, andfilter, handler);
	}

	public void list(FolderItem folder, ItemFilter filter, final ItemHandler handler) {
		if (folder == null) {
			return;
		}

		String id = toId(folder);
		List<Item> itemlist;
		synchronized (cache) {
			itemlist = cache.get(id);
			if (itemlist == null) {
				itemlist = new ArrayList<Item>();
				final List<Item> tmpitemlist = itemlist;
				cache.put(id, itemlist);

				folder.list(new ItemHandler() {

					@Override
					public void exceptionCaught(Item data, Throwable e) {
						cache.remove(id);
						handler.exceptionCaught(data, e);
					}

					@Override
					public HandleFeedback handle(Integer type, Item data) {
						if (data != null) {
							tmpitemlist.add(data);

							if (filter != null && !filter.accept(data)) {
								return null;
							}
						}

						HandleFeedback feedback = handler.handle(type, data);
						
						if(feedback == HandleFeedback.interrupted) {
							cache.remove(id);
						}
						
						return feedback;
					}
				});
			} else {
				for (Item item : itemlist) {
					if (filter != null && !filter.accept(item)) {
						continue;
					}

					HandleFeedback feedback = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (feedback == HandleFeedback.interrupted) {
						break;
					}
				}
				handler.handle(ItemEvent.EXEC_END, null);
			}
		}
	}

	public void refresh(FolderItem folder, ItemFilter filter, final ItemHandler handler) {
		cache.remove(toId(folder));
		list(folder, filter, handler);
	}

	public void clear(FolderItem folder) {
		cache.remove(toId(folder));
	}

	public void clear() {
		cache.clear();
	}

	private String toId(FolderItem folder) {
		String id = folder.getPath();
		if (id == null || id.length() == 0) {
			id = "-RISON-ROOT-RISON-";
		}
		return id;
	}

}
