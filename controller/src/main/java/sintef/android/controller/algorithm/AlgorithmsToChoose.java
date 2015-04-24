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

public enum AlgorithmsToChoose {

    PHONE_THRESHOLD(new ThresholdPhone()),
    PHONE_PATTERN_RECOGNITION(new PatternRecognitionPhone()),

    WATCH_THRESHOLD(new ThresholdWatch()),
    WATCH_PATTERN_RECOGNITION(new PatternRecognitionWatch()),

    ALL(new PatternRecognitionPhone(), new PatternRecognitionWatch());

    public static final int ID_PHONE_THRESHOLD = 0;
    public static final int ID_PHONE_PATTERN_RECOGNITION = 1;

    public static final int ID_WATCH_THRESHOLD = 2;
    public static final int ID_WATCH_PATTERN_RECOGNITION = 3;

    static {
        PHONE_THRESHOLD.mId = ID_PHONE_THRESHOLD;
        PHONE_PATTERN_RECOGNITION.mId = ID_PHONE_PATTERN_RECOGNITION;
        WATCH_THRESHOLD.mId = ID_WATCH_THRESHOLD;
        WATCH_PATTERN_RECOGNITION.mId = ID_WATCH_PATTERN_RECOGNITION;
    }

    private int mId = -1;

    private Algorithm[] mAlgorithms;

    AlgorithmsToChoose(Algorithm... algorithms) {
        mAlgorithms = algorithms;
    }

    public Algorithm[] getClasses() {
        return mAlgorithms;
    }

    public int getId() {
        return mId;
    }

    @Override public String toString() {
        return getCorrectString();
    }

    public String getCorrectString() {
        String name = "";
        for (Algorithm algorithm : mAlgorithms) {
            name += (algorithm.getClass().getSimpleName() + ", ");
        }
        if (!name.equals("")) {
            name = name.substring(0, name.length()-2);
        }
        return name;
    }

    public static AlgorithmsToChoose getAlgorithm(int id) {
        switch (id) {
            case ID_PHONE_THRESHOLD:
                return PHONE_THRESHOLD;
            case ID_PHONE_PATTERN_RECOGNITION:
                return PHONE_PATTERN_RECOGNITION;
            case ID_WATCH_THRESHOLD:
                return WATCH_THRESHOLD;
            case ID_WATCH_PATTERN_RECOGNITION:
                return WATCH_PATTERN_RECOGNITION;
            default:
                return ALL;
        }
    }
}
