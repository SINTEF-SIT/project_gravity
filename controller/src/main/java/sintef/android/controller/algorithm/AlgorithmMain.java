/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package sintef.android.controller.algorithm;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.PreferencesHelper;

public class AlgorithmMain {

    private static AlgorithmMain instance;

    public static void initializeAlgorithmMaster() {
        instance = new AlgorithmMain();
    }

    private AlgorithmMain() {
        EventBus.getDefault().register(this);
    }

    public void onEvent(SensorAlgorithmPack pack){
        boolean isFall = false;

        int algorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);
        AlgorithmsToChoose algorithmChoice = AlgorithmsToChoose.getAlgorithm(algorithmId);

        long id = System.currentTimeMillis();
        for (Algorithm algorithm : algorithmChoice.getClasses()) {
            isFall = algorithm.isFall(id, pack);

            if (!PreferencesHelper.isRecording()) if (!isFall) break;
        }

        if (isFall && PreferencesHelper.isFallDetectionEnabled()) {
            EventBus.getDefault().post(EventTypes.FALL_DETECTED);
        }
    }

    public static AlgorithmMain getInstance() {
        return instance;
    }

}
