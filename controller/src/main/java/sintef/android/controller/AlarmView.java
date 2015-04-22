/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package sintef.android.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import sintef.android.controller.utils.DonutProgress;
import sintef.android.model.R;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class AlarmView extends RelativeLayout {

    private View mAlarmProgressBackground;
    private DonutProgress mAlarmProgress;
    private TextView mAlarmText;

    private boolean mAlarmStartedAgain = false;

    private AsyncTask<Void, Integer, Void> mCurrentAlarmTask;
    private int mCorrectIndex = -1;

    private static OnStopListener mStopListener;
    private static OnAlarmListener mAlarmListener;

    private int seconds = 60;
    private int resolution_multiplier = 4;
    private int max = seconds * resolution_multiplier;
    private int second = 1000;
    private int resolution_second = second / resolution_multiplier;

    private float mStrokeWidth = 30;

    public AlarmView(Activity activity, int layout_id) {
        super(activity);
        init(layout_id);
    }

    public AlarmView(Context context) {
        super(context);
        init(-1);
    }

    public AlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(-1);
    }

    public AlarmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(-1);
    }

    private void init(int layout_id) {
        inflate(getContext(), layout_id == -1 ? R.layout.alarm_view : layout_id, this);
        init();
    }

    private void init() {
        mAlarmText = (TextView) findViewById(R.id.alarm_text);
        mAlarmProgress = (DonutProgress) findViewById(R.id.alarm_progress);
        mAlarmProgressBackground = findViewById(R.id.alarm_progress_background);

        setStrokeWidth(mStrokeWidth);

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
            }
        };
        mAlarmText.setOnClickListener(clickListener);
        mAlarmProgress.setOnClickListener(clickListener);
        mAlarmProgressBackground.setOnClickListener(clickListener);

        mAlarmProgress.setMax(max);
        resetAlarmProgress();
    }

    public void setStrokeWidth(float width) {
        mStrokeWidth = width;
    }

    private void resetAlarmProgress() {
        mAlarmText.setText(R.string.alarm_progress_disabled);
        mAlarmText.setTextColor(Color.WHITE);
        mAlarmProgress.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setBackgroundResource(R.drawable.circle_disabled);
        mAlarmProgress.setUnfinishedStrokeColor(Color.parseColor("#EEEEEE"));
        mAlarmProgress.setProgress(0);
    }

    private void runAlarmProgress() {
        mAlarmText.setText(R.string.alarm_progress_running);
        mAlarmText.setTextColor(Color.WHITE);
        mAlarmProgress.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setVisibility(View.VISIBLE);
        mAlarmProgressBackground.setBackgroundResource(R.drawable.circle);
        mAlarmProgress.setUnfinishedStrokeColor(getResources().getColor(R.color.red_stroke));
        mAlarmProgress.setFinishedStrokeColor(getResources().getColor(R.color.orange));
        mAlarmProgress.setProgress(0);
    }

    private void setAlarm(boolean cancelled) {
        mAlarmStartedAgain = false;
        if (!cancelled) {
            if (mAlarmListener != null) mAlarmListener.onAlarm();
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

    public void setOnStopListener(OnStopListener listener) {
        mStopListener =  listener;
    }

    public void setOnAlarmListener(OnAlarmListener listener) {
        mAlarmListener =  listener;
    }

    public void setAlarmProgress(int progress) {
        mCorrectIndex = progress;
    }

    public void stopAlarm() {
        if (mCurrentAlarmTask != null) {
            mCurrentAlarmTask.cancel(true);

            if (mStopListener == null) EventBus.getDefault().post(EventTypes.ALARM_STOPPED);
            else mStopListener.onStop();
        }
    }

    public void stopAlarmWithoutNotify() {
        if (mCurrentAlarmTask != null) {
            mCurrentAlarmTask.cancel(true);
        }
    }

    public void startAlarm() {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentAlarmTask != null) return;
                mCorrectIndex = -1;
                init();
                mCurrentAlarmTask = new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        mAlarmStartedAgain = true;

                        for (int i = 0; i < max; i++) {
                            if (isCancelled()) {
                                break;
                            }
                            if (mCorrectIndex > 0) {
                                i = mCorrectIndex;
                                mCorrectIndex = -1;
                            }
                            try {
                                Thread.sleep(resolution_second);
                                onProgressUpdate(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(final Integer... values) {
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mAlarmProgress != null)
                                    mAlarmProgress.setProgress(values[0] + 1);
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
                mCurrentAlarmTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
          });
    }

    public interface OnStopListener {
        void onStop();
    }

    public interface OnAlarmListener {
        void onAlarm();
    }
}
