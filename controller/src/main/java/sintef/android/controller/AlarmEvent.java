package sintef.android.controller;

/**
 * Created by samyboy89 on 09/03/15.
 */
public class AlarmEvent {

    public final int max;
    public final int progress;
    public final AlarmEventType type;

    public AlarmEvent(int max, int progress, AlarmEventType type) {
        this.max = max;
        this.progress = progress;
        this.type = type;
    }

    public static enum AlarmEventType {
        RUNNING, STOPPED, CANCELLED
    }
}
