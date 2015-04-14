package sintef.android.controller;

/**
 * Created by samyboy89 on 29/01/15.
 */
public class RecordAlgorithmData implements Comparable<RecordAlgorithmData> {

    public final long id;
    public final String name;
    public final boolean isFall;
    public final long time;

    public RecordAlgorithmData(long id, String name, boolean isFall) {
        this.id = id;
        this.name = name;
        this.isFall = isFall;
        this.time = System.currentTimeMillis();
    }

    @Override
    public int compareTo(RecordAlgorithmData recordAlgorithmData) {
        return (int) (id - recordAlgorithmData.id);
    }
}
