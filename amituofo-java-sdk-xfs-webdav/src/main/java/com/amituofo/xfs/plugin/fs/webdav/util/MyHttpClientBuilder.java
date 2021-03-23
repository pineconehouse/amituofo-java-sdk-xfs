/*                                                                             
 * Copyright (C) 2019 Rison Han                                     
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");           
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,         
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */
package com.amituofo.xfs.plugin.fs.webdav.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.IgnoreSpecProvider;

import com.amituofo.common.define.Protocol;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemEntryConfig;

public class MyHttpClientBuilder {// implements Builder<CloseableHttpClient> {
	private PoolingHttpClientConnectionManager connectionManager;
	private org.apache.http.impl.client.HttpClientBuilder httpClientBuilder;

	public static final TrustManager[] DUMMY_TRUST_MGR = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;// new java.security.cert.X509Certificate[] {};
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

	} };

	/**
	 * 跳过主机验证
	 */
	public static final HostnameVerifier DUMMY_HOST_NAME_VERIFIER = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	public MyHttpClientBuilder(WebDavFileSystemEntryConfig configuration) {
		try {

			RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
			if (configuration.getProtocol() == Protocol.HTTPS) {
				SSLContext sc;
				SSLSocketFactory sslSocketFactory;
				sc = SSLContext.getInstance("TLS");
				sc.init(null, DUMMY_TRUST_MGR, new java.security.SecureRandom());
				sslSocketFactory = sc.getSocketFactory();

				// if (configuration.getSslSocketFactory() != null) {
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslSocketFactory,
						// configuration.getSupportedProtocols(),
						// configuration.getSupportedCipherSuites(),
						DUMMY_HOST_NAME_VERIFIER);
				registryBuilder.register(Protocol.HTTPS.toString(), sslsf);
				// } else {
				// registryBuilder.register(Protocol.HTTPS.toString(), SSLConnectionSocketFactory.getSocketFactory());
				// }
			} else if (configuration.getProtocol() == Protocol.HTTP) {
				registryBuilder.register(Protocol.HTTP.toString(), new PlainConnectionSocketFactory());
			}

			Registry<ConnectionSocketFactory> registry = registryBuilder.build();

			connectionManager = new PoolingHttpClientConnectionManager(registry);
			// connectionManager = new PoolingHttpClientConnectionManager(registry, configuration.getDnsResolver());
			// connectionManager.setMaxTotal(configuration.getMaxConnections());
			// connectionManager.setDefaultMaxPerRoute(configuration.getDefaultMaxConnectionsPerRoute());
			// connectionManager = new PoolingHttpClientConnectionManager();
			// connectionManager.setMaxTotal(20);
			// connectionManager.setDefaultMaxPerRoute(20);

			this.httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);

			// remove Accept-Encoding: gzip,deflate
			this.httpClientBuilder.disableContentCompression();

			// if (configuration.getDnsResolver() != null) {
			// this.httpClientBuilder.setDnsResolver(configuration.getDnsResolver());
			// }

			int configCount = 0;
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			// if (configuration.getConnectTimeout() > 0) {
			// requestConfigBuilder.setConnectTimeout(configuration.getConnectTimeout());
			// configCount++;
			// }
			// if (configuration.getRequestTimeout() > 0) {
			// requestConfigBuilder.setConnectionRequestTimeout(configuration.getRequestTimeout());
			// configCount++;
			// }

			String proxyHost = configuration.getProxyHost();
			int proxyPort = configuration.getProxyPort();
			if (proxyHost != null && proxyPort > 0) {
				HttpHost proxy = new HttpHost(proxyHost, proxyPort, configuration.getProtocol().name());
				requestConfigBuilder.setProxy(proxy);

				String userName = configuration.getProxyUsername();
				String password = configuration.getProxyPassword();
				if (StringUtils.isNotEmpty(userName)) {
					CredentialsProvider cp = new BasicCredentialsProvider();
					cp.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(userName, password));
					this.httpClientBuilder.setDefaultCredentialsProvider(cp);
				}

				configCount++;
			}

			CredentialsProvider credentialsProvider;
			credentialsProvider = this.createDefaultCredentialsProvider(configuration.getUser(), configuration.getPassword(), null, null);
			this.httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			configCount++;

			this.httpClientBuilder.setDefaultCookieSpecRegistry(new Lookup<CookieSpecProvider>() {
				@Override
				public CookieSpecProvider lookup(String name) {
					return new IgnoreSpecProvider();
				}
			});

			if (configCount > 0) {
				RequestConfig defaultRequestConfig = requestConfigBuilder.build();
				this.httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private CredentialsProvider createDefaultCredentialsProvider(String username, String password, String domain, String workstation) {
		CredentialsProvider provider = new BasicCredentialsProvider();
		if (username != null) {
			provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.NTLM),
					new NTCredentials(username, password, workstation, domain));
			provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.BASIC),
					new UsernamePasswordCredentials(username, password));
			provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.DIGEST),
					new UsernamePasswordCredentials(username, password));
			provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.SPNEGO),
					new NTCredentials(username, password, workstation, domain));
			provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.KERBEROS),
					new UsernamePasswordCredentials(username, password));
		}
		return provider;
	}

	public CloseableHttpClient build() {
		// CloseableHttpClient httpClient = HttpClients.custom().setDnsResolver(dnsResolver).setConnectionManager(connectionManager).build();
		CloseableHttpClient httpClient = httpClientBuilder.build();
		return httpClient;
	}

}
