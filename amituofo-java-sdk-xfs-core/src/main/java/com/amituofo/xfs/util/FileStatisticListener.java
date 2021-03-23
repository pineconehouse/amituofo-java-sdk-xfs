package com.amituofo.xfs.util;

public interface FileStatisticListener {
	void setDefaultItemStatistics(ItemStatistic statistics);

	void updateStatistics();
}
