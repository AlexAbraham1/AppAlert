package io.appalert.appalert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    public ToggleButton toggleAlerts;

    public static final String TAG = "AppAlert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the toggle button
        toggleAlerts = (ToggleButton) findViewById(R.id.toggleAlerts);
        toggleAlerts.setChecked(loadBooleanPreference("AppCheckServiceRunning", false));
        toggleAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggle, boolean isChecked) {

                alertButtonPressed(toggle, isChecked);

            }
        });

        checkServiceState();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertButtonPressed(CompoundButton alertButton, boolean isChecked) {

        String message;

        if (isChecked) {
            message = "Button is pressed";
        } else {
            message = "Button is NOT pressed";
        }

        Log.i(TAG, message);
        checkServiceState();

    }

    private void checkServiceState() {

        if (toggleAlerts.isChecked()) {
            Intent startServiceIntent = new Intent(MainActivity.this, AppCheckService.class);
            startService(startServiceIntent);
            Log.i(TAG, "Starting Service from MainActivity");
            saveBooleanPreference("AppCheckServiceRunning", true);
        } else {
            Intent stopServiceIntent = new Intent(MainActivity.this, AppCheckService.class);
            stopService(stopServiceIntent);
            Log.i(TAG, "Stopping Service from MainActivity");
            saveBooleanPreference("AppCheckServiceRunning", false);
        }

    }

    private void saveBooleanPreference(String name, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    private boolean loadBooleanPreference(String name, boolean defaultvalue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return  preferences.getBoolean(name, defaultvalue);
    }
}
