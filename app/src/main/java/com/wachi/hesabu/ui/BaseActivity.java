package com.wachi.hesabu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wachi.hesabu.R;
import com.wachi.hesabu.utils.Constant;

public class BaseActivity extends AppCompatActivity {

    SharedPreferences mSharedPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSharedPrefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            setTheme(R.style.Okenwa_Black);
        }
        Constant.setDefaultLanguage(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feedback:
                sendFeedback(BaseActivity.this);
                return true;
            case R.id.menu_share:
                share(BaseActivity.this);
                return true;

            case R.id.menu_rate:
                rate(BaseActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendFeedback(Activity activity) {

        String E_MAIL = activity.getResources().getString(R.string.feedback_mail);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        PackageManager manager = activity.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = null;
        if (info != null) {
            version = info.versionName;
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("sage/rfc822" + E_MAIL);
        i.putExtra(Intent.EXTRA_SUBJECT, activity.getPackageName() + version);
        i.putExtra(Intent.EXTRA_TEXT,
                "\n" + "Have a problem?" +
                        "\n");
        activity.startActivity(Intent.createChooser(i, getString(R.string.send_email)));

    }


    public void share(Activity activity) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Xyz");
        share.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.SHARE_APP_LINK)
                + activity.getPackageName());
        activity.startActivity(Intent.createChooser(share, getString(R.string.share_link)));

    }

    public void rate(Activity activity) {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_api) + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.SHARE_APP_LINK) + appPackageName)));
        }
    }
}