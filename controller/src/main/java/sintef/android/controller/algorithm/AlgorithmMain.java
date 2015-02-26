package sintef.android.controller.algorithm;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.hardware.Sensor;

import com.google.android.gms.wearable.MessageEvent;

//import org.apache.commons.collections.bag.SynchronizedSortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.greenrobot.event.EventBus;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.sensor.RemoteSensorManager;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.GyroscopeData;
import sintef.android.controller.sensor.data.RotationVectorData;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class AlgorithmMain {

    private static AlgorithmMain sAlgorithmMain;
    private Context mContext;

    public static void initializeAlgorithmMaster(Context context)
    {
        sAlgorithmMain = new AlgorithmMain(context);
    }

    private AlgorithmMain(Context context)
    {
        mContext = context;
        EventBus.getDefault().registerSticky(this);
    }

    private boolean phoneAlgorithm(List<AccelerometerData> accData, List<RotationVectorData> rotData, SensorAlgorithmPack pack, boolean hasWatch)
    {
        //TODO: Find out if the watch is connected. Done, but not sure if it works or not
        int numberOfIterations;
        if (accData.size() <= rotData.size()) numberOfIterations = accData.size();
        else numberOfIterations = rotData.size();
        for (int i=0; i < numberOfIterations; i++){
            if (AlgorithmPhone.isFall(accData.get(i).getX(), accData.get(i).getY(), rotData.get(i).getEstimatedHeadingAccuracy(), accData.get(i).getZ(), rotData.get(i).getEstimatedHeadingAccuracy()))
            {
                if (hasWatch) return watchAlgorithm(pack);
                return true;
            }
        }
        return false;
    }

    private boolean watchAlgorithm(SensorAlgorithmPack pack)
    {
        List <AccelerometerData> accData = new ArrayList<>();

        RemoteSensorManager mRemoteSensorManager = RemoteSensorManager.getInstance(mContext);
        mRemoteSensorManager.getBuffer();

        accData = getWatchData(pack);

        return AlgorithmWatch.patternRecognition(accData);
    }


    private List <AccelerometerData> getWatchData (SensorAlgorithmPack pack) {
        List<AccelerometerData> accData = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            if (entry.getKey().getSensorDevice().equals(BluetoothClass.Device.WEARABLE_WRIST_WATCH)) {
                if (entry.getKey().getSensorType() == Sensor.TYPE_ACCELEROMETER) {
                    for (int i = 0; i < entry.getValue().size(); i++)
                    {
                        accData.add((AccelerometerData) entry.getValue().get(i).getSensorData());
                    }
                }
            }
        }
        return accData;
    }

    public void onEvent(SensorAlgorithmPack pack)
    {
        boolean hasWatch = false;
        List<AccelerometerData> accelerometerData = new ArrayList<>();
        List<RotationVectorData> rotationVectorData = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            if (!hasWatch && entry.getKey().getSensorDevice().equals(BluetoothClass.Device.WEARABLE_WRIST_WATCH)) {hasWatch = true;}
            switch (entry.getKey().getSensorType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < entry.getValue().size(); i++)
                    {
                        accelerometerData.add((AccelerometerData) entry.getValue().get(i).getSensorData());
                    }
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    for (int i = 0; i < entry.getValue().size(); i++)
                    {
                        rotationVectorData.add((RotationVectorData) entry.getValue().get(i).getSensorData());
                    }
                    break;
            }
            System.out.println(phoneAlgorithm(accelerometerData, rotationVectorData, pack, hasWatch) + " was here");
        }
    }

    public static AlgorithmMain getsAlgorithmMain() {
        return sAlgorithmMain;
    }

}
