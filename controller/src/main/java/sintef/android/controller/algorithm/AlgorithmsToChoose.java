package sintef.android.controller.algorithm;

/**
 * Created by Andreas on 08.04.2015.
 */
public enum AlgorithmsToChoose {

    PhoneThreshold(new ThresholdPhone()),
    PhonePatternRecognition(new PatternRecognitionPhone()),
    WatchThreshold(new ThresholdWatch()),
    WatchPatternRecognition(new PatternRecognitionWatch()),
    All(new PatternRecognitionPhone(), new PatternRecognitionWatch());

    private AlgorithmInterface[] mAlgorithms;

    AlgorithmsToChoose(AlgorithmInterface... algorithms) {
        mAlgorithms = algorithms;
    }

    public AlgorithmInterface[] getClasses() {
        return mAlgorithms;
    }
}
