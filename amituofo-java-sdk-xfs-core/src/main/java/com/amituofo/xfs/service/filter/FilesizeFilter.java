package com.amituofo.xfs.service.filter;

import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;

public class FilesizeFilter implements ItemFilter {
	private long size1;
	private long size2;
	private Operator operator;

	public FilesizeFilter(long size, Operator operator) {
		super();
		this.size1 = size;
		this.operator = operator;
	}

	public FilesizeFilter(Long size1, Long size2) {
		super();
		if (size1 != null && size2 != null) {
			this.size1 = size1;
			this.size2 = size2;
			this.operator = Operator.between;
		} else if (size1 == null && size2 != null) {
			this.size1 = size2;
			this.operator = Operator.less;
		} else if (size1 != null && size2 == null) {
			this.size1 = size1;
			this.operator = Operator.greater;
		}
	}

	@Override
	public boolean accept(Item file) {
		if (file.isDirectory()) {
			return false;
		}

		long fileSize = file.getSize();

//		System.out.println("file=" + file + " size=" + fileSize + " min=" + size1 + " max=" + size2 + " operator="+operator);

		switch (operator) {
			case equal:
				return fileSize == size1;
			case greater:
				return fileSize >= size1;
			case less:
				return fileSize <= size1;
			case between:
				return fileSize >= size1 && fileSize <= size2;
			default:
				return false;
		}
	}

}
