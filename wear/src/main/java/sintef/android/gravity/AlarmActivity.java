package sintef.android.gravity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    private Vibrator mVibrator;
    private RemoteSensorManager mRemoteSensorManager;
    private EventBus mEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

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

        intent = this.getIntent();
        keep = intent.getExtras().getBoolean("keep");
        if (keep) {
            stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
                @Override
                public void onLayoutInflated(WatchViewStub stub) {
                    showAlarm(findViewById(R.id.watch_view_stub));

                }
            });
        }
    }

    public void showAlarm(View view) {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(Constants.ALARM_VIBRATION_PATTERN_ON_WATCH, 0);
        setContentView(mAlarmView);
        mAlarmView.startAlarm();

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
        mVibrator.cancel();
        AlarmActivity.this.finish();
    }

    public void onEvent(int progress) {
        mAlarmView.setAlarmProgress(progress);
    }

}
