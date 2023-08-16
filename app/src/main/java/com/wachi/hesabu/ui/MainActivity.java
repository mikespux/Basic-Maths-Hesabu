package com.wachi.hesabu.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.wachi.hesabu.R;
import com.wachi.hesabu.adapter.LanguageAdapter;
import com.wachi.hesabu.adapter.MainAdapter;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.model.MainModel;
import com.wachi.hesabu.model.SetModel;
import com.wachi.hesabu.receiver.AlarmReceiver;
import com.wachi.hesabu.utils.Constant;
import com.google.android.material.navigation.NavigationView;
import com.thekhaeng.pushdownanim.PushDownAnim;
import com.wachi.hesabu.utils.PreferenceSettings;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wachi.hesabu.utils.Constant.getLanguageCode;
import static com.wachi.hesabu.utils.Constant.setDefaultLanguage;
import static com.wachi.hesabu.utils.Constant.setLanguageCode;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_PUSH_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_RELEASE_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainAdapter.MainItemClick {
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    MainAdapter mainAdapter;

    DrawerLayout drawer;
    LinearLayout btn_find_missing;
    List<MainModel> mainModels = new ArrayList<>();
    int id;
    Menu menu;
    SwitchCompat switcher;
    MenuItem nav_language;
    MenuItem nav_sound;
    MenuItem nav_coin;
    SharedPreferences mSharedPrefs;
    ImageView img_find_missing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSharedPrefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            setTheme(R.style.Okenwa_Black);
        }


        Constant.setDefaultLanguage(this);
        setContentView(R.layout.activity_main);

        setNotification();
        init();


    }


    public void setNotification() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 100, pendingIntent);
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(this, AlarmReceiver.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ActivityCompat.finishAffinity(this);
        }
    }

    private void init() {
        progressDialog = new ProgressDialog(MainActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        nav_language = menu.findItem(R.id.nav_setting);
        nav_sound = menu.findItem(R.id.nav_sound);
        nav_coin = menu.findItem(R.id.nav_coin);

        View actionView = MenuItemCompat.getActionView(nav_sound);

        switcher = actionView.findViewById(R.id.drawer_switch);


        switcher.setOnCheckedChangeListener((compoundButton, b) -> {
            Constant.setSound(getApplicationContext(), b);
            setOnOffSound();
        });
        setOnOffSound();
        nav_language.setTitle(getString(R.string.language) + getString(R.string.single_space) + ":" + getString(R.string.single_space) + getLanguageCode(getApplicationContext()));
        nav_coin.setTitle(getString(R.string.coins) + getString(R.string.single_space) + Constant.getCoins(getApplicationContext()));

        img_find_missing = findViewById(R.id.img_find_missing);

        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            img_find_missing.setColorFilter(Color.argb(255, 255, 255, 255));

        }

        btn_find_missing = findViewById(R.id.btn_find_missing);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        Constant.setDefaultLanguage(this);
        btn_find_missing.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink));

        if (Constant.getIsFirstTime(getApplicationContext())) {
            Constant.setIsFirstTime(getApplicationContext());
            new GetAllData().execute();

        } else {
            setAdapter();
        }


        btn_find_missing.setOnClickListener(v -> {
            passFindMissingIntent();
        });

    }

    public void passFindMissingIntent() {
        Intent intent = new Intent(MainActivity.this, MixedLevelActivity.class);
        intent.putExtra(Constant.TITLE, getString(R.string.title_find_missing));
        intent.putExtra(Constant.ID, Constant.TYPE_FIND_MISSING);
        intent.putExtra(Constant.THEMEPOSITION, 1);
        intent.putExtra(Constant.MAIN_ID, 5);
        startActivity(intent);
    }


    public void setAdapter() {
        mainModels.clear();
        mainModels = Constant.getMainModel(MainActivity.this);


        int actionBarHeight = 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        mainAdapter = new MainAdapter(MainActivity.this, mainModels, (width), (height - (actionBarHeight + 80)));
        recyclerView.setAdapter(mainAdapter);
        mainAdapter.setMainClickListener(MainActivity.this);
        recyclerView.scrollToPosition(getIntent().getIntExtra(Constant.POSITION, 0));


    }


    public class GetAllData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... voids) {
            List<SetModel> setModelList;
            List<SetModel> setModelList1;
            List<Integer> integers = new ArrayList<>();
            List<Integer> integers1 = new ArrayList<>();

            int practice_set = 0;
            int practice_set1 = 0;

            for (int i = 0; i < 4; i++) {

                String tableName;
                if (i == 0) {
                    tableName = getString(R.string.addition_set);
                } else if (i == 1) {
                    tableName = getString(R.string.subtraction_set);
                } else if (i == 2) {
                    tableName = getString(R.string.multiplication_set);
                } else {
                    tableName = getString(R.string.division_set);
                }
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MainActivity.this);
                databaseAccess.open();
                setModelList = databaseAccess.getSetdata(tableName, 1, false);
                databaseAccess.close();

                for (int j = 0; j < setModelList.size(); j++) {
                    integers.add(setModelList.get(j).practice_set);
                }
                DatabaseAccess db = DatabaseAccess.getInstance(MainActivity.this);
                db.open();
                setModelList1 = db.getSetdata(tableName, 2, false);
                db.close();
                for (int j = 0; j < setModelList1.size(); j++) {
                    integers1.add(setModelList1.get(j).practice_set);
                }
            }

            for (int i = 0; i < integers.size(); i++) {
                practice_set = practice_set + integers.get(i);
            }
            for (int i = 0; i < integers1.size(); i++) {

                practice_set1 = practice_set1 + integers1.get(i);
            }

            Constant.setIntegerQuiz(getApplicationContext(), practice_set);
            Constant.setDecimalQuiz(getApplicationContext(), practice_set1);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            setAdapter();


        }


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation analytical.xml item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_pdf) {
            Intent intent = new Intent(MainActivity.this, WorkSheetActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            showLanguageDialog();
        }else if (id == R.id.nav_settings) {
            finish();
            Intent intent = new Intent(MainActivity.this, PreferenceSettings.class);
            startActivity(intent);
    } else if (id == R.id.nav_daily_test) {
            Intent intent = new Intent(MainActivity.this, ActivityDailyTest.class);
            startActivity(intent);
        } else if (id == R.id.nav_reminder) {
            Intent intent = new Intent(MainActivity.this, ActivityRemider.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, AllPdfActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Constant.share(MainActivity.this);
        } else if (id == R.id.nav_find_missing) {
            passFindMissingIntent();
        } else if (id == R.id.nav_rate_us) {
            showRatingDialog();
        } else if (id == R.id.nav_feedback) {
            showFeedbackDialog(this);
        } else if (id == R.id.nav_privacy_policy) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_link)));
            startActivity(browserIntent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setOnOffSound() {
        if (Constant.getSound(getApplicationContext())) {
            nav_sound.setTitle(getString(R.string.sound) + "/" + getString(R.string.on));
            switcher.setChecked(true);
        } else {
            nav_sound.setTitle(getString(R.string.sound) + "/" + getString(R.string.off));
            switcher.setChecked(false);
        }

    }

    @Override
    public void mainItemClick(int main_id, int position, String title, int themePosition) {
        boolean isFraction = false;
        Intent intent;
        String tableName;

        tableName = Constant.getTableName(getApplicationContext(), position);

        if (main_id == 3) {
            isFraction = true;
            intent = new Intent(MainActivity.this, LevelActivity.class);
            intent.putExtra(Constant.ID, (position + 1));
            tableName = getString(R.string.fraction_data_table);
        } else if (main_id == 4) {
            intent = new Intent(MainActivity.this, MixedLevelActivity.class);
            intent.putExtra(Constant.TITLE, title);
            intent.putExtra(Constant.ID, (position + 1));
        } else {
            intent = new Intent(MainActivity.this, SetActivity.class);
        }

        if (!TextUtils.isEmpty(tableName)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constant.MAIN_ID, main_id);
            intent.putExtra(Constant.POSITION, position);
            intent.putExtra(Constant.TABLE_NAME, tableName);
            intent.putExtra(Constant.THEMEPOSITION, themePosition);
            intent.putExtra(Constant.MAIN_THEME, themePosition);
            intent.putExtra(Constant.IsFraction, isFraction);
            startActivity(intent);

        }

    }

    LanguageAdapter languageAdapter;

    public void showLanguageDialog() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_language, null);
        builder.setView(view);

        TextView btn_ok, btn_cancel;

        btn_ok = view.findViewById(R.id.btn_ok);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        final AlertDialog dialog = builder.create();
        dialog.show();
        languageAdapter = new LanguageAdapter(getApplicationContext(), Constant.getLanguageList(getApplicationContext()), s -> {
            setDefaultLanguage(MainActivity.this);
            setLanguageCode(getApplicationContext(), Constant.getLanguageCodeFromLanguage(getApplicationContext(), s));
            languageAdapter.notifyDataSetChanged();
            nav_language.setTitle(getString(R.string.language) + getString(R.string.single_space) + ":" + getString(R.string.single_space) + getLanguageCode(getApplicationContext()));

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
            dialog.dismiss();

        });
        recyclerView.setAdapter(languageAdapter);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        btn_cancel.setOnClickListener(view1 -> dialog.dismiss());
        btn_ok.setOnClickListener(view12 -> {
            dialog.dismiss();
        });

    }

    public static void showFeedbackDialog(Activity activity) {
        final androidx.appcompat.app.AlertDialog alertDialog;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_feedback, null);
        builder.setView(view);
        final EditText edt_feedback = view.findViewById(R.id.edt_feedback);
        TextView btn_submit = view.findViewById(R.id.btn_submit);
        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        PushDownAnim.setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alertDialog.show();
        btn_cancel.setOnClickListener(v -> alertDialog.dismiss());
        btn_submit.setOnClickListener(v -> {

            if (!TextUtils.isEmpty(edt_feedback.getText().toString())) {
                alertDialog.dismiss();
                sendFeedbackFromUser(activity, edt_feedback.getText().toString());
            } else {
                Toast.makeText(activity, "" + activity.getString(R.string.empty_feedback), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void showRatingDialog() {
        final AlertDialog alert_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null);
        builder.setView(view);
        final RatingBar rating_bar = view.findViewById(R.id.rating_bar);
        TextView btn_submit = view.findViewById(R.id.btn_submit);
        TextView tv_no = view.findViewById(R.id.tv_no);
        PushDownAnim.setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);

        alert_dialog = builder.create();
        alert_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alert_dialog.show();

        btn_submit.setOnClickListener(v -> {
            if (rating_bar.getRating() >= 1) {
                try {
                    rateApp();
                 //   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")));

                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")));
                }
                alert_dialog.dismiss();
            } else if (rating_bar.getRating() <= 0) {
                Toast.makeText(MainActivity.this, "" + getString(R.string.rating_error), Toast.LENGTH_SHORT).show();
            }

        });
        tv_no.setOnClickListener(v -> alert_dialog.dismiss());
    }

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }
    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getApplicationContext().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
    private static void sendFeedbackFromUser(Activity activity, String txt) {
        String mailto = "mailto:" + activity.getString(R.string.feedback_mail) +
                "?cc=" + "" +
                "&subject=" + Uri.encode(activity.getString(R.string.app_name)) +
                "&body=" + Uri.encode(txt);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            activity.startActivity(emailIntent);
        } catch (ActivityNotFoundException ignored) {
        }

    }


}
