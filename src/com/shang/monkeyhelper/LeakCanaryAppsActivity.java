package com.shang.monkeyhelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class LeakCanaryAppsActivity extends Activity {

	private ListView listView;
	private BaseAdapter adapter;
	private List<ResolveInfo> resolveInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leak_canary_apps);
		resolveInfos = new ArrayList<ResolveInfo>();
		adapter = new LeakCanaryAppsAdapter(getApplicationContext(), resolveInfos);

		listView = (ListView) findViewById(R.id.apps_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				ResolveInfo mainInfo = (ResolveInfo) adapter.getItem(position);
				Log.i(LeakCanaryAppsActivity.class.getName(), ShellUtils.execCmd("sh", "-c",
						"am start -n " + mainInfo.activityInfo.packageName + "/" + ResolveUtils.DISPLAY_LEAK_ACTIVITY));
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		loadApps();
		showApps();
		super.onResume();
	}

	private void showApps() {
		// TODO Auto-generated method stub
		adapter.notifyDataSetChanged();
	}

	private void loadApps() {
		// TODO Auto-generated method stub
		resolveInfos.clear();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> allInfos = getPackageManager().queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : allInfos) {
			if (ResolveUtils.resolveDisplayLeakActivity(getApplicationContext(),
					info.activityInfo.packageName) != null) {
				if (!info.activityInfo.name.equals(ResolveUtils.DISPLAY_LEAK_ACTIVITY)) {
					resolveInfos.add(info);
				}
			}
		}
		Log.i(LeakCanaryAppsActivity.class.getName(), resolveInfos.toString());
	}

}
