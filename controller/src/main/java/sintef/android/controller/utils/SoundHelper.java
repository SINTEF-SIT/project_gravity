package sintef.android.controller.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import sintef.android.model.R;

/**
 * Created by sammw on 05.10.13.
 */
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
            soundPoolMap.put(ALARM, soundPool.load(a, R.raw.beeper_alarm, 1));
        }
    }

    public static void playAlarmSound() {
        int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        soundPool.play(soundPoolMap.get(ALARM), streamVolume, streamVolume, 1, 0, 1f);
    }
}
