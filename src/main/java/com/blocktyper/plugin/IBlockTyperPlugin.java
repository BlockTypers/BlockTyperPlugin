package com.blocktyper.plugin;

import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.plugin.Plugin;

import com.blocktyper.config.BlockTyperConfig;
import com.blocktyper.recipes.IBlockTyperRecipeRegistrar;

public interface IBlockTyperPlugin extends Plugin {

	String getLocalizedMessage(String key);

	BlockTyperConfig config();

	IBlockTyperRecipeRegistrar recipeRegistrar();

	void info(String info);

	void info(String warning, Integer mode);

	void info(String warning, Integer mode, Integer stackTraceCount);

	void warning(String warning);

	void warning(String warning, Integer mode);

	void warning(String warning, Integer mode, Integer stackTraceCount);

	void debugInfo(String info);

	void debugInfo(String warning, Integer mode);

	void debugInfo(String warning, Integer mode, Integer stackTraceCount);

	void debugWarning(String warning);

	void debugWarning(String warning, Integer mode);

	void debugWarning(String warning, Integer mode, Integer stackTraceCount);

	void section(boolean isWarning);

	void section(boolean isWarning, String line);

	boolean setData(String key, Object value, boolean flush);

	boolean setData(String key, Object value);

	Map<String,Object> getAllData();
	
	Object getData(String key);

	<T> T getTypeData(String key, Class<T> type);

	ResourceBundle getBundle();

}
