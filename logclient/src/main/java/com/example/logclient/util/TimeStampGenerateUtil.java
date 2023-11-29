package com.example.logclient.util;

import java.sql.Timestamp;

public class TimeStampGenerateUtil {

    public static String get() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();
    }
}
