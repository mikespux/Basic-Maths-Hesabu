package com.wachi.hesabu.ui;

import static com.wachi.hesabu.utils.Constant.getTextString;
import static com.wachi.hesabu.utils.Constant.setDefaultLanguage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.wachi.hesabu.R;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.model.ProgressModel;
import com.wachi.hesabu.utils.ConnectionDetector;
import com.wachi.hesabu.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends BaseActivity {

    TextView tv_right_answer, tv_wrong_answer;
    int right_answer_count, wrong_answer_count;
    TextView btn_home, btn_next;
    ImageView img_retry, img_next, img_home, img_share;
    Button btn_view_answer;
    ProgressBar progress_bar;
    DatabaseAccess databaseAccess;
    String tableName;
    boolean isFraction, isRemider;
    int main_theme, id, practice_set, level, main_id, score, best_score;
    String title;
    ConnectionDetector cd;
    List<ProgressModel> progressModels;
    Intent intent;
    TextView btn_share, btn_retry, tv_coin, tv_set_count, tv_total_count, tv_score, tv_best_score;
    LinearLayout progressView;
    Toolbar toolbar;
    int textColor, themePosition;
    int percentageProgress;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        cd = new ConnectionDetector(this);

    }



    @Override
    protected void onPause() {
        super.onPause();
    }



    private void init() {
        progressModels = new ArrayList<>();
        themePosition = getIntent().getIntExtra(Constant.THEMEPOSITION, 0);
        main_theme = getIntent().getIntExtra(Constant.MAIN_THEME, 0);
        isFraction = getIntent().getBooleanExtra(Constant.IsFraction, false);
        isRemider = getIntent().getBooleanExtra(Constant.IsReminder, false);
        right_answer_count = getIntent().getIntExtra(Constant.RIGHT_ANSWER, 0);
        wrong_answer_count = getIntent().getIntExtra(Constant.WRONG_ANSWER, 0);
        score = getIntent().getIntExtra(Constant.SCORE, 0);
        id = getIntent().getIntExtra(Constant.ID, 0);
        level = getIntent().getIntExtra(Constant.LEVEL, 0);
        practice_set = getIntent().getIntExtra(Constant.PRACTICE_SET, 0);
        main_id = getIntent().getIntExtra(Constant.MAIN_ID, 0);
        title = getIntent().getStringExtra(Constant.TITLE);
        tableName = getIntent().getStringExtra(Constant.TABLE_NAME);
        textColor = ContextCompat.getColor(getApplicationContext(), Constant.getDrawbles().get(themePosition).DarkColor);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());
        getSupportActionBar().setTitle(null);

        img_home = findViewById(R.id.img_home);
        img_next = findViewById(R.id.img_next);
        img_retry = findViewById(R.id.img_retry);
        img_share = findViewById(R.id.img_share);
        btn_share = findViewById(R.id.btn_share);
        btn_retry = findViewById(R.id.btn_retry);
        tv_total_count = findViewById(R.id.tv_total_count);
        tv_set_count = findViewById(R.id.tv_set_count);
        tv_coin = findViewById(R.id.tv_coin);
        tv_score = findViewById(R.id.tv_score);
        tv_best_score = findViewById(R.id.tv_best_score);
        progress_bar = findViewById(R.id.progress_bar);
        progressView = findViewById(R.id.progressView);
        tv_wrong_answer = findViewById(R.id.tv_wrong_answer);
        tv_right_answer = findViewById(R.id.tv_right_answer);
        btn_home = findViewById(R.id.btn_home);
        btn_next = findViewById(R.id.btn_next);
        TextView mTitle = findViewById(R.id.toolbar_title);
        btn_view_answer = findViewById(R.id.btn_view_answer);


        tv_score.setText(getTextString(getString(R.string.score) + getString(R.string.str_space) + score));
        tv_coin.setText(String.valueOf(Constant.getCoins(getApplicationContext())));
        tv_set_count.setText(String.valueOf(level));
        tv_total_count.setText(getTextString(getString(R.string.slash) + practice_set));


        String title;
        if (tableName.equals(getString(R.string.addition_set))) {
            title = getString(R.string.addition);
        } else if (tableName.equals(getString(R.string.multiplication_set))) {
            title = getString(R.string.multiplication);
        } else if (tableName.equals(getString(R.string.division_set))) {
            title = getString(R.string.division);
        } else {
            title = getString(R.string.subtraction);
        }


        mTitle.setText(title);


        tv_right_answer.setText(String.valueOf(right_answer_count));
        tv_wrong_answer.setText(String.valueOf(wrong_answer_count));


        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        progressModels = databaseAccess.getScore(tableName, id, main_id, level);
        databaseAccess.close();
        if (progressModels.size() > 0) {
            best_score = progressModels.get(0).score;
            tv_best_score.setText(getTextString(getString(R.string.best_score) + getString(R.string.str_space) + best_score));
        }

        if (best_score > score) {
            score = best_score;
        }

        percentageProgress = (right_answer_count * 100) / Constant.DEFAULT_QUESTION_SIZE;

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.updateProgress(id, level, main_id, tableName, percentageProgress);
        databaseAccess.close();

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.updateScore(id, level, main_id, tableName, score);
        databaseAccess.close();


        if (practice_set <= level || percentageProgress < 75) {
            btn_next.setAlpha(0.5f);
            img_next.setAlpha(0.5f);
        }

        setClick();
        setThemes(percentageProgress);


    }


    public void setThemes(int percentageProgress) {

        toolbar.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);
        btn_view_answer.setBackgroundResource(Constant.getDrawbles().get(themePosition).drawable);

        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);

            if (percentageProgress == 0) {
                imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_star_border));
            } else {
                if (Constant.getStarCount(percentageProgress) >= (i + 1)) {
                    imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_star));
                } else {
                    imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_star_border));
                }
            }
            progressView.addView(imageView);
        }


        LayerDrawable layerDrawable = (LayerDrawable) getResources()
                .getDrawable(R.drawable.circular_progress_drawable);
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                .findDrawableByLayerId(R.id.progress);
        gradientDrawable.setColor(textColor);
        progress_bar.setProgressDrawable(layerDrawable);
        progress_bar.setMax(practice_set);
        progress_bar.setProgress(level);


        btn_share.setTextColor(textColor);
        btn_retry.setTextColor(textColor);
        btn_next.setTextColor(textColor);
        btn_home.setTextColor(textColor);

        img_share.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        img_retry.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        img_home.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        img_next.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);


    }

    public Drawable getThemeDrawable(int drawableID) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), drawableID);
        assert drawable != null;
        drawable.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        return drawable;
    }


    public void setClick() {
        btn_next.setOnClickListener(v -> {
            if (Constant.getStarCount(percentageProgress) >= 2) {
                if (practice_set >= level) {
                    if (isFraction) {
                        intent = new Intent(ScoreActivity.this, FractionActivity.class);
                    } else {
                        intent = new Intent(ScoreActivity.this, QuizActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    passData();
                    intent.putExtra(Constant.LEVEL, (level + 1));
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "" + getString(R.string.clear_previous_level), Toast.LENGTH_SHORT).show();
            }

        });

        btn_home.setOnClickListener(v -> {
            if (isFraction) {
                intent = new Intent(ScoreActivity.this, LevelActivity.class);
            } else {
                intent = new Intent(ScoreActivity.this, SetActivity.class);

            }
            passData();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btn_view_answer.setOnClickListener(v -> showDialogs());


        btn_share.setOnClickListener(v -> Constant.share(ScoreActivity.this));

        btn_retry.setOnClickListener(v -> {





        });
    }


    public void passData() {
        intent.putExtra(Constant.TITLE, title);
        intent.putExtra(Constant.LEVEL, level);
        intent.putExtra(Constant.IsFraction, isFraction);
        intent.putExtra(Constant.PRACTICE_SET, practice_set);
        intent.putExtra(Constant.IsReminder, isRemider);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.THEMEPOSITION, themePosition);
        intent.putExtra(Constant.MAIN_THEME, main_theme);
        intent.putExtra(Constant.TABLE_NAME, tableName);
        intent.putExtra(Constant.MAIN_ID, main_id);
    }

    @Override
    public void onBackPressed() {
        backIntent();
    }

    public void backIntent() {
        intent = new Intent(this, LevelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        passData();
        startActivity(intent);
    }


    public void showDialogs() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view_answer, null);
        builder.setView(view);

        LinearLayout btn_show_video = view.findViewById(R.id.btn_show_video);
        if (getResources().getString(R.string.ADS_VISIBILITY).equals("YES")) {
            btn_show_video.setVisibility(View.VISIBLE);
        }else{
            btn_show_video.setVisibility(View.GONE);
        }
        LinearLayout btn_use_coin = view.findViewById(R.id.btn_use_coin);
        TextView text_coint = view.findViewById(R.id.text_coint);
        ImageView btn_close = view.findViewById(R.id.btn_close);

        final AlertDialog dialog = builder.create();
        dialog.show();

        if (Constant.getCoins(getApplicationContext()) < 10) {
            text_coint.setText(getString(R.string.coins_error));
        } else {
            btn_use_coin.setOnClickListener(v -> {
                Constant.setCoins(getApplicationContext(), (Constant.getCoins(getApplicationContext()) - 10));
                tv_coin.setText(String.valueOf(Constant.getCoins(getApplicationContext())));
                showHistoryDialogs();
                dialog.dismiss();
            });
        }

        btn_close.setOnClickListener(v -> dialog.dismiss());
        btn_use_coin.setBackgroundResource(Constant.getDrawbles().get(themePosition).drawable);
        btn_show_video.setBackgroundResource(Constant.getDrawbles().get(themePosition).drawable);

        btn_show_video.setOnClickListener(v -> {
            dialog.dismiss();

        });

    }


    public void showHistoryDialogs() {

        Intent intent = new Intent(ScoreActivity.this, ReviewAnswerActivity.class);
        intent.putExtra(Constant.THEMEPOSITION, themePosition);
        intent.putExtra(Constant.RIGHT_ANSWER, right_answer_count);
        intent.putExtra(Constant.WRONG_ANSWER, wrong_answer_count);
        intent.putExtra(Constant.PRACTICE_SET, practice_set);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, 1);

    }





}
