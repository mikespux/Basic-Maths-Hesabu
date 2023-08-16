package com.wachi.hesabu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.wachi.hesabu.R;
import com.wachi.hesabu.adapter.LevelAdapter;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.model.ProgressModel;
import com.wachi.hesabu.utils.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelActivity extends BaseActivity implements LevelAdapter.ItemClick {

    RecyclerView recyclerView;

    String title;
    TextView text_header;
    int practice_set, id, main_id;
    boolean isFraction = false;
    String tableName;
    boolean isRemider;
    ProgressDialog progressDialog;
    RelativeLayout layout_cell;
    TextView tv_total_set, tv_total_question;
    View view;
    int themePosition, main_theme;
    DatabaseAccess databaseAccess;

    List<ProgressModel> progressModels = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.setDefaultLanguage(this);
        setContentView(R.layout.activity_level);
        init();


    }


    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());
        getSupportActionBar().setTitle(null);
        themePosition = getIntent().getIntExtra(Constant.THEMEPOSITION, 0);
        progressDialog = new ProgressDialog(this);
        main_theme = getIntent().getIntExtra(Constant.MAIN_THEME, 0);
        title = getIntent().getStringExtra(Constant.TITLE);
        main_id = getIntent().getIntExtra(Constant.MAIN_ID, 0);
        practice_set = getIntent().getIntExtra(Constant.PRACTICE_SET, 0);
        id = getIntent().getIntExtra(Constant.ID, 0);
        tableName = getIntent().getStringExtra(Constant.TABLE_NAME);
        isFraction = getIntent().getBooleanExtra(Constant.IsFraction, false);
        isRemider = getIntent().getBooleanExtra(Constant.IsReminder, false);


        text_header = findViewById(R.id.text_header);
        layout_cell = findViewById(R.id.layout_cell);
        tv_total_set = findViewById(R.id.tv_total_set);
        tv_total_question = findViewById(R.id.tv_total_question);
        view = findViewById(R.id.view);

        text_header.setText(title);

        toolbar.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);
        layout_cell.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);
        view.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);

        TextView mTitle = findViewById(R.id.toolbar_title);
        if (!TextUtils.isEmpty(tableName)) {
            mTitle.setText(Constant.getToolbarTitle(this, tableName));
        }

        if (isFraction) {
            text_header.setVisibility(View.GONE);
            practice_set = Constant.QUIZ_SIZE;
            title = getString(R.string.fraction);
            tableName = getString(R.string.fraction_data_table);
            mTitle.setText(Constant.getToolbarTitleFromId(this, id));
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


    public void getProgressData() {
        databaseAccess = DatabaseAccess.getInstance(LevelActivity.this);
        databaseAccess.open();
        progressModels = databaseAccess.getProgressdata(tableName, id, main_id);
        databaseAccess.close();
    }

    public void setAdapter() {


        LevelAdapter levelAdapter = new LevelAdapter(getApplicationContext(), practice_set, progressModels, themePosition);
        recyclerView.setAdapter(levelAdapter);
        levelAdapter.setClickListener(this);
        recyclerView.scrollToPosition((getIntent().getIntExtra(Constant.LEVEL, 0) - 1));

        tv_total_set.setText(String.valueOf(practice_set));
        tv_total_question.setText(String.valueOf((practice_set * Constant.QUIZ_SIZE)));


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
            for (int i = 0; i < practice_set; i++) {
                databaseAccess = DatabaseAccess.getInstance(LevelActivity.this);
                databaseAccess.open();
                databaseAccess.insertProgressData(new ProgressModel(tableName, id, main_id, (i + 1), 0));
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


    @Override
    public void itemClick(int position) {
        Intent intent;
        if (isFraction) {
            intent = new Intent(this, FractionActivity.class);
        } else {
            intent = new Intent(this, QuizActivity.class);
        }
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.IsFraction, isFraction);
        intent.putExtra(Constant.TITLE, title);
        intent.putExtra(Constant.LEVEL, (position + 1));
        intent.putExtra(Constant.TABLE_NAME, tableName);
        intent.putExtra(Constant.THEMEPOSITION, themePosition);
        intent.putExtra(Constant.IsReminder, isRemider);
        intent.putExtra(Constant.MAIN_ID, main_id);
        intent.putExtra(Constant.PRACTICE_SET, practice_set);
        intent.putExtra(Constant.MAIN_THEME, main_theme);
        startActivity(intent);
    }

    public void backIntent() {
        Intent intent;
        if (!isFraction) {
            intent = new Intent(this, SetActivity.class);
            intent.putExtra(Constant.TITLE, title);
            intent.putExtra(Constant.PRACTICE_SET, practice_set);
            intent.putExtra(Constant.ID, id);
            intent.putExtra(Constant.MAIN_ID, main_id);
            intent.putExtra(Constant.MAIN_THEME, main_theme);
            intent.putExtra(Constant.TABLE_NAME, tableName);
            startActivity(intent);
        } else {
            intent = new Intent(this, MainActivity.class);
            if (isFraction) {
                intent.putExtra(Constant.POSITION, 1);
            }
            startActivity(intent);
        }
    }


    @Override
    public void onBackPressed() {
        backIntent();
    }
}
