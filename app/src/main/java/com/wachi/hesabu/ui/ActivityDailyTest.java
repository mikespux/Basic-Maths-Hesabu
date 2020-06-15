package com.wachi.hesabu.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wachi.hesabu.R;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.utils.Constant;

import java.util.Calendar;


public class ActivityDailyTest extends AppCompatActivity {

    CalendarView calendarView;
    Button btn_daily_test, btn_review_answer;
    String str_date;
    SharedPreferences mSharedPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.setDefaultLanguage(this);
        mSharedPrefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            setTheme(R.style.Okenwa_Black);
        }
        setContentView(R.layout.activity_daily_test);
        init();
    }

    public void backIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        backIntent();
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
        txt_header.setText(getString(R.string.daily_test));

        calendarView = findViewById(R.id.calendarView);
        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            calendarView.setDateTextAppearance(R.style.CalenderViewDark);
            calendarView.setWeekDayTextAppearance(R.style.CalenderViewDateCustomTextDark);
            calendarView.setWeekDayTextAppearance(R.style.CalenderViewWeekCustomDark);


        }


        btn_review_answer = findViewById(R.id.btn_review_answer);
        btn_daily_test = findViewById(R.id.btn_daily_test);
        setClick();

        str_date = Constant.getCurrentDate(this);
        setButtonVisibility();
    }

    public void setButtonVisibility() {
        if (getHistorySize(str_date) > 0) {
            btn_review_answer.setVisibility(View.VISIBLE);
        } else {
            btn_review_answer.setVisibility(View.GONE);
        }
    }

    private void setClick() {
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            str_date = Constant.getDateFormat(ActivityDailyTest.this, calendar.getTime());
            setButtonVisibility();
        });

        btn_daily_test.setOnClickListener(view -> {

            if (getHistorySize(Constant.getCurrentDate(ActivityDailyTest.this)) <= 0) {
                Intent intent = new Intent(ActivityDailyTest.this, DailyQuizActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(this, ""+getString(R.string.test_already_done), Toast.LENGTH_SHORT).show();
            }
        });


        btn_review_answer.setOnClickListener(view -> {

            Intent intent = new Intent(ActivityDailyTest.this, ReviewAnswerActivity.class);
            intent.putExtra(Constant.DATE, str_date);
            startActivity(intent);
        });
    }

    public int getHistorySize(String str_date) {
        int size;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        size = databaseAccess.getHistotySize(str_date).size();
        databaseAccess.close();
        return size;
    }
}
