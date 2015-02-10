package sintef.android.controller.sensor;

/**
 * Created by samyboy89 on 03/02/15.
 */
public enum SensorLocation {

    LEFT_ARM("left_arm"),
    RIGHT_ARM("right_arm"),
    LEFT_PANT_POCKET("left_pant_pocket"),
    RIGHT_PANT_POCKET("right_pant_pocket"),
    LEFT_PANT_BACK_POCKET("left_pant_back_pocket"),
    RIGHT_PANT_BACK_POCKET("right_pant_back_pocket"),
    LEFT_JACKET_POCKET("left_jacket_pocket"),
    RIGHT_JACKET_POCKET("right_jacket_pocket"),
    HEAD("head"),
    NECK("neck"),
    STOMACH("stomach"),
    PURSE("purse"),
    BACK("back"),
    LEFT_FOOT("left_foot"),
    RIGHT_FOOT("right_foot"),
    LEFT_SHOULDER("left_shoulder"),
    RIGHT_SHOULDER("right_shoulder"),
    OTHER("other");

    private String data;

    private SensorLocation(String data) {
        this.data = data;
    }

    public String getValue() {
        return data;
    }
}
