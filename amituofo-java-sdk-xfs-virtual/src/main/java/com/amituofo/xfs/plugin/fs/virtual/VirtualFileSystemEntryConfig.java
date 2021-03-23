package com.amituofo.xfs.plugin.fs.virtual;

import java.io.File;

import com.amituofo.common.define.SizeUnit;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.RemoteEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class VirtualFileSystemEntryConfig extends RemoteEntryConfig {
	public static final String SYSTEM_NAME = "Virtual File System";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP53467235";

	public static final String FILE_COUNT_MIN = "FILE_COUNT_MIN";
	public static final String FILE_COUNT_MAX = "FILE_COUNT_MAX";

	public static final String FILE_SIZE_MIN = "FILE_SIZE_MIN";
	public static final String FILE_SIZE_MAX = "FILE_SIZE_MAX";

	public static final String FILE_SIZE_UNIT_MIN = "FILE_SIZE_UNIT_MIN";
	public static final String FILE_SIZE_UNIT_MAX = "FILE_SIZE_UNIT_MAX";

	public static final String FILE_NAME_LEN_MIN = "FILE_NAME_LEN_MIN";
	public static final String FILE_NAME_LEN_MAX = "FILE_NAME_LEN_MAX";

	public static final String FILE_NAME_ALGORITHM = "FILE_NAME_ALGORITHM";
	public static final String FILE_NAME_CHAR_POOL = "FILE_NAME_CHAR_POOL";

	public static final String FOLDER_COUNT_MIN = "FOLDER_COUNT_MIN";
	public static final String FOLDER_COUNT_MAX = "FOLDER_COUNT_MAX";

	public static final String FOLDER_DEEP_MAX = "FOLDER_DEEP_MAX";
	
	public static final String DATABASE_URL = "DATABASE_URL";
	public static final String DATABASE_DRIVER = "DATABASE_DRIVER";
	
//	private static final RootItem DEFAULT_SECTION = new DefaultSection("VirtualSpace-0");

	public VirtualFileSystemEntryConfig() {
		super(VirtualFileSystemEntryConfig.class, SYSTEM_ID, "", URL_ROOT_PATH, URL_PS, 3306);
	}

	@Override
	protected void validate() throws InvalidParameterException {
		ValidUtils.invalidIfNotA2zOr0to9(this.getName(), "Name must consist of English letters and numbers (a-z A-Z 0-9)");
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new VirtualFileSystemEntry(this, (VirtualFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new VirtualFileSystemPreference();
	}

	public String getDatabaseUrl() {
		return config.getString(DATABASE_URL);
	}

	public void setDatabaseUrl(String url) {
		config.set(DATABASE_URL, url);
	}

	public String getDatabaseDriver() {
		return config.getString(DATABASE_DRIVER);
	}

	public void setDatabaseDriver(String driver) {
		config.set(DATABASE_DRIVER, driver);
	}
	
	public String getFilenameCharPool() {
		return StringUtils.encodeBase64String(config.getString(FILE_NAME_CHAR_POOL));
	}

	public void setFilenameCharPool(String str) {
		config.set(FILE_NAME_CHAR_POOL, StringUtils.encodeBase64String(str));
	}

	public String getFilenameAlgorithm() {
		return config.getString(FILE_NAME_ALGORITHM);
	}

	public void setFilenameAlgorithm(String algorithm) {
		config.set(FILE_NAME_ALGORITHM, algorithm);
	}

	public int[] getFileCountRange() {
		return new int[] { config.getInteger(FILE_COUNT_MIN, 0), config.getInteger(FILE_COUNT_MAX, 0) };
	}

	public void setFileCountRange(int min, int max) {
		config.set(FILE_COUNT_MIN, min);
		config.set(FILE_COUNT_MAX, max);
	}

	public int[] getFilenameLengthRange() {
		return new int[] { config.getInteger(FILE_NAME_LEN_MIN, 0), config.getInteger(FILE_NAME_LEN_MAX, 50) };
	}

	public void setFileNameLengthRange(int min, int max) {
		config.set(FILE_NAME_LEN_MIN, min);
		config.set(FILE_NAME_LEN_MAX, max);
	}

	public int[] getFolderCountRange() {
		return new int[] { config.getInteger(FOLDER_COUNT_MIN, 0), config.getInteger(FOLDER_COUNT_MAX, 0) };
	}

	public void setFolderCountRange(int min, int max) {
		config.set(FOLDER_COUNT_MIN, min);
		config.set(FOLDER_COUNT_MAX, max);
	}

	public int getMaxFolderDeep() {
		return config.getInteger(FOLDER_DEEP_MAX, 0);
	}

	public void setMaxFolderDeep(int max) {
		config.set(FOLDER_DEEP_MAX, max);
	}

	public int getRootCount() {
		return 1;
	}

	public long[] getFilesizeRange() {
		return new long[] { config.getLong(FILE_SIZE_MIN, 0L), config.getLong(FILE_SIZE_MAX, 1024L * 1024L * 2) };
		// return new long[] { 0, 1024L * 1024L * 2 };
		// return new long[] { 0, 1024L * 1024L * 1024L * 20 };
	}

	public long[] getFilesizeInBytesRange() {
		SizeUnit[] units = getFilesizeUnits();
		return new long[] { config.getLong(FILE_SIZE_MIN, 0L) * units[0].getUnitSize(), config.getLong(FILE_SIZE_MAX, 1024L * 1024L * 2) * units[1].getUnitSize() };
		// return new long[] { 0, 1024L * 1024L * 2 };
		// return new long[] { 0, 1024L * 1024L * 1024L * 20 };
	}

	public SizeUnit[] getFilesizeUnits() {
		return new SizeUnit[] { SizeUnit.valueOf(config.getString(FILE_SIZE_UNIT_MIN, SizeUnit.Bytes.name())),
				SizeUnit.valueOf(config.getString(FILE_SIZE_UNIT_MAX, SizeUnit.Bytes.name())) };
	}

	public void setFilesizeRange(long min, SizeUnit minSizeUnit, long max, SizeUnit maxSizeUnit) {
		config.set(FILE_SIZE_MIN, min);
		config.set(FILE_SIZE_MAX, max);

		config.set(FILE_SIZE_UNIT_MIN, minSizeUnit.name());
		config.set(FILE_SIZE_UNIT_MAX, maxSizeUnit.name());
	}

	public File getVirtualDataLocation() {
		final String systemTempFolder = System.getProperty("java.io.tmpdir");
		final String scetwf = URLUtils.catFilePath(systemTempFolder, "virtual-data");
		File TEMP_WORKING_FOLDER = new File(scetwf + File.separator + this.getName());
		if (!TEMP_WORKING_FOLDER.exists()) {
			TEMP_WORKING_FOLDER.mkdirs();
		}

		return TEMP_WORKING_FOLDER;// .getAbsolutePath();
	}

	@Override
	public void validateConfig() throws InvalidParameterException {
		super.validateConfig();
		ValidUtils.invalidIfEmpty(this.getDatabaseUrl(), "Database url could not be empty!");
		ValidUtils.invalidIfEmpty(this.getDatabaseDriver(), "Database driver could not be empty!");
	}

//	@Override
//	public RootItem getDefaultSection() {
//		return DEFAULT_SECTION;
//	}

}
