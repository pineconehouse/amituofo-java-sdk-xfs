package com.amituofo.xfs.util;

import java.util.Comparator;

import com.amituofo.common.util.FormatUtils;

public class ItemSizeColumn {
	public final static ItemSizeColumn EMPTY = new ItemSizeColumn(null, false);

	private Long size;
	private boolean showSizeInByte = false;
	private ItemStatistic folderStatistic;

	public ItemSizeColumn(Long size) {
		super();
		this.size = size;
	}

	public ItemSizeColumn(Long size, boolean showSizeInByte) {
		super();
		this.size = size;
		this.showSizeInByte = showSizeInByte;
	}

	public ItemSizeColumn(ItemStatistic folderStatistic) {
		if (folderStatistic != null) {
			this.folderStatistic = folderStatistic;
			this.size = folderStatistic.getTotalSize();
		}
	}

	@Override
	public String toString() {
		if (folderStatistic != null) {
			return folderStatistic.toStringTotalCountAndSize(false);

		}
		if (size != null) {
			return FormatUtils.getPrintSize(size, showSizeInByte) + " ";
		}

		return "";
	}

	public final static Comparator COMPARATOR = new Comparator() {

		@Override
		public int compare(Object o1, Object o2) {
			ItemSizeColumn a = (ItemSizeColumn) o1;
			ItemSizeColumn b = (ItemSizeColumn) o2;

			if (a.size == b.size) {
				return 0;
			}

			if (a.size != null && b.size == null) {
				return 1;
			}

			if (a.size == null && b.size != null) {
				return -1;
			}

			if (a.size != null && b.size != null) {
				return a.size.compareTo(b.size);
			}

			return 0;
		}
	};

}
