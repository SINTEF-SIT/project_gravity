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

    private boolean phoneAlgorithm(List<List> accData, List<List> rotData, SensorAlgorithmPack pack, boolean hasWatch)
    {
        //TODO: Find out if the watch is connected. Done, but not sure if it works or not
        List <AccelerometerData> accelerationData = new ArrayList<>();
        List <GyroscopeData> rotationData = new ArrayList<>();

        for (List list : accData)
        {
            for (Object acc : list)
            {
                //System.out.println(acc + " was here");
                accelerationData.add((AccelerometerData) acc);
            }
        }
        for (List list : rotData)
        {
            for (Object rot : list)
            {
                rotationData.add((GyroscopeData) rot);
            }
        }
        System.out.println("Gravity was here");
        for (int i=0; i < accData.size(); i++){
            if (AlgorithmPhone.isFall(accelerationData.get(i).getX(), accelerationData.get(i).getY(), rotationData.get(i).getY(), accelerationData.get(i).getZ(), rotationData.get(i).getZ()))
            {
                if (hasWatch){ return watchAlgorithm(pack);}
                return true;
            }
        }
        return false;
    }

    private boolean watchAlgorithm(SensorAlgorithmPack pack)
    {
        List <AccelerometerData> accData = new ArrayList<>();
        List <List> accelerationData;

        RemoteSensorManager mRemoteSensorManager = RemoteSensorManager.getInstance(mContext);
        mRemoteSensorManager.getBuffer();

        accelerationData = getWatchData(pack);
        for (List l : accelerationData)
        {
            for (Object acc : l)
            {
                accData.add((AccelerometerData) acc);
            }
        }

        return AlgorithmWatch.patternRecognition(accData);
    }


    private List <List> getWatchData (SensorAlgorithmPack pack) {
        List<List> accData = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            if (entry.getKey().getSensorDevice().equals(BluetoothClass.Device.WEARABLE_WRIST_WATCH)) {
                if (entry.getKey().getSensorType() == Sensor.TYPE_ACCELEROMETER) {
                    accData.add(entry.getValue());
                }
            }
        }
        return accData;
    }

    public void onEvent(SensorAlgorithmPack pack)
    {
        boolean hasWatch = false;
        List<List> accelerometerData = new ArrayList<>();
        List<List> rotationVectorData = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            if (!hasWatch && entry.getKey().getSensorDevice().equals(BluetoothClass.Device.WEARABLE_WRIST_WATCH)) {hasWatch = true;}
            switch (entry.getKey().getSensorType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerData.add(entry.getValue());
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    rotationVectorData.add(entry.getValue());
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
