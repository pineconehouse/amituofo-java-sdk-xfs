package com.amituofo.xfs.service.impl;

import java.io.File;
import java.util.Iterator;

import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemList;
import com.amituofo.xfs.util.ItemUtils;

public class TestCOLPerformance {

	public TestCOLPerformance() {
	}

	public static void main(String[] args) {
		long a = System.currentTimeMillis();
		long b = System.currentTimeMillis();
		final ItemList sourceFileItemList = new MassFileItemList(new File("E:\\TEMP"),"FILES-"+System.currentTimeMillis());
//		final List<FileItem> sourceFileItemList = new ArrayList<FileItem>();
		for (int i = 0; i < 100000; i++) {
			sourceFileItemList.add((FileItem)ItemUtils.toLazyLocalItem(new File("C:\\VDisk\\DriverE\\TEMP\\BKPJS\\WORKSPACE3.7\\wrapper-windows-x86-32-3.5.17-2\\JobScheduler.jar")));
			
			if (i%10000==0) {
				System.out.print("*("+(System.currentTimeMillis() - b)+")");
				b = System.currentTimeMillis();
			}
		}
		System.out.println();
		System.out.println("ok " + (System.currentTimeMillis() - a));
		a = System.currentTimeMillis();

		int i = 0;
//		for (File file : sourceFileItemList) {
//			i++;
//		}
//		
//		i=0;
		for (Iterator iterator = sourceFileItemList.iterator(); iterator.hasNext();) {
			FileItem file = (FileItem) iterator.next();
			i++;
		}

		System.out.println(i + " f " + (System.currentTimeMillis() - a));
	}
	
}
