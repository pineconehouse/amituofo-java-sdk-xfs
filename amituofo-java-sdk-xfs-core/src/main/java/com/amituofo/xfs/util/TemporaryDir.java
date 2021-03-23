package com.amituofo.xfs.util;

import java.io.File;
import java.io.IOException;

import com.amituofo.common.util.RandomUtils;

public class TemporaryDir {
	private static File SYSTEM_TMP_FOLDER;

	public final static TemporaryDir INSTANCE = new TemporaryDir();

	public TemporaryDir() {

		try {
			File temp = File.createTempFile("TF" + RandomUtils.randomInt(100000, 999999), "");
			SYSTEM_TMP_FOLDER = temp.getParentFile();
			temp.delete();

			// SYSTEM_CONFIG = new File("./config/system.config");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public File getTempWorkingFolder() {
		return SYSTEM_TMP_FOLDER;
	}

	public File getTempWorkingFile(String name) {
		File tempWorkingDir = new File(getTempWorkingFolder().getAbsolutePath() + File.separator + "TEMP_WF" + File.separator);
		tempWorkingDir.mkdirs();
		return new File(tempWorkingDir.getAbsolutePath() + File.separator + name);
	}

	public File getTempWorkingFolder(String name) {
		File tempWorkingDir = new File(getTempWorkingFolder().getAbsolutePath() + File.separator + "TEMP_WF" + File.separator + name);
		tempWorkingDir.mkdirs();
		return tempWorkingDir;
	}

	public File getRandomTempWorkingFolder() {
		File tempWorkingDir = new File(getTempWorkingFolder().getAbsolutePath() + File.separator + "TEMP_WF" + File.separator + RandomUtils.randomInt(100000, 999999));
		tempWorkingDir.mkdirs();
		return tempWorkingDir;
	}

	public void clearTemp() {

	}

}
