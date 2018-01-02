package com.blocktyper.v1_2_6.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.entity.HumanEntity;

import com.blocktyper.localehelper.LocaleHelper;

public abstract class BlockTyperLocalePlugin extends BlockTyperPlugin {

	protected Map<String, Locale> localeMap = new HashMap<String, Locale>();
	protected ResourceBundle bundle = null;
	protected Locale locale;

	public BlockTyperLocalePlugin() {
		super();
		loadServerLocale();
		bundle = getBundle(locale);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		debugInfo("locale value at start of onEnable: " + (locale != null ? locale.getLanguage() : "null"));
	}

	//////////////
	// MESSAGES///
	//////////////
	public String getLocalizedMessage(String key, HumanEntity player) {
		String playersLocaleCode = getPlayerHelper().getLocale(player);
		return getLocalizedMessage(key, playersLocaleCode);
	}

	public String getLocalizedMessage(String key, String localeCode) {

		String keyWithMessagesPrefix = "messages." + key;

		String valueFromConfig = getLocalizedMessageFromConfig(keyWithMessagesPrefix, localeCode);

		if (valueFromConfig != null) {
			return valueFromConfig;
		}

		ResourceBundle playersBundle = getBundle(localeCode);
		boolean defaultBundelUsed = false;
		if (localeCode != null && playersBundle != null && playersBundle.getLocale() != null
				&& !playersBundle.getLocale().toString().equals(localeCode)) {
			String playersLanguageCode = getPlayerHelper().getLanguageFromLocaleCode(localeCode);
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

	private String getLocalizedMessageFromConfig(String key, String localeCode) {
		String value = getConfig().getString(key + "." + localeCode, null);

		if (localeCode != null && value == null) {
			String playersLanguageCode = getPlayerHelper().getLanguageFromLocaleCode(localeCode);
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

	private void loadServerLocale() {
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
	}

}
