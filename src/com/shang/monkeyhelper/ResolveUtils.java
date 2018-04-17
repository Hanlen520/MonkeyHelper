package com.shang.monkeyhelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

public class ResolveUtils {
	public static final String DISPLAY_LEAK_ACTIVITY = "com.squareup.leakcanary.internal.DisplayLeakActivity";

	public static ResolveInfo resolveDisplayLeakActivity(Context context, String packageName) {
		Intent intent = new Intent();
		intent.setClassName(packageName, DISPLAY_LEAK_ACTIVITY);
		return context.getApplicationContext().getPackageManager().resolveActivity(intent, 0);
	}
}
