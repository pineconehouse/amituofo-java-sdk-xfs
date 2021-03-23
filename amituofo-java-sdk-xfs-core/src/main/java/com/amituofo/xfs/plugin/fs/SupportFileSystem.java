package com.amituofo.xfs.plugin.fs;

import java.util.HashMap;
import java.util.Map;

import com.amituofo.xfs.config.SimpleConfigurationEntry;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.logic.LogicFileSystemEntryConfig;

public class SupportFileSystem {
	private final static Map<String, Class<? extends SimpleConfigurationEntry>> SUPPORT_ENTRY_CONFIG = new HashMap<String, Class<? extends SimpleConfigurationEntry>>();

	static {
		SUPPORT_ENTRY_CONFIG.put(LocalFileSystemEntryConfig.SYSTEM_ID, LocalFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(FTPFileSystemEntryConfig.SYSTEM_ID, FTPFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(SFTPFileSystemEntryConfig.SYSTEM_ID, SFTPFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(HCPFileSystemEntryConfig.SYSTEM_ID, HCPFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(MQEFileSystemEntryConfig.SYSTEM_ID, MQEFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(OSSFileSystemEntryConfig.SYSTEM_ID, OSSFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(AmazonS3FileSystemEntryConfig.SYSTEM_ID, AmazonS3FileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(CompatibleS3FileSystemEntryConfig.SYSTEM_ID, CompatibleS3FileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(BlobFileSystemEntryConfig.SYSTEM_ID, BlobFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(HadoopHDFileSystemEntryConfig.SYSTEM_ID, HadoopHDFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(VirtualFileSystemEntryConfig.SYSTEM_ID, VirtualFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(MemoryFileSystemEntryConfig.SYSTEM_ID, MemoryFileSystemEntryConfig.class);
//		SUPPORT_ENTRY_CONFIG.put(SmbFileSystemEntryConfig.SYSTEM_ID, SmbFileSystemEntryConfig.class);
		SUPPORT_ENTRY_CONFIG.put(LogicFileSystemEntryConfig.SYSTEM_ID, LogicFileSystemEntryConfig.class);
	}

	public static void register(String systemid, Class<? extends SimpleConfigurationEntry> cls) {
		SUPPORT_ENTRY_CONFIG.put(systemid, cls);
	}
	
	public static Class<? extends SimpleConfigurationEntry> lookup(String systemid){
		return SUPPORT_ENTRY_CONFIG.get(systemid);
	}

}
