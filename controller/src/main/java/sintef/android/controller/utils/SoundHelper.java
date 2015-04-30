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

package sintef.android.controller.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import sintef.android.model.R;

public class SoundHelper {

    public static Context activity;

    // sound
    public static final int ALARM = 1;

    private static SoundPool soundPool;
    private static SparseIntArray soundPoolMap;
    private static AudioManager mgr;

    public static void initializeSoundsHelper(Context a) {

        if (activity == null) {
            activity = a;
            mgr = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            soundPoolMap = new SparseIntArray();
            soundPoolMap.put(ALARM, soundPool.load(a, R.raw.alarm, 1));
        }
    }

    public static void playAlarmSound() {
        int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        soundPool.play(soundPoolMap.get(ALARM), streamVolume, streamVolume, 1, 0, 1f);
    }
}
