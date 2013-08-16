package com.nichtemna.chipsedittext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nichtemna.chipsedittext.R;
import com.nichtemna.chipsedittext.actions.ActionManager;
import com.nichtemna.chipsedittext.actions.MyAction;
import com.nichtemna.chipsedittext.model.Friend;
import com.nichtemna.chipsedittext.view.MyAutoCompleteTextView;

public class MainActivity extends Activity {
	public static final String UNCKECK_ITEM_ACTION = "com.example.chips.main.uncheckitem";
	public static final String FILTER_ITEM_ACTION = "com.example.chips.main.filteritem";
	public static final String HIDE_KEYBOARD_ACTION = "com.example.chips.main.hidekeyboard";
	public static final String FRIEND = "friend";
	public static final String FILTER_TEXT = "filter_text";
	private ListView listview;
	private MyAutoCompleteTextView editText;
	private MyAdapter myAdapter;
	private UncheckItemAction unckeckItemAction;
	private FilterItemAction filterItemAction;
	private HideKeyboardAction hideKeyboardAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		filterItemAction = new FilterItemAction();
		unckeckItemAction = new UncheckItemAction();
		hideKeyboardAction = new HideKeyboardAction();

		editText = (MyAutoCompleteTextView) findViewById(R.id.editText1);
	}

	@Override
	public void onResume() {
		super.onResume();
		ActionManager.registrateAction(this, null, unckeckItemAction);
		ActionManager.registrateAction(this, null, filterItemAction);
		ActionManager.registrateAction(this, null, hideKeyboardAction);

		if (myAdapter == null) {
			myAdapter = new MyAdapter(this);
		}
		listview = (ListView) findViewById(R.id.listView1);
		listview.setAdapter(myAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (((Friend) myAdapter.getItem(position)).getArrayOfEmails()
						.size() > 1
						&& !((Friend) myAdapter.getItem(position)).isChecked()) {
					showChooseEmailDialog(position);
				} else {
					checkItem(position);
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		ActionManager.unregistrateAction(this, unckeckItemAction);
		ActionManager.unregistrateAction(this, filterItemAction);
		ActionManager.unregistrateAction(this, hideKeyboardAction);
	}

	// protected void onSaveInstanceState(Bundle bundle) {
	// Log.d("tag", " onsave ");
	// super.onSaveInstanceState(bundle);
	// bundle.putSerializable(FRIEND, myAdapter.getFriendsList());
	// }
	//
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	// Log.d("tag", " onrestore ");
	// ArrayList<Friend> friends = (ArrayList<Friend>) savedInstanceState
	// .getSerializable(FRIEND);
	// myAdapter = new MyAdapter(this, friends);
	// }

	private void checkItem(int position) {
		myAdapter.checkItem(position);
		editText.setItem((Friend) myAdapter.getItem(position));
		myAdapter.notifyDataSetChanged();
	}

	private void showChooseEmailDialog(final int position) {
		final Friend friend = (Friend) myAdapter.getItem(position);
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Choose one email");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, friend.getArrayOfEmails());
		adb.setAdapter(adapter, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				checkItem(position);
				myAdapter.setChosenEmail(friend,
						friend.getArrayOfEmails().get(which));
			}
		});
		AlertDialog dialog = adb.create();
		dialog.show();
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

	class HideKeyboardAction extends MyAction {

		@Override
		public void onReceive(Context context, Intent intent) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			editText.setCursorVisible(false);
		}

		@Override
		protected void onRegistrate(Activity activity, Fragment fragment) {
			super.onRegistrate(activity, fragment);
		}

		@Override
		protected IntentFilter init() {
			IntentFilter intentFilter = new IntentFilter(HIDE_KEYBOARD_ACTION);
			return intentFilter;
		}
	}
}
