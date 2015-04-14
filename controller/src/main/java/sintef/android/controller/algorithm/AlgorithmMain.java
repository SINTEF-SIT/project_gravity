package sintef.android.controller.algorithm;

import android.content.Context;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.PreferencesHelper;

//import static sintef.android.controller.algorithm.AlgorithmPhone.patternRecognition;

//import org.apache.commons.collections.bag.SynchronizedSortedBag;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class AlgorithmMain {

    private static AlgorithmMain sAlgorithmMain;
    private Context mContext;

    private static final String TAG = "ALG";
    public static final boolean DEBUG = true;

    public static void initializeAlgorithmMaster(Context context){
        sAlgorithmMain = new AlgorithmMain(context);
    }

    private AlgorithmMain(Context context){
        mContext = context;
        EventBus.getDefault().registerSticky(this);
    }

    /*private boolean phoneAlgorithm(List<LinearAccelerationData> accData, List<RotationVectorData> rotData, List<MagneticFieldData> geoRotVecData, boolean hasWatch)
    {
        if ( AlgorithmPhone.isFall(accData, rotData, geoRotVecData) ){
            if (PatternRecognitionPhone.isFall(accData)){
                return true;
            }
        }*/
        /*int numberOfIterations;
        float[] degs = new float[3];
        float[] rotationMatrix = new float[9];
        double tetaY;
        double tetaZ;

        if (accData.size() <= rotData.size() && accData.size() <= geoRotVecData.size()) numberOfIterations = accData.size();
        else if (geoRotVecData.size() <= accData.size() && geoRotVecData.size() <= rotData.size()) numberOfIterations = geoRotVecData.size();
        else numberOfIterations = rotData.size();
        for (int i=0; i < numberOfIterations; i++){
            SensorManager.getRotationMatrix(rotationMatrix, null, rotData.get(i).getValues(), geoRotVecData.get(i).getValues());
            SensorManager.getOrientation(rotationMatrix, degs);
            tetaY = degs[2];
            tetaZ = degs[0];
            if (AlgorithmPhone.isFall(accData.get(i).getX(), accData.get(i).getY(), accData.get(i).getZ(), tetaY, tetaZ))
            {
                if (DEBUG) Log.wtf(TAG, "POSSIBLE FALL: possible fall, checking pattern recognition");
                if (PatternRecognitionPhone.isFall(accData))
                {
                    if (DEBUG) Log.wtf(TAG, "FALL: pattern recognition said it was a fall");
                    if (hasWatch) {
                        if (DEBUG) Log.wtf(TAG, "WATCH: enabling watch");
                        RemoteSensorManager mRemoteSensorManager = RemoteSensorManager.getInstance(mContext);
                        mRemoteSensorManager.getBuffer();
                        //might have to change this, but for now this is the idea of how it should work
                        return false;
                    }
                    return true;
                }
                if (DEBUG) Log.wtf(TAG, "NO FALL: pattern recognition said it was not a fall");
                return false;
            }
        }
        return false;
    }*/

    /*private List <AccelerometerData> getWatchData (SensorAlgorithmPack pack) {
        List<AccelerometerData> accData = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            if (entry.getKey().getSensorDevice().equals(WEARABLE_WRIST_WATCH)) {
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
    */

    public void onEvent(SensorAlgorithmPack pack){
        boolean isFall = false;

        int algorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);
        AlgorithmsToChoose algorithmChoice = AlgorithmsToChoose.getAlgorithm(algorithmId);

        for (AlgorithmInterface algorithm : algorithmChoice.getClasses()) {
            isFall = algorithm.isFall(pack);

            if (!isFall) break;
        }


        //TODO: better way to check if the watch is connected or not

        /*boolean hasWatch = false; //RemoteSensorManager.getInstance(this.mContext).validateConnection();
        List<RotationVectorData> rotationVectorData = new ArrayList<>();
        List<MagneticFieldData> magneticFieldData = new ArrayList<>();
        List<LinearAccelerationData> linearAccelerationData = new ArrayList<>();
        List<LinearAccelerationData> accDataWatch = new ArrayList<>();
        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            switch (entry.getKey().getSensorDevice()) {
                case PHONE:
                    switch (entry.getKey().getSensorType()) {
                        case Sensor.TYPE_LINEAR_ACCELERATION:
                            for (int i = 0; i < entry.getValue().size(); i++)
                            {
                                linearAccelerationData.add((LinearAccelerationData) entry.getValue().get(i).getSensorData());
                            }
                            break;
                        case Sensor.TYPE_ROTATION_VECTOR:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                rotationVectorData.add((RotationVectorData) entry.getValue().get(i).getSensorData());
                            }
                            break;
                        case Sensor.TYPE_MAGNETIC_FIELD:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                magneticFieldData.add((MagneticFieldData) entry.getValue().get(i).getSensorData());
                            }
                            break;
                    }
                    break;
                case WATCH:
                    switch (entry.getKey().getSensorType()){
                        case Sensor.TYPE_LINEAR_ACCELERATION:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                accDataWatch.add((LinearAccelerationData) entry.getValue().get(i).getSensorData());
                            }
                            break;
                    }
                    break;
                case OTHER:
                    break;
            }

        }
        boolean isFall;
        if (!accDataWatch.isEmpty() && hasWatch)
        {
            if (DEBUG) Log.w(TAG, "WATCH: checking watch algorithm");
            isFall = watchAlgorithm(accDataWatch);
        }
        else
        {
            if (DEBUG) Log.w(TAG, "PHONE: checking phone algorithm");
            isFall = phoneAlgorithm(linearAccelerationData, rotationVectorData, magneticFieldData, hasWatch);
        }

        if (DEBUG) Log.w(TAG, "?FALL?: is fall = " + String.valueOf(isFall).toUpperCase());
        */
        if (isFall) {
            if (PreferencesHelper.isFallDetectionEnabled()) {
                EventBus.getDefault().post(EventTypes.FALL_DETECTED);
            }

            EventBus.getDefault().post(EventTypes.FALL_DETECTED_FOR_RECORDING);
            EventBus.getDefault().post(EventTypes.TEST_FALL);
        } else {
            EventBus.getDefault().post(EventTypes.TEST_NO_FALL);
        }
    }

    public static AlgorithmMain getsAlgorithmMain() {
        return sAlgorithmMain;
    }

}
