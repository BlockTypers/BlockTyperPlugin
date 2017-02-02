package com.blocktyper.v1_1_8.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.v1_1_8.config.BlockTyperConfig;
import com.blocktyper.v1_1_8.helpers.ClickedBlockHelper;
import com.blocktyper.v1_1_8.helpers.IClickedBlockHelper;
import com.blocktyper.v1_1_8.helpers.IPlayerHelper;
import com.blocktyper.v1_1_8.helpers.InvisibleLoreHelper;
import com.blocktyper.v1_1_8.helpers.PlayerHelper;
import com.blocktyper.v1_1_8.recipes.IBlockTyperRecipeRegistrar;
import com.blocktyper.v1_1_8.recipes.RecipeRegistrar;
import com.blocktyper.v1_1_8.recipes.translation.TranslateOnInventoryClickListener;
import com.blocktyper.v1_1_8.recipes.translation.TranslateOnInventoryOpenListener;
import com.blocktyper.v1_1_8.recipes.translation.TranslateOnPickupListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class BlockTyperPlugin extends JavaPlugin implements IBlockTyperPlugin {

	protected BlockTyperConfig config;

	protected DataBackupTask dataBackupTask;
	protected Map<String, Object> data = new HashMap<String, Object>();

	protected Locale locale;
	protected List<String> initMessages = null;

	IBlockTyperRecipeRegistrar recipeRegistrar;

	protected IPlayerHelper playerHelper;

	InvisibleLoreHelper invisibleLoreHelper;

	protected IClickedBlockHelper clickedBlockHelper;

	// public static final List<String> PERMISSIONS =
	// Arrays.asList("bountysigns.add.new.bounty.sign");

	protected boolean useOnPickupTranslationListener = true;
	protected boolean useOnInventoryOpenTranslationListener = true;
	protected boolean useOnInventoryClickTranslationListener = true;

	public static final String EMPTY = "";
	public static final String DASHES = "-----------------------------------";
	public static final String HASHES = "###################################";
	public static final int DASHES_TOP = 1;
	public static final int DASHES_BOTTOM = 2;
	public static final int DASHES_TOP_AND_BOTTOM = 3;
	public static final int METHOD_NAME = 4;
	public static final int DEFAULT_WARNING_STACK_TRACE_COUNT = -1;

	public BlockTyperPlugin() {
		super();
		this.config = BlockTyperConfig.getConfig(this);
		playerHelper = new PlayerHelper(this);
		invisibleLoreHelper = new InvisibleLoreHelper(this);
		clickedBlockHelper = new ClickedBlockHelper(this);
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

			info("Disable BlockTyperPlugin");
			if (data == null || data.isEmpty()) {
				info("No data to write");
				return;
			}

			new DataBackupTask(this).run();
		}

	}

	//////////////////
	// RECIPE HOOKS///
	/////////////////
	@Override
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {

	}

	@Override
	public void onCraftItem(CraftItemEvent event) {

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
