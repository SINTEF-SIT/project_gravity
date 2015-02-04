package sintef.android.algorithm;

import de.greenrobot.event.EventBus;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class Main {

    private EventBus mEventBus = EventBus.getDefault();

    public Main() {
        mEventBus.registerSticky(this);
    }

}
