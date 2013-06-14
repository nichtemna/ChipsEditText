package com.example.chips_my2;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;

public abstract class MyAction extends BroadcastReceiver {

    private IntentFilter mIntentFilter;
    private WeakReference<Fragment> mFragment;
    private WeakReference<Activity> mActivity;

    protected MyAction() {
        mIntentFilter = init();
    }

    public Activity getActivity() {
        return mActivity.get();
    }

    public Fragment getFragment() {
        return mFragment.get();
    }

    protected abstract IntentFilter init();

    public IntentFilter getIntentFilter() {
        return mIntentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

    }

    /**
     * Called when ActionManager sign Action to Activity
     */
    protected void onRegistrate(Activity activity, Fragment fragment) {
        // TODO wrap to weak ref add getters
        mActivity = new WeakReference<Activity>(activity);
        mFragment = new WeakReference<Fragment>(fragment);
    }

    /**
     * Called when ActionManager sign Action to Activity
     */
    protected void onUnRegistrate() {
    }

}
