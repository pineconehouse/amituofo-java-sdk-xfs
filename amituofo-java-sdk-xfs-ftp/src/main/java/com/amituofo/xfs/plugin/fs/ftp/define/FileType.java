package com.amituofo.xfs.plugin.fs.ftp.define;

import org.apache.commons.net.ftp.FTP;

public enum FileType {
	ASCII(FTP.ASCII_FILE_TYPE), BINARY(FTP.BINARY_FILE_TYPE), EBCDIC(FTP.EBCDIC_FILE_TYPE), LOCAL(FTP.LOCAL_FILE_TYPE);

	private int typeCode;

	FileType(int typeCode) {
		this.typeCode = typeCode;
	}

	public int code() {
		return typeCode;
	}
}
