package com.blocktyper.v1_2_4.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;

import com.blocktyper.v1_2_4.IBlockTyperPlugin;

public class VillagerHelper implements IVillagerHelper {

	private IBlockTyperPlugin plugin;

	/**
	 * 
	 * @param plugin
	 */
	public VillagerHelper(IBlockTyperPlugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int getVillagerCareer(Villager villager) {
		try {
			Class craftVillagerClass = getClass("org.bukkit.craftbukkit.{0}.entity.CraftVillager");

			Object craftVillager = craftVillagerClass.cast(villager);

			java.lang.reflect.Method getHandleMethod = craftVillagerClass.getMethod("getHandle");
			Object nmsVillager = getHandleMethod.invoke(craftVillager);

			Class entityVillagerClass = getClass("net.minecraft.server.{0}.EntityVillager");
			Field careerField = entityVillagerClass.getDeclaredField("bJ");
			careerField.setAccessible(true);

			return careerField.getInt(nmsVillager);
		} catch (IllegalArgumentException ex) {
			plugin.warning("IllegalArgumentException Failed to get villager career: " + ex.getMessage());
			return -1;
		} catch (IllegalAccessException ex) {
			plugin.warning("IllegalAccessException Failed to get villager career: " + ex.getMessage());
			return -1;
		} catch (NoSuchFieldException ex) {
			plugin.warning("NoSuchFieldException Failed to get villager career: " + ex.getMessage());
			return -1;
		} catch (SecurityException ex) {
			plugin.warning("SecurityException Failed to get villager career: " + ex.getMessage());
			return -1;
		} catch (NoSuchMethodException e) {
			plugin.warning("NoSuchMethodException Failed to get villager career: " + e.getMessage());
		} catch (InvocationTargetException e) {
			plugin.warning(" InvocationTargetExceptionFailed to get villager career: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			plugin.warning(" ClassNotFoundException to get villager career: " + e.getMessage());
		}

		return -1;
	}

	// private methods

	@SuppressWarnings("rawtypes")
	private Class getClass(String className) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		Class clazz = Class.forName(MessageFormat.format(className, version));
		return clazz;
	}

}
