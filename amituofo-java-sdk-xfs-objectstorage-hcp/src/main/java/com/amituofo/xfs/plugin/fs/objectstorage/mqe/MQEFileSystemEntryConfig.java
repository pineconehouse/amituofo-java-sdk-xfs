package com.amituofo.xfs.plugin.fs.objectstorage.mqe;

import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.hitachivantara.hcp.query.define.ObjectProperty;
import com.hitachivantara.hcp.query.define.Order;
import com.hitachivantara.hcp.query.model.request.ObjectBasedQueryRequest;
import com.hitachivantara.hcp.query.model.request.QueryRequest;

public class MQEFileSystemEntryConfig extends HCPFileSystemEntryConfig {
	public static final String SYSTEM_NAME = "HCP Metadata Query Engine (MQE)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP43252839";

	public static final String HCP_ENTRY_CONFIG_NAME = "HCP_ENTRY_CONFIG_NAME";
	public static final String QUERY_EXPRESSION = "QUERY_EXPRESSION";
	public static final String PAGE_SIZE = "PAGE_SIZE";

	private HCPFileSystemEntry hcpEntry;

	public MQEFileSystemEntryConfig() {
		this("");
	}

	public MQEFileSystemEntryConfig(HCPFileSystemEntry hcpEntry) {
		this(hcpEntry.getEntryConfig().getName());
		super.config.setSimpleConfiguration(hcpEntry.getEntryConfig().getSimpleConfiguration());
		super.config.set(FILE_SYSTEM_ID, SYSTEM_ID);
		this.hcpEntry = hcpEntry;
	}

	public MQEFileSystemEntryConfig(HCPFileSystemEntryConfig config) {
		this(config.getName());
		super.config.setSimpleConfiguration(config.getSimpleConfiguration());
		super.config.set(FILE_SYSTEM_ID, SYSTEM_ID);
	}

	public MQEFileSystemEntryConfig(String name) {
		super(MQEFileSystemEntryConfig.class, SYSTEM_ID, name, URL_ROOT_PATH);
	}

	@Override
	public FileSystemEntry createFileSystemEntry() {
		return new MQEFileSystemEntry(this, (MQEFileSystemPreference) createPreference());
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new MQEFileSystemEntry(this, (MQEFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new MQEFileSystemPreference();
	}

	public HCPFileSystemEntry createHCPFileSystemEntry() {
		if (hcpEntry == null) {
			hcpEntry = (HCPFileSystemEntry) new HCPFileSystemEntryConfig(this).createFileSystemEntry();
		}
		return hcpEntry;
	}

	// public void setHCPEntryConfigName(String exp) {
	// config.set(HCP_ENTRY_CONFIG_NAME, exp);
	// }
	//
	// public String getHCPEntryConfigName() {
	// return config.getString(HCP_ENTRY_CONFIG_NAME);
	// }

	public void setPageSize(int pagesize) {
		config.set(PAGE_SIZE, pagesize);
	}

	public int getPageSize() {
		return config.getInteger(PAGE_SIZE, 1000);
	}

	public void setQueryExpression(String exp) {
		config.set(QUERY_EXPRESSION, StringUtils.encodeBase64String(exp));
	}

	public String getQueryExpression() {
		String val = config.getString(QUERY_EXPRESSION);

		if (StringUtils.isNotEmpty(val)) {
			val = StringUtils.decodeBase64String(val);
		}
		return val;
	}

	public QueryRequest getQueryRequest() {
		return createBasedQueryRequest(getQueryExpression(), this.getPageSize());
	}

	public static ObjectBasedQueryRequest createBasedQueryRequest(String query, int pagesize) {
		ObjectBasedQueryRequest basedQueryRequest = new ObjectBasedQueryRequest();

		basedQueryRequest.setQuery(query);
		// ??????????????????

		// ?????????????????? ?????????????????????????????????HCP??????????????????.doc???

		// ??????key??????abcdefg?????????
		// request.setQuery(query);
		// request.setQuery(namespaceForQuery + " +(objectPath:\\" + this.getActualPath()+"*)");
		// ?????????????????????????????????110223201009028931?????????
		// request.setQuery("+(customMetadataContent:110223201009028931)");
		// ????????????????????????male????????????key??????beijing?????????
		// request.setQuery("+(customMetadataContent:male) +(objectPath:beijing)");
		// +(namespace:"ns1.tenant1") +(objectPath:\/HDS)

		// ?????????????????????????????????????????????????????????
		basedQueryRequest.addSort(ObjectProperty.ingestTime, Order.asc);
		// request.addSort(ObjectProperty.size); //????????????????????? asc ????????????
		// ?????????????????????????????????????????????100???
		if (pagesize <= 0) {
			pagesize = 1000;
		}
		basedQueryRequest.setResults(pagesize);
		// ??????????????????n???????????????
		// requestBody.setOffset(10);

		// ?????????????????????????????????????????????
		// ??????????????????????????????????????????????????????changeTimeMilliseconds/key/name/urlName/versionId/operation???
		// basedQueryRequest.addProperty(ObjectProperty.accessTime);
		// basedQueryRequest.addProperty(ObjectProperty.accessTimeString);
		// basedQueryRequest.addProperty(ObjectProperty.acl);
		// basedQueryRequest.addProperty(ObjectProperty.aclGrant);
		// basedQueryRequest.addProperty(ObjectProperty.changeTimeMilliseconds);
		// basedQueryRequest.addProperty(ObjectProperty.changeTimeString);
		// basedQueryRequest.addProperty(ObjectProperty.customMetadata);
		// basedQueryRequest.addProperty(ObjectProperty.customMetadataAnnotation);
		basedQueryRequest.addProperty(ObjectProperty.dpl);
		// basedQueryRequest.addProperty(ObjectProperty.gid);
		 basedQueryRequest.addProperty(ObjectProperty.hash);
		// basedQueryRequest.addProperty(ObjectProperty.hashScheme);
		// basedQueryRequest.addProperty(ObjectProperty.hold);
		// basedQueryRequest.addProperty(ObjectProperty.index);
		basedQueryRequest.addProperty(ObjectProperty.ingestTime);
		// basedQueryRequest.addProperty(ObjectProperty.ingestTimeString);
		basedQueryRequest.addProperty(ObjectProperty.namespace);
		basedQueryRequest.addProperty(ObjectProperty.objectPath);
		basedQueryRequest.addProperty(ObjectProperty.operation);
		basedQueryRequest.addProperty(ObjectProperty.owner);
		basedQueryRequest.addProperty(ObjectProperty.permissions);
		// basedQueryRequest.addProperty(ObjectProperty.replicated);
		// basedQueryRequest.addProperty(ObjectProperty.replicationCollision);
		basedQueryRequest.addProperty(ObjectProperty.retention);
		// basedQueryRequest.addProperty(ObjectProperty.retentionClass);
		// basedQueryRequest.addProperty(ObjectProperty.retentionString);
		// basedQueryRequest.addProperty(ObjectProperty.shred);
		basedQueryRequest.addProperty(ObjectProperty.size);
		// basedQueryRequest.addProperty(ObjectProperty.type);
		// basedQueryRequest.addProperty(ObjectProperty.uid);
		// basedQueryRequest.addProperty(ObjectProperty.updateTime);
		// basedQueryRequest.addProperty(ObjectProperty.updateTimeString);
		basedQueryRequest.addProperty(ObjectProperty.urlName);
		// basedQueryRequest.addProperty(ObjectProperty.utf8Name);
		// basedQueryRequest.addProperty(ObjectProperty.version);
		
//		basedQueryRequest.addSort(objectProperty);
		
		// ????????????????????????
		// basedQueryRequest.addFacet(Facet.namespace);
		// basedQueryRequest.addFacet(Facet.hold);
		// basedQueryRequest.addFacet(Facet.retention);
		// basedQueryRequest.addFacet(Facet.retentionClass);

		return basedQueryRequest;
	}

}
