package sintef.android.gravity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import sintef.android.controller.common.Constants;

public class AlarmActivity extends Activity {

    private boolean keep;
    private Intent intent;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
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
        setContentView(R.layout.show_alarm);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        keep = intent.getExtras().getBoolean("keep");
        if (!keep) {
            mVibrator.cancel();
            AlarmActivity.this.finish();
        }
    }
}
