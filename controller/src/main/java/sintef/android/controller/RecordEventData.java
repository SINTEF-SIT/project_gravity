package sintef.android.controller;

/**
 * Created by samyboy89 on 29/01/15.
 */
public class RecordEventData {

    public final EventTypes type;
    public final double value;
    public final long time;

    public RecordEventData(EventTypes type, double value) {
        this.type = type;
        this.value = value;
        this.time = System.currentTimeMillis();
    }
}
