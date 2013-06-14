package com.example.chips_my2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter implements Filterable {
	private Context mContext;
	private ArrayList<Friend> friends = new ArrayList<Friend>();
	private ArrayList<Friend> friends_data = new ArrayList<Friend>();
	private GetDataAsyncTask getDataTask;

	public MyAdapter(Context context) {
		Log.d("tag", "MyAdapter");
		getDataTask = new GetDataAsyncTask();
		this.mContext = context;
		try {
			this.friends = getDataTask.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.friends_data = friends;
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Object getItem(int pos) {
		return friends.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	public void checkItem(int pos) {
		boolean checked = friends.get(pos).isChecked();
		friends.get(pos).setChecked(checked ? false : true);
		friends_data.get(pos).setChecked(checked ? false : true);
	}

	public void checkItem(Friend friend, boolean checked) {
		for (Friend one_friend : friends) {
			if (one_friend.equals(friend)) {
				one_friend.setChecked(checked);
			}
		}
		for (Friend one_friend : friends_data) {
			if (one_friend.equals(friend)) {
				one_friend.setChecked(checked);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		FriendHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.friends_row, parent, false);

			holder = new FriendHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.friend_row_tv);
			holder.chb = (CheckBox) row.findViewById(R.id.checkBox1);

			row.setTag(holder);
		} else {
			holder = (FriendHolder) row.getTag();
		}

		Friend friend = friends.get(position);
		holder.txtTitle.setText(friend.getName());
		holder.chb.setChecked(friend.isChecked());
		return row;
	}

	static class FriendHolder {
		TextView txtTitle;
		CheckBox chb;
	}

	public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint == null || constraint.length() == 0) {
					results.values = friends_data;
					results.count = friends_data.size();
				} else {
					List<Friend> mFriends = new ArrayList<Friend>();
					for (Friend friend : friends_data) {
						if (friend
								.getName()
								.toUpperCase(Locale.getDefault())
								.startsWith(
										constraint.toString().toUpperCase(
												Locale.getDefault()))) {
							mFriends.add(friend);
						}
					}
					results.values = mFriends;
					results.count = mFriends.size();
				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results.count == 0)
					notifyDataSetInvalidated();
				else {
					friends = (ArrayList<Friend>) results.values;
					notifyDataSetChanged();
				}
			}
		};
	}

	class GetDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Friend>> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			Log.d("tag", "onPreExecute");
			super.onPreExecute();
			dialog = ProgressDialog.show(mContext, "", "Загрузка...", true);
		}

		@Override
		protected ArrayList<Friend> doInBackground(Void... params) {
			ArrayList<Friend> friends = new ArrayList<Friend>();
			ContentResolver cr = mContext.getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
					null, null, null);
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					String id = cur.getString(cur
							.getColumnIndex(ContactsContract.Contacts._ID));
					Cursor cur1 = cr.query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (cur1.moveToNext()) {
						// to get the contact names
						String name = cur1
								.getString(cur1
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						String email = cur1
								.getString(cur1
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						if (email != null && email.length() != 0) {
							Friend newFriend = new Friend(name, email, false);
							if (friends.contains(newFriend)) {
								for (Friend friend : friends) {
									if (friend.equals(newFriend)) {
										friend.addEmail(email);
										break;
									}
								}
							} else {
								friends.add(newFriend);
							}
						}
					}
					cur1.close();
				}
			}
			return friends;
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result) {
			Log.d("tag", "onPostExecute");
			super.onPostExecute(result);
			dialog.dismiss();
		}
	}
}
