package sintef.android.controller.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class SensorAlgorithmPack {

    private HashMap<SensorSession, List<SensorData>> mSensorData = new HashMap<>();

    public static SensorAlgorithmPack processNewSensorData(Map<SensorSession, List<SensorData>> first, Map<SensorSession, List<SensorData>> last) {
        SensorAlgorithmPack pack = new SensorAlgorithmPack();
        pack.getSensorData().putAll(first);

        if (last == null) return pack;

        for (Map.Entry<SensorSession, List<SensorData>> entry : last.entrySet()) {
            if (pack.getSensorData().containsKey(entry.getKey())) {
                pack.getSensorData().get(entry.getKey()).addAll(entry.getValue());
            }
        }

        return pack;
    }

    public HashMap<SensorSession, List<SensorData>> getSensorData() {
        return mSensorData;
    }
}
