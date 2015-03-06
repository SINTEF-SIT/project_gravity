package sintef.android.gravity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class MainService extends Service {

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private TimerState mState;
    private TimerRunnable mTimerRunnable;

    @Override
    public void onCreate() {
        EventBus.getDefault().registerSticky(this);
        mState = TimerState.PENDING;

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;    //super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ServiceLauncher.class), 0);

        mNotificationBuilder = new Notification.Builder(getApplicationContext())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Detecting")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_on);

        mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
    }

    public void onEvent(String update) {
        mNotificationBuilder.setContentText(update);
        mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
    }

    public void onEvent(EventTypes type) {
        switch (type) {
            case FALL_DETECTED:
                PendingIntent stopIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainService.class), 0);

                mState = TimerState.TIMER_RUNNING;

                if (mTimerRunnable != null) {
                    mNotificationBuilder = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Detecting")
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .setSmallIcon(R.drawable.ic_stat_on);

                    mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
                    mTimerRunnable.kill();
                } else {
                    mTimerRunnable = new TimerRunnable();
                }

                mNotificationBuilder.setContentText("Waiting to send alarm");
                mNotificationBuilder.addAction(android.R.drawable.presence_busy, "Cancel", stopIntent);
                mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());

                Thread thread = new Thread(mTimerRunnable);
                thread.start();
                break;
            case ALARM_STOPPED:
                if (mTimerRunnable != null) {
                    mTimerRunnable.kill();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mNotificationBuilder = new Notification.Builder(getApplicationContext())
                                .setContentTitle("Detecting")
                                .setAutoCancel(false)
                                .setOngoing(true)
                                .setSmallIcon(R.drawable.ic_stat_on);

                        mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
                    }
                }, 5000);
                break;

        }
    }

    public class TimerRunnable implements Runnable {

        private volatile boolean isRunning = true;

        public void run() {
            boolean alarm = false;
            for (int i = 0; i <= 100; i++) {
                if (!isRunning) {
                    alarm = false;
                    break;
                }
                mNotificationBuilder.setProgress(100, i, false);
                mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    alarm = false;
                    Log.d("Alarm", "sleep failure");
                }
                alarm = true;
            }

            mNotificationBuilder = new Notification.Builder(getApplicationContext())
                    .setContentTitle(alarm ? "Alarm sent" : "Alarm cancelled")
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_stat_on);

            mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
            mTimerRunnable = null;
        }

        public void kill() {
            isRunning = false;
        }

    }

    public static enum TimerState {
        PENDING, TIMER_RUNNING, TIMER_CANCELLED, ALARM_SENT,
    }

}
