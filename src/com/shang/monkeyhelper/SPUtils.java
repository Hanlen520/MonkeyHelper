package com.shang.monkeyhelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Shang 要想XPosedSharedPreference使用本xml必须加上MODE_WORLD_READABLE
 */
public class SPUtils {

	public static final String SPFILE = "config";
	// public static final int DISABLE_NONE = 0x00000000;
	// public static final int DISABLE_EXPAND = 0x00010000;
	// public static final String STATUSBAR = "statusbar";
	// public static final String FIRST = "default";
	public static final String BLACKLIST = "blacklist";
	public static final String EXPAND_DISABLED = "expand_disabled";
	public static final String FLOATVIEW_MESSAGE_READED = "floatview_message_readed";
	public static final String SHOW_LEAKICON = "show_leakicon";

	public static void setValue(Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SPFILE, Context.MODE_WORLD_READABLE);
		sharedPreferences.edit().putString(key, value).commit();
	}

	public static String getValue(Context context, String key, String defValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SPFILE, Context.MODE_WORLD_READABLE);
		return sharedPreferences.getString(key, defValue);
	}

	public static void setValue(Context context, String key, List<String> values) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SPFILE, Context.MODE_WORLD_READABLE);
		sharedPreferences.edit().putStringSet(key, new HashSet<String>(values)).commit();
	}

	public static List<String> getValue(Context context, String key, List<String> defvalues) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SPFILE, Context.MODE_WORLD_READABLE);
		return new ArrayList<String>(
				new TreeSet<String>(sharedPreferences.getStringSet(key, new HashSet<String>(defvalues))));
	}

	public static boolean getValue(Context context, String key, boolean defValue) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(SPFILE, Context.MODE_WORLD_READABLE);
		return sharedPreferences.getBoolean(key, defValue);
	}

	public static void setValue(Context context, String key, boolean value) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(SPFILE, Context.MODE_WORLD_READABLE);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

}
