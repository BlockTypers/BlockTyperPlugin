package com.blocktyper.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.config.BlockTyperConfig;
import com.blocktyper.localehelper.LocaleHelper;
import com.blocktyper.recipes.BlockTyperRecipeRegistrar;
import com.blocktyper.recipes.IBlockTyperRecipeRegistrar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class BlockTyperPlugin extends JavaPlugin implements IBlockTyperPlugin {

	public static BlockTyperPlugin plugin;
	private BlockTyperConfig config;

	private DataBackupTask dataBackupTask;
	private Map<String, Object> data = new HashMap<String, Object>();

	protected Locale locale;

	IBlockTyperRecipeRegistrar registrar;

	public BlockTyperPlugin() {
		super();
		plugin = this;
		this.config = BlockTyperConfig.getConfig(this);
		locale = new LocaleHelper(getLogger(), getFile() != null ? getFile().getParentFile() : null).getLocale();

		if (locale == null) {
			getLogger().info("Using default locale.");
			locale = Locale.getDefault();
		}
	}

	public BlockTyperConfig config() {
		return config;
	}

	public IBlockTyperRecipeRegistrar recipeRegistrar() {
		return registrar;
	}

	@Override
	public void onEnable() {
		super.onEnable();

		section(false, DASHES);
		try {
			String dataDirName = config.dataFolderName();

			File dataFolder = new File(getDataFolder(), config.dataFolderName());

			if (dataFolder != null && dataFolder.exists()) {
				info("data folder was located");
			} else if (dataFolder != null) {
				if (dataFolder.mkdirs()) {
					info("Data dir created: " + dataDirName);
				} else {
					warning("Could not create data dir: " + dataDirName);
				}
			}

			int dataBackupFrequencySec = config.dataBackupFrequencySec();
			dataBackupFrequencySec = dataBackupFrequencySec >= 5 ? dataBackupFrequencySec : 30;

			info("Starting data backup service to run every " + dataBackupFrequencySec + " sec");
			dataBackupTask = new DataBackupTask(this);
			dataBackupTask.runTaskTimer(this, dataBackupFrequencySec * 20L, dataBackupFrequencySec * 20L);

		} catch (Exception e) {
			warning("Error while enabling BlockTyperConfig: " + e.getMessage());
		}

		section(false, DASHES);
		section(false, DASHES);
		registrar = new BlockTyperRecipeRegistrar(this);
		registrar.registerRecipesFromConfig();
		section(false, DASHES);
		section(false, DASHES);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		section(false, DASHES);
		info("Disable BlockTyperPlugin");
		if (data == null || data.isEmpty()) {
			info("No data to write");
			return;
		}
		new DataBackupTask(this).run();
		section(false, DASHES);
	}

	//////////////
	// MESSAGES///
	//////////////
	public String getLocalizedMessage(String key) {

		if (key == null) {
			return "null-key";
		}

		String value = key;

		try {
			ResourceBundle bundle = getBundle();
			value = bundle != null ? bundle.getString(key) : key;

		} catch (Exception e) {
			debugWarning("Unexpected error getting localized string for key(" + key + "). Message: " + e.getMessage());
			value = null;
		}

		value = value != null && !value.trim().isEmpty() ? value : key;

		return value;
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
			plugin.debugInfo("getting data for '" + key + "' from cache");
			T inst = type.cast(data.get(key));
			return inst;
		}
		plugin.debugInfo("getting data for '" + key + "' from file system");

		try {
			File file = getDataFile(key);

			if (file != null) {
				for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
					if (line != null && !line.isEmpty()) {
						T obj = new Gson().fromJson(line, type);
						if (obj == null) {
							continue;
						} else {
							data.put(key, obj);
							break;
						}
					}
				}
			} else {
				plugin.debugInfo("no file for '" + key + "' found in file system");
			}
		} catch (JsonSyntaxException e) {
			plugin.debugInfo("JsonSyntaxException while getting file from file sytem for '" + key + "'. Message: "
					+ e.getMessage());
		} catch (IOException e) {
			plugin.debugInfo(
					"IOException while getting file from file sytem for '" + key + "'. Message: " + e.getMessage());
		}

		if(data.containsKey(key))
			return type.cast(data.get(key));
		
		return null;
	}

	/////////////
	// LOGGING///
	/////////////

	public static final String EMPTY = "";
	public static final String DASHES = "-----------------------------------";
	public static final String HASHES = "###################################";
	public static final int DASHES_TOP = 1;
	public static final int DASHES_BOTTOM = 2;
	public static final int DASHES_TOP_AND_BOTTOM = 3;
	public static final int DEFAULT_WARNING_STACK_TRACE_COUNT = -1;

	public void info(String info) {
		info(info, null);
	}

	public void info(String warning, Integer mode) {
		info(warning, mode, null);
	}

	public void info(String warning, Integer mode, Integer stackTraceCount) {
		log(warning, mode, false, stackTraceCount);
	}

	public void warning(String warning) {
		warning(warning, null);
	}

	public void warning(String warning, Integer mode) {
		warning(warning, mode, null);
	}

	public void warning(String warning, Integer mode, Integer stackTraceCount) {
		log(warning, mode, true, stackTraceCount);
	}

	public void debugInfo(String info) {
		debugInfo(info, null);
	}

	public void debugInfo(String info, Integer mode) {
		debugInfo(info, mode, null);
	}

	public void debugInfo(String info, Integer mode, Integer stackTraceCount) {
		if (!config.debugEnabled())
			return;
		log(" [DEBUG] " + info, mode, false, stackTraceCount);
	}

	public void debugWarning(String warning) {
		debugWarning(warning, null);
	}

	public void debugWarning(String warning, Integer mode) {
		debugWarning(warning, mode, DEFAULT_WARNING_STACK_TRACE_COUNT);
	}

	public void debugWarning(String warning, Integer mode, Integer stackTraceCount) {
		if (!config.debugEnabled())
			return;
		log(" [DEBUG] " + warning, mode, true, stackTraceCount);
	}

	public void section(boolean isWarning, String line) {
		if (isWarning) {
			getLogger().info(line);
		} else {
			getLogger().warning(line);
		}
	}

	public void section(boolean isWarning) {
		section(isWarning, EMPTY);
	}

	///////////////////////
	// PROTECTED HELPERS///
	///////////////////////
	protected void log(String info, Integer mode, boolean isWarning, Integer stackTraceCount) {
		if (mode != null && (mode.equals(DASHES_TOP) || mode.equals(DASHES_TOP_AND_BOTTOM))) {
			section(isWarning, DASHES);
		}

		if (isWarning) {
			getLogger().info(info);
		} else {
			getLogger().warning(info);
		}

		if (stackTraceCount != null && stackTraceCount >= 0) {
			printStackTrace(stackTraceCount);
		}

		if (mode != null && (mode.equals(DASHES_BOTTOM) || mode.equals(DASHES_TOP_AND_BOTTOM))) {
			section(isWarning, DASHES);
		}
	}

	protected void printStackTrace(int levelsBack) {
		section(false, HASHES);
		for (int i = 0; i <= levelsBack; i++) {
			StackTraceElement stackTraceElement = null;
			try {
				stackTraceElement = Thread.currentThread().getStackTrace()[i];

				if (stackTraceElement == null)
					continue;

				String className = "[" + stackTraceElement.getClassName() + "]";
				String methodName = "[" + stackTraceElement.getMethodName() + "]";
				int lineNumber = stackTraceElement.getLineNumber();

				getLogger().info("  --className: " + className);
				getLogger().info("  --methodName: " + methodName);
				getLogger().info("  --lineNumber: " + lineNumber);

			} catch (Exception e) {
			}

		}
		section(false, HASHES);
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
						section(false, DASHES);
						debugInfo(json);
						section(false, DASHES);
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

	public class BlockTyperPluginException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 201610090027L;

		public BlockTyperPluginException(String message) {
			super(message);
		}
	}

}
