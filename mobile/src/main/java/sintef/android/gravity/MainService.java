package sintef.android.gravity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmEvent;
import sintef.android.controller.EventTypes;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class MainService extends Service {

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;

    private TimerState mState = TimerState.PENDING;

    public static final String ALARM_STARTED = "alarm_started";

    private final int WAIT_BEFORE_RESET_PERIOD = 5000;

    private AsyncTask<Void, Integer, Boolean> mAlarmTask;
    private int seconds = 60;
    private int resolution_multiplier = 4;
    private int max = seconds * resolution_multiplier;
    private int second = 1000;
    private int resolution_second = second / resolution_multiplier;
    private int update_frequency = resolution_multiplier * 2;

    @Override
    public void onCreate() {
        if (!MainActivity.TEST) EventBus.getDefault().registerSticky(this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        resetNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;    //super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(R.string.app_name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public synchronized void onEvent(EventTypes type) {
        switch (type) {
            case FALL_DETECTED:
                if (!mState.equals(TimerState.PENDING)) return;
                EventBus.getDefault().post(EventTypes.START_ALARM);

                mState = TimerState.TIMER_RUNNING;

                Intent start_app_intent = new Intent(this, MainActivity.class);
                start_app_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                start_app_intent.putExtra(ALARM_STARTED, true);
                startActivity(start_app_intent);

                PendingIntent start_app_pending_intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

                mNotificationBuilder.setContentTitle("Waiting to send alarm");
                // mNotificationBuilder.addAction(android.R.drawable.presence_busy, "Cancel", stopIntent);
                mNotificationBuilder.setContentIntent(start_app_pending_intent);
                mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());

                mAlarmTask = getAlarmTask();
                mAlarmTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case ALARM_STOPPED:
                if (mAlarmTask != null) {
                    mAlarmTask.cancel(true);
                }
                break;

        }
    }

    private AsyncTask<Void, Integer, Boolean> getAlarmTask() {
        return new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                for (int i = 0; i <= max; i++) {
                    if (isCancelled()) {
                        return false;
                    }

                    try {
                        Thread.sleep(resolution_second);
                        publishProgress(i);
                    } catch (InterruptedException e) {
                        Log.d("Alarm", "sleep failure");
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                int i = values[0];

                if (i % update_frequency == 0) EventBus.getDefault().post(new AlarmEvent(max, i));
                mNotificationBuilder.setProgress(max, i, false);
                mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
            }

            @Override
            protected void onPostExecute(Boolean alarm) {
                super.onPostExecute(alarm);
                updateTaskState(alarm);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                updateTaskState(false);
            }

            @Override
            protected void onCancelled(Boolean alarm) {
                super.onCancelled(alarm);
                updateTaskState(alarm);
            }

            private void updateTaskState(boolean alarm) {
                mNotificationBuilder = new Notification.Builder(getApplicationContext())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(alarm ? "Alarm sent" : "Alarm cancelled")
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.ic_stat_on);
                mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());

                mState = alarm ? TimerState.ALARM_SENT : TimerState.TIMER_CANCELLED;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetNotification();
                        mAlarmTask = null;
                    }
                }, WAIT_BEFORE_RESET_PERIOD);
            }
        };
    }

    private void resetNotification() {
        if (mState == TimerState.TIMER_RUNNING) return;

        mState = TimerState.PENDING;

        mNotificationBuilder = new Notification.Builder(getApplicationContext())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Detecting")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_on);

        mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
    }

    public static enum TimerState {
        PENDING, TIMER_RUNNING, TIMER_CANCELLED, ALARM_SENT,
    }

}
