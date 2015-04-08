package sintef.android.gravity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.WindowManager;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmView;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.RemoteSensorManager;

public class AlarmActivity extends Activity {

    private AlarmView mAlarmView;

    private static PowerManager.WakeLock mWakeLock;
    private static Vibrator mVibrator;
    private RemoteSensorManager mRemoteSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showAlarm() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(Constants.ALARM_VIBRATION_PATTERN_ON_WATCH, 0);

        setContentView(mAlarmView);
        mAlarmView.startAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getExtras() == null) return;

        boolean keep = getIntent().getExtras().containsKey("keep") && getIntent().getExtras().getBoolean("keep");
        if (keep) {
            if (mAlarmView == null) {
                EventBus.getDefault().register(this);
                mRemoteSensorManager = RemoteSensorManager.getInstance(this);
                mAlarmView = new AlarmView(this, R.layout.show_alarm);
                mAlarmView.setOnStopListener(new AlarmView.OnStopListener() {
                    @Override
                    public void onStop() {
                        mRemoteSensorManager.stopAlarm();
                        stopAlarmActivity();
                    }
                });
                mAlarmView.setStrokeWidth(14);
                mAlarmView.setOnAlarmListener(new AlarmView.OnAlarmListener() {
                    @Override
                    public void onAlarm() {
                        if (mVibrator != null) mVibrator.cancel();
                    }
                });

                showAlarm();
            }
        } else {
            stopAlarmActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void stopAlarmActivity() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVibrator != null) mVibrator.cancel();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVibrator != null) mVibrator.cancel();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(int progress) {
        mAlarmView.setAlarmProgress(progress);
    }

}
