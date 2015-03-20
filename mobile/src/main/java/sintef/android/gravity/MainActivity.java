package sintef.android.gravity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import sintef.android.controller.Controller;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.controller.utils.SoundHelper;
import sintef.android.gravity.wizard.WizardMain;

public class MainActivity extends ActionBarActivity {

    public static final boolean TEST = false;
    public static final String ADVANCED_MENU_AVAILABLE = "advanced_menu_available";
    private static final String TAG = "Main Activity";

    private int mClickCount = 0;
    private static final int ADVANCED_MENU_CLICK_MAX = 7;
    private Handler mSevenClickResetHandler = new Handler();
    private Runnable mSevenClickResetRunnable = new Runnable() {
        @Override
        public void run() {
            mClickCount = 0;
        }
    };

    private static Toast sToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDetector();

        SoundHelper.initializeSoundsHelper(this);
        PreferencesHelper.initializePreferences(this);

        if (PreferencesHelper.getBoolean(Constants.PREFS_FIRST_START, true)) {
            startActivity(new Intent(this, WizardMain.class));
            // finish();
            return;
        }

        Controller.initializeController(getApplicationContext());

        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        boolean alarm_started = false;
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(MainService.ALARM_STARTED)) alarm_started = true;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, NormalFragment.newInstance(alarm_started));
        ft.commit();

        findViewById(R.id.fragment_placeholder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSevenClickResetHandler.removeCallbacksAndMessages(null);
                mSevenClickResetHandler.postDelayed(mSevenClickResetRunnable, 2000);
                updateAdvancedClickProgress();
            }
        });
    }

    private void updateAdvancedClickProgress() {
        if (PreferencesHelper.getBoolean(ADVANCED_MENU_AVAILABLE, false)) return;

        mClickCount++;
        if (mClickCount >= 3) {
            if (sToast != null) sToast.cancel();
            String message = "";
            if (mClickCount >= ADVANCED_MENU_CLICK_MAX) {
                message = "Advanced mode enabled. Access it using the overflow menu on the top right.";
                PreferencesHelper.putBoolean(ADVANCED_MENU_AVAILABLE, true);
                invalidateOptionsMenu();
            } else {
                message = "You're " + (ADVANCED_MENU_CLICK_MAX -mClickCount) + " steps away from advanced mode";
            }
            sToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            sToast.show();
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void startDetector() {
        startService(new Intent(this, MainService.class));
    }

    private void openActionActivity() {
        startActivity(new Intent(this, AdvancedActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.wtf("MAIN ACTIVITY", "onResume");
        EventBus.getDefault().post(EventTypes.ONRESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.wtf("MAIN ACTIVITY", "onPause");
        EventBus.getDefault().post(EventTypes.ONPAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.wtf("MAIN ACTIVITY", "onStop");
        EventBus.getDefault().post(EventTypes.ONSTOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.wtf("MAIN ACTIVITY", "onDestroy");
        EventBus.getDefault().post(EventTypes.ONDESTROY);

        startActivity(new Intent(this, this.getClass()));
    }

    @Override
    public void finish() {
        super.finish();
        Log.wtf("MAIN ACTIVITY", "finish");
        EventBus.getDefault().post(EventTypes.FINISH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean advanced_menu_available = PreferencesHelper.getBoolean(ADVANCED_MENU_AVAILABLE, false);
        menu.findItem(R.id.action_advanced).setVisible(advanced_menu_available);
        menu.findItem(R.id.action_advanced_remove).setVisible(advanced_menu_available);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ken:
                new NextOfKinDialog(this);
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, WizardMain.class));
                return true;
            case R.id.action_advanced:
                openActionActivity();
                return true;
            case R.id.action_advanced_remove:
                PreferencesHelper.putBoolean(ADVANCED_MENU_AVAILABLE, false);
                invalidateOptionsMenu();
                return true;
            case R.id.action_about:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
