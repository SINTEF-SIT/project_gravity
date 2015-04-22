package sintef.android.gravity.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.gravity.R;
import sintef.android.gravity.wizard.FloatingHintEditText;

/**
 * Created by samyboy89 on 08/11/14.
 */
public class NextOfKinDialog extends Dialog {

    @InjectView(R.id.dialog_next_of_kin_name)           public FloatingHintEditText mNameEdit;
    @InjectView(R.id.dialog_next_of_kin_telephone)      public FloatingHintEditText mTelephoneEdit;
    @InjectView(R.id.ok_button)                         public View mOK;
    @InjectView(R.id.cancel_button)                     public View mCancel;

    @SuppressLint("NewApi")
    public NextOfKinDialog(final Context context) {
        super(context);

        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        lp.alpha = 1f;
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        setTitle(R.string.wizard_next_of_kin_header);
        setContentView(R.layout.dialog_next_of_kin);
        ButterKnife.inject(this);

        String name = PreferencesHelper.getString(Constants.PREFS_NEXT_OF_KIN_NAME);
        if (!name.equals(PreferencesHelper.INVALID_STRING)) mNameEdit.setText(name);

        String telephone = PreferencesHelper.getString(Constants.PREFS_NEXT_OF_KIN_TELEPHONE);
        if (!telephone.equals(PreferencesHelper.INVALID_STRING)) mTelephoneEdit.setText(telephone);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNameEdit.getText().length() == 0) {
                    Toast.makeText(getContext(), R.string.wizard_next_of_kin_name_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mTelephoneEdit.getText().length() == 0) {
                    Toast.makeText(getContext(), R.string.wizard_next_of_kin_telephone_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                PreferencesHelper.putString(Constants.PREFS_NEXT_OF_KIN_NAME, mNameEdit.getText().toString());
                PreferencesHelper.putString(Constants.PREFS_NEXT_OF_KIN_TELEPHONE, mTelephoneEdit.getText().toString());
                dismiss();
            }
        });

        show();

        getWindow().getDecorView().setSystemUiVisibility(((Activity) context).getWindow().getDecorView().getSystemUiVisibility());
    }

}
