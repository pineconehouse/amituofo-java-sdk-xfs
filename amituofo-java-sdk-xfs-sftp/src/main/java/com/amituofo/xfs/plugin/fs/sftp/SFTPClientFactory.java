package com.amituofo.xfs.plugin.fs.sftp;

import java.io.FileNotFoundException;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.FileUtils;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.sftp.define.AuthUserInfo;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 连接池工厂类
 * 
 * @author PYY
 *
 */
public class SFTPClientFactory implements PooledObjectFactory<ChannelSftp> {

	private SFTPFileSystemEntryConfig config;

	public SFTPClientFactory(SFTPFileSystemEntryConfig config) {
		this.config = config;
	}

	private synchronized ChannelSftp create() throws Exception {
		JSch jsch = new JSch();
		ChannelSftp ftpClient = null;

		String user = config.getUser();// host.substring(0, host.indexOf('@'));
		String host = config.getHost();// host.substring(host.indexOf('@')+1);
		int port = config.getPort();

		AuthUserInfo ui = new AuthUserInfo();
		ui.setDefaultPromptSelectYes(true);
		ui.setDefaultPassword(config.getPassword());

		Session session = jsch.getSession(user, host, port);
		// username and password will be given via UserInfo interface.
		String privateKeyfile = config.getPrivateKeyflie();
		if (StringUtils.isNotEmpty(privateKeyfile)) {
			if (FileUtils.isFileExist(privateKeyfile)) {
				String passphrase = config.getPassphrase();
				try {
					if (StringUtils.isNotEmpty(passphrase)) {
						jsch.addIdentity(privateKeyfile, passphrase);
						ui.setDefaultPassphrase(passphrase);
					} else {
						jsch.addIdentity(privateKeyfile);
					}
				} catch (Exception e) {
					throw new ServiceException("Private key is invalid!", e);
				}
			} else {
				throw new FileNotFoundException("Private file " + privateKeyfile + " not found!");
			}
		}

		session.setUserInfo(ui);

		try {
			session.connect();
			Channel channel = session.openChannel("sftp");
			ftpClient = (ChannelSftp) channel;
			ftpClient.connect();
		} catch (Exception e) {
			if (e.getMessage().equalsIgnoreCase("Auth fail")) {
				throw new ServiceException("Authentication error, please confirm that your login information is correct", e);
			} else {
				throw new ServiceException("Unable to connect to target server!", e);
			}
		}

		try {
			String encoding = config.getFilenameEncoding();
			int sversion = ftpClient.getServerVersion();
			if (3 <= sversion && sversion <= 5 && !encoding.equalsIgnoreCase("UTF-8")) {
			} else {
				ftpClient.setFilenameEncoding(encoding);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// throw new ServiceException("Failed to set filename encoding, Please change filename encoding configuration and try again!", e);
		}
		
		return ftpClient;
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 */
	@Override
	public void passivateObject(PooledObject<ChannelSftp> pooledObject) {
		ChannelSftp ftpClient = pooledObject.getObject();
		try {
			// if (ftpClient != null && ftpClient.isConnected()) {
			// 有些情况logout导致锁定问题，
			// ftpClient.logout();
			// }
			pooledObject.getObject().disconnect();
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
	public void activateObject(PooledObject<ChannelSftp> client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyObject(PooledObject<ChannelSftp> client) {
		passivateObject(client);
	}

	@Override
	public PooledObject<ChannelSftp> makeObject() throws Exception {
		return new DefaultPooledObject<ChannelSftp>(create());
	}

	@Override
	public boolean validateObject(PooledObject<ChannelSftp> client) {
		try {
			return client.getObject().isConnected();
		} catch (Exception e) {
			// System.out.println("Failed to validate client " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

}