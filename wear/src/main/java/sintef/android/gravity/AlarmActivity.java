package sintef.android.gravity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmView;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.RemoteSensorManager;

public class AlarmActivity extends Activity {

    private AlarmView mAlarmView;

    private boolean keep;
    private Intent intent;
    private static Vibrator mVibrator;
    private RemoteSensorManager mRemoteSensorManager;
    private EventBus mEventBus;
    private static PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        mRemoteSensorManager = mRemoteSensorManager.getInstance(this);
        mAlarmView = new AlarmView(this, R.layout.show_alarm);
        mAlarmView.setOnStopListener(new AlarmView.OnStopListener() {
            @Override
            public void onStop() {
                mRemoteSensorManager.stopAlarm();
                stopAlarmActivity();
            }
        });


    }

    public void showAlarm() {
        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Clock");
        mWakeLock.acquire(1000);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(Constants.ALARM_VIBRATION_PATTERN_ON_WATCH, 0);
        setContentView(mAlarmView);
        mAlarmView.startAlarm();

    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = this.getIntent();
        keep = intent.getExtras().containsKey("keep") && intent.getExtras().getBoolean("keep");
        if (keep) {
            showAlarm();
        } else {
            stopAlarmActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        keep = intent.getExtras().containsKey("keep") && intent.getExtras().getBoolean("keep");
        if (!keep) {
            stopAlarmActivity();
        }
    }

    private void stopAlarmActivity() {
        if (mVibrator != null) mVibrator.cancel();
        AlarmActivity.this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVibrator != null) mVibrator.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVibrator != null) mVibrator.cancel();
    }

    public void onEvent(int progress) {
        mAlarmView.setAlarmProgress(progress);
    }

}
