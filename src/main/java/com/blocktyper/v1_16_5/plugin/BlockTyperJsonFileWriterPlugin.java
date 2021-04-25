package com.blocktyper.v1_16_5.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class BlockTyperJsonFileWriterPlugin extends BlockTyperLoggerPlugin {

	public BlockTyperJsonFileWriterPlugin() {
		super();
	}
	
	protected DataBackupTask dataBackupTask;
	protected Map<String, Object> data = new HashMap<String, Object>();

	@Override
	public void onEnable() {
		super.onEnable();

		try {
			String dataDirName = config.dataFolderName();

			File dataFolder = new File(getDataFolder(), config.dataFolderName());

			if (dataFolder != null && dataFolder.exists()) {
				debugInfo("data folder was located");
			} else if (dataFolder != null) {
				if (dataFolder.mkdirs()) {
					debugInfo("Data dir created: " + dataDirName);
				} else {
					debugWarning("Could not create data dir: " + dataDirName);
				}
			}

			int dataBackupFrequencySec = config.dataBackupFrequencySec();

			if (dataBackupFrequencySec >= 0) {
				dataBackupFrequencySec = dataBackupFrequencySec >= 5 ? dataBackupFrequencySec : 30;
				debugInfo("Starting data backup service to run every " + dataBackupFrequencySec + " sec");
				dataBackupTask = new DataBackupTask(this);
				dataBackupTask.runTaskTimer(this, dataBackupFrequencySec * 20L, dataBackupFrequencySec * 20L);
			} else {
				debugInfo("no backup task scheduled");
			}

		} catch (Exception e) {
			warning("Error while enabling BlockTyperConfig: " + e.getMessage());
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();

		int dataBackupFrequencySec = config.dataBackupFrequencySec();
		if (dataBackupFrequencySec >= 0) {

			info("Disable BlockTyperPlugin");
			if (data == null || data.isEmpty()) {
				info("No data to write");
				return;
			}

			new DataBackupTask(this).run();
		}
	}

	/////////////
	// DATA//////
	/////////////
	public boolean setData(String key, Object value, boolean flush) {
		if (key == null) {
			return false;
		}

		key = getCleanedDataKey(key);

		if (flush) {
			synchronized (data) {
				data.put(key, value);
				try {
					writeJsonDataFile(key, getDataFolder());
				} catch (BlockTyperPluginException e) {
					warning(e.getMessage());
					return false;
				}
			}
		} else {
			data.put(key, value);
		}

		return true;
	}

	public boolean setData(String key, Object value) {
		return setData(key, value, false);
	}

	public Map<String, Object> getAllData() {
		return data;
	}

	public <T> T getTypeData(String key, Class<T> type) {
		if (key == null) {
			return null;
		}

		key = getCleanedDataKey(key);
		if (data.containsKey(key)) {
			debugInfo("getting data for '" + key + "' from cache");
			T inst = type.cast(data.get(key));
			return inst;
		}
		debugInfo("getting data for '" + key + "' from file system");

		try {
			File file = getDataFile(key);

			if (file != null) {
				for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
					if (line != null && !line.isEmpty()) {
						T obj = deserializeJson(line, type);
						if (obj == null) {
							continue;
						} else {
							data.put(key, obj);
							break;
						}
					}
				}
			} else {
				debugInfo("no file for '" + key + "' found in file system");
			}
		} catch (JsonSyntaxException e) {
			debugInfo("JsonSyntaxException while getting file from file sytem for '" + key + "'. Message: "
					+ e.getMessage());
		} catch (IOException e) {
			debugInfo("IOException while getting file from file sytem for '" + key + "'. Message: " + e.getMessage());
		}

		if (data.containsKey(key))
			return type.cast(data.get(key));

		return null;
	}

	protected String getDataFileSuffix() {
		return ".json";
	}

	protected String getCleanedDataKey(String key) {
		if (key == null) {
			return null;
		}
		return key.toLowerCase().trim();
	}

	protected File getDataFile(String key) {
		key = getCleanedDataKey(key);
		if (key == null) {
			return null;
		}

		File dataFolder = new File(getDataFolder(), config.dataFolderName());
		if (dataFolder.exists()) {
			if (dataFolder.isDirectory() && dataFolder.listFiles() != null) {
				for (File file : dataFolder.listFiles()) {
					if (file != null && file.isFile()) {
						String cleanKey = getCleanedDataKey(file.getName());
						if (cleanKey != null && cleanKey.equals(key + getDataFileSuffix())) {
							return file;
						}
					}
				}
			}
		}
		return null;
	}

	protected void writeJsonDataFile(String key, File parentFolder) throws BlockTyperPluginException {

		if (key == null) {
			throw new BlockTyperPluginException("writeJsonDataFile: null data key");
		}
		if (parentFolder == null) {
			throw new BlockTyperPluginException("writeJsonDataFile: null parentFolder");
		}
		try {
			File dataFolder = new File(parentFolder, config.dataFolderName());
			if (dataFolder.exists() || dataFolder.mkdirs()) {

				debugInfo("Writing data for key: " + key);

				Object value = data.get(key);

				if (value != null) {
					File fileForKey = new File(dataFolder, key + getDataFileSuffix());

					PrintWriter writer = null;
					try {
						writer = new PrintWriter(fileForKey.getAbsolutePath(), "UTF-8");
						String json = new Gson().toJson(value);
						writer.println(json);
					} catch (FileNotFoundException e) {
						warning("writeJsonDataFile - FileNotFoundException: " + e.getMessage());
					} catch (UnsupportedEncodingException e) {
						warning("writeJsonDataFile - FileNotFoundException: " + e.getMessage());
					} catch (Exception e) {
						warning("writeJsonDataFile - FileNotFoundException: " + e.getMessage());
					} finally {
						if (writer != null) {
							try {
								writer.close();
							} catch (Exception e) {
								warning("could not close writer");
							}
						}
					}
				}

			} else {
				throw new BlockTyperPluginException("Could not create data dirs");
			}
		} catch (BlockTyperPluginException e) {
			throw e;
		} catch (Exception e) {
			throw new BlockTyperPluginException("unexpected exception. " + e.getMessage());
		}
	}

	public <T> T deserializeJsonSafe(String json, Class<T> type) {
		if (json == null) {
			return null;
		}

		try {
			return deserializeJson(json, type);
		} catch (JsonSyntaxException e) {
			debugInfo("JsonSyntaxException while deserializing json '" + json + "'. Message: " + e.getMessage());
		}

		return null;
	}

	protected <T> T deserializeJson(String json, Class<T> type) throws JsonSyntaxException {
		if (json == null) {
			return null;
		}

		T obj = new Gson().fromJson(json, type);
		return obj;
	}

}
