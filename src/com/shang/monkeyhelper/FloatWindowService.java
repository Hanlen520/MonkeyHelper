package com.shang.monkeyhelper;

import java.util.ArrayList;
import java.util.List;

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
				String output = ShellUtils.execCmd("su", "-c", "dumpsys activity |grep \"mFocusedActivity\"");
				Log.i(FloatWindowService.class.getName(), output);
				String[] temps = output.split(" ");
				String target = "";
				for (String sp : temps) {
					if (sp.contains("/")) {
						target = sp;
					}
				}
				String packagename = target.substring(0, target.indexOf("/"));
				String classname = target.substring(target.indexOf("/") + 1, target.length());
				if (classname.startsWith(".")) {
					classname = packagename + classname;
				}
				List<String> list = new ArrayList<String>();
				list.addAll(SPUtils.getValue(getApplicationContext(), SPUtils.BLACKLIST, list));
				list.add(classname);
				System.out.println(list);
				SPUtils.setValue(getApplicationContext(), SPUtils.BLACKLIST, list);
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
