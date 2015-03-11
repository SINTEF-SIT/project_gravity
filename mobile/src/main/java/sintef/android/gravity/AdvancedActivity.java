package sintef.android.gravity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class AdvancedActivity extends ActionBarActivity {

    private static final int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColor(Color.WHITE);
        tabs.setViewPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(EventTypes.ONRESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(EventTypes.ONPAUSE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GraphFragment();
                case 1:
                    return new AdvancedFragment();
                case 2:
                    return new RecordFragment();
            }
            return new NormalFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.fragment_graph);
                case 1:
                    return getString(R.string.fragment_advanced);
                case 2:
                    return getString(R.string.fragment_record);
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
