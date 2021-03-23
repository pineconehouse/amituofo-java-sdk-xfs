package com.amituofo.xfs.util;

import java.util.Comparator;

import com.amituofo.xfs.service.ItemType;

public class ItemTypeColumn {
	private ItemType type;
	private String ext;

	public ItemTypeColumn(ItemType type, String ext) {
		super();
		this.type = type;
		if (ext != null)
			this.ext = " " + ext.toUpperCase();
	}

	@Override
	public String toString() {
		if (type == ItemType.Directory) {
			return " Directory";
		}

		return ext;
	}

	public final static Comparator COMPARATOR = new Comparator() {

		@Override
		public int compare(Object o1, Object o2) {
			ItemTypeColumn a = (ItemTypeColumn) o1;
			ItemTypeColumn b = (ItemTypeColumn) o2;

			if (a.type == b.type) {
				if (a.type == ItemType.Directory) {
					// 两边都是目录
					return 0;
				} else {
					// 两边都是文件
					if (a.ext != null && b.ext != null) {
						return a.ext.compareTo(b.ext);
					}
				}

				// return 0;
			}

			if (a.type != b.type) {
				if (a.type == ItemType.Directory) {
					return -1;
				}

				if (b.type == ItemType.Directory) {
					return 1;
				}
			}

			return 0;
		}
	};

}
