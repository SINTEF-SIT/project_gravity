package sintef.android.gravity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;
import sintef.android.gravity.wizard.WizardMain;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Main Activity";

    private static final String RUN_WIZARD = "run_wizard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, WizardMain.class));

        /*
        if (PreferencesHelper.getBoolean(RUN_WIZARD, true)) {
            startActivity(new Intent(this, WizardMain.class));
            finish();
            return;
        }
        Controller.initializeController(this);
        PreferencesHelper.initializePreferences(this);

        setContentView(R.layout.activity_main);

        init();
        startDetector();
        */
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, new NormalFragment());
        ft.commit();

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
        EventBus.getDefault().post(EventTypes.ONRESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(EventTypes.ONPAUSE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ken:
                return true;
            case R.id.action_advanced:
                openActionActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
