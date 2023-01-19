package com.example.test000111;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public static  final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("yyyy/MM/dd",cal).toString();
        return date;

    }
}
