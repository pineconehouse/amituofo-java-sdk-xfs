package com.amituofo.xfs.util;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.amituofo.common.util.FormatUtils;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.service.Item;

public class ItemStatistic implements Serializable {
	private final AtomicLong size = new AtomicLong(0);
	private final AtomicInteger fileCount = new AtomicInteger(0);
	private final AtomicInteger folderCount = new AtomicInteger(0);

	private long snapshotTime;
	private ItemStatistic snapshot = null;

	public ItemStatistic() {
	}

	public ItemStatistic(Item item) {
		this.sum(item);
	}

	public ItemStatistic(ItemStatistic summary) {
		this.size.set(summary.getTotalSize());
		this.fileCount.set(summary.getFileCount());
		this.folderCount.set(summary.getFolderCount());
	}

	public void reset(ItemStatistic summary) {
		reset();
		sum(summary);
	}

	public void sum(List<? extends Item> fileItems) {
		if (fileItems != null && fileItems.size() != 0) {
			for (Item fileItem : fileItems) {
				sum(fileItem);
			}
		}
	}

	public void sum(Item[] fileItems) {
		if (fileItems != null && fileItems.length != 0) {
			for (Item fileItem : fileItems) {
				sum(fileItem);
			}
		}
	}

	public void sum(Item item) {
		if (item == null) {
			return;
		}

		if (item.isDirectory()) {
			folderCount.incrementAndGet();
		} else {
			fileCount.incrementAndGet();
		}

		Long filesize = item.getSize();
		if (filesize != null) {
			size.addAndGet(filesize);
		}

	}

	public void sum(File file) {
		if (file == null) {
			return;
		}

		if (file.isDirectory()) {
			folderCount.incrementAndGet();
		} else {
			size.addAndGet(file.length());
			fileCount.incrementAndGet();
		}
	}

	public void sum(ItemStatistic summary) {
		folderCount.addAndGet(summary.getFolderCount());
		size.addAndGet(summary.getTotalSize());
		fileCount.addAndGet(summary.getFileCount());
	}

	public void sumFileCount(int count) {
		fileCount.addAndGet(count);
	}

	public void sumFolderCount(int count) {
		folderCount.addAndGet(count);
	}

	public void sumFileSize(long filesize) {
		size.addAndGet(filesize);
	}

	public void subtract(Item item) {
		if (item.isDirectory()) {
			folderCount.decrementAndGet();
		} else {
			fileCount.decrementAndGet();
		}

		Long filesize = item.getSize();
		if (filesize != null) {
			size.addAndGet(filesize * -1);
		}
	}

	public void subtract(File file) {
		if (file.isDirectory()) {
			folderCount.decrementAndGet();
		} else {
			size.addAndGet(file.length() * -1);
			fileCount.decrementAndGet();
		}
	}

	public void subtract(ItemStatistic summary) {
		folderCount.addAndGet(summary.getFolderCount() * -1);
		size.addAndGet(summary.getTotalSize() * -1);
		fileCount.addAndGet(summary.getFileCount() * -1);
	}

	public void subtractSize(int size) {
		this.size.addAndGet(size * -1);
	}

	public void subtractCount(Item item) {
		if (item.isDirectory()) {
			folderCount.decrementAndGet();
		} else {
			fileCount.decrementAndGet();
		}
	}

	public long getSnapshotTime() {
		return snapshotTime;
	}

	public ItemStatistic snapshot() {
		snapshotTime = System.currentTimeMillis();
		snapshot = new ItemStatistic(this);
		return snapshot;
	}

	public ItemStatistic getSnapshotStatistics() {
		return snapshot;
	}

	public void print(PrintStream print, String head) {
		print.println(head + FormatUtils.formatNumber(fileCount.get()) + " File(s) " + FormatUtils.getPrintSize(size.get(), true));
		print.println(head + FormatUtils.formatNumber(folderCount.get()) + " Directory(s)");
	}

	public String toStringFileCount() {
		return FormatUtils.formatNumber(fileCount.get()) + " File(s) ";
	}

	public String toStringFolderCount() {
		return FormatUtils.formatNumber(folderCount.get()) + " Directory(s) ";
	}

	public String toStringFileCountAndSize(boolean showByteSize) {
		return toStringFileCount() + FormatUtils.getPrintSize(size.get(), showByteSize);
	}

	public String toStringFolderCountAndSize(boolean showByteSize) {
		return toStringFolderCount() + FormatUtils.getPrintSize(size.get(), showByteSize);
	}

	public String toStringTotalCount() {
		int fdc = folderCount.get();
		if (fdc > 0) {
			return toStringFolderCount() + " / " + toStringFileCount();
		} else {
			return toStringFileCount();
		}
	}

	public String toStringSimpleTotalCount() {
		String filecount = FormatUtils.formatNumber(fileCount.get()) + " Files ";

		int fdc = folderCount.get();
		if (fdc > 0) {
			String foldercount = FormatUtils.formatNumber(folderCount.get()) + " Dirs ";
			return foldercount + " / " + filecount;
		} else {
			return filecount;
		}
	}

	public String toStringAligningTotalCount() {
		String filecount = toStringFileCount();

		int fdc = folderCount.get();
		if (fdc > 0) {
			String foldercount = toStringFolderCount();
			int add1 = 19 - filecount.length();
			// int add2 = 24 - foldercount.length();
			// return StringUtils.SPACES[add2] + foldercount + " / " + StringUtils.SPACES[add1] + filecount;

			return foldercount + " / " + StringUtils.SPACES[add1] + filecount;
		} else {
			return filecount;
		}
	}

	public String toStringTotalCountAndSize(boolean showByteSize) {
		// StringBuilder buf = new StringBuilder();
		// int fdc = folderCount.get();
		// if (fdc > 0) {
		// buf.append(FormatUtils.formatNumber(fdc));
		// buf.append(" Directory(s)");
		// buf.append(" / ");
		// }
		//
		// buf.append(FormatUtils.formatNumber(fileCount.get()));
		// buf.append(" File(s) ");
		//
		// // if (fileCount.intValue() > 0) {
		// buf.append(FormatUtils.getPrintSize(size.get(), showByteSize));
		// // }
		// // }
		// // }
		//
		// return buf.toString();
		return toStringTotalCount() + FormatUtils.getPrintSize(size.get(), showByteSize);
	}

	public String toString() {
		return toStringTotalCountAndSize(true);
	}

	public long getTotalSize() {
		return size.get();
	}

	public int getFileCount() {
		return fileCount.get();
	}

	public int getFolderCount() {
		return folderCount.get();
	}

	public int getTotalCount() {
		return fileCount.get() + folderCount.get();
	}

	public void reset() {
		size.set(0);
		fileCount.set(0);
		folderCount.set(0);
	}

	public void subtractFileCount(int count) {
		fileCount.addAndGet(count * -1);
	}

	public void subtractFolderCount(int count) {
		folderCount.addAndGet(count * -1);
	}

}
