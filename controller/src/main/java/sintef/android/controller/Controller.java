package sintef.android.controller;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import sintef.android.controller.algorithm.AlgorithmMain;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorManager;
import sintef.android.controller.sensor.SensorSession;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class Controller {

    private static Controller sController;
    private static EventBus sEventBus;
    private Context mContext;

    private static HashMap<SensorSession, List<SensorData>> mAllSensorData = new HashMap<>();

    public static void initializeController(Context context) {
        sController = new Controller(context);
    }

    private Controller(Context context) {
        mContext = context;
        sEventBus = EventBus.getDefault();
        sEventBus.registerSticky(this);

        AlgorithmMain.initializeAlgorithmMaster(context);
        SensorManager.getInstance(context);

        /** SENDING PACKETS TO ALGORITHM
         *
         * new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SensorAlgorithmPack pack = new SensorAlgorithmPack();
                mAllSensorData = SensorAlgorithmPack.processNewSensorData(pack, System.currentTimeMillis() - 1000, mAllSensorData);
                sEventBus.post(pack);
                // printHash(mAllSensorData);
            }
        }, 500, 1000);*/

    }

    public static Controller getController() {
        return sController;
    }

    public void onEvent(SensorData data) {
        if (true) return; /*** DELETE ***/

        if (data.getSensorSession() == null) return;
        if (!mAllSensorData.containsKey(data.getSensorSession())) {
            mAllSensorData.put(data.getSensorSession(), new ArrayList<SensorData>());
        }

        mAllSensorData.get(data.getSensorSession()).add(data);
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
