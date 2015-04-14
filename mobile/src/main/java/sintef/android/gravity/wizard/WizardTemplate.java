package sintef.android.gravity.wizard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by samyboy89 on 26/02/15.
 */
public abstract class WizardTemplate extends Fragment {

    public final static String RES_ID = "res_id";
    public final static String POSITION = "position";

    protected WizardMain mWizardMain;
    protected int mPosition;

    public WizardTemplate() {}

    protected static <T extends WizardTemplate> T newInstance(Class<T> clazz, int resId, int position) {
        T f;
        try {
            f = clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        Bundle args = new Bundle();
        args.putInt(RES_ID, resId);
        args.putInt(POSITION, position);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        mWizardMain = (WizardMain) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getArguments().getInt(RES_ID), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        mPosition = getArguments().getInt(POSITION);

        ButterKnife.inject(this, getView());
        init();
    }

    public abstract void init();
}
