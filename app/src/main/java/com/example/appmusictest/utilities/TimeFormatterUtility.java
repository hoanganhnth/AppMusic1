package com.example.appmusictest.utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeFormatterUtility {
    public static String formatTime(int time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return simpleDateFormat.format(time);
    }
}
