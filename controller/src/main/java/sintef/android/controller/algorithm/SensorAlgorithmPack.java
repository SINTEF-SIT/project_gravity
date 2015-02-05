package sintef.android.controller.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sintef.android.controller.DeepClone;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class SensorAlgorithmPack {

    private HashMap<SensorSession, List<SensorData>> mSensorData = new HashMap<>();

    public static HashMap<SensorSession, List<SensorData>> processNewSensorData(SensorAlgorithmPack pack, long from, HashMap<SensorSession, List<SensorData>> sensorData) {
        for (Map.Entry<SensorSession, List<SensorData>> entry : sensorData.entrySet()) {
            SensorSession sensorSession = entry.getKey();
            List<SensorData> datas = new ArrayList<>();
            for (SensorData data : entry.getValue()) {
                if (data.getTimeCaptured() > from) {
                    datas.add(data);
                }
            }
            Collections.sort(datas);
            pack.mSensorData.put(sensorSession, datas);
        }

        return DeepClone.deepClone(pack.mSensorData);
    }

    public HashMap<SensorSession, List<SensorData>> getSensorData() {
        return mSensorData;
    }
}
