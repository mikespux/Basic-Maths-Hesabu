package com.wachi.hesabu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wachi.hesabu.utils.Constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        DateFormat simpleDateFormat = new SimpleDateFormat(Constant.TIME_FORMAT, Locale.ENGLISH);


        Calendar calendar = Calendar.getInstance();
        String time = simpleDateFormat.format(calendar.getTime());
        if (Constant.getReminderTime(context).equals(time)) {
            NotificationScheduler.showReminderNotification(context);

        }


    }
}