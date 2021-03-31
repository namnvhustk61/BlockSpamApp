package com.stork.blockspam.service.foreground;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.stork.blockspam.R;
import com.stork.blockspam.database.model.CallPhone.CallPhone;
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY;
import com.stork.blockspam.ui.MainActivity;
import com.stork.http.API;
import com.stork.http.ServiceResult;
import com.stork.http.model.BlockPhone;
import com.stork.http.thread.RxThread;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class AppForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String CHANNEL_NAME = "ChannelName";
    public static final int AppForegroundService_ID = 10;

    @Override
    public void onCreate() {
        super.onCreate();
//        String input = .getStringExtra("inputExtra");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        setAlarmTime();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Block Started")
                .setContentText("Running")
                .setSmallIcon(R.drawable.ic_logo_app)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(null) // Passing null here silently fails
                .setContentIntent(pendingIntent);

        //do heavy work on a background thread
        //stopSelf();
        startForeground(AppForegroundService_ID, notification.build());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.enableVibration(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    AlarmManager _alarmManager;
    private void setAlarmTime(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(_alarmManager == null){
                _alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }
            long alarmTime = System.currentTimeMillis() + 86400000L;
            String tagStr = "TAG";
            Handler handler = null; // `null` means the callback will be run at the Main thread
            getPhoneServer();
            _alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, tagStr, new AlarmManager.OnAlarmListener() {
                @Override
                public void onAlarm() {
                    getPhoneServer();
                    setAlarmTime();
                }
            }, null);
        }
    }

    private void getPhoneServer(){

        API.getAllBLockPhone(new API.ApiItf<List<BlockPhone>>() {
            @Override
            public void onSuccess(ServiceResult<List<BlockPhone>> response) {
                if(response.code.equals("OK")){
                    RxThread.onDoInIO(() -> {
                        for (BlockPhone item: response.data) {
                            CallPhone callPhone = new CallPhone();
                            callPhone.phone = item.phone;
                            callPhone.name  = item.name;
                            callPhone.type = item.type;
                            callPhone.status = CallPhoneKEY.STATUS.STATUS_BLOCK;

                            callPhone.insertDB(getBaseContext());
                        }
                    });
                }

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    /*
    *
    *
    * */

    public static void startService(Activity activity) {
        Intent serviceIntent = new Intent(activity, AppForegroundService.class);
        ContextCompat.startForegroundService(activity, serviceIntent);
    }

    public static void stopForegroundService(Activity activity) {
        Intent serviceIntent = new Intent(activity, AppForegroundService.class);
        activity.stopService(serviceIntent);
    }




}
