package com.wachi.hesabu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.wachi.hesabu.R;
import com.wachi.hesabu.adapter.LevelAdapter;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.model.ProgressModel;
import com.wachi.hesabu.utils.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MixedLevelActivity extends BaseActivity implements LevelAdapter.ItemClick {

    RecyclerView recyclerView;

    String title;
    TextView text_header;
    int id, main_id;
    ProgressDialog progressDialog;
    DatabaseAccess databaseAccess;
    RelativeLayout layout_cell;
    TextView tv_total_set, tv_total_question;
    View view;
    int themePosition;
    List<ProgressModel> progressModels = new ArrayList<>();
    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        init();
        adView = findViewById(R.id.adView);

        if (getResources().getString(R.string.ADS_VISIBILITY).equals("YES")) {
            adview();
        }else{
        adView.setVisibility(View.GONE);
    }
    }
    private void adview() {

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                        .build());

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        adView = findViewById(R.id.adView);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }
    private void init() {

        themePosition = getIntent().getIntExtra(Constant.THEMEPOSITION, 0);
        progressDialog = new ProgressDialog(this);
        title = getIntent().getStringExtra(Constant.TITLE);
        main_id = getIntent().getIntExtra(Constant.MAIN_ID, 0);
        id = getIntent().getIntExtra(Constant.ID, 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());
        getSupportActionBar().setTitle(null);

        text_header = findViewById(R.id.text_header);
        layout_cell = findViewById(R.id.layout_cell);
        tv_total_set = findViewById(R.id.tv_total_set);
        tv_total_question = findViewById(R.id.tv_total_question);
        view = findViewById(R.id.view);
        TextView mTitle = findViewById(R.id.toolbar_title);

        text_header.setVisibility(View.GONE);

        toolbar.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);
        layout_cell.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);
        view.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);

        if (title.equals(getString(R.string.title_find_missing))) {
            mTitle.setText(getString(R.string.find_missing));
        } else {
            mTitle.setText(title);
        }


        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);


        getProgressData();
        if (progressModels.size() <= 0) {
            new addProgressModel().execute();
        } else {
            setAdapter();

        }


    }

    public class addProgressModel extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {


            for (int i = 0; i < Constant.LEVEL_SIZE; i++) {

                databaseAccess = DatabaseAccess.getInstance(MixedLevelActivity.this);
                databaseAccess.open();
                databaseAccess.insertProgressData(new ProgressModel(title, id, main_id, (i + 1), 0));
                databaseAccess.close();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();


            getProgressData();
            setAdapter();
        }
    }


    public void getProgressData() {
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        progressModels = databaseAccess.getProgressdata(title, id, main_id);
        databaseAccess.close();
    }

    public void setAdapter() {
        LevelAdapter levelAdapter = new LevelAdapter(getApplicationContext(), Constant.LEVEL_SIZE, progressModels, themePosition);
        recyclerView.setAdapter(levelAdapter);
        levelAdapter.setClickListener(this);
        recyclerView.scrollToPosition((getIntent().getIntExtra(Constant.LEVEL, 0) - 1));

        tv_total_set.setText(String.valueOf(Constant.LEVEL_SIZE));
        tv_total_question.setText(String.valueOf((Constant.LEVEL_SIZE * Constant.QUIZ_SIZE)));
    }


    @Override
    public void itemClick(int pos) {
        Intent intent = new Intent(this, MixedQuizActivity.class);
        intent.putExtra(Constant.TITLE, title);
        intent.putExtra(Constant.LEVEL, (pos + 1));
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.MAIN_ID, main_id);
        intent.putExtra(Constant.THEMEPOSITION, themePosition);
        startActivity(intent);
    }

    public void backIntent() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (id != Constant.TYPE_FIND_MISSING) {
            intent.putExtra(Constant.POSITION, 3);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backIntent();
    }

}
