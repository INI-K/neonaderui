package com.inik.neonadeuri.utils;

public class HardwareInformation {
    public static int displayWidth;
    public static int displayHeight;
    public static float dpToPxDensity = 0;

    public static int dpToPx(int dp) {
        return Math.round((float) dp * dpToPxDensity);
    }
}
