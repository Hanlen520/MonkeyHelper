package com.shang.monkeyhelper;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Restriction implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

	private static int currentApiVersion = android.os.Build.VERSION.SDK_INT;

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		// TODO Auto-generated method stub
		if (!resparam.packageName.equals("com.android.systemui")) {
			XposedBridge.log("Cannot find com.android.systemui");
		} else {
			XposedBridge.log("Find com.android.systemui");
			resparam.res.hookLayout("com.android.systemui", "layout", "status_bar", new XC_LayoutInflated() {

				@Override
				public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
					// TODO Auto-generated method stub
					final View view = liparam.view
							.findViewById(liparam.res.getIdentifier("status_bar", "id", "com.android.systemui"));
					XposedBridge.log("status_bar: " + view);
					final XSharedPreferences xSharedPreferences = new XSharedPreferences("com.shang.monkeyhelper",
							SPUtils.SPFILE);

					XposedHelpers.findAndHookMethod(view.getClass(), "panelsEnabled", new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							// param.setResult(false); // 开机时执行，禁用通知栏下拉
							xSharedPreferences.reload();
							param.setResult(!xSharedPreferences.getBoolean(SPUtils.EXPAND_DISABLED, false));
							XposedBridge.log("panelsEnabled() is invoked!");
							XposedHelpers.findAndHookMethod(com.shang.monkeyhelper.MainActivity.class, "special",
									Object.class, boolean.class, new XC_MethodHook() { // 该方法并没有被执行
										@Override
										protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
											// TODO Auto-generated method stub
											param.args[0] = view; // 可以回传对象至应用空间
											XposedHelpers.callMethod(view, "panelsEnabled");
											super.beforeHookedMethod(param);
										}
									});
							super.beforeHookedMethod(param);
						}
					});
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.robv.android.xposed.IXposedHookLoadPackage#handleLoadPackage(de.robv.
	 * android.xposed.callbacks.XC_LoadPackage.LoadPackageParam)
	 */
	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		// TODO Auto-generated method stub
		/**
		 * 禁止Activity启动
		 */
		if (XposedHelpers.findClassIfExists("android.app.Instrumentation", lpparam.classLoader) == null) {
			XposedBridge.log("android.app.Instrumentation cannot be found!");
		} else {
//			XposedBridge.log("android.app.Instrumentation is found!");
			final XSharedPreferences xSharedPreferences = new XSharedPreferences("com.shang.monkeyhelper",
					SPUtils.SPFILE);
			xSharedPreferences.reload(); // 在包重新加载时会重新加载偏好设置，因此对于统一应用需要停止掉才能重新加载偏好设置（这是合理的）
			XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "execStartActivity",
					Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							if (param.args[4] != null) { // 不用去判断param存在与数组越界
								Intent intent = (Intent) param.args[4];
								String activity_name = intent.getComponent().getClassName();
								XposedBridge.log("Find Activity: " + activity_name);
								Set<String> set = xSharedPreferences.getStringSet(SPUtils.BLACKLIST,
										new HashSet<String>());
								if (set.contains(activity_name)) { // 暂时只考虑代码包名
									XposedBridge.log("This activity is in the blacklist!");
									param.setResult(null);
								}
							}
							super.beforeHookedMethod(param);
						}
					});
		}

		/**
		 * hook special方法
		 */
		if (XposedHelpers.findClassIfExists("com.shang.monkeyhelper.MainActivity", lpparam.classLoader) == null) {
			XposedBridge.log("com.shang.monkeyhelper.MainActivity cannot be found!");
		} else {
			XposedBridge.log("com.shang.monkeyhelper.MainActivity is found!");
			/*XposedHelpers.findAndHookMethod("com.shang.monkeyhelper.MainActivity", lpparam.classLoader, "special",
					Object.class, boolean.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							param.args[0] = lpparam; // 可以回传对象至应用空间
							super.beforeHookedMethod(param);
						}
					});*/
		}

		/**
		 * hook disable、expandNotificationsPanel方法
		 */
		if (XposedHelpers.findClassIfExists("android.app.StatusBarManager", lpparam.classLoader) == null) {
			XposedBridge.log("android.app.StatusBarManager cannot be found!");
		} else {
			XposedBridge.log("android.app.StatusBarManager is found!");
			XposedHelpers.findAndHookMethod("android.app.StatusBarManager", lpparam.classLoader, "disable", int.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log("disable() is invoked!");
							XposedBridge.log("param: " + param.thisObject);
							// param.args[0] = 0x00010000; // 开机时执行，禁用通知栏下拉
							super.beforeHookedMethod(param);
						}
					});
			final XSharedPreferences xSharedPreferences = new XSharedPreferences("com.shang.monkeyhelper",
					SPUtils.SPFILE);
			if (currentApiVersion > 16) { // 桌面下滑也会用到expand，但不知为何在加载包时更新偏好设置并不能让系统空间中的偏好设置得到更新，因此将reload写在函数调用时
				XposedHelpers.findAndHookMethod("android.app.StatusBarManager", lpparam.classLoader,
						"expandNotificationsPanel", new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
								// TODO Auto-generated method stub
								XposedBridge.log("expandNotificationsPanel() is invoked!");
								xSharedPreferences.reload();
								XposedBridge.log("EXPAND_DISABLED is "
										+ xSharedPreferences.getBoolean(SPUtils.EXPAND_DISABLED, false));
								if (xSharedPreferences.getBoolean(SPUtils.EXPAND_DISABLED, false)) {
									param.setResult(null);
								}
								super.beforeHookedMethod(param);
							}
						});
			} else {
				XposedHelpers.findAndHookMethod("android.app.StatusBarManager", lpparam.classLoader, "expand",
						new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
								// TODO Auto-generated method stub
								xSharedPreferences.reload();
								if (xSharedPreferences.getBoolean(SPUtils.EXPAND_DISABLED, false)) {
									param.setResult(null);
								}
								super.beforeHookedMethod(param);
							}
						});
			}
		}

		/**
		 * hook leakcanary展示页面
		 */
		if (XposedHelpers.findClassIfExists("android.app.ApplicationPackageManager", lpparam.classLoader) == null) {
			XposedBridge.log("android.app.ApplicationPackageManager cannot be found!");
		} else {
			XposedBridge.log("android.app.ApplicationPackageManager is found!");
			XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", lpparam.classLoader,
					"setComponentEnabledSetting", ComponentName.class, int.class, int.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log("setComponentEnabledSetting() is invoked!");
							param.args[1] = 2;
							super.beforeHookedMethod(param);
						}
					});
		}
		
		if(XposedHelpers.findClassIfExists("com.squareup.leakcanary.internal.RequestStoragePermissionActivity", lpparam.classLoader) == null) {
			XposedBridge.log("com.squareup.leakcanary.internal.RequestStoragePermissionActivity cannot be found!");
		} else {
			XposedBridge.log("com.squareup.leakcanary.internal.RequestStoragePermissionActivity is found!");
		}
		
		/**
		 * Activity生命周期
		 */
		if(XposedHelpers.findClassIfExists("android.app.Activity", lpparam.classLoader) == null) {
			XposedBridge.log("android.app.Activity cannot be found!");
		} else {
			XposedBridge.log("android.app.Activity is found!");
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onStart", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onPause", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onStop", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onRestart", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onDestroy", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
		}
	}

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		// TODO Auto-generated method stub
		if (XposedHelpers.findClassIfExists("com.android.internal.statusbar.IStatusBarService$Stub", null) == null) {
			XposedBridge.log("com.android.internal.statusbar.IStatusBarService$Stub cannot be found!");
		} else {
			XposedBridge.log("com.android.internal.statusbar.IStatusBarService$Stub is found!");
		}
		if (XposedHelpers.findClassIfExists("com.android.server.statusbar.StatusBarManagerService", null) == null) {
			XposedBridge.log("com.android.server.statusbar.StatusBarManagerService cannot be found!");
		} else {
			XposedBridge.log("com.android.server.statusbar.StatusBarManagerService is found!");
		}
		if (XposedHelpers.findClassIfExists("com.android.systemui.statusbar.phone.PhoneStatusBarView", null) == null) {
			XposedBridge.log("com.android.systemui.statusbar.phone.PhoneStatusBarView cannot be found!");
		} else {
			XposedBridge.log("com.android.systemui.statusbar.phone.PhoneStatusBarView is found!");
		}
		if (XposedHelpers.findClassIfExists("com.android.layoutlib.bridge.bars.StatusBar", null) == null) {
			XposedBridge.log("com.android.layoutlib.bridge.bars.StatusBar cannot be found!");
		} else {
			XposedBridge.log("com.android.layoutlib.bridge.bars.StatusBar is found!");
		}
	}

}
