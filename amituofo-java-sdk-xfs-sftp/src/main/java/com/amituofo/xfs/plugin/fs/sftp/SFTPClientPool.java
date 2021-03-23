package com.amituofo.xfs.plugin.fs.sftp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.jcraft.jsch.ChannelSftp;

/**
 * 自定义实现ftp连接池
 * 
 * @author PYY
 *
 */
public class SFTPClientPool implements ObjectPool<ChannelSftp> {
	private static final int MIN_POOL_SIZE = 1;
	private static final int MAX_POOL_SIZE = 50;

	private final BlockingQueue<ChannelSftp> pool;
	private final Set<ChannelSftp> connInUsed = new HashSet<ChannelSftp>();

	private SFTPClientFactory factory;

	public SFTPClientPool(SFTPClientFactory factory) throws Exception {
		this(MIN_POOL_SIZE, MAX_POOL_SIZE, factory);
	}

	public SFTPClientPool(int minPoolSize, int maxPoolSize, SFTPClientFactory factory) throws Exception {
		this.factory = factory;
		this.pool = new ArrayBlockingQueue<ChannelSftp>(maxPoolSize);
		initPool(minPoolSize);
	}

	/**
	 * 初始化连接池
	 * 
	 * @param minPoolSize
	 *            最小连接数
	 * @throws Exception
	 */
	private void initPool(int minPoolSize) throws Exception {
		int count = 0;
		while (count < minPoolSize) {
			this.addObject();
			count++;
		}
	}

	/**
	 * 从连接池中获取对象
	 */
	@Override
	public ChannelSftp borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
		synchronized (connInUsed) {
			if (pool.size() == 0 && connInUsed.size() < MAX_POOL_SIZE) {
				addNewFTPSClient();
			}

			ChannelSftp client = pool.take();
			if (client == null) {
				client = addNewFTPSClient();
			} else if (!factory.validateObject(new DefaultPooledObject<ChannelSftp>(client))) {
				try {
					invalidateObject(client);
				} catch (Exception e) {
					e.printStackTrace();
				}

				client = addNewFTPSClient();
			}

			connInUsed.add(client);
			// System.out.println("borrowObject="+client);
			return client;
		}
	}

	/**
	 * 返还一个对象(链接)
	 */
	@Override
	public void returnObject(ChannelSftp client) {
		if ((client != null)) {
			// System.out.println("returnObject="+client);
			synchronized (connInUsed) {
				boolean offered = false;
				// try {
				// offered = pool.offer(client, 3, TimeUnit.SECONDS);
				offered = pool.offer(client);
				// } catch (InterruptedException e1) {
				// // e1.printStackTrace();
				// offered = false;
				// }

				if (!offered) {
					factory.destroyObject(new DefaultPooledObject<ChannelSftp>(client));
					connInUsed.remove(client);
				} else {
					connInUsed.remove(client);
				}
			}
		}
	}

	/**
	 * 移除无效的对象(FTP客户端)
	 */
	@Override
	public void invalidateObject(ChannelSftp client) throws Exception {
		factory.destroyObject(new DefaultPooledObject<ChannelSftp>(client));
		pool.remove(client);
		connInUsed.remove(client);
	}

	/**
	 * 增加一个新的链接，超时失效
	 */
	@Override
	public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
		addNewFTPSClient();
	}

	private ChannelSftp addNewFTPSClient() throws Exception, IllegalStateException, UnsupportedOperationException {
		PooledObject<ChannelSftp> client = factory.makeObject();
		// pool.offer(client, 3, TimeUnit.SECONDS);
		pool.offer(client.getObject());
//		System.out.println("New client " + client.getLocalPort());
		return client.getObject();
	}

	/**
	 * 获取空闲链接数(这里暂不实现)
	 */
	@Override
	public int getNumIdle() {
		return 0;
	}

	/**
	 * 获取正在被使用的链接数
	 */
	@Override
	public int getNumActive() {
		return 0;
	}

	@Override
	public void clear() throws Exception, UnsupportedOperationException {

	}

	/**
	 * 关闭连接池
	 */
	@Override
	public void close() {
		for (ChannelSftp client : connInUsed) {
			factory.destroyObject(new DefaultPooledObject<ChannelSftp>(client));
		}

		connInUsed.clear();

		for (Iterator<ChannelSftp> it = pool.iterator(); it.hasNext();) {
			ChannelSftp client = (ChannelSftp) it.next();
			factory.destroyObject(new DefaultPooledObject<ChannelSftp>(client));
		}

		pool.clear();
	}

}