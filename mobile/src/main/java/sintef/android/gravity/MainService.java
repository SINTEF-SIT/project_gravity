package sintef.android.gravity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import de.greenrobot.event.EventBus;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class MainService extends Service {

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;

    @Override
    public void onCreate() {
        EventBus.getDefault().registerSticky(this);

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
                .setContentTitle("GRAVITY")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_on);

        mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
    }

    public void onEvent(String update) {
        mNotificationBuilder.setContentText(update);
        mNotificationManager.notify(R.string.app_name, mNotificationBuilder.build());
    }
}
