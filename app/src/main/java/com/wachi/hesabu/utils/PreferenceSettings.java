package com.wachi.hesabu.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.wachi.hesabu.R;
import com.wachi.hesabu.ui.MainActivity;


public class PreferenceSettings extends PreferenceActivity {

    SharedPreferences mSharedPrefs;
    CheckBoxPreference checkthemeMode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_settings);
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        root.addView(toolbar, 0);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.action_settings);
        toolbar.setTitleTextColor(Color.WHITE);
        mSharedPrefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        /*if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            setTheme(R.style.Okenwa_Black);
        }*/
        checkthemeMode = ((CheckBoxPreference) findPreference("enablethemeMode"));
        if(checkthemeMode.isChecked()){
            getListView().setBackgroundColor(Color.rgb(0, 0, 0));
            setTheme(R.style.PreferencesTheme);
        }else{

            getListView().setBackgroundColor(Color.rgb(255, 255, 255));
            setTheme(R.style.FullScreenWindow);
        }        checkthemeMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                    if(checkthemeMode.isChecked()){

                        setTheme(R.style.PreferencesTheme);
                        getListView().setBackgroundColor(Color.rgb(0, 0, 0));
                        finish();
                        Intent intent = new Intent(PreferenceSettings.this, PreferenceSettings.class);
                        startActivity(intent);

                }else{

                        getListView().setBackgroundColor(Color.rgb(255, 255, 255));
                        setTheme(R.style.FullScreenWindow);
                        finish();
                        Intent intent = new Intent(PreferenceSettings.this, PreferenceSettings.class);
                        startActivity(intent);
                    }

                return true;
            }
        });

    }

    public void onBackPressed() {

        finish();
        Intent intent = new Intent(PreferenceSettings.this, MainActivity.class);
        startActivity(intent);

    }
}
