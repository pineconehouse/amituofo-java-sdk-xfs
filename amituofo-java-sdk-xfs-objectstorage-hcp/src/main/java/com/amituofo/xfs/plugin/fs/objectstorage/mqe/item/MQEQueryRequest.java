package com.amituofo.xfs.plugin.fs.objectstorage.mqe.item;

import java.util.List;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.MQEFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.MQEFileSystemPreference;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.QueryStatusHandler;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemspaceBase;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.query.model.ObjectQueryResult;
import com.hitachivantara.hcp.query.model.ObjectSummary;
import com.hitachivantara.hcp.query.model.QueryResult;
import com.hitachivantara.hcp.query.model.request.ObjectBasedQueryRequest;
import com.hitachivantara.hcp.query.model.request.QueryRequest;

public class MQEQueryRequest extends ItemspaceBase<MQEFileSystemEntry, MQEFileSystemPreference> {
	private QueryRequest queryRequest = new QueryRequest();;
	protected HCPQuery hcpQueryClient;
	protected String namespaceForQuery;
	private HCPFileSystemEntry hcpEntry;
	private ObjectQueryResult pageresult;

	public MQEQueryRequest(MQEFileSystemEntry entry, HCPFileSystemEntry hcpEntry, HCPQuery hcpQueryClient, QueryRequest basedQueryRequest) {
		super(entry);
		this.hcpEntry = hcpEntry;
		this.queryRequest = basedQueryRequest;
		this.hcpQueryClient = hcpQueryClient;
	}

	@Override
	public String getName() {
		return queryRequest.toString();// getQuery();
	}

	@Override
	public FolderItem newFolderItemInstance(String queryExp) {
		return new MQEFolderItem(this);
	}

	@Override
	public FileItem newFileItemInstance(String key) {
		return new MQEFileItem((HCPBucketspace) hcpEntry.getDefaultItemspace(), key);
	}

	public QueryRequest getRequest() {
		return queryRequest;
	}

	public HCPQuery getHcpQueryClient() {
		return hcpQueryClient;
	}

	public HCPFileSystemEntry getHcpEntry() {
		return hcpEntry;
	}

	public Integer queryCount() throws InvalidResponseException, HSCException {
		ObjectBasedQueryRequest basedQueryRequest = (ObjectBasedQueryRequest) queryRequest;
		ObjectQueryResult result = hcpQueryClient.query(basedQueryRequest);
		Integer count = result.getStatus().getTotalResults();
		return count;
	}

	// public void queryFirst(ItemHandler itemhandler, QueryStatusHandler statushandler) throws InvalidResponseException, HSCException {
	// // EXEC TEST FUNCTION ---------------------------------------------------------------------
	// queryRequest.resetOffset();
	//
	// // 触发搜索
	// ObjectBasedQueryRequest basedQueryRequest = (ObjectBasedQueryRequest) queryRequest;
	//
	// pageresult = hcpQueryClient.query(basedQueryRequest);
	//
	// if (statushandler != null && !statushandler.queryStatusChanged(pageresult.getStatus())) {
	// return;
	// }
	//
	// // 处理搜索结果
	// HandleFeedback ef = handleResult(pageresult, itemhandler);
	//
	// itemhandler.handle(ItemEvent.EXEC_END, null);
	// }
	//
	// private void queryPage(ObjectBasedQueryRequest request, ItemHandler itemhandler, QueryStatusHandler statushandler) throws
	// InvalidResponseException, HSCException {
	// // EXEC TEST FUNCTION ---------------------------------------------------------------------
	//
	// // 触发搜索
	// if (pageresult == null) {
	// queryFirst(itemhandler, statushandler);
	// return;
	// }
	//
	// // 判断是否还有下一页
	// if (pageresult.isIncomplete()) {
	// // 如果还有下一页搜索Nextpage
	// pageresult = hcpQueryClient.query(request);
	//
	// if (statushandler != null && !statushandler.queryStatusChanged(pageresult.getStatus())) {
	// return;
	// }
	// }
	//
	// itemhandler.handle(ItemEvent.EXEC_END, null);
	// }
	//
	// public void queryNext(ItemHandler itemhandler, QueryStatusHandler statushandler) throws InvalidResponseException, HSCException {
	//
	// ObjectBasedQueryRequest basedQueryRequest = (ObjectBasedQueryRequest) queryRequest;
	//
	// queryPage(basedQueryRequest.withNextPage(), itemhandler, statushandler);
	// }
	//
	// public void queryPrev(ItemHandler itemhandler, QueryStatusHandler statushandler) throws InvalidResponseException, HSCException {
	//
	// ObjectBasedQueryRequest basedQueryRequest = (ObjectBasedQueryRequest) queryRequest;
	//
	// queryPage(basedQueryRequest.withPrevPage(), itemhandler, statushandler);
	// }

	public void queryOffset(int offset, int pageSize, ItemHandler itemhandler, QueryStatusHandler statushandler) throws InvalidResponseException, HSCException {
		// EXEC TEST FUNCTION ---------------------------------------------------------------------

		queryRequest.resetOffset();

		// 触发搜索
		ObjectBasedQueryRequest basedQueryRequest = (ObjectBasedQueryRequest) queryRequest;

		if (pageSize > 0) {
			basedQueryRequest.setResults(pageSize);
		}

		if (offset > 0) {
			basedQueryRequest.setOffset(offset);
		}

		ObjectQueryResult result = hcpQueryClient.query(basedQueryRequest);

		if (statushandler != null && HandleFeedback.interrupted == statushandler.queryStatusChanged(result.getStatus())) {
			return;
		}

		// 处理搜索结果
		HandleFeedback ef = handleResult(result, itemhandler);

		itemhandler.handle(ItemEvent.EXEC_END, null);
	}

	public void queryAll(ItemHandler itemhandler, QueryStatusHandler statushandler) throws InvalidResponseException, HSCException {
		// EXEC TEST FUNCTION ---------------------------------------------------------------------

		queryRequest.resetOffset();

		// 触发搜索
		ObjectBasedQueryRequest basedQueryRequest = (ObjectBasedQueryRequest) queryRequest;

		ObjectQueryResult result = hcpQueryClient.query(basedQueryRequest);

		if (statushandler != null && HandleFeedback.interrupted == statushandler.queryStatusChanged(result.getStatus())) {
			return;
		}

		// 处理搜索结果
		HandleFeedback ef = handleResult(result, itemhandler);
		if (ef == HandleFeedback.interrupted) {
			return;
		}

		// 判断是否还有下一页
		while (result.isIncomplete()) {
			// System.out.println(request.getRequestBody().build());

			// 如果还有下一页搜索Nextpage
			result = hcpQueryClient.query(basedQueryRequest.withNextPage());

			if (statushandler != null && HandleFeedback.interrupted == statushandler.queryStatusChanged(result.getStatus())) {
				return;
			}

			// 处理搜索结果
			ef = handleResult(result, itemhandler);
			if (ef == HandleFeedback.interrupted) {
				return;
			}
		}

		itemhandler.handle(ItemEvent.EXEC_END, null);
	}

	// public ObjectQueryResult query(ItemHandler event) throws InvalidResponseException, HSCException {
	// // EXEC TEST FUNCTION ---------------------------------------------------------------------
	//
	// ObjectBasedQueryRequest request = ((MQEFileSystemEntry) fileSystemEntry).getBasedQueryRequest();
	//
	// // 触发搜索
	// result = hcpQueryClient.query(request);
	//
	//// long totalFound = result.getStatus().getTotalResults();
	// // System.out.println("一共找到 " + totalFound + " 件匹配记录");
	//
	// // 处理搜索结果
	// handleResult(result, event);
	// return result;
	// }
	//
	// ObjectQueryResult result = null;
	// public ObjectQueryResult queryNext(ItemHandler event) throws InvalidResponseException, HSCException {
	// // EXEC TEST FUNCTION ---------------------------------------------------------------------
	//
	// ObjectBasedQueryRequest request = ((MQEFileSystemEntry) fileSystemEntry).getBasedQueryRequest();
	//
	// if (result == null) {
	// return query(event);
	// }
	//
	// // 判断是否还有下一页
	// if (result.isIncomplete()) {
	// // System.out.println(request.getRequestBody().build());
	//
	// // 如果还有下一页搜索Nextpage
	// result = hcpQueryClient.query(request.withNextPage());
	//
	// // 处理搜索结果
	// ef = handleResult(result, event);
	// if (ef == HandleFeedback.stop) {
	// return;
	// }
	// }
	//
	// return result;
	// }

	private HandleFeedback handleResult(final QueryResult result, ItemHandler event) {
		List<ObjectSummary> list = result.getResults();
		for (int i = 0; i < list.size(); i++) {
			ObjectSummary summary = list.get(i);

			HCPBucketspace namespace;

//			if (summary.getOperation() != Operation.CREATED) {
//				return null;
//			}

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
			// MQEFileItem item = (MQEFileItem) this.newFileItemInstance(summary.getKey());
			// switch (hcpObjectEntry.getType()) {
			// case object:
			// item = new MQEFileItem((HCPFileSystemEntry) fileSystemEntry, preference, hcpNamespace, namespaceSetting);
			// break;
			// case directory:
			// item = new MQEFolderItem(preference, (HCPFileSystemEntry) fileSystemEntry, hcpQueryClient, hcpNamespace, namespaceSetting);
			// break;
			// default:
			// item = new MQEFileItem(preference, (HCPFileSystemEntry) fileSystemEntry, hcpQueryClient, hcpNamespace, namespaceSetting);
			// }

			HCPFileItem.setHCPItemProperties(item, summary);

			// if (filter != null && !filter.accept(item)) {
			// continue;
			// }

			HandleFeedback ef = event.handle(ItemEvent.ITEM_FOUND, item);
			if (ef == HandleFeedback.interrupted) {
				// event.handle(ItemEvent.EXEC_END, null);
				return ef;
			}
		}
		return null;
	}

}
