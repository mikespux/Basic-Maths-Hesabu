package com.wachi.hesabu.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.adapter.HistoryAdapter;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.model.HistoryModel;
import com.wachi.hesabu.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class ReviewAnswerActivity extends BaseActivity {


    int themePosition, practice_set;
    RecyclerView recyclerView;
    String date;
    TextView tv_right_count, tv_wrong_count;
    int right_answer_count, wrong_answer_count;
    List<HistoryModel> historyNotesList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.setDefaultLanguage(this);
        setContentView(R.layout.activity_review_answer);
        init();

    }

    private void init() {
        date = getIntent().getStringExtra(Constant.DATE);
        themePosition = getIntent().getIntExtra(Constant.THEMEPOSITION, 0);
        practice_set = getIntent().getIntExtra(Constant.PRACTICE_SET, 0);
        right_answer_count = getIntent().getIntExtra(Constant.RIGHT_ANSWER, 0);
        wrong_answer_count = getIntent().getIntExtra(Constant.WRONG_ANSWER, 0);


        if (!TextUtils.isEmpty(date)) {
            themePosition = 1;
            practice_set = Constant.DEFAULT_QUESTION_SIZE;
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());
        getSupportActionBar().setTitle(null);

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.view_answer));

        TextView tv_total_set = findViewById(R.id.tv_total_set);
        tv_total_set.setText(String.valueOf(practice_set));


        tv_right_count = findViewById(R.id.tv_right_count);
        tv_right_count.setText(String.valueOf(right_answer_count));

        tv_wrong_count = findViewById(R.id.tv_wrong_count);
        tv_wrong_count.setText(String.valueOf(wrong_answer_count));

        RelativeLayout layout_cell = findViewById(R.id.layout_cell);
        View view = findViewById(R.id.view);

        toolbar.setBackgroundResource(getDrawable());
        layout_cell.setBackgroundResource(getDrawable());
        view.setBackgroundResource(getDrawable());

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        historyNotesList.add(new HistoryModel(0, getString(R.string.str_question), getString(R.string.answer), getString(R.string.your_answer)));
        if (!TextUtils.isEmpty(date)) {
            themePosition = 1;
            new SetHistoryReviewAnswer().execute();
        } else {

            new SetReviewAnswer().execute();
        }


    }

    public class SetHistoryReviewAnswer extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {


            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ReviewAnswerActivity.this);
            databaseAccess.open();
            historyNotesList = databaseAccess.getHistoty(date, historyNotesList);
            databaseAccess.close();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            HistoryAdapter historyAdapter = new HistoryAdapter(ReviewAnswerActivity.this, historyNotesList, themePosition);
            recyclerView.setAdapter(historyAdapter);

            for (int i = 0; i < historyNotesList.size(); i++) {

                if (i != 0) {
                    if (historyNotesList.get(i).answer.equals(historyNotesList.get(i).userAnswer)) {
                        right_answer_count = right_answer_count + 1;
                    } else {
                        wrong_answer_count = wrong_answer_count + 1;
                    }
                }
            }

            tv_right_count.setText(String.valueOf(right_answer_count));
            tv_wrong_count.setText(String.valueOf(wrong_answer_count));


        }
    }

    public class SetReviewAnswer extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {


            historyNotesList.addAll(Constant.getHistoryModel(getApplicationContext()));

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            HistoryAdapter historyAdapter = new HistoryAdapter(ReviewAnswerActivity.this, historyNotesList, themePosition);
            recyclerView.setAdapter(historyAdapter);
        }
    }

    public int getDrawable() {
        return Constant.getDrawbles().get(themePosition).cell;
    }

    public void backIntent() {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
