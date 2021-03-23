package com.amituofo.xfs.util;

import com.amituofo.common.util.DateUtils;
import com.amituofo.common.util.FormatUtils;

public class PerformanceCalculator {

	public static enum PerformanceUnit {
		BITS_PER_SECOND, SIZE, ALL, TOTAL_COUNT, FILE_COUNT// FOLDER_COUNT,
	}

	public PerformanceCalculator() {
	}

	public static String calcPerformance(PerformanceUnit sp, long snapshotTime, ItemStatistic a, ItemStatistic b) {
		long endT = System.currentTimeMillis();
		return calcPerformance(sp, snapshotTime, endT, a, b);
	}

//	public static int calcRemainSecond(PerformanceUnit sp, int usedSecond, FileStatistics total, FileStatistics current) {
//
//		if (usedSecond <= 0) {
//			usedSecond = 1;
//		}
//
//		long totalValue;
//		switch (sp) {
//			case TOTAL_COUNT: {
//				totalValue = total.getTotalCount();
//				break;
//			}
//			case FILE_COUNT: {
//				totalValue = total.getFileCount();
//				break;
//			}
//			case ALL:
//			case BITS_PER_SECOND:
//			case SIZE:
//			default: {
//				totalValue = total.getTotalSize();
//				break;
//			}
//		}
//
//		double performanceValue = calcAveragePerformance(sp, usedSecond, total, current);
//		return (int)(totalValue / performanceValue);
//	}
	
	/**
	 * 计算剩余用时， 剩余处理量 除以用时秒数就是平均性能，剩余量除以平均性能就是剩余时间
	 * @param sp
	 * @param usedSecond
	 * @param total
	 * @param current
	 * @return
	 */
	public static int calcRemainSecond(PerformanceUnit sp, double usedSecond, ItemStatistic total, ItemStatistic current) {

		if (usedSecond <= 0) {
			usedSecond = 1;
		}

		double performanceValue = 1;
		long remainedValue;
		switch (sp) {
			case TOTAL_COUNT: {
				remainedValue =current.getTotalCount();
				double totalCount = Math.abs(total.getTotalCount() - current.getTotalCount());
				performanceValue = (double) (totalCount / usedSecond);
				break;
			}
			case FILE_COUNT: {
				remainedValue =current.getFileCount();
				double fileCount = Math.abs(total.getFileCount() - current.getFileCount());
				performanceValue = (double) (fileCount / usedSecond);
				break;
			}
			case ALL:
			case BITS_PER_SECOND:
			case SIZE:
			default: {
				remainedValue =current.getTotalSize();
				long totalSize = Math.abs(total.getTotalSize() - current.getTotalSize());
//				totalSize = totalSize / 1024 / 1024;
				performanceValue = (double) (totalSize / usedSecond);
//				System.out.println(total.getTotalSize() + "-" + current.getTotalSize() + "  " + totalSize + "/" + usedSecond + "=" + performanceValue + "ps="+(int)(remainedValue / performanceValue));
				break;
			}
		}

		return (int)(remainedValue / performanceValue);
	}

//	public static double calcAveragePerformance(PerformanceUnit sp, int usedSecond, FileStatistics total, FileStatistics current) {
//
//		if (usedSecond <= 0) {
//			usedSecond = 1;
//		}
//
//		double performanceValue = 1;
//
//		switch (sp) {
//			case TOTAL_COUNT: {
//				double totalCount = Math.abs(total.getTotalCount() - current.getTotalCount());
//				performanceValue = (double) (totalCount / usedSecond);
//				break;
//			}
//			case FILE_COUNT: {
//				double fileCount = Math.abs(total.getFileCount() - current.getFileCount());
//				performanceValue = (double) (fileCount / usedSecond);
//				break;
//			}
//			case ALL:
//			case BITS_PER_SECOND:
//			case SIZE:
//			default: {
//				long totalSize = Math.abs(total.getTotalSize() - current.getTotalSize());
////				totalSize = totalSize / 1024 / 1024;
//				performanceValue = (double) (totalSize / usedSecond);
////				System.out.println(total.getTotalSize() + "-" + current.getTotalSize() + "  " + totalSize + "/" + usedSecond + "=" + performanceValue);
//				break;
//			}
//		}
//
//		return performanceValue;
//	}

	public static String calcPerformance(PerformanceUnit sp, long snapshotTime, long endTime, ItemStatistic a, ItemStatistic b) {
		if (a == null || b == null) {
			return "";
		}

		// to second
		double usedSecond = DateUtils.getSecond(endTime, snapshotTime);

		if (usedSecond <= 0) {
			usedSecond = 1;
		}

		String performance = "";
		switch (sp) {
			case ALL: {
				long totalSize = Math.abs(a.getTotalSize() - b.getTotalSize());
				long perBytesSec = (long)(totalSize / usedSecond);
				double totalCount = Math.abs(a.getTotalCount() - b.getTotalCount());
				int countPerSec = (int) (totalCount / usedSecond);
				performance = FormatUtils.getPrintBps(
						perBytesSec) + " | " + FormatUtils.getPrintSize(perBytesSec, false) + "/s | " + FormatUtils.formatNumber(countPerSec) + " file/s";
				break;
			}
			case BITS_PER_SECOND: {
				long totalSize = Math.abs(a.getTotalSize() - b.getTotalSize());
				long perBytesSec = (long)(totalSize / usedSecond);
				performance = FormatUtils.getPrintBps(perBytesSec);
				break;
			}
			case SIZE: {
				long totalSize = Math.abs(a.getTotalSize() - b.getTotalSize());
				long perBytesSec = (long)(totalSize / usedSecond);
				performance = FormatUtils.getPrintSize(perBytesSec, false) + "/s";
				break;
			}
			case TOTAL_COUNT: {
				double totalCount = Math.abs(a.getTotalCount() - b.getTotalCount());
				int countPerSec = (int) (totalCount / usedSecond);
				performance = FormatUtils.formatNumber(countPerSec) + " file/s";
				break;
			}
			case FILE_COUNT: {
				double fileCount = Math.abs(a.getFileCount() - b.getFileCount());
				int countPerSec = (int) (fileCount / usedSecond);
				performance = FormatUtils.formatNumber(countPerSec) + " file/s";
				break;
			}
		}

		return performance;
	}

}
