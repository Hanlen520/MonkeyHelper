package com.shang.monkeyhelper;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
		final XSharedPreferences xSharedPreferences = new XSharedPreferences("com.shang.monkeyhelper", SPUtils.SPFILE);
		xSharedPreferences.reload(); // 在包重新加载时会重新加载偏好设置，因此对于统一应用需要停止掉才能重新加载偏好设置（这是合理的）
		/**
		 * 禁止Activity启动——合并至Hook Monkey 启动 leakcanary中（不行）
		 */
		if (XposedHelpers.findClassIfExists("android.app.Instrumentation", lpparam.classLoader) == null) {
			// XposedBridge.log("android.app.Instrumentation cannot be found!");
		} else {
			// XposedBridge.log("android.app.Instrumentation is found!");
			XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "execStartActivity",
					Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							Intent intent = (Intent) param.args[4];
							XposedBridge.log(intent.toString());
							String activity_name = intent.getComponent().getClassName();
							Set<String> set = xSharedPreferences.getStringSet(SPUtils.BLACKLIST, new HashSet<String>());
							if (set.contains(activity_name)) { // 暂时只考虑代码包名
								XposedBridge.log(activity_name + " is in the blacklist!");
								param.setResult(null);
							}

							super.beforeHookedMethod(param);
						}
					});
		}

		/**
		 * hook special方法
		 */
		if (XposedHelpers.findClassIfExists("com.shang.monkeyhelper.MainActivity", lpparam.classLoader) == null) {
			// XposedBridge.log("com.shang.monkeyhelper.MainActivity cannot be
			// found!");
		} else {
			// XposedBridge.log("com.shang.monkeyhelper.MainActivity is
			// found!");

			XposedHelpers.findAndHookMethod("com.shang.monkeyhelper.MainActivity", lpparam.classLoader, "special",
					Object.class, boolean.class, new XC_MethodHook() {

						@Override
						protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							// param.args[0] = lpparam; // 可以回传对象至应用空间
							super.beforeHookedMethod(param);
						}
					});

		}

		/**
		 * hook disable、expandNotificationsPanel方法
		 */
		if (XposedHelpers.findClassIfExists("android.app.StatusBarManager", lpparam.classLoader) == null) {
			// XposedBridge.log("android.app.StatusBarManager cannot be
			// found!");
		} else {
			// XposedBridge.log("android.app.StatusBarManager is found!");
			XposedHelpers.findAndHookMethod("android.app.StatusBarManager", lpparam.classLoader, "disable", int.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							// XposedBridge.log("disable() is invoked!");
							// XposedBridge.log("param: " + param.thisObject);
							// param.args[0] = 0x00010000; // 开机时执行，禁用通知栏下拉
							super.beforeHookedMethod(param);
						}
					});
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

		Class<?> systemservicemanager_clazz = XposedHelpers.findClassIfExists("com.android.server.SystemServiceManager",
				lpparam.classLoader);
		XposedBridge.log(
				systemservicemanager_clazz == null ? "systemservicemanager_clazz null" + " in " + lpparam.packageName
						: "Find systemservicemanager_clazz: " + systemservicemanager_clazz.toString() + " in "
								+ lpparam.packageName);
		Class<?> systemserver_clazz = XposedHelpers.findClassIfExists("com.android.server.SystemServer",
				lpparam.classLoader);
		XposedBridge.log(systemserver_clazz == null ? "systemserver_clazz null" + " in " + lpparam.packageName
				: "Find systemserver_clazz: " + systemserver_clazz.toString() + " in " + lpparam.packageName);

		/**
		 * Hook monkey 启动 leakcanary， 由于AMS只在开机时生成单例，所以在此使用黑名单需要重启才能生效
		 */
		Class<?> ams_clazz = XposedHelpers.findClassIfExists("com.android.server.am.ActivityManagerService",
				lpparam.classLoader);
		XposedBridge.log(ams_clazz == null ? "ams_clazz null" + " in " + lpparam.packageName
				: "Find ams_clazz: " + ams_clazz.getName() + " in " + lpparam.packageName);
		if (ams_clazz != null) {
			XposedHelpers.findAndHookMethod(ams_clazz, "startActivity", Class.forName("android.app.IApplicationThread"),
					String.class, Intent.class, String.class, IBinder.class, String.class, int.class, int.class,
					Class.forName("android.app.ProfilerInfo"), Bundle.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							Intent intent = (Intent) param.args[2];
							XposedBridge.log("---In ams---" + intent.toString());
							if (intent.getAction() != null && intent.getSourceBounds() == null && intent.getComponent()
									.getClassName().equals("com.squareup.leakcanary.internal.DisplayLeakActivity")) { // Monkey的Intent来了
								XposedBridge.log("Monkey Intent"); //
								if (!xSharedPreferences.getBoolean(SPUtils.MONKEY_TO_LEAKCANARY, false)) { // 禁止跳转启用（默认也启用）
									param.setResult(0);
								}
							}
							super.beforeHookedMethod(param);
						}
					});
		}

		/**
		 * Activity生命周期
		 */
		if (XposedHelpers.findClassIfExists("android.app.Activity", lpparam.classLoader) == null) {
			XposedBridge.log("android.app.Activity cannot be found!");
		} else {
			XposedBridge.log("android.app.Activity is found!");
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onStart",
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume",
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onPause",
					new XC_MethodHook() {
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
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onRestart",
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onDestroy",
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
		}

		/**
		 * Application生命周期
		 */
		if (XposedHelpers.findClassIfExists("android.app.Application", lpparam.classLoader) == null) {
			XposedBridge.log("android.app.Application cannot be found!");
		} else {
			XposedBridge.log("android.app.Application is found!");
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onCreate",
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onTerminate",
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onConfigurationChanged",
					Configuration.class, new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onLowMemory",
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onTrimMemory", int.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							// TODO Auto-generated method stub
							XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
							super.afterHookedMethod(param);
						}
					});
		}
		
		/**
		 * Application生命周期
		 */
		if(XposedHelpers.findClassIfExists("android.app.Application", lpparam.classLoader) == null) {
			XposedBridge.log("android.app.Application cannot be found!");
		} else {
			XposedBridge.log("android.app.Application is found!");
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onCreate", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onTerminate", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onConfigurationChanged", Configuration.class, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onLowMemory", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					// TODO Auto-generated method stub
					XposedBridge.log(param.thisObject + " " + param.method.getName() + " is invoked!");
					super.afterHookedMethod(param);
				}
			});
			XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onTrimMemory", int.class, new XC_MethodHook() {
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
		if (XposedHelpers.findClassIfExists("com.android.server.am.ActivityManagerShellCommand", null) == null) {
			XposedBridge.log("com.android.server.am.ActivityManagerShellCommand cannot be found!");
		} else {
			XposedBridge.log("com.android.server.am.ActivityManagerShellCommand is found!");
		}
		if (XposedHelpers.findClassIfExists("com.android.server.am.ActivityManagerService", null) == null) {
			XposedBridge.log("com.android.server.am.ActivityManagerService cannot be found!");
		} else {
			XposedBridge.log("com.android.server.am.ActivityManagerService is found!");
		}
	}

}
