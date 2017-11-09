package com.shang.monkeyhelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class FloatWindowService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		if (Constant.DEBUG) {
			Log.i(FloatWindowService.class.getName(), "conCreate() is invoked!");
		}
		FloatWindowManager.createFloatView(this); // 使用this更好？
		FloatView floatView = FloatWindowManager.getFloatView(this);
		floatView.setOnUpListener(new FloatView.OnUpListener() {

			@Override
			public void onUp(View v) {
				// TODO Auto-generated method stub
				Log.i(FloatWindowService.class.getName(), "onUp()");
				Log.i(FloatWindowService.class.getName(),
						ShellUtils.execCmd("su", "-c", "dumpsys activity |grep \"mFocusedActivity\""));
			}
		});
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		FloatWindowManager.removeFloatView(this);
		super.onDestroy();
	}

}
