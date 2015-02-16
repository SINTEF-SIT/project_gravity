package sintef.android.gravity;

import android.hardware.SensorEvent;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

import sintef.android.controller.common.Constants;


/**
 * Created by iver on 16.02.15.
 */
public class SensorEventBuffer {

    private Buffer fifo;
    private static SensorEventBuffer instance;

    public static synchronized SensorEventBuffer getInstance() {
        if (instance == null) {
            instance = new SensorEventBuffer();
        }
        return instance;
    }

    private SensorEventBuffer() {
        fifo = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(Constants.WEAR_BUFFER_SIZE));
    }

    public void addSensorData(final String session, final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        addData(new SensorEventData(session, sensorType, accuracy, timestamp, values));
    }

    private void addData(SensorEventData event) {
        fifo.add(event);
    }

    public SensorEventData[] getBufferAsArray() {
        return (SensorEventData[]) fifo.toArray();
    }

    public Buffer getBuffer() {
        return fifo;
    }

    public class SensorEventData {
        private String session;
        private int sensorType;
        private int accuracy;
        private long timestamp;
        private float[] values;

        public SensorEventData(String session, int sensorType, int accuracy, long timestamp, float[] values) {
            this.session = session;
            this.sensorType = sensorType;
            this.accuracy = accuracy;
            this.timestamp = timestamp;
            this.values = values;
        }


        public String getSession() {
            return session;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getAccuracy() {
            return accuracy;
        }

        public int getSensorType() {
            return sensorType;
        }

        public float[] getValues() {
            return values;
        }
    }
}
