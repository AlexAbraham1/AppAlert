package io.appalert.appalert;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
public class AppCheckService extends IntentService {

    public static final String TAG = "AppCheckService";

    public AppCheckService(){
        super("AppCheckService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Service Started");

        final Handler h = new Handler();
        final int delay = 10000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                //do something

                String message = getCurrentRunningApp();

                if (message != null) {
                    Log.i(TAG, message);
                    showAlert(message);
                } else {
                    Log.i(TAG, "MESSAGE IS NULL");
                }

                h.postDelayed(this, delay);
            }
        }, delay);

//        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
//
//        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                String message = getCurrentRunningApp();
//
//                if (message != null) {
//                    Log.i(TAG, message);
//                    showAlert(message);
//                } else {
//                    Log.i(TAG, "MESSAGE IS NULL");
//                }
//            }
//        }, 10, 10, TimeUnit.SECONDS);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Service Intent");
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

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service Destroyed");
    }

}
