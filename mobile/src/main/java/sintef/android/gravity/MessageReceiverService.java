package sintef.android.gravity;

import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.ClientPaths;

/**
 * Created by iver on 13.03.15.
 */
public class MessageReceiverService extends WearableListenerService {

    private EventBus mEventBus;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("MRS", "started MRS");
        mEventBus = EventBus.getDefault();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.w("MRS", "Received message: " + messageEvent.getPath());

        switch(messageEvent.getPath()) {
            case ClientPaths.STOP_ALARM:
                mEventBus.post(EventTypes.STOP_ALARM);
                break;
            default:
                break;
        }
    }

}
