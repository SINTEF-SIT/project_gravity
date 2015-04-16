package sintef.android.controller;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import sintef.android.controller.algorithm.AlgorithmMain;
import sintef.android.controller.algorithm.SensorAlgorithmPack;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorManager;
import sintef.android.controller.sensor.SensorSession;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class Controller {

    public static boolean DBG = false;
    private static boolean DBG_RATE = false;

    private static String TAG = "G:CONTROLLER:C";

    private static Controller sController;
    private static EventBus sEventBus;
    private Context mContext;

    private static List<Map<SensorSession, List<SensorData>>> allData = new ArrayList<Map<SensorSession, List<SensorData>>>();

    public static void initializeController(Context context) {
        if (sController == null) sController = new Controller(context);
    }

    private Controller(Context context) {
        mContext = context;
        sEventBus = EventBus.getDefault();
        sEventBus.register(this);

        AlgorithmMain.initializeAlgorithmMaster();
        SensorManager.getInstance(context);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                SensorAlgorithmPack pack;
                allData.add(0, new HashMap<SensorSession, List<SensorData>>());
                if (allData.size() > 2) {
                    pack = SensorAlgorithmPack.processNewSensorData(allData.get(2), allData.get(1));
                } else if (allData.size() > 1) {
                    pack = SensorAlgorithmPack.processNewSensorData(allData.get(1), null);
                } else {
                    return;
                }
                sEventBus.post(pack);

                if (allData.size() > 2) allData.remove(2);

            }
        }, 0, Constants.ALGORITHM_SEND_FREQUENCY);


    }

    public static Controller getController() {
        return sController;
    }

    public Context getContext() {
        return mContext;
    }

     private static long time = System.currentTimeMillis();
     private static int times_in_sek = 0;

    public synchronized void onEvent(SensorData data) {
        if (allData.isEmpty()) allData.add(0, new HashMap<SensorSession, List<SensorData>>());
        Map<SensorSession, List<SensorData>> sensorData = allData.get(0);

        if (data.getSensorSession().getSensorType() == Sensor.TYPE_LINEAR_ACCELERATION) times_in_sek += 1;
        if (time + 1000 <= System.currentTimeMillis() ) {
            if (DBG_RATE) Log.d(TAG, String.format("%d @ %d", times_in_sek, time));

            if (times_in_sek < 10) {
                EventBus.getDefault().post(EventTypes.RESET_SENSOR_LISTENERS);
            }

            time = System.currentTimeMillis();
            times_in_sek = 0;
        }

        if (data.getSensorSession() == null) return;
        if (!sensorData.containsKey(data.getSensorSession())) {
            sensorData.put(data.getSensorSession(), new ArrayList<SensorData>());
        }

        sensorData.get(data.getSensorSession()).add(data);
    }

    private void printHash(HashMap<SensorSession, List<SensorData>> sensor) {
        for (SensorSession sensorSession : sensor.keySet()) {
            System.out.println(sensorSession.getId());
            for (SensorData data : sensor.get(sensorSession)) {
                System.out.println("(Data) Value: " + data.getSensorData().getValues()[0] + " Time: " + data.getTimeCaptured());
            }
        }
    }
}
