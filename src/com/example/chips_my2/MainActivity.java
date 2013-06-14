package com.example.chips_my2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	public static final String UNCKECK_ITEM_ACTION = "com.example.chips.main.uncheckitem";
	public static final String FILTER_ITEM_ACTION = "com.example.chips.main.filteritem";
	public static final String FRIEND = "friend";
	public static final String FILTER_TEXT = "filter_text";
	private ListView listview;
	private MyAutoCompleteTextView editText;
	private MyAdapter myAdapter;
	private UncheckItemAction unckeckItemAction;
	private FilterItemAction filterItemAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("tag", "MainActivity");
		setContentView(R.layout.activity_main);
		filterItemAction = new FilterItemAction();
		unckeckItemAction = new UncheckItemAction();

		editText = (MyAutoCompleteTextView) findViewById(R.id.editText1);

		if (myAdapter == null) {
			Log.d("tag", "new adapter onCreate");
			myAdapter = new MyAdapter(this);
		}
		listview = (ListView) findViewById(R.id.listView1);
		listview.setAdapter(myAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				myAdapter.checkItem(position);
				editText.setItem((Friend) myAdapter.getItem(position));
				myAdapter.notifyDataSetChanged();

			}
		});
	}


	@Override
	public void onResume() {
		super.onResume();
		ActionManager.registrateAction(this, null, unckeckItemAction);
		ActionManager.registrateAction(this, null, filterItemAction);
	}

	@Override
	public void onPause() {
		super.onPause();
		ActionManager.unregistrateAction(this, unckeckItemAction);
		ActionManager.unregistrateAction(this, filterItemAction);
	}

	class UncheckItemAction extends MyAction {

		@Override
		public void onReceive(Context context, Intent intent) {
			Friend friend = (Friend) intent.getSerializableExtra(FRIEND);
			myAdapter.checkItem(friend, false);
			myAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onRegistrate(Activity activity, Fragment fragment) {
			super.onRegistrate(activity, fragment);
		}

		@Override
		protected IntentFilter init() {
			IntentFilter intentFilter = new IntentFilter(UNCKECK_ITEM_ACTION);
			return intentFilter;
		}
	}

	class FilterItemAction extends MyAction {

		@Override
		public void onReceive(Context context, Intent intent) {
			String s = intent.getStringExtra(FILTER_TEXT);
			// Log.d("tag", "onReceive " + s);
			myAdapter.getFilter().filter(s);
		}

		@Override
		protected void onRegistrate(Activity activity, Fragment fragment) {
			super.onRegistrate(activity, fragment);
		}

		@Override
		protected IntentFilter init() {
			IntentFilter intentFilter = new IntentFilter(FILTER_ITEM_ACTION);
			return intentFilter;
		}
	}

}
