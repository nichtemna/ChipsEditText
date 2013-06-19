package com.example.chips_my2.view;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.example.chips_my2.MainActivity;
import com.example.chips_my2.model.Friend;
import com.example.chips_my2.view.FriendsClickSpan.OnChangeSpanListener;

public class MyAutoCompleteTextView extends MultiAutoCompleteTextView implements
		OnChangeSpanListener {
	private ArrayList<Friend> friends = new ArrayList<Friend>();
	private ArrayList<FriendsClickSpan> friendsClickSpans = new ArrayList<FriendsClickSpan>();
	private Context mContext;

	public MyAutoCompleteTextView(Context context) {
		super(context);
		init(context);
	}

	public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyAutoCompleteTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		this.mContext = context;
		addTextChangedListener(textWather);
		setMovementMethod(LinkMovementMethod.getInstance());

		/*
		 * on touch we detect cursor position, if cursor at the end - show
		 * keyboard and cursorif cursor not at the end - we hide keyboard and
		 * cursor
		 */
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				long offset = -1;
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					Layout layout = ((EditText) v).getLayout();
					float x = event.getX() + getScrollX();
					float y = event.getY() + getScrollY();
					int line = layout.getLineForVertical((int) y);
					offset = layout.getOffsetForHorizontal(line, x);
					if (offset < getText().length()) {
						sendActionHideKeyboard();
					} else {
						setCursorVisible(true);
					}
				}
				return false;
			}
		});
	}

	/*
	 * symbols by one and remove whole span and item from friends list send
	 * action to listview to uncheck item if it's removed
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_DEL
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (getAllSpanText().length() + 1 < getText().length()) {
				return super.dispatchKeyEvent(event);
			} else if (getText().length() > 1) {
				removeFriend(friends.get(friends.size() - 1));
				setText(getAllSpanText());
				setChips();
				return false;
			} else if (getText().length() <= 1) {
				return false;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	private void sendActionUncheckItem(Friend friend) {
		Intent intent = new Intent(MainActivity.UNCKECK_ITEM_ACTION);
		intent.putExtra(MainActivity.FRIEND, friend);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}

	public void sendActionFilterItem(String s) {
		Intent intent = new Intent(MainActivity.FILTER_ITEM_ACTION);
		intent.putExtra(MainActivity.FILTER_TEXT, s);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}

	public void sendActionHideKeyboard() {
		Intent intent = new Intent(MainActivity.HIDE_KEYBOARD_ACTION);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}

	/*
	 * method to know if added symbol
	 */
	private boolean isSymbol(CharSequence s) {
		int spanSize = 0;
		for (Friend friend : friends) {
			spanSize += friend.getName().length();
		}
		Log.d("tag", " isSymbol " + (s.length() - 1 >= spanSize));
		return s.length() - 1 >= spanSize;
	}

	private String getAllSpanText() {
		StringBuilder sb = new StringBuilder();
		for (Friend friend : friends) {
			sb.append(friend.getName());
		}
		return sb.toString();
	}

	private TextWatcher textWather = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (isSymbol(s.toString())) {
				/*
				 * we have one space at the end of the all spans, so when we
				 * filter list we take text without this space
				 */
				String search = s.toString().substring(
						getAllSpanText().length() + 1);
				sendActionFilterItem(search);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	/*
	 * on listview item click we show or remove items
	 */
	public void setItem(Friend friend) {
		if (friend.isChecked()) {
			// add friend to list
			friends.add(friend);

			// add span to list
			FriendsClickSpan spanClick = new FriendsClickSpan(friend, mContext,
					this);
			friendsClickSpans.add(spanClick);
		} else {
			removeFriend(friend);
		}
		setText(getAllSpanText());
		setChips();
	}

	/*
	 * remove spans from lists of drawable and clickable span
	 */
	private void removeFriend(Friend friend) {
		sendActionUncheckItem(friend);

		FriendsClickSpan clickSpanToBeRemoved = null;
		for (FriendsClickSpan span : friendsClickSpans) {
			if (span.getFriend().equals(friend)) {
				clickSpanToBeRemoved = span;
			}
		}
		friendsClickSpans.remove(clickSpanToBeRemoved);
		friends.remove(friend);
	}

	private void setOnlyOneSpanRemoving(Friend friend) {
		for (FriendsClickSpan span : friendsClickSpans) {
			if (span.getFriend().equals(friend)) {
				span.setReadyToDelete(true);
			} else {
				span.setReadyToDelete(false);
			}
		}
	}

	/*
	 * set chips to edittext from list of drawable span and clickable span
	 */
	public void setChips() {
		SpannableStringBuilder ssb = new SpannableStringBuilder(
				getAllSpanText());
		int x = 0;
		for (Friend friend : friends) {
			ssb.setSpan(getClickSpan(friend), x, x + friend.getName().length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			ssb.setSpan(getClickSpan(friend).getSpan(), x, x
					+ friend.getName().length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			x = x + friend.getName().length();
		}
		/*
		 * append one space, so on touch of edittext the last span do not get
		 * clicked
		 */
		ssb.append(" ");
		setText(ssb);
		setSelection(getText().length());
	}

	private FriendsClickSpan getClickSpan(Friend friend) {
		FriendsClickSpan rez = null;
		for (int i = 0; i < friendsClickSpans.size(); i++) {
			if (friendsClickSpans.get(i).getFriend().equals(friend)) {
				rez = friendsClickSpans.get(i);
			}
		}
		return rez;
	}

	@Override
	public void onSelectionChanged(int start, int end) {

		CharSequence text = getText();
		if (text != null) {
			if (start != text.length() || end != text.length()) {
				setSelection(text.length(), text.length());
				return;
			}
		}

		super.onSelectionChanged(start, end);
	}

	@Override
	public void onChangeSpan(Friend friend) {
		setOnlyOneSpanRemoving(friend);
		setChips();
	}

	@Override
	public void removeSpan(Friend friend) {
		removeFriend(friend);
		setChips();
	}
}
