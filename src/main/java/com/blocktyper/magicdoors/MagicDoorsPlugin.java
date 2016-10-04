package com.blocktyper.magicdoors;

import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.localehelper.LocaleHelper;

public class MagicDoorsPlugin extends JavaPlugin {

	public void onEnable() {
		resourceName = "com.blocktyper.magicdoors.resources.MagicDoorsMessages";
		locale = new LocaleHelper(getLogger(), getFile() != null ? getFile().getParentFile() : null).getLocale();
	}

	// begin localization
	private Locale locale = null;
	private ResourceBundle bundle = null;
	private boolean bundleLoadFailed = false;
	private String resourceName;

	public String getLocalizedMessage(String key) {

		String value = key;
		try {
			if (bundle == null) {

				if (locale == null) {
					getLogger().info("Using default locale.");
					locale = Locale.getDefault();
				}

				try {
					bundle = ResourceBundle.getBundle(resourceName, locale);
				} catch (Exception e) {
					getLogger().warning(resourceName + " bundle did not load successfully.");
				}

				if (bundle == null) {
					getLogger().warning(
							"Messages will appear as dot separated key names.  Please remove this plugin from your plugin folder if this behaviour is not desired.");
					bundleLoadFailed = true;
					return key;
				} else {
					getLogger().info(resourceName + " bundle loaded successfully.");
				}
			}

			if (bundleLoadFailed) {
				return key;
			}

			value = bundle.getString(key);

			value = key != null ? (value != null && !value.trim().isEmpty() ? value : key) : "null key";
		} catch (Exception e) {
			getLogger().warning(
					"Unexpected error getting localized string for key(" + key + "). Message: " + e.getMessage());
		}
		return value;
	}

	// end localization
}
