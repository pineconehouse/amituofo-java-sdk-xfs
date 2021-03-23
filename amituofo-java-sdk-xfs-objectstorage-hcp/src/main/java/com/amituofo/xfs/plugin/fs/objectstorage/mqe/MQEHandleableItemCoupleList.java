package com.amituofo.xfs.plugin.fs.objectstorage.mqe;

import java.util.Iterator;
import java.util.List;

import com.amituofo.common.ex.HSCException;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.item.MQEFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.item.MQEQueryRequest;
import com.amituofo.xfs.service.HandleableItemCouple;
import com.amituofo.xfs.service.ItemList;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;

public class MQEHandleableItemCoupleList implements ItemList<HandleableItemCouple> {

	private MQEQueryRequest request;

	public MQEHandleableItemCoupleList(MQEQueryRequest request) {
		this.request = request;
	}

	@Override
	public void add(HandleableItemCouple item) {
	}

	@Override
	public long size() {
		try {
			return request.queryCount();
		} catch (InvalidResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HSCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public Iterator<HandleableItemCouple> iterator() {
		return new MQEHandleableItemCoupleIterator(request);
	}

	@Override
	public void updateStatus(HandleableItemCouple item, int status) {
	}

	@Override
	public void release() {

	}

	@Override
	public void clear() {
		request.getRequest().resetOffset();
	}

//	@Override
//	public List<HandleableItemCouple> toList() {
//		return null;
//	}

}
