package com.amituofo.xfs.plugin.fs.objectstorage.hcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPClientFactory.ClientType;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPBucketspace;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.model.NamespaceBasicSetting;

public class HCPFileSystemEntry extends OSDFileSystemEntry<HCPFileSystemEntryConfig, HCPFileSystemPreference, HCPBucketspace> {
	private final Map<String, HCPNamespace> hcpClientMap = new HashMap<String, HCPNamespace>();
	protected NamespaceBasicSetting namespaceSetting = null;
	protected HCPTenantManagement tenantMgrClient = null;

	public HCPFileSystemEntry(HCPFileSystemEntryConfig entryConfig, HCPFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected HCPBucketspace createDefaultItemspace() throws ServiceException {

		HCPNamespace namespace;
		NamespaceBasicSetting namespaceSetting;
		try {
			namespace = getHcpNamespace(entryConfig.getNamespace());
			namespaceSetting = getNamespaceSetting(entryConfig.getNamespace());

			HCPBucketspace root = new HCPBucketspace(this, namespace, namespaceSetting);

			return root;
		} catch (ServiceException e) {
			hcpClientMap.remove(entryConfig.getNamespace());
			HCPClientFactory.getInstance().clearCache();
			throw e;
		}
	}

	// @Override
	// public RootItem getRoot(String rootId) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	protected List<HCPBucketspace> listAccessibleItemspaces() throws ServiceException {
		List<HCPBucketspace> itemspaces = new ArrayList<HCPBucketspace>();
		List<NamespaceBasicSetting> namespaces = null;
		try {
			HCPNamespace namespace = getHcpNamespace(entryConfig.getNamespace());
			namespaces = namespace.listAccessibleNamespaces();
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}

		for (int i = 0; i < namespaces.size(); i++) {
			NamespaceBasicSetting namespaceSetting = namespaces.get(i);
			HCPNamespace namespace = getHcpNamespace(namespaceSetting.getName());

			HCPBucketspace root = new HCPBucketspace(this, namespace, namespaceSetting);

			itemspaces.add(root);
		}

		// 避免两个桶一个http 一个https，会导致getroot失败
		return itemspaces;
	}

	@Override
	public boolean isAvailable() {
		try {
			return getHcpNamespace(entryConfig.getNamespace()).doesNamespacesExist(entryConfig.getNamespace());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void close() throws ServiceException {
		namespaceSetting = null;
		hcpClientMap.clear();
		HCPClientFactory.getInstance().clearCache();
	}

	public NamespaceBasicSetting getNamespaceSetting(String namespaceName) throws ServiceException {
		if (namespaceSetting == null) {
			HCPNamespace namespace = getHcpNamespace(namespaceName);
			try {
				namespaceSetting = namespace.getNamespaceSetting();
			} catch (Exception e) {
				throw new ServiceException((e.getCause() != null ? e.getCause().getMessage() : e.getMessage()), e);
			}
		}
		return namespaceSetting;
	}

	protected HCPNamespace getCachedHcpNamespace(String name) {
		try {
			return getHcpNamespace(name);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	public HCPNamespace getHcpNamespace(String namespace) throws ServiceException {
		HCPNamespace hcpNamespace = hcpClientMap.get(namespace);
		if (hcpNamespace == null) {
			ClientConfiguration clientConfig = entryConfig.getClientConfiguration();

			// try {
			hcpNamespace = HCPClientFactory.getInstance().getHCPClient(ClientType.REST,
					com.hitachivantara.core.http.Protocol.valueOf(entryConfig.getProtocol()),
					namespace,
					entryConfig.getTenant(),
					entryConfig.getDomain(),
					entryConfig.getAuthenticationType(),
					entryConfig.getAccesskey(),
					entryConfig.getSecretkey(),
					clientConfig,
					false);

			hcpClientMap.put(namespace, hcpNamespace);
			// } catch (ServiceException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// return null;
			// }
		}

		return hcpNamespace;
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
//		try {
//			String namespaceName = config.getName();
//			NamespaceSettings settings = SettingBuilders.createNamespaceBuilder()
//					.withName(namespaceName)
//					.withHardQuota(11.2, QuotaUnit.GB)
//					.bulid();
//			// 执行创建桶
//			getHCPTenantManagementClient().createNamespace(settings);
//		} catch (InvalidParameterException e) {
//			e.printStackTrace();
//			throw new ServiceException(e);
//		} catch (HSCException e) {
//			e.printStackTrace();
//			throw new ServiceException(e);
//		}
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void deleteItemSpace(String name) throws ServiceException {
		try {
			String namespaceName = name;
			getHCPTenantManagementClient().deleteNamespace(namespaceName);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch (HSCException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	private HCPTenantManagement getHCPTenantManagementClient() throws ServiceException {
		if (tenantMgrClient == null) {
			// 指定需要登录的HCP 租户 及 桶
			String hcpdomain = entryConfig.getDomain();
			String tenant = entryConfig.getTenant();
			// 登录需要的用户名
			// The access key encoded by Base64
			String accessKey = entryConfig.getAccesskey();
			// 登录需要的密码
			// The AWS secret access key encrypted by MD5
			String secretKey = entryConfig.getSecretkey();

			ClientConfiguration myClientConfig1 = new ClientConfiguration();
			// myClientConfig1.setProxy("localhost", 8080);
			// myClientConfig1.ignoreSslVerification();
			// myClientConfig1.ignoreHostnameVerification();

			tenantMgrClient = HCPClientFactory.getInstance().getHCPTenantManagementClient(hcpdomain, tenant, accessKey, secretKey, myClientConfig1, false);
		}

		return tenantMgrClient;
	}

}
