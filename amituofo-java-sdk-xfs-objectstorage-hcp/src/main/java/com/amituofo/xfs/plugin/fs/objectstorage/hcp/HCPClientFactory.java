package com.amituofo.xfs.plugin.fs.objectstorage.hcp;

import java.util.HashMap;
import java.util.Map;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.DigestUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntryConfig.AuthenticationType;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;
import com.hitachivantara.hcp.build.HCPClientBuilder;
import com.hitachivantara.hcp.build.HCPNamespaceClientBuilder;
import com.hitachivantara.hcp.build.HCPQueryClientBuilder;
import com.hitachivantara.hcp.common.auth.ADCredentials;
import com.hitachivantara.hcp.common.auth.AnonymousCredentials;
import com.hitachivantara.hcp.common.auth.Credentials;
import com.hitachivantara.hcp.common.auth.LocalCredentials;
import com.hitachivantara.hcp.management.api.HCPTenantManagement;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.standard.api.HCPNamespace;

public class HCPClientFactory {
	private static HCPClientFactory instance = new HCPClientFactory();
	private static Map<String, HCPNamespace> clientMap = new HashMap<String, HCPNamespace>();
	private static Map<String, HCPTenantManagement> mgrClientMap = new HashMap<String, HCPTenantManagement>();
	private static Map<String, HCPQuery> mqeClientMap = new HashMap<String, HCPQuery>();

	// protected String user = "admin";
	// protected String password = "P@ssw0rd";
	//
	// protected final String bucketName = "cloud";
	// protected final String tenant = "tn9";
	// protected final String domain = "hcp8.hdim.lab";

	protected ClientConfiguration myClientConfig = new ClientConfiguration();

	public static enum ClientType {
		S3, REST;
	}

	private HCPClientFactory() {
	}

	public static HCPClientFactory getInstance() {
		return instance;
	}

//	public HCPNamespace getHCPClient(HCPNamespace client, String namespace) throws ServiceException {
//		ClientType type = ClientType.REST;
//		Protocol protocol = ((HCPClient) client).getClientConfiguration().getProtocol();
//		String endpoint = ((HCPClient) client).getEndpoint();
//		String userInBase64 = ((HCPClient) client).getCredentials().getAccessKey();
//		String passwordInMd5 = ((HCPClient) client).getCredentials().getSecretKey();
//
//		return getHCPClient(type, protocol, namespace, endpoint, userInBase64, passwordInMd5, ((HCPClient) client).getClientConfiguration(), false);
//	}

	public HCPNamespace getHCPClient(ClientType type,
			Protocol protocol,
			String namespace,
			String endpoint,
			AuthenticationType authType,
			String userInBase64,
			String passwordInMd5,
			ClientConfiguration clientConfig,
			boolean forceNewClient) throws ServiceException {

		String key = DigestUtils.format2Hex(DigestUtils.calcMD5(namespace
				+ endpoint
				+ authType
				+ userInBase64
				+ passwordInMd5
				+ (clientConfig != null ? clientConfig.getProxyHost() + clientConfig.getProxyPort() + clientConfig.getProxyUsername() + clientConfig.getProxyPassword() : "")));

		HCPNamespace hcpClient = null;

		if (!forceNewClient) {
			hcpClient = clientMap.get(key);
		}

		if (hcpClient == null) {
			if (clientConfig != null) {
				this.myClientConfig = clientConfig;
			}

			this.myClientConfig.setProtocol(protocol);

			// if (Config.getInstance().getInt(ConfigKeys.CONNECTION_CONNECT_TIMEOUT) > 0) {
			// myClientConfig.setConnectTimeout(Config.getInstance().getInt(ConfigKeys.CONNECTION_CONNECT_TIMEOUT));
			// }
			// if (protocol == Protocol.HTTPS) {
			// myClientConfig.ignoreHostnameVerification();
			// myClientConfig.ignoreSslVerification();
			// }
			// myClientConfig.setProxy("localhost", 8080);
			
			Credentials credentials;
			switch(authType) {
				case Local:
					credentials = new LocalCredentials(userInBase64, passwordInMd5);
					break;
				case Active_Directory:
					credentials = new ADCredentials(userInBase64, passwordInMd5);
					break;
				default:
				case Anonymous:
					credentials = new AnonymousCredentials();
					break;
			}

			try {
				HCPNamespaceClientBuilder builder = null;

				// builder = (type == ClientType.REST ? HCPClientBuilder.defaultHCPClient() : HCPClientBuilder.s3CompatibleClient());
				builder = HCPClientBuilder.defaultHCPClient();

				hcpClient = builder
						.withEndpoint(endpoint)
						.withNamespace(namespace)
						.withCredentials(credentials)
						.withClientConfiguration(myClientConfig)
						.bulid();

				clientMap.put(key, hcpClient);
			} catch (HSCException e) {
				throw new ServiceException("Error when creating client instance for endpoint " + endpoint, e);
				// e.printStackTrace();
			}
		}

		return hcpClient;
	}

	public HCPNamespace getHCPClient(ClientType type,
			Protocol protocol,
			String namespace,
			String tenant,
			String domain,
			AuthenticationType authType,
			String userInBase64,
			String passwordInMd5,
			ClientConfiguration clientConfig,
			boolean forceNewClient) throws ServiceException {
		return getHCPClient(type, protocol, namespace, tenant + "." + domain, authType, userInBase64, passwordInMd5, clientConfig, forceNewClient);
	}

	public HCPQuery getHCPQuery(Protocol protocol,
			String endpoint,
			String userInBase64,
			String passwordInMd5,
			ClientConfiguration clientConfig,
			boolean forceNewClient) throws ServiceException {

		String key = DigestUtils.format2Hex(DigestUtils.calcMD5(endpoint
				+ userInBase64
				+ passwordInMd5
				+ (clientConfig != null ? clientConfig.getProxyHost() + clientConfig.getProxyPort() + clientConfig.getProxyUsername() + clientConfig.getProxyPassword() : "")));

		HCPQuery hcpQueryClient = null;

		if (!forceNewClient) {
			hcpQueryClient = mqeClientMap.get(key);
		}

		if (hcpQueryClient == null) {
			if (clientConfig != null) {
				this.myClientConfig = clientConfig;
			}

			this.myClientConfig.setProtocol(protocol);

			try {
				// builder = (type == ClientType.REST ? HCPClientBuilder.defaultHCPClient() : HCPClientBuilder.s3CompatibleClient());
				HCPQueryClientBuilder builder = HCPClientBuilder.queryClient();
				hcpQueryClient = builder.withClientConfiguration(clientConfig).withCredentials(new LocalCredentials(userInBase64, passwordInMd5)).withEndpoint(endpoint).bulid();

				mqeClientMap.put(key, hcpQueryClient);
			} catch (HSCException e) {
				throw new ServiceException("Error when creating client instance for endpoint " + endpoint, e);
				// e.printStackTrace();
			}
		}

		return hcpQueryClient;
	}
	
	public HCPTenantManagement getHCPTenantManagementClient(
			String hcpdomain,
			String tenant,
			String accessKey,
			String secretKey,
			ClientConfiguration clientConfig,
			boolean forceNewClient) throws ServiceException {

		String key = DigestUtils.format2Hex(DigestUtils.calcMD5(hcpdomain
				+ tenant
				+ accessKey
				+ secretKey
				+ (clientConfig != null ? clientConfig.getProxyHost() + clientConfig.getProxyPort() + clientConfig.getProxyUsername() + clientConfig.getProxyPassword() : "")));

		HCPTenantManagement tenantMgrClient = null;
		if (!forceNewClient) {
			tenantMgrClient = mgrClientMap.get(key);
		}
		
		if (tenantMgrClient == null) {
			if (clientConfig != null) {
				this.myClientConfig = clientConfig;
			}

			ClientConfiguration myClientConfig1 = new ClientConfiguration();
//			myClientConfig1.setProxy("localhost", 8080);
//			myClientConfig1.ignoreSslVerification();
//			myClientConfig1.ignoreHostnameVerification();
			try {
				tenantMgrClient = HCPClientBuilder.tenantManagementClient()
						.withEndpoint(hcpdomain)
						.withTenant(tenant)
						.withCredentials(new LocalCredentials(accessKey, secretKey))
						.withClientConfiguration(myClientConfig1)
						.bulid();
				mgrClientMap.put(key, tenantMgrClient);
			} catch (HSCException e) {
				throw new ServiceException("Error when creating client instance for " + hcpdomain, e);
				// e.printStackTrace();
			}
		}

		return tenantMgrClient;
	}

	public void clearCache() {
		clientMap.clear();
		mqeClientMap.clear();
		mgrClientMap.clear();
	}

}
