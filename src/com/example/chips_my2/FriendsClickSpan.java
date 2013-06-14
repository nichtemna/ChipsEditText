package com.example.chips_my2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.TextView;

public class FriendsClickSpan extends ClickableSpan {
	private Friend friend;
	private FriendsSpan span;
	private Context context;
	private OnChangeSpanListener listener;
	private boolean readyToDelete = false;

	public FriendsClickSpan(Friend friend, Context context, EditText editText) {
		super();
		this.context = context;
		this.friend = friend;
		this.span = createImageSpan(false);
		if (editText instanceof OnChangeSpanListener) {
			listener = (OnChangeSpanListener) editText;
		} else {
			throw new ClassCastException(editText.getClass().getSimpleName()
					.toString()
					+ " must implement OnChangeSpanListener");
		}
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setUnderlineText(true);
	}

	public Friend getFriend() {
		return friend;
	}

	public void setFriends(Friend friend) {
		this.friend = friend;
	}

	public boolean isReadyToDelete() {
		return readyToDelete;
	}

	public void setReadyToDelete(boolean readyToDelete) {
		this.readyToDelete = readyToDelete;
		span = createImageSpan(readyToDelete);
	}

	public FriendsSpan getSpan() {
		return span;
	}

	public void setFriends(FriendsSpan span) {
		this.span = span;
	}

	@Override
	public void onClick(View view) {
		Log.d("tag", "readyToDelete " + readyToDelete);
		if (readyToDelete) {
			listener.removeSpan(friend);
		} else {
			//
			listener.onChangeSpan(friend);
			// readyToDelete = true;
		}
	}

	private FriendsSpan createImageSpan(boolean deleting) {
		BitmapDrawable bmpDrawable = createDrawableForSpan(getFriend(),
				deleting);
		return new FriendsSpan(bmpDrawable);
	}

	private static class FriendsSpan extends ImageSpan {

		public FriendsSpan(BitmapDrawable bmpDrawable) {
			super(bmpDrawable);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(true);
		}

	}

	private BitmapDrawable createDrawableForSpan(Friend friend, boolean deleting) {
		LayoutInflater lf = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		TextView textView = (TextView) lf
				.inflate(R.layout.chips_textview, null);
		textView.setText(friend.getName());
		if (deleting) {
			textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.delete, 0);
		} else {
			textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.usual, 0);
		}
		int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		textView.measure(spec, spec);
		textView.layout(0, 0, textView.getMeasuredWidth(),
				textView.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(textView.getWidth(),
				textView.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(b);
		canvas.translate(-textView.getScrollX(), -textView.getScrollY());
		textView.draw(canvas);
		textView.setDrawingCacheEnabled(true);
		Bitmap cacheBmp = textView.getDrawingCache();
		Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
		textView.destroyDrawingCache();
		BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
		bmpDrawable.setBounds(5, 0, bmpDrawable.getIntrinsicWidth(),
				bmpDrawable.getIntrinsicHeight());
		return bmpDrawable;
	}

	public static interface OnChangeSpanListener {
		void onChangeSpan(Friend friend);

		void removeSpan(Friend friend);
	}
}
