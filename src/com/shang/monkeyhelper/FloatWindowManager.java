package com.shang.monkeyhelper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class FloatWindowManager {

	private static FloatView floatView;

	private static LayoutParams floatViewParams;

	private static WindowManager mWindowManager;

	public static void createFloatView(Context context) {
		WindowManager windowManager = getWindowManager(context);
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		if (floatView == null) {
			floatView = FloatView.getInstance(context);
			if (floatViewParams == null) {
				floatViewParams = new LayoutParams();
				floatViewParams.x = screenWidth * 4 / 5;
				floatViewParams.y = screenHeight * 4 / 5;
				floatViewParams.type = LayoutParams.TYPE_TOAST;
				floatViewParams.format = PixelFormat.RGBA_8888;
				floatViewParams.gravity = Gravity.START | Gravity.TOP;
				floatViewParams.width = floatView.getViewWidth();
				floatViewParams.height = floatView.getViewHeight();
				floatViewParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
			}
			floatView.setParams(floatViewParams);
			windowManager.addView(floatView, floatViewParams);
		}
	}

	public static void removeFloatView(Context context) {
		if (floatView != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(floatView);
			floatView = null;
		}
	}

	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}
}
