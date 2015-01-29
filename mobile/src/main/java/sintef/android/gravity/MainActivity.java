package sintef.android.gravity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import sintef.android.algorithm.utils.EventTypes;
import sintef.android.algorithm.utils.SensorData;
import sintef.android.algorithm.utils.SensorHelper;

public class MainActivity extends ActionBarActivity {

    private EventBus mEventBus;

    private TextView mUpdates;
    private LinearLayout mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = EventBus.getDefault();
        mEventBus.registerSticky(this);

        setContentView(R.layout.activity_main);
        mUpdates = (TextView) findViewById(R.id.text);
        mChart = (LinearLayout) findViewById(R.id.chart);

        new Chart(this, mChart);
        new SensorHelper(this);
    }

    public void onEvent(String message) {
        mUpdates.setText(message + "\n" + mUpdates.getText());
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
