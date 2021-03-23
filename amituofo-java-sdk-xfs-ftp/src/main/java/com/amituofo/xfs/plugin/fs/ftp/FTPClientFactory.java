package com.amituofo.xfs.plugin.fs.ftp;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPSSocketFactory;
import org.apache.commons.net.util.TrustManagerUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.ftp.FTPFileSystemEntryConfig.DataTransferMode;
import com.amituofo.xfs.plugin.fs.ftp.define.Protocol;

/**
 * 连接池工厂类
 * 
 * @author PYY
 *
 */
public class FTPClientFactory implements PooledObjectFactory<FTPClient> {
	public static final String OS_DEFAULT_ENCODING = System.getProperty("sun.jnu.encoding");

	private FTPFileSystemEntryConfig config;
	private FTPFileSystemPreference preference;

	public FTPClientFactory(FTPFileSystemEntryConfig config, FTPFileSystemPreference preference) {
		this.config = config;
		this.preference = preference;
	}

	private FTPClient create() throws Exception {
		FTPClient ftpClient = null;
		FTPSClient ftpsClient = null;
		// https://www.codota.com/code/java/methods/org.apache.commons.net.ftp.FTPSClient/execPBSZ
		final Protocol protocol = config.getProtocol();
		switch (protocol) {
			case TLS_IMPLICIT:
			case SSL_IMPLICIT:
				ftpsClient = new FTPSClient(true);
				// ftpsClient = new SSLSessionReuseFTPSClient(true);
				ftpClient = ftpsClient;
				break;
			// case SSL_IMPLICIT:
			// ftpsClient = new FTPSClient("SSL", true);
			// ftpClient = ftpsClient;
			// break;
			case TLS_EXPLICIT:
			case SSL_EXPLICIT:
				ftpsClient = new FTPSClient(false);
				ftpClient = ftpsClient;
				break;
			// case SSL_EXPLICIT:
			// ftpsClient = new FTPSClient("SSL", false);
			// ftpClient = ftpsClient;
			// break;
			case NO_ENCRYPTION:
			default:
				ftpClient = new FTPClient();
				break;
		}

		// ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		if (ftpsClient != null) {
			// ftpsClient.setAuthValue(protocol.name().substring(0, 3));
			// ftpsClient.setTrustManager(trustManager);
			// ftpsClient.setEnabledCipherSuites(this.cipherSuites);
			// ftpsClient.setEnabledProtocols(this.protocols);
			// ftpsClient.setEnabledSessionCreation(this.sessionCreation);
			// ftpsClient.setUseClientMode(this.useClientMode);
			// ftpsClient.setEnabledSessionCreation(true);
			// ftpsClient.setKeyManager(this.keyManager);
			// ftpsClient.setNeedClientAuth(true);
			// ftpsClient.setWantClientAuth(true);

			// FTPSClient does not support implicit data connections, so we hack it ourselves
			// SSLContext context = SSLContext.getInstance("TLSv1.2");// protocol.name().substring(0, 3));
			// KeyManager clientKeyManager = new KeyManager() {
			// };

			// KeyManagerUtils.createClientKeyManager(storePath, storePass)
			// these are the same key and trust managers that we initialize the client with
			// context.init(new KeyManager[] { clientKeyManager }, new TrustManager[] { trustManager }, null);
			// context.init(null, new TrustManager[] { trustManager }, null);
			// ftpsClient.setSocketFactory(new FTPSSocketFactory(context));
			// SSLServerSocketFactory ssf = context.getServerSocketFactory();
			// ftpsClient.setServerSocketFactory(ssf);

			// ftpsClient.setSocketFactory(SocketFactory.getDefault());
			// ftpsClient.setServerSocketFactory(ServerSocketFactory.getDefault());
			// ftpsClient.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});
			// ftpsClient.setEnabledProtocols(new String[] { "TLSv1.1"});

			ftpsClient.setTrustManager(config.getTrustManager());
		}

		// FTPClientConfig ftpClientConfig = new FTPClientConfig();
		// ftpClient.configure(ftpClientConfig);

		// ftps时不设置超时，否则出现socket close错误
		if (ftpsClient == null && config.getConnectionTimeout() > 0) {
			ftpClient.setConnectTimeout(config.getConnectionTimeout());
		}

		try {
			ftpClient.connect(config.getHost(), config.getPort());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		int reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			throw new IOException("FTP Server " + config.getHost() + ":" + config.getPort() + " refused connection");
		}

		if (StringUtils.isNotEmpty(config.getUser())) {
			boolean result;
			if (StringUtils.isNotEmpty(config.getAccount())) {
				result = ftpClient.login(config.getUser(), config.getPassword(), config.getAccount());
			} else {
				result = ftpClient.login(config.getUser(), config.getPassword());
			}

			if (!result) {
				// System.out.println("ftpClient login failed... username is {}" + config.getUser());
				throw new IOException("Unable to login FTP server. Please check your username or password!");
			}
		}

		if (ftpsClient != null) {
			ftpsClient.execPBSZ(0);
			ftpsClient.execPROT("P");
		}

		{
			String[] logincmds = config.getLoginCommandRows();
			for (String logincmd : logincmds) {
				String params;
				String command;
				int firsti = logincmd.indexOf(' ');
				if (firsti != -1) {
					command = logincmd.substring(0, firsti);
					params = logincmd.substring(firsti + 1);
				} else {
					command = logincmd;
					params = null;
				}
				ftpClient.doCommand(command, params);
			}
			ftpClient.setBufferSize(config.getBufferSizeInKb() * 1024);

			if (config.getLocalDataTransferMode() == DataTransferMode.PASSIVE) {
				ftpClient.enterLocalPassiveMode();
			} else if (config.getLocalDataTransferMode() == DataTransferMode.ACTIVE) {
				ftpClient.enterLocalActiveMode();
			}

			ftpClient.setListHiddenFiles(config.isListHiddenFiles());
			ftpClient.setFileType(config.getDefaultFileType().code());
//			ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);

			if (config.isEnableUTF8Support()) {
				ftpClient.setAutodetectUTF8(true);

				if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
					ftpClient.setControlEncoding("UTF-8");
					// ftpClient.setCharset(Charset.forName(localCharset));
					preference.setControlEncoding("UTF-8");
					config.setControlEncoding("UTF-8");
				} else {
					if (StringUtils.isNotEmpty(config.getControlEncoding())) {
						ftpClient.setControlEncoding(config.getControlEncoding());
					} else {
						if (StringUtils.isNotEmpty(OS_DEFAULT_ENCODING)) {
							ftpClient.setControlEncoding(OS_DEFAULT_ENCODING);
							preference.setControlEncoding(OS_DEFAULT_ENCODING);
							config.setControlEncoding(OS_DEFAULT_ENCODING);
						}
					}
				}
			} else {
				if (StringUtils.isNotEmpty(config.getControlEncoding())) {
					ftpClient.setControlEncoding(config.getControlEncoding());
				} else {
					if (StringUtils.isNotEmpty(OS_DEFAULT_ENCODING)) {
						ftpClient.setControlEncoding(OS_DEFAULT_ENCODING);
						preference.setControlEncoding(OS_DEFAULT_ENCODING);
						config.setControlEncoding(OS_DEFAULT_ENCODING);
					}
				}
				// if (StringUtils.isNotEmpty(config.getLocalEncoding())) {
				// ftpClient.setCharset(Charset.forName(config.getLocalEncoding()));
				// }
			}

			// ftpClient.setFileType(config.getTransferFileType());
			// ftpClient.setControlEncoding("GBK");
			// ftpClient.setCharset(Charset.forName("GBK"));
			// ftpClient.setConnectTimeout(30*1000);
			// ftpClient.setControlKeepAliveReplyTimeout(30*1000);
			// ftpClient.setControlKeepAliveTimeout(30*1000);
			// ftpClient.setDataTimeout(30*1000);
			// ftpClient.setSoTimeout(30*1000);
		}

		// System.out.println(++i + "FTP connection created "+this);
		return ftpClient;
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 */
	@Override
	public void passivateObject(PooledObject<FTPClient> pooledObject) {
		FTPClient ftpClient = pooledObject.getObject();
		try {
			// if (ftpClient != null && ftpClient.isConnected()) {
			// 有些情况logout导致锁定问题，
			// ftpClient.logout();
			// }
		} catch (Exception e) {
			// System.out.println("ftp client logout failed...{}");
			// throw e;
			e.printStackTrace();
		} finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void activateObject(PooledObject<FTPClient> client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyObject(PooledObject<FTPClient> client) {
		passivateObject(client);
	}

	@Override
	public PooledObject<FTPClient> makeObject() throws Exception {
		return new DefaultPooledObject<FTPClient>(create());
	}

	@Override
	public boolean validateObject(PooledObject<FTPClient> client) {
		try {
			return client.getObject().sendNoOp();
		} catch (Exception e) {
			System.out.println("Failed to validate client " + e.getMessage());
			return false;
		}
	}

}