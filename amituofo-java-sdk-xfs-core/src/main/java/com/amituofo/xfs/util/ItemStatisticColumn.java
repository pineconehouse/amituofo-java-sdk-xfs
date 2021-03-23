package com.amituofo.xfs.util;

import java.util.Comparator;

import com.amituofo.common.util.FormatUtils;

public class ItemStatisticColumn {
	private boolean showSizeInByte = false;
	private ItemStatistic folderStatistic;

	public ItemStatisticColumn(boolean showSizeInByte) {
		super();
		this.showSizeInByte = showSizeInByte;
	}

	public ItemStatisticColumn(ItemStatistic folderStatistic) {
		if (folderStatistic != null) {
			this.folderStatistic = folderStatistic;
		}
	}

	@Override
	public String toString() {
		if (folderStatistic != null) {
			return folderStatistic.toStringSimpleTotalCount();
		}

		return "";
	}

	public final static Comparator COMPARATOR = new Comparator() {

		@Override
		public int compare(Object o1, Object o2) {
			ItemStatisticColumn a = (ItemStatisticColumn) o1;
			ItemStatisticColumn b = (ItemStatisticColumn) o2;

			if (a.folderStatistic == b.folderStatistic) {
				return 0;
			}

			if (a.folderStatistic != null && b.folderStatistic == null) {
				return 1;
			}

			if (a.folderStatistic == null && b.folderStatistic != null) {
				return -1;
			}

			return new Long(a.folderStatistic.getFileCount()).compareTo(new Long(b.folderStatistic.getFileCount()));
		}
	};

}
