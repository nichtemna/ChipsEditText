package com.example.chips_my2.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.MultiAutoCompleteTextView;

import com.example.chips_my2.MainActivity;
import com.example.chips_my2.model.Friend;
import com.example.chips_my2.view.FriendsClickSpan.OnChangeSpanListener;

;

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
	}

	/*
	 * symbols by one and remove whole span and item from friends list send
	 * action to listview to uncheck item if it's removed
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_DEL
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (getAllSpanText().length() < getText().length()
					|| getText().length() == 0) {
				return super.dispatchKeyEvent(event);
			} else {
				removeFriend(friends.get(friends.size() - 1));
				setText(getTextString());
				setChips();
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

	/*
	 * method to know if added symbol
	 */
	private boolean isSymbol(CharSequence s) {
		int spanSize = 0;
		for (Friend friend : friends) {
			spanSize += friend.getName().length();
		}
		return s.length() >= spanSize;
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
				String search = s.toString().substring(
						getAllSpanText().length());

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
		setText(getTextString());
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
		SpannableStringBuilder ssb = new SpannableStringBuilder(getTextString());
		int x = 0;
		for (Friend friend : friends) {
			ssb.setSpan(getClickSpan(friend), x, x + friend.getName().length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			ssb.setSpan(getClickSpan(friend).getSpan(), x, x
					+ friend.getName().length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			x = x + friend.getName().length();
		}
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

	private String getTextString() {
		StringBuilder sb = new StringBuilder();
		for (Friend friend : friends) {
			sb.append(friend.getName());
		}
		return sb.toString();
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
		// Log.d("tag", "onChangeSpan");
		setOnlyOneSpanRemoving(friend);
		setChips();

	}

	@Override
	public void removeSpan(Friend friend) {
		// Log.d("tag", "removeSpan");
		removeFriend(friend);
		setChips();
	}
}
