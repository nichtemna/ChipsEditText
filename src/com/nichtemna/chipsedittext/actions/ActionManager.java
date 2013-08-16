package com.nichtemna.chipsedittext.actions;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

public class ActionManager {

    public static void registrateAction(Activity activity, Fragment fragment, List<MyAction> actions) {
        if (actions != null) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(activity);
            for (MyAction action : actions) {
                if (action != null) {
                    action.onRegistrate(activity, fragment);
                    broadcastManager.registerReceiver(action, action.getIntentFilter());
                }
            }
        }
    }

    public static void registrateAction(Activity activity, Fragment fragment, MyAction action) {
        if (action != null) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(activity
                            .getApplication());
            action.onRegistrate(activity, fragment);
            broadcastManager.registerReceiver(action, action.getIntentFilter());
        }
    }

    public static void unregistrateActions(Context context, List<MyAction> actions) {
        if (actions != null) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            for (MyAction action : actions) {
                if (action != null) {
                    action.onUnRegistrate();
                    broadcastManager.unregisterReceiver(action);
                }
            }
        }
    }

    public static void unregistrateAction(Context context, MyAction action) {
        if (action != null) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            action.onUnRegistrate();
            broadcastManager.unregisterReceiver(action);
        }
    }

}
