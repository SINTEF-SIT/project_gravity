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

package sintef.android.gravity.advanced;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sintef.android.controller.algorithm.AlgorithmsToChoose;
import sintef.android.controller.algorithm.PatternRecognitionPhone;
import sintef.android.controller.algorithm.PatternRecognitionWatch;
import sintef.android.controller.algorithm.ThresholdPhone;
import sintef.android.controller.algorithm.ThresholdWatch;
import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.gravity.R;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class ThresholdsFragment extends Fragment {

    @InjectView(R.id.algorithm_choice)  Spinner mAlgorithmChoice;
    @InjectView(R.id.list)              ListView mList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thresholds, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        ButterKnife.inject(this, getView());

        init();
    }

    private void init() {
        final AlgorithmsToChoose[] data = AlgorithmsToChoose.values();

        int algorithm = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);

        int position = 0;
        for (AlgorithmsToChoose algorithmsToChoose : data) {
            if (algorithmsToChoose.getId() == algorithm)
                break;
            position++;
        }

        ArrayAdapter<AlgorithmsToChoose> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_static_item, data);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dialog_item);
        mAlgorithmChoice.setAdapter(dataAdapter);
        mAlgorithmChoice.setSelection(position);
        mAlgorithmChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, data[i].getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ThresholdItem[] items = {
                new ThresholdItem(
                        ThresholdPhone.TOTAL_ACCELEROMETER_THRESHOLD,
                        getString(R.string.fragment_thresholds_phone_total_acc),
                        ThresholdPhone.DEFAULT_TOT_ACC_THRESHOLD),
                new ThresholdItem(
                        ThresholdPhone.VERTICAL_ACCELEROMETER_THRESHOLD,
                        getString(R.string.fragment_thresholds_phone_vertical_acc),
                        ThresholdPhone.DEFAULT_VERTICAL_ACC_THRESHOLD),
                new ThresholdItem(
                        ThresholdPhone.ACCELEROMETER_COMPARISON_THRESHOLD,
                        getString(R.string.fragment_thresholds_phone_vertical_total_acc),
                        ThresholdPhone.DEFAULT_ACC_COMPARISON_THRESHOLD),
                new ThresholdItem(
                        PatternRecognitionPhone.PRE_IMPACT_THRESHOLD,
                        getString(R.string.fragment_thresholds_phone_pre_impact),
                        PatternRecognitionPhone.DEFAULT_PREIMPACT_THRESHOLD),
                new ThresholdItem(
                        PatternRecognitionPhone.IMPACT_THRESHOLD,
                        getString(R.string.fragment_thresholds_phone_impact),
                        PatternRecognitionPhone.DEFAULT_IMPACT_THRESHOLD),
                new ThresholdItem(
                        PatternRecognitionPhone.POST_IMPACT_THRESHOLD,
                        getString(R.string.fragment_thresholds_phone_avg_post_impact),
                        PatternRecognitionPhone.DEFAULT_POST_IMPACT_THRESHOLD),

                new ThresholdItem(
                        ThresholdWatch.FALLINDEX_IMPACT,
                        getString(R.string.fragment_thresholds_watch_fallindex_impact),
                        ThresholdWatch.DEFAULT_THRESHOLD_FALL),
                new ThresholdItem(
                        PatternRecognitionWatch.FALLINDEX_POST_IMPACT,
                        getString(R.string.fragment_thresholds_watch_fallindex_post_impact),
                        PatternRecognitionWatch.DEFAULT_THRESHOLD_STILL),
                new ThresholdItem(
                        PatternRecognitionWatch.FALL_DURATION,
                        getString(R.string.fragment_thresholds_watch_fall_duration),
                        PatternRecognitionWatch.DEFAULT_MOVEMENT_THRESHOLD)
        };

        ThresholdsAdapter adapter = new ThresholdsAdapter(new ArrayList<>(Arrays.asList(items)));
        mList.setAdapter(adapter);

    }

}
