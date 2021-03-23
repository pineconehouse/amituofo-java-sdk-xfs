package com.amituofo.xfs.plugin.fs.objectstorage.s3common;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContextBuilder;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amituofo.common.ex.ServiceException;

public class S3ClientFactory {
	private static S3ClientFactory instance = new S3ClientFactory();

	private S3ClientFactory() {
	}

	public static S3ClientFactory getInstance() {
		return instance;
	}

	public AmazonS3 getS3Client(Protocol protocol,
			boolean ignoreSSLCertification,
			Regions region,
			String userInBase64,
			String passwordInMd5,
			boolean forceGlobalBucketAccessEnabled,
			boolean pathStyleAccessEnabled,
			boolean accelerateModeEnabled,
			boolean dualstackEnabled,
			boolean payloadSigningEnabled,
//			boolean enableUseArnRegion,
//			boolean enableRegionalUsEast1Endpoint,

			ClientConfiguration clientConfig) throws ServiceException {
		AmazonS3 hs3Client = null;
		
		ClientConfiguration myClientConfig;
		if (hs3Client == null) {
			if (clientConfig != null) {
				myClientConfig = clientConfig;
			} else {
				myClientConfig = new ClientConfiguration();
			}
			try {
				// Using HTTP protocol
				myClientConfig.setProtocol(protocol);

				if (protocol == Protocol.HTTPS && ignoreSSLCertification) {
					// 全部信任 不做身份鉴定
					ignoreSSLCertification(myClientConfig);
				}

				// myClientConfig.setProxyHost("127.0.0.1");
				// myClientConfig.setProxyPort(8080);

				AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
						.withClientConfiguration(myClientConfig)
//						.withRegion(Regions.DEFAULT_REGION)
//						.withRegion(null)
						.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(userInBase64, passwordInMd5)))
//						.withForceGlobalBucketAccessEnabled(forceGlobalBucketAccessEnabled)
//						.withPathStyleAccessEnabled(pathStyleAccessEnabled)
//						.withAccelerateModeEnabled(accelerateModeEnabled)
//						.withDualstackEnabled(dualstackEnabled)
//						.withPayloadSigningEnabled(payloadSigningEnabled)
						;

				if (forceGlobalBucketAccessEnabled) {
					builder.enableForceGlobalBucketAccess();
					builder.withEndpointConfiguration(new EndpointConfiguration("s3.amazonaws.com", "us-east-1"));
				} else {
					builder.withRegion(region);
				}

				if (pathStyleAccessEnabled) {
					builder.enablePathStyleAccess();
				}

				if (accelerateModeEnabled) {
					builder.enableAccelerateMode();
				}
				
				if (dualstackEnabled) {
					builder.enableDualstack();
				}
				
				if (payloadSigningEnabled) {
					builder.enablePayloadSigning();
				}
				
//				if (enableUseArnRegion) {
//					builder.enableUseArnRegion();
//				}
//				
//				if (enableRegionalUsEast1Endpoint) {
//					builder.enableRegionalUsEast1Endpoint();
//				}

				hs3Client = builder.build();
				
			} catch (Exception e) {
				throw new ServiceException("Error when creating client instance for region " + region, e);
				// e.printStackTrace();
			}
		}

		return hs3Client;
	}

	private void ignoreSSLCertification(ClientConfiguration clientConfiguration) {
		// 全部信任 不做身份鉴定
		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
		try {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			});

			SSLConnectionSocketFactory sslsf = null;
			// sslsf = new SSLConnectionSocketFactory(builder.build(), new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" }, null,
			// NoopHostnameVerifier.INSTANCE);
//			sslsf = new SSLConnectionSocketFactory(
//					builder.build(),
//					// new String[] { "TLSv1","TLSv1.1","TLSv1.2" }, // For Java 1.7
//					new String[] { "TLSv1" }, // For Java1.6-1.7
//					new String[] { "TLS_RSA_WITH_AES_128_CBC_SHA" },
//					NoopHostnameVerifier.INSTANCE);
			
			sslsf = new SSLConnectionSocketFactory(
					builder.build(),
					NoopHostnameVerifier.INSTANCE);

			clientConfiguration.getApacheHttpClientConfig().setSslSocketFactory(sslsf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	}

	public AmazonS3 getS3Client(Protocol protocol,
			boolean ignoreSSLCertification,
			String endpoint,
			String userInBase64,
			String passwordInMd5,
			boolean pathStyleAccessEnabled,
//			boolean accelerateModeEnabled,
//			boolean dualstackEnabled,
//			boolean payloadSigningEnabled,
			ClientConfiguration clientConfig
			) throws ServiceException {
		AmazonS3 hs3Client = null;

		ClientConfiguration myClientConfig;
		if (hs3Client == null) {
			if (clientConfig != null) {
				myClientConfig = clientConfig;
			} else {
				myClientConfig = new ClientConfiguration();
			}
			// myClientConfig.setProtocol(protocol);
			// if (Config.getInstance().getInt(ConfigKeys.CONNECTION_CONNECT_TIMEOUT) > 0) {
			// myClientConfig.setConnectionTimeout(Config.getInstance().getInt(ConfigKeys.CONNECTION_CONNECT_TIMEOUT));
			// }

			// if (protocol == Protocol.HTTPS) {
			// myClientConfig.ignoreHostnameVerification();
			// myClientConfig.ignoreSslVerification();
			// }
			// myClientConfig.setProxyHost("localhost");
			// myClientConfig.setProxyPort(8080);
			try {
				// builder = (type == ClientType.REST ? HCPClientBuilder.defaultHCPClient() : HCPClientBuilder.s3CompatibleClient());

				// Using HTTP protocol
				myClientConfig.setProtocol(protocol);
				// myClientConfig.setSignerOverride("S3SignerType");
				// myClientConfig.setRequestTimeout(1000);
				// myClientConfig.setConnectionTimeout(1000);
				// myClientConfig.setSocketTimeout(3000);
				// myClientConfig.setSignerOverride(value);

				if (protocol == Protocol.HTTPS && ignoreSSLCertification) {
					// 全部信任 不做身份鉴定
					ignoreSSLCertification(myClientConfig);
				}

				hs3Client = AmazonS3ClientBuilder.standard()
						.withClientConfiguration(myClientConfig)
						.withEndpointConfiguration(new EndpointConfiguration(endpoint, ""))
						.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(userInBase64, passwordInMd5)))
						.withPathStyleAccessEnabled(pathStyleAccessEnabled)
//						.withAccelerateModeEnabled(accelerateModeEnabled)
//						.enableUseArnRegion()
//						.enableRegionalUsEast1Endpoint()
//						.withDualstackEnabled(dualstackEnabled)
//						.withPayloadSigningEnabled(payloadSigningEnabled)
						.build();
			} catch (Exception e) {
				throw new ServiceException("Error when creating client instance for endpoint " + endpoint, e);
				// e.printStackTrace();
			}
		}

		return hs3Client;
	}

}
