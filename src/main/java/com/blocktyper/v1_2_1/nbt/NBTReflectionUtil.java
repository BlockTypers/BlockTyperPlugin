package com.blocktyper.v1_2_1.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class NBTReflectionUtil {

	static final Gson DEFAULT_GSON = new Gson();
	static final int NBT_ITEM_JSON_STRING_LIMIT = 500;
	static final String NBT_ITEM_JSON_SUFFIX = "_";
	static final int NBT_ITEM_START_INDEX = 0;

	@SuppressWarnings("rawtypes")
	private static Class getCraftItemStack() {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		try {
			Class c = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
			// Constructor<?> cons = c.getConstructor(ItemStack.class);
			// return cons.newInstance(item);
			return c;
		} catch (Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	private static Object getNewNBTTag() {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
			return c.newInstance();
		} catch (Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	private static Object setNBTTag(Object NBTTag, Object NMSItem) {
		try {
			java.lang.reflect.Method method;
			method = NMSItem.getClass().getMethod("setTag", NBTTag.getClass());
			method.invoke(NMSItem, NBTTag);
			return NMSItem;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Object getNMSItemStack(ItemStack item) {
		@SuppressWarnings("rawtypes")
		Class cis = getCraftItemStack();
		java.lang.reflect.Method method;
		try {
			method = cis.getMethod("asNMSCopy", ItemStack.class);
			Object answer = method.invoke(cis, item);
			return answer;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	private static ItemStack getBukkitItemStack(Object item) {
		@SuppressWarnings("rawtypes")
		Class cis = getCraftItemStack();
		java.lang.reflect.Method method;
		try {
			method = cis.getMethod("asCraftMirror", item.getClass());
			Object answer = method.invoke(cis, item);
			return (ItemStack) answer;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	private static Object getNBTTagCompound(Object nmsitem) {
		@SuppressWarnings("rawtypes")
		Class c = nmsitem.getClass();
		java.lang.reflect.Method method;
		try {
			method = c.getMethod("getTag");
			Object answer = method.invoke(nmsitem);
			return answer;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ItemStack setString(ItemStack item, String key, String text) {
		if (text == null)
			return remove(item, key);
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			nbttag = getNewNBTTag();
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("setString", String.class, String.class);
			method.invoke(nbttag, key, text);
			nmsitem = setNBTTag(nbttag, nmsitem);
			return getBukkitItemStack(nmsitem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}

	public static String getString(ItemStack item, String key) {
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			return null;
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("getString", String.class);
			return (String) method.invoke(nbttag, key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static ItemStack setInt(ItemStack item, String key, Integer i) {
		if (i == null)
			return remove(item, key);
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			nbttag = getNewNBTTag();
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("setInt", String.class, int.class);
			method.invoke(nbttag, key, i);
			nmsitem = setNBTTag(nbttag, nmsitem);
			return getBukkitItemStack(nmsitem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}

	public static Integer getInt(ItemStack item, String key) {
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			return null;
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("getInt", String.class);
			return (Integer) method.invoke(nbttag, key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static ItemStack setDouble(ItemStack item, String key, Double d) {
		if (d == null)
			return remove(item, key);
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			nbttag = getNewNBTTag();
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("setDouble", String.class, double.class);
			method.invoke(nbttag, key, d);
			nmsitem = setNBTTag(nbttag, nmsitem);
			return getBukkitItemStack(nmsitem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}

	public static Double getDouble(ItemStack item, String key) {
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			return null;
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("getDouble", String.class);
			return (Double) method.invoke(nbttag, key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static ItemStack setBoolean(ItemStack item, String key, Boolean d) {
		if (d == null)
			return remove(item, key);
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			nbttag = getNewNBTTag();
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("setBoolean", String.class, boolean.class);
			method.invoke(nbttag, key, d);
			nmsitem = setNBTTag(nbttag, nmsitem);
			return getBukkitItemStack(nmsitem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}

	public static Boolean getBoolean(ItemStack item, String key) {
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			return null;
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("getBoolean", String.class);
			return (Boolean) method.invoke(nbttag, key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static ItemStack setObject(ItemStack item, String key, Object value, Gson gson) {
		try {
			String json = gson.toJson(value);
			
			List<String> lines = getMultiLineJson(json, NBT_ITEM_JSON_STRING_LIMIT);
			
			int lineNumber = NBT_ITEM_START_INDEX - 1;
			if(lines != null && !lines.isEmpty()){
				for(String line : lines){
					lineNumber++;
					item = setString(item, getKeyWithSuffixForNbtLine(key, lineNumber), line);
				}
			}
			
			item = setString(item, key, (lines != null ? lines.size() : 0)+"");
			
			int possibleLineNumber = lineNumber + 1;
			
			String possibleKey;
			while(hasKey(item, possibleKey = getKeyWithSuffixForNbtLine(key, possibleLineNumber))){
				item = remove(item, possibleKey);
				possibleLineNumber++;
			}
			return item;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	static String getKeyWithSuffixForNbtLine(String key, int lineNumber){
		return key + NBT_ITEM_JSON_SUFFIX + lineNumber;
	}

	public static <T> T getObject(ItemStack item, String key, Class<T> type, Gson gson) {
		
		String json = null;
		int intTest = -1000;
		int intTest2 = -2000;
		
		try {
			int possibleLineNumber = NBT_ITEM_START_INDEX;
			
			String initialKey = getKeyWithSuffixForNbtLine(key, possibleLineNumber);
			
			if (!hasKey(item, initialKey) && hasKey(item, key)) {
				json = getString(item, key);
				
			}else{
				json = getString(item, initialKey);
				possibleLineNumber++;
				
				String possibleKey;
				boolean doContinue = true;
				while(doContinue){
					possibleKey = getKeyWithSuffixForNbtLine(key, possibleLineNumber);
					doContinue = hasKey(item, possibleKey);
					if(doContinue){
						String part = getString(item, possibleKey);
						doContinue = part != null && !part.isEmpty();
						if(doContinue){
							json += part;
							possibleLineNumber++;
						}
					}
				}
			}
			
			if (json == null) {
				return null;
			}
			
			return deserializeJson(json, type, gson);
		} catch (JsonSyntaxException ex) {
			ex.printStackTrace();
			System.out.println("JSON: " + json);
			System.out.println("intTest: " + intTest);
			System.out.println("intTest2: " + intTest2);
		}
		return null;
	}

	private static <T> T deserializeJson(String json, Class<T> type, Gson gson) throws JsonSyntaxException {
		if (json == null) {
			return null;
		}

		T obj = gson.fromJson(json, type);
		return type.cast(obj);
	}

	public static ItemStack remove(ItemStack item, String key) {
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			nbttag = getNewNBTTag();
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("remove", String.class);
			method.invoke(nbttag, key);
			nmsitem = setNBTTag(nbttag, nmsitem);
			return getBukkitItemStack(nmsitem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}

	public static Boolean hasKey(ItemStack item, String key) {
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			return false;
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("hasKey", String.class);
			return (Boolean) method.invoke(nbttag, key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Set<String> getKeys(ItemStack item) {
		Object nmsitem = getNMSItemStack(item);
		if (nmsitem == null) {
			System.out.println("Got null! (Outdated Plugin?)");
			return null;
		}
		Object nbttag = getNBTTagCompound(nmsitem);
		if (nbttag == null) {
			nbttag = getNewNBTTag();
		}
		java.lang.reflect.Method method;
		try {
			method = nbttag.getClass().getMethod("c");
			return (Set<String>) method.invoke(nbttag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getMultiLineJson(String json, int lengthLimit) {
		
		List<String> jasonLines = new ArrayList<>();

		while (true) {

			String startJson = json;

			boolean isBreak = startJson.length() <= lengthLimit;

			int endIndex = !isBreak ? lengthLimit : startJson.length();

			jasonLines.add(startJson.substring(0, endIndex));

			if (isBreak)
				break;

			json = startJson.substring(endIndex);
		}
		
		return jasonLines;
	}

}
