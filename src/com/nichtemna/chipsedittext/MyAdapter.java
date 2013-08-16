package com.nichtemna.chipsedittext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

import com.nichtemna.chipsedittext.R;
import com.nichtemna.chipsedittext.model.Friend;

public class MyAdapter extends BaseAdapter implements Filterable {
	private Context mContext;
	private ArrayList<Friend> friends = new ArrayList<Friend>();
	private ArrayList<Friend> friends_data = new ArrayList<Friend>();
	private GetDataAsyncTask getDataTask;

	public MyAdapter(Context context) {
		getDataTask = new GetDataAsyncTask();
		this.mContext = context;
		try {
			this.friends = getDataTask.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.friends_data = friends;
	}

	public MyAdapter(Context context, ArrayList<Friend> friends) {
		this.mContext = context;
		this.friends = friends;
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

	/*
	 * return current friend list
	 */
	public ArrayList<Friend> getFriendsList() {
		for (Friend one_friend : friends) {
			Iterator<Map.Entry<String, Boolean>> iterator = one_friend
					.getEmail().entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Boolean> mapEntry = (Map.Entry<String, Boolean>) iterator
						.next();
				Log.d("tag",
						one_friend.getName() + " " + one_friend.isChecked()
								+ " " + mapEntry.getKey() + " "
								+ mapEntry.getValue());
			}
		}
		return friends;
	}

	/*
	 * check or uncheck user in both lists
	 */
	public void checkItem(int pos) {
		boolean checked = friends.get(pos).isChecked();
		friends.get(pos).setChecked(checked ? false : true);
		friends_data.get(pos).setChecked(checked ? false : true);
	}

	
	/*
	 * if user has few emails we mark chosen as true, other as false
	 * if user has only one email it's always true
	 */
	public void setChosenEmail(Friend friend, String chosen_email) {
		for (Friend one_friend : friends) {
			if (one_friend.equals(friend)) {
				Iterator<Map.Entry<String, Boolean>> iterator = one_friend
						.getEmail().entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Boolean> mapEntry = (Map.Entry<String, Boolean>) iterator
							.next();
					if (mapEntry.getKey().equals(chosen_email)) {
						one_friend.addEmail(mapEntry.getKey(), true);
					} else {
						one_friend.addEmail(mapEntry.getKey(), false);
					}
				}
			}
		}
		for (Friend one_friend : friends_data) {
			if (one_friend.equals(friend)) {
				Iterator<Map.Entry<String, Boolean>> iterator = one_friend
						.getEmail().entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Boolean> mapEntry = (Map.Entry<String, Boolean>) iterator
							.next();
					if (mapEntry.getKey().equals(chosen_email)) {
						one_friend.addEmail(mapEntry.getKey(), true);
					} else {
						one_friend.addEmail(mapEntry.getKey(), false);
					}
				}
			}
		}
	}

	/*
	 * mark user as checked or unchecked
	 */
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
				friends = (ArrayList<Friend>) results.values;
				notifyDataSetChanged();
			}
		};
	}

	/*
	 * get data about user's contacts
	 */
	class GetDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Friend>> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(mContext, "", "Загрузка...", true);
		}

		@Override
		protected ArrayList<Friend> doInBackground(Void... params) {
			ArrayList<Friend> friends = new ArrayList<Friend>();
			ContentResolver contentResolver = mContext.getContentResolver();
			Cursor cur = contentResolver.query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					String id = cur.getString(cur
							.getColumnIndex(ContactsContract.Contacts._ID));
					Cursor cur1 = contentResolver.query(
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
							Friend newFriend = new Friend(name, email);
							if (friends.contains(newFriend)) {
								for (Friend friend : friends) {
									if (friend.equals(newFriend)) {
										friend.addEmail(email, true);
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
			super.onPostExecute(result);
			dialog.dismiss();
		}
	}

}
