package com.wachi.hesabu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.wachi.hesabu.R;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.model.HistoryModel;
import com.wachi.hesabu.model.QuizModel;
import com.wachi.hesabu.receiver.NotificationScheduler;
import com.wachi.hesabu.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wachi.hesabu.utils.Constant.DELAY_SEOCND;
import static com.wachi.hesabu.utils.Constant.getLayout;
import static com.wachi.hesabu.utils.Constant.getPlusScore;
import static com.wachi.hesabu.utils.Constant.getTextString;
import static com.wachi.hesabu.utils.Constant.getToolbarTitleFromId;

public class FractionActivity extends BaseActivity implements View.OnClickListener {

    ImageView layout_set;
    TextView tv_set, tv_score, tv_plus_score, tv_right_count, tv_wrong_count, tv_timer, tv_coin, tv_question_count, tv_total_count, text_sign, textView1, textView2, btn_op_1, btn_op_2, btn_op_3, btn_op_4;
    List<QuizModel> quizModelList = new ArrayList<>();
    ProgressDialog progressDialog;
    int history_id, practice_set, id, level, right_answer_count, main_theme, helpLineCount, coin, ref_id, textColor, countTime, score, plusScore, wrong_answer_count, position, main_id, themePosition;
    List<TextView> optionViewList = new ArrayList<>();
    List<View> viewArrayList = new ArrayList<>();
    RelativeLayout view_1, view_2, view_3, view_4;
    Intent intent;
    QuizModel quizModel;
    List<HistoryModel> historyModels = new ArrayList<>();
    String tableName, title, historyQuestion, historyAnswer, historyUserAnswer;
    boolean isClick = false, isFraction, isTimer, isCount = false;
    LinearLayout helpLineView;
    Handler handler = new Handler();
    RelativeLayout layout_cell;
    ProgressBar progress_bar;
    CountDownTimer countDownTimer;
    Toolbar toolbar;
    MediaPlayer answerPlayer;
    final Runnable r = this::setNextData;
    RelativeLayout rt_timer;
    Button btnNext;
    public void speak(int sound) {
        if (Constant.getSound(getApplicationContext())) {
            if (answerPlayer != null) {
                answerPlayer.release();
            }
            answerPlayer = MediaPlayer.create(getApplicationContext(), sound);
            if (answerPlayer != null) {
                answerPlayer.start();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout(this, true, false));
        init();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startTimer(countTime);
    }

    public void startTimer(final int count) {
        countDownTimer = new CountDownTimer(count * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimer = true;
                countTime = (int) millisUntilFinished / 1000;
                tv_timer.setText(String.valueOf((millisUntilFinished / 1000)));
                progress_bar.setProgress(countTime);
                plusScore = getPlusScore(countTime);
                tv_plus_score.setText(getTextString(getString(R.string.addition_sign) + plusScore));
            }

            @Override
            public void onFinish() {
                isTimer = false;
                handler.postDelayed(r, DELAY_SEOCND);
            }
        }.start();
    }

    public void setCoins() {
        coin = Constant.getCoins(getApplicationContext());
        tv_coin.setText(String.valueOf(coin));
    }

    public void addCoins() {
        Constant.setCoins(getApplicationContext(), (coin + 2));
    }

    private void init() {

        themePosition = getIntent().getIntExtra(Constant.THEMEPOSITION, 0);
        main_theme = getIntent().getIntExtra(Constant.MAIN_THEME, 0);
        id = getIntent().getIntExtra(Constant.ID, 0);
        level = getIntent().getIntExtra(Constant.LEVEL, 0);
        tableName = getIntent().getStringExtra(Constant.TABLE_NAME);
        main_id = getIntent().getIntExtra(Constant.MAIN_ID, 0);
        isFraction = getIntent().getBooleanExtra(Constant.IsFraction, false);
        title = getIntent().getStringExtra(Constant.TITLE);
        practice_set = getIntent().getIntExtra(Constant.PRACTICE_SET, 0);

        textColor = ContextCompat.getColor(getApplicationContext(), Constant.getDrawbles().get(themePosition).DarkColor);
        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());
        getSupportActionBar().setTitle(null);

        rt_timer = findViewById(R.id.rt_timer);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setVisibility(View.GONE);
         if(Constant.TIMER(getApplicationContext())==0){
             btnNext.setVisibility(View.VISIBLE);
             rt_timer.setVisibility(View.GONE);
         }

        progress_bar = findViewById(R.id.progress_bar);
        tv_timer = findViewById(R.id.tv_timer);

        tv_question_count = findViewById(R.id.tv_question_count);
        tv_right_count = findViewById(R.id.tv_right_count);
        tv_score = findViewById(R.id.tv_score);
        tv_plus_score = findViewById(R.id.tv_plus_score);
        tv_wrong_count = findViewById(R.id.tv_wrong_count);
        tv_set = findViewById(R.id.tv_set);
        layout_cell = findViewById(R.id.layout_cell);
        tv_coin = findViewById(R.id.tv_coin);
        helpLineView = findViewById(R.id.helpLineView);
        tv_question_count = findViewById(R.id.tv_question_count);
        tv_total_count = findViewById(R.id.tv_total_count);
        text_sign = findViewById(R.id.text_sign);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        view_1 = findViewById(R.id.view_1);
        view_2 = findViewById(R.id.view_2);
        view_3 = findViewById(R.id.view_3);
        view_4 = findViewById(R.id.view_4);
        btn_op_1 = findViewById(R.id.btn_op_1);
        btn_op_2 = findViewById(R.id.btn_op_2);
        btn_op_3 = findViewById(R.id.btn_op_3);
        btn_op_4 = findViewById(R.id.btn_op_4);
        TextView mTitle = findViewById(R.id.toolbar_title);

        progress_bar.setMax(Constant.TIMER(getApplicationContext()));
        mTitle.setText(getToolbarTitleFromId(this, id));
        ref_id = Constant.getRefIdType(level);

        tv_set.setText(getTextString(level + "\n" + getString(R.string.set)));

        quizModelList.clear();
        setClick();
        setCoins();
        setHelpLineView();


        setTheme();
        new GetAllData().execute();
    }

    public void setTheme() {
        layout_set = findViewById(R.id.layout_set);
        if (Constant.getLanguageCode(getApplicationContext()).equals(getString(R.string.es_code))) {
            layout_set.setBackgroundResource(R.drawable.level_set_es);
        } else {
            layout_set.setBackgroundResource(R.drawable.level_set);
        }

        toolbar.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);
        layout_cell.setBackgroundResource(Constant.getDrawbles().get(themePosition).cell);

        tv_total_count.setTextColor(textColor);
        tv_question_count.setTextColor(textColor);
        tv_timer.setTextColor(textColor);
        textView1.setTextColor(textColor);
        textView2.setTextColor(textColor);
        text_sign.setTextColor(textColor);

        LayerDrawable layerDrawable = (LayerDrawable) getResources()
                .getDrawable(R.drawable.circular_progress_drawable);
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                .findDrawableByLayerId(R.id.progress);
        gradientDrawable.setColor(textColor);
        progress_bar.setProgressDrawable(layerDrawable);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.line);
        assert drawable != null;
        drawable.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);

        textView1.setBackground(drawable);
        textView2.setBackground(drawable);
    }

    public void setHelpLineView() {
        helpLineView.removeAllViews();
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(3, 3, 3, 3);
            imageView.setLayoutParams(layoutParams);
            if (helpLineCount > i) {
                imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_favorite_border_black_24dp));
            } else {
                imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_favorite_black_24dp));
            }
            helpLineView.addView(imageView);
        }
    }

    public Drawable getThemeDrawable(int drawableID) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), drawableID);
        assert drawable != null;
        drawable.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    private void setClick() {
        view_1.setOnClickListener(this);
        view_2.setOnClickListener(this);
        view_3.setOnClickListener(this);
        view_4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view_1) {
            checkAnswer(btn_op_1, 0);
        } else if (id == R.id.view_2) {
            checkAnswer(btn_op_2, 1);
        } else if (id == R.id.view_3) {
            checkAnswer(btn_op_3, 2);
        } else if (id == R.id.view_4) {
            checkAnswer(btn_op_4, 3);
        }

    }

    public void setNextData() {
        if(Constant.TIMER(getApplicationContext())==0){
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position < quizModelList.size() - 1) {
                        position++;
                        setData(position);
                    } else {
                        if (!isClick) {
                            isClick = true;
                            passIntent();
                        }

                    }
                }
            });

        }else{
        if (position < quizModelList.size() - 1) {
            position++;
            setData(position);
        } else {
            if (!isClick) {
                isClick = true;
                passIntent();
            }

        }
        }
    }

    @Override
    public void onBackPressed() {
        backIntent();
    }

    public void backIntent() {
        cancelTimer();
        quizModelList.clear();
        intent = new Intent(this, LevelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        passData();
        startActivity(intent);

    }

    public void cancelTimer() {
        if (isTimer) {
            countDownTimer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacks(r);
        }
        if (answerPlayer != null) {
            answerPlayer.release();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    public void passIntent() {
        NotificationScheduler.showNotification(getApplicationContext(), level);
        Constant.addModel(getApplicationContext(), historyModels);
        quizModelList.clear();
        intent = new Intent(this, ScoreActivity.class);
        intent.putExtra(Constant.SCORE, score);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        passData();
        intent.putExtra(Constant.RIGHT_ANSWER, right_answer_count);
        intent.putExtra(Constant.WRONG_ANSWER, wrong_answer_count);
        startActivity(intent);
    }

    public void passData() {
        intent.putExtra(Constant.TITLE, title);
        intent.putExtra(Constant.PRACTICE_SET, practice_set);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.LEVEL, level);
        intent.putExtra(Constant.MAIN_THEME, main_theme);
        intent.putExtra(Constant.THEMEPOSITION, themePosition);
        intent.putExtra(Constant.MAIN_ID, main_id);
        intent.putExtra(Constant.IsFraction, isFraction);
        intent.putExtra(Constant.TABLE_NAME, tableName);
    }

    public void setFalseAction() {
        if (!isCount) {
            isCount = true;
            wrong_answer_count++;
            if ((score - 250) > 0) {
                score = score - 250;
            }
            tv_wrong_count.setText(String.valueOf(wrong_answer_count));
        }
        helpLineCount++;
        setHelpLineView();
        speak(R.raw.wrong);
        if (helpLineCount > 3) {
            if (!isClick) {
                isClick = true;
                passIntent();
            }
        }
        handler.postDelayed(r, DELAY_SEOCND);
    }

    public void setTrueAction() {
        if (!isCount) {
            isCount = true;
            right_answer_count++;
            score = score + plusScore;
            tv_right_count.setText(String.valueOf(right_answer_count));
            addCoins();
            setCoins();
            setScore();
            speak(R.raw.right);
        }
        handler.postDelayed(r, DELAY_SEOCND);
    }

    public void setScore() {
        tv_score.setText(String.valueOf(score));
    }

    public String getSplitAnswer(String answer) {
        return splitString(answer)[0] + "\n" + splitString(answer)[1];
    }

    public void checkAnswer(String s) {
        for (int i = 0; i < optionViewList.size(); i++) {
            if (optionViewList.get(i).getText().toString().equals(s)) {
                viewArrayList.get(i).setBackgroundResource(R.drawable.right_bg);
                optionViewList.get(i).setTextColor(Color.WHITE);
                optionViewList.get(i).setBackgroundResource(R.drawable.white_line);
            }
        }
    }

    public void checkAnswer(TextView textView, int pos) {
        if (quizModel != null) {
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.white_line);
            if (!isCount) {
                historyUserAnswer = textView.getText().toString();
                historyUserAnswer = historyUserAnswer.replace("\n", "/");
                history_id++;
                historyModels.add(new HistoryModel(history_id, historyQuestion, historyAnswer, historyUserAnswer));
            }
            if (textView.getText().toString().equals(getSplitAnswer(quizModel.answer))) {
                setTrueAction();
                viewArrayList.get(pos).setBackgroundResource(R.drawable.right_bg);
            } else {
                setFalseAction();
                checkAnswer(getSplitAnswer(quizModel.answer));
                viewArrayList.get(pos).setBackgroundResource(R.drawable.wrong_bg);
            }
        }
    }

    public void setBGView() {
        viewArrayList.clear();
        viewArrayList.add(view_1);
        viewArrayList.add(view_2);
        viewArrayList.add(view_3);
        viewArrayList.add(view_4);
        for (int i = 0; i < optionViewList.size(); i++) {
            viewArrayList.get(i).setBackgroundResource(R.drawable.quiz_bg);
        }
    }

    public void setOptionView() {
        optionViewList.clear();
        optionViewList.add(btn_op_1);
        optionViewList.add(btn_op_2);
        optionViewList.add(btn_op_3);
        optionViewList.add(btn_op_4);

        for (int i = 0; i < optionViewList.size(); i++) {
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.line);
            assert drawable != null;
            drawable.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            optionViewList.get(i).setBackground(drawable);
            optionViewList.get(i).setTextColor(textColor);
        }
    }

    public void setData(int position) {

        cancelTimer();
        plusScore = 500;
        countTime = Constant.TIMER(getApplicationContext());
        startTimer(countTime);

        isCount = false;

        setBGView();
        setOptionView();
        List<String> optionList;

        quizModel = quizModelList.get(position);
        optionList = quizModel.optionList;
        text_sign.setText(quizModel.sign);
        setText(textView1, quizModel.firstDigit);
        setText(textView2, quizModel.secondDigit);
        tv_question_count.setText(String.valueOf((position + 1)));
        quizModelList.get(position).setOptionList(optionList);
        historyQuestion = quizModel.firstDigit + quizModel.sign + quizModel.secondDigit;

        for (int i = 0; i < optionViewList.size(); i++) {
            setText(optionViewList.get(i), optionList.get(i));
        }
        historyAnswer = quizModel.answer;

    }

    public void setText(TextView textView, String s) {
        textView.setText(getTextString(splitString(s)[0] + "\n" + splitString(s)[1]));
    }

    public String[] splitString(String s) {
        return s.split(getString(R.string.slash));
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
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(FractionActivity.this);
            databaseAccess.open();
            quizModelList = databaseAccess.getFractionQuizData(id, ref_id);
            databaseAccess.close();


            for (int i = 0; i < quizModelList.size(); i++) {
                List<String> optionList = new ArrayList<>();
                quizModel = quizModelList.get(i);
                optionList.add(quizModel.op_1);
                optionList.add(quizModel.op_2);
                optionList.add(quizModel.op_3);
                optionList.add(quizModel.answer);
                Collections.shuffle(optionList);
                quizModelList.get(i).setOptionList(optionList);

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            tv_total_count.setText(getTextString(getString(R.string.slash) + quizModelList.size()));
            if (quizModelList.size() > 0) {
                setData(position);
            }
        }
    }

}
