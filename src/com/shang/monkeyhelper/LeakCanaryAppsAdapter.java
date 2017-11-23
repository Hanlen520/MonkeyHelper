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
		if(convertView == null) {
			convertView = View.inflate(context, R.layout.leak_canary_apps_item, null);
		}
		ImageView imageView = (ImageView) convertView.findViewById(R.id.apps_icon);
		TextView textView = (TextView) convertView.findViewById(R.id.apps_name);
		imageView.setImageDrawable(getItem(position).activityInfo.loadIcon(context.getPackageManager()));
		textView.setText(getItem(position).activityInfo.name);
		return convertView;
	}

}
