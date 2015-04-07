package sintef.android.gravity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmEvent;
import sintef.android.controller.AlarmView;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.SoundHelper;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class AlarmFragment extends Fragment {

    private AlarmView mAlarmView;
    private static Vibrator mVibrator;

    public AlarmFragment() { }

    public static AlarmFragment newInstance(boolean start_alarm) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainService.ALARM_STARTED, start_alarm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mVibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        mAlarmView = (AlarmView) getView().findViewById(R.id.alarm_view);
        mAlarmView.setOnStopListener(new AlarmView.OnStopListener() {
            @Override
            public void onStop() {
                if (mVibrator != null) mVibrator.cancel();
                EventBus.getDefault().post(EventTypes.ALARM_STOPPED);
            }
        });

        EventBus.getDefault().register(this);

        boolean start_alarm = getArguments().getBoolean(MainService.ALARM_STARTED);

        if (start_alarm) mAlarmView.startAlarm();
    }

    public void onEvent(EventTypes type) {
        switch (type) {
            case START_ALARM:
                mVibrator.vibrate(Constants.ALARM_VIBRATION_PATTERN_ON_WATCH, 0);
                mAlarmView.startAlarm();
                break;
            case STOP_ALARM:
                if (mVibrator != null) mVibrator.cancel();
                mAlarmView.stopAlarmWithoutNotify();
                break;
        }
    }
    public void onEvent(AlarmEvent event) {
        SoundHelper.playAlarmSound();
        mAlarmView.setAlarmProgress(event.progress);
    }
}