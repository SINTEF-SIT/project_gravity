package sintef.android.gravity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmEvent;
import sintef.android.controller.AlarmView;
import sintef.android.controller.EventTypes;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class NormalFragment extends Fragment {

    private AlarmView mAlarmView;

    public NormalFragment() { }

    public static NormalFragment newInstance(boolean start_alarm) {
        NormalFragment fragment = new NormalFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainService.ALARM_STARTED, start_alarm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_normal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        mAlarmView = (AlarmView) getView().findViewById(R.id.alarm_view);

        EventBus.getDefault().register(this);

        boolean start_alarm = getArguments().getBoolean(MainService.ALARM_STARTED);

        if (start_alarm) mAlarmView.startAlarm();
    }

    public void onEvent(EventTypes type) {
        switch (type) {
            case FALL_DETECTED:
                mAlarmView.startAlarm();
                break;

        }
    }
    public void onEvent(AlarmEvent event) {
        mAlarmView.setAlarmProgress(event.progress);
    }
}
