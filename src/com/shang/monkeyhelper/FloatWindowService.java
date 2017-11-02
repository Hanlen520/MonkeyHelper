package com.shang.monkeyhelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FloatWindowService extends Service {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		if(Constant.DEBUG) {
			Log.i(FloatWindowService.class.getName(), "conCreate() is invoked!");
		}
		FloatWindowManager.createFloatView(getApplicationContext());
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
		FloatWindowManager.removeFloatView(getApplicationContext());
		super.onDestroy();
	}

}
