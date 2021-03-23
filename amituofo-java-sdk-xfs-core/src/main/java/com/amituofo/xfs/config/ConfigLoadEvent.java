package com.amituofo.xfs.config;

import java.io.File;

public interface ConfigLoadEvent {

	boolean validate(EntryConfig setting);
	
	void failedLoading(File file, Throwable e);

}
