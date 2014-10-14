package io.appalert.appalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by alexabraham on 10/4/14.
 */
public class AppCheckBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "AppCheckBroadcast";

    public SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (loadBooleanPreference("AppCheckServiceRunning", false)) {
            Log.i(TAG, "Starting Service");
            Intent startServiceIntent = new Intent(context, AppCheckService.class);
            context.startService(startServiceIntent);
        } else {
            Log.i(TAG, "Service Disabled");
        }

    }

    private boolean loadBooleanPreference(String name, boolean defaultvalue) {
        return  preferences.getBoolean(name, defaultvalue);
    }

}
