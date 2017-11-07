package com.shang.monkeyhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class BlacklistActivity extends Activity {

	private ListView listview_blacklist;
	private List<String> blacklist;
	private BaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacklist);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		blacklist = new ArrayList<String>();
		adapter = new BlacklistAdapter(getApplicationContext(), blacklist);

		listview_blacklist = (ListView) findViewById(R.id.listview_blacklist);
		listview_blacklist.setAdapter(adapter);
		load_blacklist();
		show_blacklist();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blacklist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_edit:
			edit_blacklist();
			break;
		case R.id.action_reload:

			break;
		case R.id.action_share:

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void edit_blacklist() {
		// TODO Auto-generated method stub
		Builder builder = new Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
		View view = View.inflate(getApplicationContext(), R.layout.dialog_blacklist, null);
		final EditText editText = (EditText) view.findViewById(R.id.edittext_blacklist);
		load_blacklist();
		editText.setText(blacklist.toString());
		builder.setIcon(R.drawable.ic_launcher).setTitle("编辑黑名单").setView(view);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String after_edit = editText.getText().toString();
				replace_blacklist(
						Arrays.asList(after_edit.replace(" ", "").replace("[", "").replace("]", "").split(",")));
				store_blacklist();
				load_blacklist();
				show_blacklist();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show().setCanceledOnTouchOutside(false);

	}

	private void show_blacklist() {
		adapter.notifyDataSetChanged();
	}

	private void load_blacklist() {
		blacklist.clear();
		blacklist.addAll(SPUtils.getValue(getApplicationContext(), SPUtils.BLACKLIST, blacklist));
	}

	private void replace_blacklist(List<String> inlist) {
		blacklist.clear();
		blacklist.addAll(inlist);
	}

	private void store_blacklist() {
		SPUtils.setValue(getApplicationContext(), SPUtils.BLACKLIST, blacklist);
	}
}
