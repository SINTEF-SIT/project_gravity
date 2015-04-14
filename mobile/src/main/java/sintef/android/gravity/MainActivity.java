package sintef.android.gravity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import sintef.android.controller.Controller;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.Constants;
import sintef.android.controller.tests.IntegrationTest;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.controller.utils.SoundHelper;
import sintef.android.controller.utils.Utils;
import sintef.android.gravity.advanced.AdvancedActivity;
import sintef.android.gravity.wizard.WizardMain;

public class MainActivity extends ActionBarActivity {

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
        if (!Utils.isServiceRunning(this, MainService.class)) startDetector();

        EventBus.getDefault().register(this);
        SoundHelper.initializeSoundsHelper(this);
        PreferencesHelper.initializePreferences(this);

        if (PreferencesHelper.getBoolean(Constants.PREFS_FIRST_START, true)) {
            PreferencesHelper.putBoolean(PreferencesHelper.FALL_DETECTION_ENABLED, false);
            startActivity(new Intent(this, WizardMain.class));
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
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, AlarmFragment.newInstance(alarm_started));
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
                message = getString(R.string.advanced_became);
                PreferencesHelper.putBoolean(ADVANCED_MENU_AVAILABLE, true);
                EventBus.getDefault().post(EventTypes.ADVANCED_MODE_CHANGED);
                invalidateOptionsMenu();
            } else {
                message = String.format(getString(R.string.advanced_away_from), ADVANCED_MENU_CLICK_MAX - mClickCount);
            }
            sToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            sToast.show();
        }
    }

    public void onEvent(EventTypes types) {
        switch (types) {
            case ALARM_STOPPED:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void startDetector() {
        startService(new Intent(this, MainService.class));
    }

    private void openAdvancedActivity() {
        startActivity(new Intent(this, AdvancedActivity.class));
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
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().post(EventTypes.ONSTOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(EventTypes.ONDESTROY);

        startActivity(new Intent(this, this.getClass()));
    }

    @Override
    public void finish() {
        super.finish();
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
                openAdvancedActivity();
                return true;
            case R.id.action_advanced_remove:
                PreferencesHelper.putBoolean(ADVANCED_MENU_AVAILABLE, false);
                EventBus.getDefault().post(EventTypes.ADVANCED_MODE_CHANGED);
                invalidateOptionsMenu();

                if (sToast != null) sToast.cancel();
                sToast = Toast.makeText(getApplicationContext(), R.string.advanced_removed, Toast.LENGTH_SHORT);
                sToast.show();
                return true;
            case R.id.action_about:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
