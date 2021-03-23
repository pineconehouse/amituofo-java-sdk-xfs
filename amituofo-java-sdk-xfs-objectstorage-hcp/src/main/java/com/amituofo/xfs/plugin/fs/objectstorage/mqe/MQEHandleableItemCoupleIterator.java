package com.amituofo.xfs.plugin.fs.objectstorage.mqe;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.item.MQEFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.item.MQEQueryRequest;
import com.amituofo.xfs.service.HandleableItemCouple;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.query.define.Operation;
import com.hitachivantara.hcp.query.model.ObjectQueryResult;
import com.hitachivantara.hcp.query.model.ObjectSummary;
import com.hitachivantara.hcp.query.model.QueryResult;
import com.hitachivantara.hcp.query.model.request.ObjectBasedQueryRequest;
import com.hitachivantara.hcp.query.model.request.QueryRequest;

public class MQEHandleableItemCoupleIterator implements Iterator<HandleableItemCouple> {
	private QueryRequest queryRequest;
	private HCPQuery hcpQueryClient;
	private ObjectQueryResult result;
	private HCPFileSystemEntry hcpEntry;
	private Queue<HandleableItemCouple> page = new ArrayDeque<HandleableItemCouple>();

	public MQEHandleableItemCoupleIterator(MQEQueryRequest request) {
		this.hcpQueryClient = request.getHcpQueryClient();
		this.queryRequest = request.getRequest();
		this.hcpEntry = request.getHcpEntry();
		queryRequest.resetOffset();
	}

	@Override
	public boolean hasNext() {
		if (page.size() > 0) {
			return true;
		}

		try {
			ObjectBasedQueryRequest basedQueryRequest = (ObjectBasedQueryRequest) queryRequest;
			if (result == null) {
				// 触发搜索
				result = hcpQueryClient.query(basedQueryRequest);

				// 处理搜索结果
				handleResult(result);
			} else {
				if (result.isIncomplete()) {
					result = hcpQueryClient.query(basedQueryRequest.withNextPage());

					// 处理搜索结果
					handleResult(result);
				}
			}
		} catch (InvalidResponseException e) {
			e.printStackTrace();
		} catch (HSCException e) {
			e.printStackTrace();
		}

		return page.size() > 0;
	}

	@Override
	public HandleableItemCouple next() {
		HandleableItemCouple item = page.poll();
		return item;
	}

	private HandleFeedback handleResult(final QueryResult result) {
		List<ObjectSummary> list = result.getResults();
		for (int i = 0; i < list.size(); i++) {
			ObjectSummary summary = list.get(i);
			
//			switch(summary.getOperation()) {
//				case NOT_FOUND:
//				case DELETED:
//				case PURGED:
//				case DISPOSED:
//				continue;
//			}
			
			if(summary.getOperation()!=Operation.CREATED) {
				continue;
			}

			HCPBucketspace namespace;

			String nsname = summary.getNamespace();
			// System.out.println(nsname + "\t" + summary.getKey());
			if (StringUtils.isEmpty(nsname)) {
				namespace = (HCPBucketspace) hcpEntry.getDefaultItemspace();
			} else {
				int tenantIndex = nsname.indexOf('.');
				if (tenantIndex > 0) {
					nsname = nsname.substring(0, tenantIndex);
				}

				namespace = (HCPBucketspace) hcpEntry.getItemspace(nsname);
			}

			MQEFileItem item = new MQEFileItem(namespace, summary.getKey());
			HCPFileItem.setHCPItemProperties(item, summary);
			page.add(new HandleableItemCouple(item));
		}
		return null;
	}

}
