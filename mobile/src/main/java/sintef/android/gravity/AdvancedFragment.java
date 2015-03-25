package sintef.android.gravity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sintef.android.controller.algorithm.AlgorithmPhone;
import sintef.android.controller.utils.PreferencesHelper;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class AdvancedFragment extends Fragment {

    @InjectView(R.id.tat_edit)          EditText mTATEdit;
    @InjectView(R.id.tat_reset)         Button mTATReset;
    @InjectView(R.id.tat_save)          Button mTATSave;
    @InjectView(R.id.vat_edit)          EditText mVATEdit;
    @InjectView(R.id.vat_reset)         Button mVATReset;
    @InjectView(R.id.vat_save)          Button mVATSave;
    @InjectView(R.id.act_edit)          EditText mACTEdit;
    @InjectView(R.id.act_save)          Button mACTSave;
    @InjectView(R.id.act_reset)         Button mACTReset;
    @InjectView(R.id.it_edit)           EditText mITEdit;
    @InjectView(R.id.it_save)           Button mITSave;
    @InjectView(R.id.it_reset)          Button mITReset;
    @InjectView(R.id.pre_it_edit)       EditText mPrITEdit;
    @InjectView(R.id.pre_it_save)       Button mPrITSave;
    @InjectView(R.id.pre_it_reset)      Button mPrITReset;
    @InjectView(R.id.post_it_edit)      EditText mPoITEdit;
    @InjectView(R.id.post_it_save)      Button mPoITSave;
    @InjectView(R.id.post_it_reset)     Button mPoITReset;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advanced, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        ButterKnife.inject(this, getView());

        init();
    }

    private void init() {
        mTATEdit.setText(String.valueOf(AlgorithmPhone.getTotAccThreshold()));
        mVATEdit.setText(String.valueOf(AlgorithmPhone.getVerticalAccThreshold()));
        mACTEdit.setText(String.valueOf(AlgorithmPhone.getAccComparisonThreshold()));
        mITEdit.setText(String.valueOf(AlgorithmPhone.getImpactThreshold()));
        mPrITEdit.setText(String.valueOf(AlgorithmPhone.getPreImpactThreshold()));
        mPoITEdit.setText(String.valueOf(AlgorithmPhone.getPostImpactThreshold()));

        mTATSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesHelper.putFloat(AlgorithmPhone.TOTAL_ACCELEROMETER_THRESHOLD, Float.valueOf(mTATEdit.getText().toString()));
            }
        });

        mTATReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float def_value = (float) AlgorithmPhone.default_totAccThreshold;
                PreferencesHelper.putFloat(AlgorithmPhone.TOTAL_ACCELEROMETER_THRESHOLD, def_value);
                mTATEdit.setText(String.valueOf(def_value));
            }
        });

        mVATSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesHelper.putFloat(AlgorithmPhone.VERTICAL_ACCELEROMETER_THRESHOLD, Float.valueOf(mVATEdit.getText().toString()));
            }
        });

        mVATReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float def_value = (float) AlgorithmPhone.default_verticalAccThreshold;
                PreferencesHelper.putFloat(AlgorithmPhone.VERTICAL_ACCELEROMETER_THRESHOLD, def_value);
                mVATEdit.setText(String.valueOf(def_value));
            }
        });

        mACTSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesHelper.putFloat(AlgorithmPhone.ACCELEROMETER_COMPARISON_THRESHOLD, Float.valueOf(mACTEdit.getText().toString()));
            }
        });

        mACTReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float def_value = (float) AlgorithmPhone.default_accComparisonThreshold;
                PreferencesHelper.putFloat(AlgorithmPhone.ACCELEROMETER_COMPARISON_THRESHOLD, def_value);
                mACTEdit.setText(String.valueOf(def_value));
            }
        });

        mITSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesHelper.putFloat(AlgorithmPhone.IMPACT_THRESHOLD, Float.valueOf(mITEdit.getText().toString()));
            }
        });

        mITReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float def_value = (float) AlgorithmPhone.default_impactThreshold;
                PreferencesHelper.putFloat(AlgorithmPhone.IMPACT_THRESHOLD, def_value);
                mITEdit.setText(String.valueOf(def_value));
            }
        });

        mPrITSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesHelper.putFloat(AlgorithmPhone.PRE_IMPACT_THRESHOLD, Float.valueOf(mPrITEdit.getText().toString()));
            }
        });

        mPrITReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float def_value = (float) AlgorithmPhone.default_preimpactThreshold;
                PreferencesHelper.putFloat(AlgorithmPhone.PRE_IMPACT_THRESHOLD, def_value);
                mPrITEdit.setText(String.valueOf(def_value));
            }
        });

        mPoITSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesHelper.putFloat(AlgorithmPhone.POST_IMPACT_THRESHOLD, Float.valueOf(mPoITEdit.getText().toString()));
            }
        });

        mPoITReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float def_value = (float) AlgorithmPhone.default_postImpactThreshold;
                PreferencesHelper.putFloat(AlgorithmPhone.POST_IMPACT_THRESHOLD, def_value);
                mPoITEdit.setText(String.valueOf(def_value));
            }
        });
    }

}
