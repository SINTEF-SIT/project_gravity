package sintef.android.controller;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorHandshake;
import sintef.android.controller.sensor.SensorManager;
import sintef.android.controller.sensor.SensorSession;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class Controller {

    private static EventBus sEventBus;
    private static Controller sController;
    private Context mContext;

    private static HashMap<SensorSession, List<SensorData>> mAllSensorData = new HashMap<>();

    public static void initializeController(Context context) {
        sController = new Controller(context);
    }

    private Controller(Context context) {
        mContext = context;
        sEventBus = EventBus.getDefault();
        sEventBus.registerSticky(this);

        new SensorManager(context);
    }

    public static Controller getController() {
        return sController;
    }

    public void onEvent(SensorHandshake handshake) {
        switch (handshake.getType()) {
            case CONNECT:
                if (mAllSensorData.containsKey(handshake.getSensorSession())) return;
                mAllSensorData.put(handshake.getSensorSession(), new ArrayList<SensorData>());
                break;
            case DISCONNECT:
                if (!mAllSensorData.containsKey(handshake.getSensorSession())) return;
                mAllSensorData.remove(handshake.getSensorSession());
                break;
        }
    }

    public void onEvent(SensorData data) {
        if (data.getSensorSession() == null) return;
        if (mAllSensorData.containsKey(data.getSensorSession())) {
            List<SensorData> sensorData = mAllSensorData.get(data.getSensorSession());
            sensorData.add(data);

            sEventBus.post("ACC: " + ((float) data.getSensorData()));

            notifySensorSessionUpdated(data.getSensorSession(), sensorData);
        }
    }

    public void notifySensorSessionUpdated(SensorSession sensorSession, List<SensorData> sensorData) {

    }
}
