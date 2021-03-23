package com.amituofo.xfs.plugin.fs.logic.item;

import com.amituofo.xfs.plugin.fs.DelegateFileItem;
import com.amituofo.xfs.service.FileItem;

public class LogicFileItem extends DelegateFileItem {
	private Object attachData;

	public LogicFileItem(FileItem fileItem) {
		super(fileItem);
	}

	public Object getAttachData() {
		return attachData;
	}

	public void setAttachData(Object attachData) {
		this.attachData = attachData;
	}

}
