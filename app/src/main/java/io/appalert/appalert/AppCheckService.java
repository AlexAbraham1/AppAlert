package io.appalert.appalert;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexabraham on 10/3/14.
 */
public class AppCheckService extends Service {

    public static final String TAG = "AppCheckService";

    public static boolean running;

    public Handler handler = new Handler();

    public Runnable runable;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Service Started");

        running = true;

        final int delay = 10000; //milliseconds

        runable = new Runnable(){
            public void run(){

                if (running) {
                    if (isScreenOn()) {

                        String message = getCurrentRunningApp();

                        if (message != null) {
                            Log.i(TAG, message);
                            showAlert(message);
                        } else {
                            Log.i(TAG, "Message is null");
                        }

                    } else {
                        Log.i(TAG, "Screen Is Off");
                    }

                    handler.postDelayed(this, delay);
                }
            }
        };

        handler.postDelayed(runable, delay);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return android.app.Service.START_STICKY;
    }

    private String getCurrentRunningApp() {
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

                return c.toString();

            } catch (Exception e) {
                Log.w(TAG, "Application name not found: " + e.toString());

            }
        }

        return null;
    }

    public void showAlert(String name) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppCheckService.this);

        alertDialogBuilder.setTitle("Current Running App");

        alertDialogBuilder.setMessage(name);

        alertDialogBuilder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();

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

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service Destroyed");

        handler.removeCallbacks(runable);

        running = false;
        super.onDestroy();
    }

}
