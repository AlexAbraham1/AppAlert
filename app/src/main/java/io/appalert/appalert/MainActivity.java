package io.appalert.appalert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
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
        toggleAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggle, boolean isChecked) {

                alertButtonPressed(toggle, isChecked);

            }
        });

//        checkServiceState();

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

        if (isScreenOn()) {
            Log.i(TAG, "SCREEN IS ON");
        } else {
            Log.i(TAG, "SCREEN IS OFF");
        }

        checkServiceState();

    }

    private void getCurrentRunningApp() {
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RecentTaskInfo> l = am.getRecentTasks(1,
                ActivityManager.RECENT_WITH_EXCLUDED);
        Iterator<ActivityManager.RecentTaskInfo> i = l.iterator();

        PackageManager pm = this.getPackageManager();

        while (i.hasNext()) {
            try {
                Intent intent = i.next().baseIntent;
                List<ResolveInfo> list = pm.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(
                        list.get(0).activityInfo.packageName,
                        PackageManager.GET_META_DATA));

                Log.w(TAG, "Application Name: " + c.toString());

            } catch (Exception e) {
                Log.w(TAG, "Application name not found: " + e.toString());
            }
        }
    }

    @SuppressLint("NewApi") //SUPRESSED SINCE API CHECK IS BELOW
    private boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        int version = Build.VERSION.SDK_INT;
        if (version >= 20) {
            return powerManager.isInteractive();
        } else {
            return powerManager.isScreenOn();
        }
    }

    private void checkServiceState() {

        if (toggleAlerts.isChecked()) {
            Intent startServiceIntent = new Intent(MainActivity.this, AppCheckService.class);
            startService(startServiceIntent);

            Log.i(TAG, "Starting Service from MainActivity");
        } else {
            Intent stopServiceIntent = new Intent(MainActivity.this, AppCheckService.class);
            stopService(stopServiceIntent);
            Log.i(TAG, "Stopping Service from MainActivity");
        }

    }
}
