package com.shang.monkeyhelper;

import java.util.List;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeakCanaryAppsAdapter extends BaseAdapter {

	private List<ResolveInfo> mApps;
	private Context context;

	public LeakCanaryAppsAdapter(Context context, List<ResolveInfo> mApps) {
		super();
		this.context = context;
		this.mApps = mApps;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mApps.size();
	}

	@Override
	public ResolveInfo getItem(int position) {
		// TODO Auto-generated method stub
		return mApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.leak_canary_apps_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mainIcon = (ImageView) convertView.findViewById(R.id.apps_icon);
			viewHolder.leakIcon = (ImageView) convertView.findViewById(R.id.apps_leak_icon);
			viewHolder.mainName = (TextView) convertView.findViewById(R.id.apps_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ResolveInfo mainInfo = getItem(position);
		viewHolder.mainIcon.setImageDrawable(mainInfo.activityInfo.loadIcon(context.getPackageManager()));
		viewHolder.mainName.setText(mainInfo.activityInfo.name);
		ResolveInfo leakInfo = ResolveUtils.resolveDisplayLeakActivity(context, mainInfo.activityInfo.packageName);
		viewHolder.leakIcon.setImageDrawable(leakInfo.activityInfo.loadIcon(context.getPackageManager()));
		return convertView;
	}

	class ViewHolder {
		ImageView mainIcon;
		ImageView leakIcon;
		TextView mainName;
		TextView leakName;
	}

}
