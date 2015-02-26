package sintef.android.gravity.wizard;

import butterknife.InjectView;
import sintef.android.gravity.R;

/**
 * Created by samyboy89 on 26/02/15.
 */
public class WizardNextOfKin extends WizardTemplate {

    @InjectView(R.id.wizard_next_of_kin_name)        public FloatingHintEditText mNameEdit;
    @InjectView(R.id.wizard_next_of_kin_telephone)   public FloatingHintEditText mTelephoneEdit;

    public static WizardNextOfKin newInstance(int position) {
        return newInstance(WizardNextOfKin.class, R.layout.wizard_next_of_kin, position);
    }

    @Override
    public void init() {
        mWizardMain.setFields(mNameEdit, mTelephoneEdit);
    }
}
