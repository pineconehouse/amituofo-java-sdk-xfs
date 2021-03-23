package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import com.amituofo.common.ex.HSCException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemInstanceCreator;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemspaceBase;
import com.amituofo.xfs.service.ItemspaceSummary;
import com.hitachivantara.hcp.common.HCPClient;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.NamespaceBasicSetting;
import com.hitachivantara.hcp.standard.model.NamespaceStatistics;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummary;

public class HCPBucketspace extends ItemspaceBase<HCPFileSystemEntry, HCPFileSystemPreference> implements OSDBucketspace, OSDItemInstanceCreator {
	private HCPNamespace namespace;
	private NamespaceBasicSetting namespaceSetting;

	public HCPBucketspace(HCPFileSystemEntry entry, HCPNamespace namespace, NamespaceBasicSetting namespaceSetting) {
		super(entry);
		this.namespace = namespace;
		this.namespaceSetting = namespaceSetting;
	}

	@Override
	public String getName() {
		return namespaceSetting.getName();
	}

	@Override
	public ItemspaceSummary getSummary() {
		NamespaceStatistics statistic;
		try {
			statistic = namespace.getNamespacesStatistics();
			return new ItemspaceSummary(statistic.getObjectCount(), statistic.getTotalCapacityBytes(), statistic.getUsedCapacityBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return super.getSummary();
		}
	}

	@Override
	public FolderItem newFolderItemInstance(String key) {
		HCPFolderItem folder = new HCPFolderItem(this, key);
		return folder;
	}

	@Override
	public FileItem newFileItemInstance(String key) {
		HCPFileItem folder = new HCPFileItem(this, key);
		return folder;
	}

	@Override
	public OSDVersionFileItem newVersionFileItemInstance(String key, String versionId) {
		return new HCPVersionFileItem(this, key, versionId);
	}

	public HCPMetadataItem newMetadataFileItemInstance(String key, HCPMetadataSummary hcpMetadataSummary) {
		return new HCPMetadataItem(this, key, hcpMetadataSummary);
	}

	public HCPMetadataItem newVersionMetadataFileItemInstance(String key, String versionId, HCPMetadataSummary hcpMetadataSummary) {
		return new HCPVersionMetadataItem(this, key, versionId, hcpMetadataSummary);
	}

	public Item newDeletedFileItemInstance(String key) {
		return new HCPDeletedFileItem(this, key, null);
	}

	public Item newDeletedFileItemInstance(String key, String versionId) {
		return new HCPDeletedFileItem(this, key, versionId);
	}

	// @Override
	// protected FolderItem createRootFolder() {
	// return newFolderItemInstance(entry.getRootPath());
	// }

	@Override
	public String getEndpoint() {
		return ((HCPClient) namespace).getEndpoint();
	}

	protected boolean isShowDeletedObjects() {
		return preference.isShowDeletedObjects() && namespaceSetting.isVersioningEnabled();
	}

	protected boolean isEnablePurgeDeletion() {
		return preference.isEnablePurgeDeletion() && namespaceSetting.isVersioningEnabled();
	}

	public HCPNamespace getHcpClient() {
		return namespace;
	}

	public NamespaceBasicSetting getNamespaceSetting() {
		return namespaceSetting;
	}

	public String getNamespace() {
		return namespaceSetting.getName();
	}

}
