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

package sintef.android.gravity;

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
        /*
        The buffer needs to hold at least two seconds worth of sensor events.
        The frequency is set to around 25Hz as we were not able to set it any higher.
        Therefor the size of the buffer is set to 3 * 25
         */
        fifo = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(3 * 25));
    }

    private SensorEventBuffer(int frequency) {
        /*
        Planned for the future: Get the actual maximum frequency of sensor events and
        use this value to instantiated the buffer.
         */
        fifo = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(3 * frequency));
    }

    public void addSensorData(final String session, final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        addData(new SensorEventData(session, sensorType, accuracy, timestamp, values));
    }

    private void addData(SensorEventData event) {
        fifo.add(event);
    }

    public Object[] getBufferAsArray() {
        return fifo.toArray();
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
