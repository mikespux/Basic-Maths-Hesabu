package com.wachi.hesabu.ui;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wachi.hesabu.R;
import com.wachi.hesabu.utils.Constant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityRemider extends AppCompatActivity {


    private FloatingActionButton btn_add;
    TextView tv_time;
    public static int DAILY_REMINDER_REQUEST_CODE = 111;
    String time = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.setDefaultLanguage(this);
        setContentView(R.layout.activity_reminder);
        init();
        setClick();
    }

    private void setClick() {
        btn_add.setOnClickListener(v -> {
            Date date = null;
            try {
                date = new SimpleDateFormat(Constant.TIME_FORMAT, Locale.ENGLISH).parse(Constant.getReminderTime(getApplicationContext()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar CurrentTime = Calendar.getInstance();
            assert date != null;
            CurrentTime.setTime(date);
            int hour = CurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = CurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(ActivityRemider.this, (view, hourOfDay, minute1) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute1);
                SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.TIME_FORMAT, Locale.US);
                time = dateFormat.format(calendar.getTime());

                Constant.setReminderTime(getApplicationContext(), time);
                tv_time.setText(Constant.getReminderTime(getApplicationContext()));

            }, hour, minute, false);
            mTimePicker.show();
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(view -> backIntent());

        TextView txt_header = findViewById(R.id.toolbar_title);
        txt_header.setText(getString(R.string.reminder));
        tv_time = findViewById(R.id.tv_time);
        btn_add = findViewById(R.id.btn_add);

        tv_time.setText(Constant.getReminderTime(getApplicationContext()));

    }


    @Override
    public void onBackPressed() {
        backIntent();
    }

    public void backIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


}
