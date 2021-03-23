package com.amituofo.xfs.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amituofo.common.ex.InvalidConfigException;
import com.amituofo.common.util.FileUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.SupportFileSystem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EntryConfigSerializer {
	private static String configLocation = "./conn/";
	private static String ext = ".xfc";

	private final static Map<String, EntryConfig> entrysMap = new HashMap<String, EntryConfig>();

	public EntryConfigSerializer(String configLocation, String ext) {
		this.configLocation = configLocation;
		this.ext = ext;
		File conndir = new File(configLocation);
		if (!conndir.exists()) {
			conndir.mkdirs();
		}
	}

	public void save(EntryConfig setting, boolean overwrite) throws InvalidConfigException {
		if (!overwrite) {
			EntryConfig existConCfg = getByName(setting.getName());
			if (existConCfg != null && !existConCfg.getId().equals(setting.getId())) {
				throw new InvalidConfigException("Entry name " + setting.getName() + " exist!");
			}
		}

		try {
			String s = SimpleConfiguration.toSerializableString(setting.getSimpleConfiguration());

			File file = new File(configLocation + File.separator + setting.getId() + ext);
			FileUtils.writeToFile(s, file, "utf-8", false);
			entrysMap.put(setting.getId(), setting);
		} catch (Exception e) {
			throw new InvalidConfigException(e);
		}
	}

	public boolean exist(EntryConfig config) {
		return entrysMap.get(config.getId()) != null;
	}

	public EntryConfig get(String id) {
		return entrysMap.get(id);
	}

	public EntryConfig getByName(String name) {
		Collection<EntryConfig> entrys = entrysMap.values();
		for (EntryConfig entry : entrys) {
			if (entry.getName().equalsIgnoreCase(name)) {
				return entry;
			}
		}

		return null;
	}

	public void delete(String id) {
		new File(configLocation + File.separator + id + ext).delete();
		entrysMap.remove(id);
	}

	public EntryConfig duplicate(String id) throws InvalidConfigException {
		try {
			File configFile = new File(configLocation + File.separator + id + ext);
			// String s;
			// s = FileUtils.fileToString(configFile, "utf-8");
			// EntryConfig entryConfig = (EntryConfig) SimpleConfiguration.parseSerializableString(s);
			EntryConfig entryConfig = parse(configFile);
			entryConfig.getSimpleConfiguration().generateNewID();

			int i = 1;
			final String newNameTemplate = entryConfig.getName() + "_Copy";
			String newName = newNameTemplate;
			while (getByName(newName) != null) {
				newName = (newNameTemplate + (i++));
			}
			entryConfig.setName(newName);
			save(entryConfig, false);
			return entryConfig;
		} catch (Exception e) {
			throw new InvalidConfigException(e);
		}
	}

	public List<EntryConfig> list() {
		Collection<EntryConfig> c = entrysMap.values();
		final List<EntryConfig> ccs = new ArrayList<EntryConfig>();
		ccs.addAll(c);

		Collections.sort(ccs, new Comparator<EntryConfig>() {
			@Override
			public int compare(EntryConfig o1, EntryConfig o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return ccs;
	}

	public void reload(ConfigLoadEvent configLoadEvent) {
		File[] configFiles = new File(configLocation).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(ext);// || name.endsWith("hcc");
			}
		});

		entrysMap.clear();
		if (configFiles != null) {
			for (int i = 0; i < configFiles.length; i++) {
				try {
					// String s = FileUtils.fileToString(configFiles[i], "utf-8");
					// EntryConfig entryConfig = (EntryConfig) SimpleConfiguration.parseSerializableString(s);
					EntryConfig entryConfig = parse(configFiles[i]);

					if (configLoadEvent != null) {
						boolean isValid = configLoadEvent.validate(entryConfig);
						if (isValid) {
							entrysMap.put(entryConfig.getId(), entryConfig);
						}
					}
					// GlobalContext.getLogger().info("Loading entry config " + setting.getName() + " " + setting.getId());
				} catch (Throwable e) {
					// System.out.println("Failed to load " + configFiles[i]);
					e.printStackTrace();
					// GlobalContext.getLogger().error("Error reading entry settings.", e);
					// throw new ServiceException(e);
					if (configLoadEvent != null) {
						configLoadEvent.failedLoading(configFiles[i], e);
					}
				}
			}
		}
	}

	public static EntryConfig parse(File configFile) throws IOException, InstantiationException, IllegalAccessException {
		String config = FileUtils.fileToString(configFile, "utf-8");

		ObjectMapper objectMapper = new ObjectMapper();
		HashMap<String, Object> configMap = objectMapper.readValue(config, new TypeReference<HashMap<String, Object>>() {
		});

		String systemid = (String) configMap.get(EntryConfigBase.FILE_SYSTEM_ID);
		Class<? extends SimpleConfigurationEntry> cls = SupportFileSystem.lookup(systemid);
		EntryConfig entryConfig = (EntryConfig) SimpleConfiguration.parseSerializableString(configMap, cls);
		return entryConfig;
	}

	public static void save(EntryConfig setting, String saveLocation) throws InvalidConfigException {
		try {
			String s = SimpleConfiguration.toSerializableString(setting.getSimpleConfiguration());
			String name = setting.getName();
			name = name.replace("\\", "").replace("/", "").replace(":", "").replace("?", "").replace("*", "").replace(">", "").replace("<", "").replace("|", "");
			File file = new File(URLUtils.catFilePath(saveLocation, name + ext));
			FileUtils.writeToFile(s, file, "utf-8", false);
		} catch (Exception e) {
			throw new InvalidConfigException(e);
		}
	}
}
