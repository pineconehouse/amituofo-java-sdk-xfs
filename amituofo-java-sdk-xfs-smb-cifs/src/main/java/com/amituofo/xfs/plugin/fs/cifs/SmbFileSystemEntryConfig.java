package com.amituofo.xfs.plugin.fs.cifs;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.RemoteEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class SmbFileSystemEntryConfig extends RemoteEntryConfig {
	public static final String SYSTEM_NAME = "Common Internet File System (CIFS/SMB)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP019811212";

	public static final String DOMAIN = "DOMAIN";

	public SmbFileSystemEntryConfig() {
		super(SmbFileSystemEntryConfig.class, SYSTEM_ID, "", "", FILE_PS, 445);
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new SmbFileSystemEntry(this, (SmbFileSystemPreference) perference);
	}

	@Override
	public SmbFileSystemPreference createPreference() {
		return new SmbFileSystemPreference();
	}

	@Override
	public void setUser(String user) {
		if (StringUtils.isNotEmpty(user)) {
			String domain = null;
			String username = user;
			int domainsp = user.indexOf('\\');
			if (domainsp != -1) {
				domain = user.substring(0, domainsp);
				username = user.substring(domainsp + 1);
			} else {
				domainsp = user.indexOf('/');
				if (domainsp != -1) {
					domain = user.substring(0, domainsp);
					username = user.substring(domainsp + 1);
				} else {
					domainsp = user.indexOf('@');
					if (domainsp != -1) {
						username = user.substring(0, domainsp);
						domain = user.substring(domainsp + 1);
					}
				}
			}
			super.setUser(username);
			this.setDomain(domain);
		} else {
			super.setUser("");
			this.setDomain(null);
		}
	}

	public String getSharename() {
		return this.getDefaultHome();
	}

	public void setSharename(String name) {
		this.setDefaultHome(name);
	}

	public void setDomain(String domain) {
		config.set(DOMAIN, domain);
	}

	public String getDomain() {
		return config.getString(DOMAIN);
	}

	@Override
	protected void validate() throws InvalidParameterException {
		super.validate();
		ValidUtils.invalidIfEmpty(getSharename(), "Share name must be specificed!");
	}
}
