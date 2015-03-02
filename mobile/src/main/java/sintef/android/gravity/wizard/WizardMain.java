package sintef.android.gravity.wizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.gravity.AdvancedFragment;
import sintef.android.gravity.MainActivity;
import sintef.android.gravity.R;

/**
 * Created by samyboy89 on 24/02/15.
 */
public class WizardMain extends ActionBarActivity {

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private Button mNextButton;
    private Button mPrevButton;

    private int mCurrentColor;

    private EditText mNameEdit;
    private EditText mTelephoneEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_main);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        //Bind the title indicator to the adapter
        /*TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setOnPageChangeListener(mPageChangeListener);*/

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // mStepPagerStrip.setCurrentPage(position);

                /*
                Drawable backgrounds[] = new Drawable[2];
                backgrounds[0] = new ColorDrawable(mCurrentColor);
                backgrounds[1] = new ColorDrawable(mCurrentColor == getResources().getColor(R.color.red_500) ? getResources().getColor(R.color.blue_500) : getResources().getColor(R.color.red_500));
                TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                crossfader.startTransition(300);

                mPager.setBackground(crossfader);

                mCurrentColor = mCurrentColor == getResources().getColor(R.color.red_500) ? getResources().getColor(R.color.blue_500) : getResources().getColor(R.color.red_500);
                */

                updateBottomBar(position);
            }
        });

        /*
        mCurrentColor = getResources().getColor(R.color.blue_500);
        mPager.setBackgroundColor(mCurrentColor);
         */

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mPagerAdapter.getCount()-1) {
                    if (mNameEdit == null && mTelephoneEdit == null) return;

                    /*
                    if (mNameEdit.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.wizard_next_of_kin_name_not_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mTelephoneEdit.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.wizard_next_of_kin_telephone_not_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }*/

                    PreferencesHelper.putString(Constants.PREFS_NEXT_OF_KIN_NAME, mNameEdit.getText().toString());
                    PreferencesHelper.putString(Constants.PREFS_NEXT_OF_KIN_TELEPHONE, mTelephoneEdit.getText().toString());
                    PreferencesHelper.putBoolean(Constants.PREFS_FIRST_START, false);

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == 0) {
                    mPager.setCurrentItem(mPagerAdapter.getCount()-1);
                    return;
                }
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        updateBottomBar(0);
    }

    private void updateBottomBar(int position) {
        if (position == mPagerAdapter.getCount()-1) {

        } else {

        }

        // mPrevButton.setText(position <= 0 ? R.string.skip : R.string.prev);
        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
        mNextButton.setText(position == mPagerAdapter.getCount() - 1 ? R.string.finish : R.string.next);
    }

    public void setFields(EditText name, EditText telephone) {
        mNameEdit = name;
        mTelephoneEdit = telephone;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return WizardXMLOnly.newInstance(WizardXMLOnly.class, R.layout.wizard_into, i);
                case 1:
                    return WizardNotification.newInstance(i);
                case 2:
                    return WizardXMLOnly.newInstance(WizardXMLOnly.class, R.layout.wizard_alarm, i);
                case 3:
                    return WizardXMLOnly.newInstance(WizardXMLOnly.class, R.layout.wizard_false_alarm, i);
                case 4:
                    return WizardNextOfKin.newInstance(i);
            }
            return new AdvancedFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}