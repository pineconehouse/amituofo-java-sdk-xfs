package com.amituofo.xfs.plugin.fs.sftp;

import com.amituofo.xfs.service.impl.FileSystemPreferenceBase;

public class SFTPFileSystemPreference extends FileSystemPreferenceBase {
	private String controlEncoding;

	// /** 本地字符编码 */
	// private static String LOCAL_CHARSET = "GBK";
	//
	// // FTP协议里面，规定文件名编码为iso-8859-1
	// private static String SERVER_CHARSET = "ISO-8859-1";

	// private String localEncoding = "utf-8";
	// private String remoteEncoding = "utf-8";

	// public String getLocalEncoding() {
	// return localEncoding;
	// }
	//
	// public void setLocalEncoding(String charset) {
	// this.localEncoding = charset;
	// }

	// public String getRemoteEncoding() {
	//// return remoteEncoding;
	// return "ISO-8859-1";
	// }

	// public void setRemoteEncoding(String remoteEncoding) {
	// this.remoteEncoding = remoteEncoding;
	// }

	public void setControlEncoding(String encoding) {
		controlEncoding = encoding;
	}

	public String getControlEncoding() {
		return controlEncoding;
	}

}
