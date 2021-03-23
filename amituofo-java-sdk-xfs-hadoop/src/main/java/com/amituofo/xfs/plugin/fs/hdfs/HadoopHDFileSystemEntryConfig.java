package com.amituofo.xfs.plugin.fs.hdfs;

import org.apache.hadoop.conf.Configuration;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.config.RemoteEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class HadoopHDFileSystemEntryConfig extends RemoteEntryConfig {
	public static final String SYSTEM_NAME = "Hadoop Distributed File System (DHFS)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP00809342";

	public HadoopHDFileSystemEntryConfig() {
		super(HadoopHDFileSystemEntryConfig.class, SYSTEM_ID, "", URL_ROOT_PATH, URL_PS, 8020);
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new HadoopHDFileSystemEntry(this, (HadoopHDFileSystemPreference) perference);
	}

	@Override
	public HadoopHDFileSystemPreference createPreference() {
		return new HadoopHDFileSystemPreference();
	}

	public Configuration getHadoopConfiguration() {
		System.setProperty("HADOOP_USER_NAME", this.getUser());
		Configuration conf = new Configuration();
//		UserGroupInformation .createRemoteUser(this.getUser()).doAs(new PrivilegedAction<Void>() {
//
//			@Override
//			public Void run() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		});
		 conf.set("fs.defaultFS", "hdfs://" + getHost() + ":" + getPort());
//		 conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		// conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
		//// conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		// // Set HADOOP user
		// System.setProperty("HADOOP_USER_NAME", "song");
		// System.setProperty("hadoop.home.dir", "/");
		return conf;
	}
}
