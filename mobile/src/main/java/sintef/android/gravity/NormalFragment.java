package sintef.android.gravity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import sintef.android.controller.utils.DonutProgress;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class NormalFragment extends Fragment implements View.OnClickListener {

    private View mAlarmProgressBackground;
    private DonutProgress mAlarmProgress;
    private Button mForceAlarmButton;
    private TextView mAlarmText;

    private boolean mAlarmStartedAgain = false;

    private AsyncTask<Void, Integer, Void> mCurrentAlarmTask;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_normal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;


        init();

    }

    private void init() {
        mAlarmText = (TextView) getView().findViewById(R.id.alarm_text);
        mAlarmProgressBackground = getView().findViewById(R.id.progress_background);
        mAlarmProgress = (DonutProgress) getView().findViewById(R.id.progress);
        mForceAlarmButton = (Button) getView().findViewById(R.id.start_alarm);

        mAlarmProgress.setOnClickListener(this);
        mForceAlarmButton.setOnClickListener(this);
        resetAlarmProgress();
    }

    private void resetAlarmProgress() {
        mAlarmText.setText(R.string.alarm_progress_disabled);
        mAlarmText.setTextColor(Color.WHITE);
        mAlarmProgress.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setBackgroundResource(R.drawable.circle_disabled);
        mAlarmProgress.setUnfinishedStrokeColor(Color.BLACK);
        mAlarmProgress.setUnfinishedStrokeWidth(30);
        mAlarmProgress.setProgress(0);
    }

    private void runAlarmProgress() {
        mAlarmText.setText(R.string.alarm_progress_running);
        mAlarmText.setTextColor(Color.WHITE);
        mAlarmProgress.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setBackgroundResource(R.drawable.circle);
        mAlarmProgress.setUnfinishedStrokeColor(getResources().getColor(R.color.red_stroke));
        mAlarmProgress.setUnfinishedStrokeWidth(30);
        mAlarmProgress.setFinishedStrokeColor(getResources().getColor(R.color.orange));
        mAlarmProgress.setFinishedStrokeWidth(30);
        mAlarmProgress.setProgress(0);
    }

    private void setAlarm(boolean cancelled) {
        mAlarmStartedAgain = false;
        if (!cancelled) {

            // MAKE ALARM

        }

        mAlarmText.setText(cancelled ? R.string.alarm_progress_cancelled : R.string.alarm_progress_done);
        mAlarmText.setTextColor(Color.BLACK);

        mAlarmProgress.setVisibility(View.GONE);
        mAlarmProgressBackground.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAlarmStartedAgain) return;
                resetAlarmProgress();
            }
        }, 5000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.progress:
            case R.id.progress_background:
            case R.id.alarm_text:
                if (mCurrentAlarmTask != null) {
                    mCurrentAlarmTask.cancel(true);
                }
                break;
            case R.id.start_alarm:
                if (mCurrentAlarmTask != null) return;
                mCurrentAlarmTask = new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        mAlarmStartedAgain = true;
                        
                        for (int i = 0; i < 100; i++) {
                            if (isCancelled()) {
                                break;
                            }
                            try {
                                Thread.sleep(300);
                                onProgressUpdate(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(final Integer... values) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mAlarmProgress != null) mAlarmProgress.setProgress(values[0] + 1);
                            }
                        });
                    }

                    @Override
                    protected void onCancelled(Void aVoid) {
                        mCurrentAlarmTask = null;
                        setAlarm(true);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        mCurrentAlarmTask = null;
                        setAlarm(false);
                    }
                };
                runAlarmProgress();
                mCurrentAlarmTask.execute();
                break;
        }
    }
}
