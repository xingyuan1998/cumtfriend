package com.flyingstudio.cumtfriend.utils;

import android.provider.ContactsContract;

import java.util.Date;

public class TimeUtil {
    public static long getTimeDay(Date startDate, Date endDate) {
        long nd = 1000 * 24 * 60 * 60;
        return (endDate.getTime() - startDate.getTime()) / nd + 1;
    }
}
