package com.shang.monkeyhelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		resolveInfos.addAll(getPackageManager().queryIntentActivities(mainIntent, 0));
	}
}
