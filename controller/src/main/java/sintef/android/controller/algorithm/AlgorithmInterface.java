package sintef.android.controller.algorithm;

import java.util.List;

import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.sensor.data.MagneticFieldData;
import sintef.android.controller.sensor.data.RotationVectorData;

/**
 * Created by Andreas on 08.04.2015.
 */
public interface AlgorithmInterface {
    //public boolean isFall(List<LinearAccelerationData> accData, List<RotationVectorData> rotData, List<MagneticFieldData> geoRotVecData);
    public boolean isFall(SensorAlgorithmPack pack);
}
