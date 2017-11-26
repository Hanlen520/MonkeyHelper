package com.shang.monkeyhelper;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author shang
 *
 */
public class MainActivity extends Activity {

	private Button to_blacklist;
	private Button to_statusbar;
	private Object binding = new Object();
	private boolean expand_disabled;
	private Button change_floatview;
	private Button to_systemui;
	// private Button change_leakicon;
	private Button restart_launcher;
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			return false;
		}
	});
	private Button to_leaks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		to_blacklist = (Button) findViewById(R.id.to_blacklist);
		to_blacklist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, BlacklistActivity.class);
				startActivity(intent);
			}

		});

		expand_disabled = SPUtils.getValue(getApplicationContext(), SPUtils.EXPAND_DISABLED, false);
		to_statusbar = (Button) findViewById(R.id.to_statusbar);
		if (isSystemUIEabled()) {
			Toast.makeText(getApplicationContext(), expand_disabled ? "通知栏已禁止下拉" : "通知栏已允许下拉", Toast.LENGTH_SHORT)
					.show();
			to_statusbar.setText(expand_disabled ? "通知栏已禁止下拉" : "通知栏已允许下拉");
			to_statusbar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SPUtils.setValue(getApplicationContext(), SPUtils.EXPAND_DISABLED, !expand_disabled);
					expand_disabled = SPUtils.getValue(getApplicationContext(), SPUtils.EXPAND_DISABLED,
							expand_disabled);
					Toast.makeText(getApplicationContext(), expand_disabled ? "通知栏已禁用" : "通知栏未禁用", Toast.LENGTH_SHORT)
							.show();
					((Button) v).setText(expand_disabled ? "通知栏已禁止下拉" : "通知栏已允许下拉");
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "通知栏已禁止下拉", Toast.LENGTH_SHORT).show();
			to_statusbar.setText("通知栏已禁止下拉");
			to_statusbar.setClickable(false);
			to_statusbar.setTextColor(Color.GRAY);
		}

		change_floatview = (Button) findViewById(R.id.change_floatview);
		change_floatview.setText(isServiceRunning(FloatWindowService.class.getName()) ? "悬浮按钮已开启" : "悬浮按钮已关闭");
		change_floatview.setOnClickListener(new View.OnClickListener() {

			private void showFloatViewAndChangeButtonText() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
				startService(intent);
				if (isServiceRunning(FloatWindowService.class.getName())) {
					change_floatview.setText("悬浮按钮已开启");
				}
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isServiceRunning(FloatWindowService.class.getName())) {
					Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
					stopService(intent);
					((Button) v).setText("悬浮按钮已关闭");
				} else {
					if (SPUtils.getValue(getApplicationContext(), SPUtils.FLOATVIEW_MESSAGE_READED, false)) {
						showFloatViewAndChangeButtonText();
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setIcon(android.R.drawable.ic_dialog_alert).setTitle("注意！");
						builder.setMessage("使用悬浮按钮获取您正在交互的页面的Activiy需要Root权限，请确保您的设备已Root。");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								showFloatViewAndChangeButtonText();
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						});
						builder.setNeutralButton("确定（不再提示）", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								showFloatViewAndChangeButtonText();
								SPUtils.setValue(getApplicationContext(), SPUtils.FLOATVIEW_MESSAGE_READED, true);
							}

						});
						builder.show().setCanceledOnTouchOutside(false);
					}
				}
			}

		});

		to_systemui = (Button) findViewById(R.id.to_systemui);
		to_systemui.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
						AlertDialog.THEME_DEVICE_DEFAULT_DARK);
				builder.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Root方式启用/禁用通知栏")
						.setMessage("您当前的systemui已" + (isSystemUIEabled() ? "启用" : "禁用") + "\n点击确定后将"
								+ (isSystemUIEabled() ? "禁用" : "启用") + "通知栏" + "\n注意：该操作将会自动重启手机");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (isSystemUIEabled()) {
							ShellUtils.execCmd("su", "-c", "pm disable com.android.systemui");
							// getPackageManager().setApplicationEnabledSetting("com.android.systemui",
							// PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
							// PackageManager.DONT_KILL_APP); // 此方式无法以root权限运行
						} else {
							ShellUtils.execCmd("su", "-c", "pm enable com.android.systemui");
						}
						ShellUtils.execCmd("su", "-c", "reboot");
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
				builder.show().setCanceledOnTouchOutside(false);
			}
		});

		// change_leakicon = (Button) findViewById(R.id.change_leakicon);
		// change_leakicon.setText(SPUtils.getValue(getApplicationContext(),
		// SPUtils.SHOW_LEAKICON, true)
		// ? "Leak Trace自动展示已开启" : "Leak Trace已禁止展示");
		// change_leakicon.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// SPUtils.setValue(getApplicationContext(), SPUtils.SHOW_LEAKICON,
		// !SPUtils.getValue(getApplicationContext(), SPUtils.SHOW_LEAKICON,
		// true));
		// ((Button) v).setText(SPUtils.getValue(getApplicationContext(),
		// SPUtils.SHOW_LEAKICON, true)
		// ? "Leak Trace自动展示已开启" : "Leak Trace已禁止展示");
		// }
		// });

		restart_launcher = (Button) findViewById(R.id.restart_launcher);
		restart_launcher.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String launcher_name = getLauncherPackageName();
				if (launcher_name.length() == 0) {
					Toast.makeText(getApplicationContext(), "无法获取当前系统的Launcher名", Toast.LENGTH_SHORT).show();
				} else {
					Log.i(MainActivity.class.getName(),
							ShellUtils.execCmd("su", "-c", "am force-stop " + launcher_name));
					/*
					 * if (!isServiceRunning(launcher_name)) { // 启动Launcher
					 * Log.i(MainActivity.class.getName(),
					 * ShellUtils.execCmd("sh", "-c", "am start -n " +
					 * launcher_name + "/" + getLauncherActivityName())); }
					 */
				}
			}
		});

		to_leaks = (Button) findViewById(R.id.to_leaks);
		to_leaks.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, LeakCanaryAppsActivity.class);
				startActivity(intent);
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

	/**
	 * 绑定相关onClick属性以启用
	 * 
	 * @param view
	 */
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
			Log.i(MainActivity.class.getName(), "statusbarmanager not found");
		}
	}

	/**
	 * 绑定相关onClick属性以启用
	 * 
	 * @param view
	 */
	public void do_special_dis(View view) {
		binding = getSystemService("statusbar");
		special(binding, true);
	}

	/**
	 * 绑定相关onClick属性以启用
	 * 
	 * @param view
	 */
	public void do_special_en(View view) {
		binding = getSystemService("statusbar");
		special(binding, false);
	}

	private void special(Object object, boolean disabled) {
		System.out.println(object);
		// Toast.makeText(getApplicationContext(), "" + object,
		// Toast.LENGTH_LONG).show();
	}

	/**
	 * @param serviceName
	 *            e.g.: "com.shang.monkeyhelper.FloatWindowService"
	 * @return
	 */
	private boolean isServiceRunning(String serviceName) {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isSystemUIEabled() {
		int state = getPackageManager().getApplicationEnabledSetting("com.android.systemui");
		switch (state) {
		case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
			return true;
		case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
			return false;
		default:
			return true;
		}
	}

	/**
	 * 判断进程是否正在运行（pm disable com.android.systemui后该进程依旧存在）
	 * 
	 * @param processName
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isProcessRunning(String processName) {
		String result = ShellUtils.execCmd("sh", "-c", "ps |grep " + processName);
		return result.contains(processName);
	}

	/**
	 * 获取当前系统的Launcher包名
	 * 
	 * @return
	 */
	private String getLauncherPackageName() {
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		final ResolveInfo res = this.getPackageManager().resolveActivity(intent, 0);
		if (res.activityInfo == null) {
			return "";
		}
		if (res.activityInfo.packageName.equals("android")) {
			// 有多个桌面程序存在，且未指定默认项时；
			return "";
		} else {
			return res.activityInfo.packageName;
		}
	}

	/**
	 * 获取当前系统的Launcher Activity名
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getLauncherActivityName() {
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		final ResolveInfo res = this.getPackageManager().resolveActivity(intent, 0);
		if (res.activityInfo == null) {
			return "";
		}
		if (res.activityInfo.packageName.equals("android")) {
			// 有多个桌面程序存在，且未指定默认项时；
			return "";
		} else {
			return res.activityInfo.name;
		}
	}
}
