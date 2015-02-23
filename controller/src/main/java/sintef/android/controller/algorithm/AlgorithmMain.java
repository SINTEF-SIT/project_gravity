package sintef.android.controller.algorithm;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.hardware.Sensor;

import com.google.android.gms.wearable.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.sensor.RemoteSensorManager;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.GyroscopeData;

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

    private boolean phoneAlgorithm(List<AccelerometerData> accData, List<GyroscopeData> rotData, SensorAlgorithmPack pack, boolean hasWatch)
    {
        //TODO: Find out if the watch is connected
        for (int i=0; i < accData.size(); i++){
            if (AlgorithmPhone.isFall(accData.get(i).getX(), accData.get(i).getY(), rotData.get(i).getY(), accData.get(i).getZ(), rotData.get(i).getZ()))
            {
                if (hasWatch){ return watchAlgorithm(pack);}
                return true;
            }
        }
        return false;
    }

    private boolean watchAlgorithm(SensorAlgorithmPack pack)
    {
        List <AccelerometerData> accData;

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
                    accData.add((AccelerometerData) entry.getValue());
                }
            }
        }
        return accData;
    }

    public void onEvent(SensorAlgorithmPack pack)
    {
        boolean hasWatch = false;
        List<AccelerometerData> accelerometerData = new ArrayList<>();
        List<GyroscopeData> rotationVectorData = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            if (hasWatch == false && entry.getKey().getSensorDevice().equals(BluetoothClass.Device.WEARABLE_WRIST_WATCH)) {hasWatch = true;}
            switch (entry.getKey().getSensorType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerData.add((AccelerometerData) entry.getValue());
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    rotationVectorData.add( (GyroscopeData) entry.getValue() );
                    break;
                //case Sensor.TYPE_GAME_ROTATION_VECTOR:
                //    break;
            }
            phoneAlgorithm(accelerometerData, rotationVectorData, pack, hasWatch);
        }
    }

    public static AlgorithmMain getsAlgorithmMain() {
        return sAlgorithmMain;
    }

}
