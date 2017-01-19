package com.blocktyper.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.config.BlockTyperConfig;
import com.blocktyper.helpers.ClickedBlockHelper;
import com.blocktyper.helpers.IClickedBlockHelper;
import com.blocktyper.helpers.IPlayerHelper;
import com.blocktyper.helpers.InvisibleLoreHelper;
import com.blocktyper.helpers.PlayerHelper;
import com.blocktyper.localehelper.LocaleHelper;
import com.blocktyper.recipes.IBlockTyperRecipeRegistrar;
import com.blocktyper.recipes.RecipeRegistrar;
import com.blocktyper.recipes.translation.TranslateOnInventoryClickListener;
import com.blocktyper.recipes.translation.TranslateOnInventoryOpenListener;
import com.blocktyper.recipes.translation.TranslateOnPickupListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class BlockTyperPlugin extends JavaPlugin implements IBlockTyperPlugin {

	private BlockTyperConfig config;

	private DataBackupTask dataBackupTask;
	private Map<String, Object> data = new HashMap<String, Object>();

	protected Locale locale;
	private Map<String, Locale> localeMap = new HashMap<String, Locale>();
	private ResourceBundle bundle = null;

	IBlockTyperRecipeRegistrar recipeRegistrar;

	protected IPlayerHelper playerHelper;

	InvisibleLoreHelper invisibleLoreHelper;

	protected IClickedBlockHelper clickedBlockHelper;

	private List<String> initMessages = null;

	public static final List<String> PERMISSIONS = Arrays.asList("bountysigns.add.new.bounty.sign");

	protected boolean useOnPickupTranslationListener = true;
	protected boolean useOnInventoryOpenTranslationListener = true;
	protected boolean useOnInventoryClickTranslationListener = true;

	public BlockTyperPlugin() {
		super();
		this.config = BlockTyperConfig.getConfig(this);
		playerHelper = new PlayerHelper(this);
		invisibleLoreHelper = new InvisibleLoreHelper(this);
		clickedBlockHelper = new ClickedBlockHelper(this);

		initMessages = new ArrayList<String>();

		String localeStringInThisConfig = this.config.getConfig().getString("locale", null);

		if (localeStringInThisConfig != null) {
			initMessages.add("Using locale found in this plugins config file");
			try {
				locale = new Locale(localeStringInThisConfig);
			} catch (Exception e) {
				locale = null;
				initMessages
						.add("Not able to use locale found in this plugins config file. Message: " + e.getMessage());
			}
		} else {
			initMessages.add("Attempting to find locale via Essentials or JVM arguments");
			locale = new LocaleHelper(getLogger(), getFile() != null ? getFile().getParentFile() : null).getLocale();
		}

		if (locale == null) {
			initMessages.add("Using default locale.");
			locale = Locale.getDefault();
		}

		bundle = getBundle(locale);
	}

	public BlockTyperConfig config() {
		return config;
	}

	public IBlockTyperRecipeRegistrar recipeRegistrar() {
		return recipeRegistrar;
	}

	public IPlayerHelper getPlayerHelper() {
		return playerHelper;
	}

	public InvisibleLoreHelper getInvisibleLoreHelper() {
		return invisibleLoreHelper;
	}

	public IClickedBlockHelper getClickedBlockHelper() {
		return clickedBlockHelper;
	}

	@Override
	public void onEnable() {
		super.onEnable();

		if (initMessages != null) {
			for (String msg : initMessages) {
				info(msg);
			}
		}

		debugInfo("locale value at start of onEnable: " + (locale != null ? locale.getLanguage() : "null"));

		section(false, DASHES);
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

		if (getRecipesNbtKey() != null) {
			recipeRegistrar = new RecipeRegistrar(this);
			recipeRegistrar.registerRecipesFromConfig();

			if (getConfig().getBoolean(RecipeRegistrar.RECIPES_CONTINUOUS_TRANSLATION_KEY, false)) {
				if (useOnInventoryClickTranslationListener) {
					new TranslateOnInventoryClickListener(this);
				}
				if (useOnInventoryOpenTranslationListener) {
					new TranslateOnInventoryOpenListener(this);
				}
				if (useOnPickupTranslationListener) {
					new TranslateOnPickupListener(this);
				}
			}
		}

	}

	@Override
	public void onDisable() {
		super.onDisable();

		int dataBackupFrequencySec = config.dataBackupFrequencySec();
		if (dataBackupFrequencySec >= 0) {

			section(false, DASHES);
			info("Disable BlockTyperPlugin");
			if (data == null || data.isEmpty()) {
				info("No data to write");
				return;
			}

			new DataBackupTask(this).run();
			section(false, DASHES);
		}

	}

	//////////////
	// MESSAGES///
	//////////////
	public String getLocalizedMessage(String key, HumanEntity player) {

		String keyWithMessagesPrefix = "messages." + key;

		String valueFromConfig = getLocalizedMessageFromConfig(keyWithMessagesPrefix, player);

		if (valueFromConfig != null) {
			return valueFromConfig;
		}

		String playersLocaleCode = getPlayerHelper().getLocale(player);
		ResourceBundle playersBundle = getBundle(getPlayerHelper().getLocale(player));
		boolean defaultBundelUsed = false;
		if (playersLocaleCode != null && playersBundle != null && playersBundle.getLocale() != null
				&& !playersBundle.getLocale().toString().equals(playersLocaleCode)) {
			String playersLanguageCode = getPlayerHelper().getLanguage(player);
			playersBundle = getBundle(playersLanguageCode);
			if (playersBundle != null && playersBundle.getLocale() != null
					&& !playersBundle.getLocale().toString().equals(playersLanguageCode)) {
				defaultBundelUsed = true;
				playersBundle = bundle;
			}
		}

		if (defaultBundelUsed) {
			valueFromConfig = getConfig().getString(keyWithMessagesPrefix + ".fallback", null);
			if (valueFromConfig != null && !valueFromConfig.isEmpty()) {
				return valueFromConfig;
			}
		}

		return getLocalizedMessage(key, playersBundle);
	}

	public String getLocalizedMessage(String key) {
		return getLocalizedMessage(key, bundle);
	}

	private String getLocalizedMessage(String key, ResourceBundle bundle) {

		if (key == null) {
			return "null-key";
		}

		String value = key;

		try {
			value = bundle != null ? bundle.getString(key) : key;
		} catch (Exception e) {
			debugWarning("Unexpected error getting localized string for key(" + key + "). Message: " + e.getMessage());
			value = null;
		}

		value = value != null && !value.trim().isEmpty() ? value : key;

		return value;
	}

	private String getLocalizedMessageFromConfig(String key, HumanEntity player) {

		String playersLocaleCode = getPlayerHelper().getLocale(player);

		String value = getConfig().getString(key + "." + playersLocaleCode, null);

		if (playersLocaleCode != null && value == null) {
			String playersLanguageCode = getPlayerHelper().getLanguage(player);
			value = getConfig().getString(key + "." + playersLanguageCode, null);
		}

		if (value == null) {
			value = getConfig().getString(key + ".default", null);
		}

		return value;
	}

	public Locale getLocaleFromLocaleCode(String localeCode) {
		Locale locale = null;
		if (localeCode != null) {
			if (localeMap.containsKey(localeCode)) {
				locale = localeMap.get(localeCode);
			} else {
				try {
					locale = new Locale(localeCode);
				} catch (Exception e) {
					locale = null;
					debugWarning("Issue loading locale: " + e.getMessage());
				}
				localeMap.put(localeCode, locale);
			}
		}
		return locale;
	}

	private Map<String, ResourceBundle> bundleMap = new HashMap<String, ResourceBundle>();

	public ResourceBundle getBundle() {
		if (bundle == null)
			bundle = getBundle(locale);
		if (locale != null) {
			bundleMap.put(locale.toString(), bundle);
		}
		return bundle;
	}

	public ResourceBundle getBundle(String localeCode) {
		ResourceBundle bundle = null;
		if (localeCode != null) {
			if (bundleMap.containsKey(localeCode)) {
				bundle = bundleMap.get(localeCode);
			} else {
				Locale locale = getLocaleFromLocaleCode(localeCode);
				if (locale != null) {
					bundle = getBundle(locale);
					bundleMap.put(locale.toString(), bundle);
				}
			}
		}
		return bundle;
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

	/////////////
	// LOGGING///
	/////////////

	public static final String EMPTY = "";
	public static final String DASHES = "-----------------------------------";
	public static final String HASHES = "###################################";
	public static final int DASHES_TOP = 1;
	public static final int DASHES_BOTTOM = 2;
	public static final int DASHES_TOP_AND_BOTTOM = 3;
	public static final int METHOD_NAME = 4;
	public static final int DEFAULT_WARNING_STACK_TRACE_COUNT = -1;

	public void info(String info) {
		info(info, null);
	}

	public void info(String warning, Integer mode) {
		log(warning, mode, false, null);
	}

	public void info(String warning, Integer mode, Integer stackTraceCount) {
		log(warning, mode, false, stackTraceCount);
	}

	public void warning(String warning) {
		log(warning, null, true, null);
	}

	public void warning(String warning, Integer mode) {
		log(warning, mode, true, null);
	}

	public void warning(String warning, Integer mode, Integer stackTraceCount) {
		log(warning, mode, true, stackTraceCount);
	}

	public void debugInfo(String info) {
		if (!config.debugEnabled())
			return;
		log(info, METHOD_NAME, false, null);
	}

	public void debugInfo(String info, Integer mode) {
		if (!config.debugEnabled())
			return;
		log(info, mode, false, null);
	}

	public void debugInfo(String info, Integer mode, Integer stackTraceCount) {
		if (!config.debugEnabled())
			return;
		log(info, mode, false, stackTraceCount);
	}

	public void debugWarning(String warning) {
		if (!config.debugEnabled())
			return;
		log(warning, METHOD_NAME, true, DEFAULT_WARNING_STACK_TRACE_COUNT);
	}

	public void debugWarning(String warning, Integer mode) {
		if (!config.debugEnabled())
			return;
		log(warning, mode, true, DEFAULT_WARNING_STACK_TRACE_COUNT);
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

		String methodName = "";
		if (mode != null && mode.equals(METHOD_NAME)) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
			methodName = stackTraceElement == null ? "[]"
					: "[" + getSimpleClassName(stackTraceElement.getClassName()) + "."
							+ stackTraceElement.getMethodName() + " (" + stackTraceElement.getLineNumber() + ")] ";
		}

		if (isWarning) {
			getLogger().warning(methodName + info);
		} else {
			getLogger().info(methodName + info);
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
						debugInfo(json, BlockTyperPlugin.DASHES_TOP_AND_BOTTOM);
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

	private String getSimpleClassName(String className) {
		if (className == null || !className.contains("."))
			return className;

		return className.substring(className.lastIndexOf(".") + 1);
	}

}
