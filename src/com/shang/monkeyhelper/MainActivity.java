package com.shang.monkeyhelper;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button to_blacklist;
	private Button to_statusbar;
	private Object binding = new Object();
	private boolean expand_disabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		expand_disabled = SPUtils.getValue(getApplicationContext(), SPUtils.EXPAND_DISABLED, false);
		Toast.makeText(getApplicationContext(), (expand_disabled ? "通知栏已禁用" : "通知栏未禁用") + "\n " + getPackageManager(),
				Toast.LENGTH_SHORT).show();
		to_blacklist = (Button) findViewById(R.id.to_blacklist);
		to_blacklist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, BlacklistActivity.class);
				startActivity(intent);
			}

		});

		to_statusbar = (Button) findViewById(R.id.to_statusbar);
		to_statusbar.setText(expand_disabled ? "通知栏已禁止下拉" : "通知栏已允许下拉");
		to_statusbar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SPUtils.setValue(getApplicationContext(), SPUtils.EXPAND_DISABLED, !expand_disabled);
				expand_disabled = SPUtils.getValue(getApplicationContext(), SPUtils.EXPAND_DISABLED, expand_disabled);
				Toast.makeText(getApplicationContext(), expand_disabled ? "通知栏已禁用" : "通知栏未禁用", Toast.LENGTH_SHORT)
						.show();
				((Button) v).setText(expand_disabled ? "通知栏已禁止下拉" : "通知栏已允许下拉");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void special(Object object, boolean disabled) {
		System.out.println(object);
		// Toast.makeText(getApplicationContext(), "" + object,
		// Toast.LENGTH_LONG).show();
	}

	public void expand_statusbar(View view) {
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		try {
			Object service = getSystemService("statusbar");
			Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
			Method expand = null;
			if (service != null) {
				if (currentApiVersion <= 16) {
					expand = statusbarManager.getMethod("expand");
				} else {
					expand = statusbarManager.getMethod("expandNotificationsPanel");
				}
				expand.setAccessible(true);
				expand.invoke(service);
			}

		} catch (Exception e) {
		}
	}

	public void do_special_dis(View view) {
		binding = getSystemService("statusbar");
		special(binding, true);
	}

	public void do_special_en(View view) {
		binding = getSystemService("statusbar");
		special(binding, false);
	}
}
