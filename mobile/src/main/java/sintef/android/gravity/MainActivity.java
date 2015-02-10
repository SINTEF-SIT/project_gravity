package sintef.android.gravity;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import de.greenrobot.event.EventBus;
import sintef.android.controller.Controller;
import sintef.android.controller.EventTypes;
import sintef.android.controller.sensor.RemoteSensorManager;
import sintef.android.controller.sensor.SensorData;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Main Activity";

    private EventBus mEventBus;

    private LinearLayout mChart;
    private RemoteSensorManager mRemoteSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Controller.initializeController(this);

        mEventBus = EventBus.getDefault();
        mEventBus.registerSticky(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChart = (LinearLayout) findViewById(R.id.chart);

        new Chart(this, mChart);

        startService(new Intent(this, MainService.class));
        mRemoteSensorManager = RemoteSensorManager.getInstance(this);

    }

    public void onEvent(String message) {
        // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onEvent(SensorData data) {

        // Toast.makeText(this, data.value+"", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.post(EventTypes.ONRESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.post(EventTypes.ONPAUSE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
