package sintef.android.controller.algorithm;

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

    public static void initializeAlgorithmMaster() {
        sAlgorithmMain = new AlgorithmMain();
    }

    private AlgorithmMain() {
        EventBus.getDefault().register(this);
    }

    public void onEvent(SensorAlgorithmPack pack){
        boolean isFall = false;

        int algorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);
        AlgorithmsToChoose algorithmChoice = AlgorithmsToChoose.getAlgorithm(algorithmId);

        long id = System.currentTimeMillis();
        for (AlgorithmInterface algorithm : algorithmChoice.getClasses()) {
            isFall = algorithm.isFall(id, pack);

            if (!PreferencesHelper.isRecording()) if (!isFall) break;
        }

        if (isFall && PreferencesHelper.isFallDetectionEnabled()) {
            EventBus.getDefault().post(EventTypes.FALL_DETECTED);
        }
    }

    public static AlgorithmMain getsAlgorithmMain() {
        return sAlgorithmMain;
    }

}
