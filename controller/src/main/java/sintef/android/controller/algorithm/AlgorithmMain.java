package sintef.android.controller.algorithm;

import android.content.Context;
import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
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

    //TODO: find a better name
    private boolean theAllmightyAlgorithm(List<AccelerometerData> accData, List<GyroscopeData> rotData)
    {
        boolean hasWatch = false;
        for (int i=0; i < accData.size(); i++){
            if (AlgorithmPhone.calculateAccelerations(accData.get(i).getX(), accData.get(i).getY(), rotData.get(i).getY(), accData.get(i).getZ(), rotData.get(i).getZ()))
            {
                long time = 0;
                if (hasWatch){ return theAllmightyAlgorithmsLittleBrother(time);}
                return true;
            }
        }
        return false;
    }

    //TODO: find a better name
    private boolean theAllmightyAlgorithmsLittleBrother (long time)
    {
        List <AccelerometerData> accData = new ArrayList<>();
        //TODO: Ask for data from watch here

        return AlgorithmWatch.patternRecognition(accData);
    }


    public void onEvent(SensorAlgorithmPack pack)
    {
        List<AccelerometerData> accelerometerData = new ArrayList<>();
        List<GyroscopeData> rotationVectorData = new ArrayList<>();
        List <Long> timeStamp = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
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
            theAllmightyAlgorithm(accelerometerData, rotationVectorData);
        }
    }

    public static AlgorithmMain getsAlgorithmMain() {
        return sAlgorithmMain;
    }

}
