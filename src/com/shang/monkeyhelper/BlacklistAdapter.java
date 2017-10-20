package com.shang.monkeyhelper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlacklistAdapter extends BaseAdapter {

	private List<String> blacklist = new ArrayList<String>();
	private Context context = null;

	public BlacklistAdapter(Context context, List<String> blacklist) {
		super();
		this.blacklist = blacklist;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return blacklist.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return blacklist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.blacklist_item, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.textview_item);
		textView.setText(blacklist.get(position));
		return convertView;
	}

}
