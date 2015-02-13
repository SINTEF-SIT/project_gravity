package sintef.android.controller;

import android.content.Context;

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

    private static Controller sController;
    private static EventBus sEventBus;
    private Context mContext;

//    private static Map<SensorSession, List<SensorData>> mAllSensorData = new ConcurrentHashMap<>();
    private static List<Map<SensorSession, List<SensorData>>> allData = new ArrayList<Map<SensorSession, List<SensorData>>>();

    public static void initializeController(Context context) {
        sController = new Controller(context);
    }

    private Controller(Context context) {
        mContext = context;
        sEventBus = EventBus.getDefault();
        sEventBus.registerSticky(this);

        AlgorithmMain.initializeAlgorithmMaster(context);
        SensorManager.getInstance(context);

        /** SENDING PACKETS TO ALGORITHM */

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                SensorAlgorithmPack pack;
                allData.add(0, new HashMap<SensorSession, List<SensorData>>());
                List<Map<SensorSession, List<SensorData>>> cat = new ArrayList<Map<SensorSession, List<SensorData>>>();
                if (allData.size() > 2) {
//                    cat.addAll((java.util.Collection<? extends Map<SensorSession, List<SensorData>>>) allData.get(2));
//                    cat.addAll((java.util.Collection<? extends Map<SensorSession, List<SensorData>>>) allData.get(1));

                    pack = SensorAlgorithmPack.processNewSensorData(allData.get(2), allData.get(1));
                } else if (allData.size() > 1) {
                    pack = SensorAlgorithmPack.processNewSensorData(allData.get(1), null);
                } else {
                    return;
                }



//                mAllSensorData = DeepClone.deepClone(pack.getSensorData());
                sEventBus.post(pack);
                // printHash(mAllSensorData);
            }
        }, 0, Constants.ALGORITHM_SEND_FREQUENCY);

    }

    public static Controller getController() {
        return sController;
    }

    public void onEvent(SensorData data) {
//        if (true) return; /*** DELETE ***/

        if (allData.isEmpty()) allData.add(0, new HashMap<SensorSession, List<SensorData>>());
        Map<SensorSession, List<SensorData>> sensorData = allData.get(0);

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
