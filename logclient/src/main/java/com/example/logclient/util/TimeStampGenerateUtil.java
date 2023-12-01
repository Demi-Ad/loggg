package com.example.logclient.util;

import java.sql.Timestamp;

public class TimeStampGenerateUtil {

    public static long get() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();
    }
}
