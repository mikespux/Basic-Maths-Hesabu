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
import android.text.Html;
import android.text.TextUtils;

import android.view.Gravity;
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
import com.wachi.hesabu.methods.RandomMixedData;
import com.wachi.hesabu.model.HistoryModel;
import com.wachi.hesabu.model.MixedModel;
import com.wachi.hesabu.utils.Constant;
import com.wachi.hesabu.receiver.NotificationScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wachi.hesabu.utils.Constant.DELAY_SEOCND;
import static com.wachi.hesabu.utils.Constant.getLayout;
import static com.wachi.hesabu.utils.Constant.getPlusScore;
import static com.wachi.hesabu.utils.Constant.getTextString;

public class MixedQuizActivity extends BaseActivity implements View.OnClickListener {

    ImageView layout_set;
    TextView tv_set, tv_score, tv_plus_score, tv_right_count, tv_wrong_count, tv_timer, tv_coin, tv_question_count, tv_total_count, textView1, textView2, btn_op_1, btn_op_2, btn_op_3, btn_op_4;
    List<MixedModel> quizModelList = new ArrayList<>();
    ProgressDialog progressDialog;
    boolean isTimer, isCount = false, isClick = false;
    List<TextView> optionViewList = new ArrayList<>();
    int position, main_id;
    String str_format;
    MixedModel quizModel;
    RandomMixedData randomMixedData;
    int history_id, level, helpLineCount, score, plusScore, wrong_answer_count, id, themePosition, textColor, countTime, right_answer_count, coin;
    Intent intent;
    LinearLayout helpLineView;
    Handler handler = new Handler();
    List<HistoryModel> historyModels = new ArrayList<>();
    String title, type, historyQuestion, historyAnswer, historyUserAnswer;
    Toolbar toolbar;
    ProgressBar progress_bar;
    CountDownTimer countDownTimer;
    RelativeLayout layout_cell;
    MediaPlayer answerPlayer;
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
        setContentView(getLayout(this, false, false));
        init();
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
        str_format = getString(R.string.equal_sign) + getString(R.string.sign_question);
        themePosition = getIntent().getIntExtra(Constant.THEMEPOSITION, 0);
        level = getIntent().getIntExtra(Constant.LEVEL, 0);
        main_id = getIntent().getIntExtra(Constant.MAIN_ID, 0);
        id = getIntent().getIntExtra(Constant.ID, 0);
        progressDialog = new ProgressDialog(this);
        type = Constant.getType(getApplicationContext(), level);
        title = getIntent().getStringExtra(Constant.TITLE);

        randomMixedData = new RandomMixedData(getApplicationContext(), type, false);
        textColor = ContextCompat.getColor(getApplicationContext(), Constant.getDrawbles().get(themePosition).DarkColor);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        tv_total_count = findViewById(R.id.tv_total_count);
        tv_coin = findViewById(R.id.tv_coin);
        textView1 = findViewById(R.id.textView1);
        helpLineView = findViewById(R.id.helpLineView);
        textView2 = findViewById(R.id.textView2);
        btn_op_1 = findViewById(R.id.btn_op_1);
        TextView mTitle = findViewById(R.id.toolbar_title);
        btn_op_2 = findViewById(R.id.btn_op_2);
        btn_op_3 = findViewById(R.id.btn_op_3);
        btn_op_4 = findViewById(R.id.btn_op_4);
        tv_right_count = findViewById(R.id.tv_right_count);
        tv_score = findViewById(R.id.tv_score);
        tv_plus_score = findViewById(R.id.tv_plus_score);
        tv_wrong_count = findViewById(R.id.tv_wrong_count);
        tv_set = findViewById(R.id.tv_set);
        layout_cell = findViewById(R.id.layout_cell);


        progress_bar.setMax(Constant.TIMER(getApplicationContext()));
        setCoins();
        quizModelList.clear();
        setClick();
        tv_set.setText(getTextString(level + "\n" + getString(R.string.set)));

        if (title.equals(getString(R.string.title_find_missing))) {
            mTitle.setText(getString(R.string.find_missing));
        } else {
            mTitle.setText(title);
        }
        setTheme();
        new GetAllData().execute();
    }

    private void setTheme() {
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

        LayerDrawable layerDrawable = (LayerDrawable) getResources()
                .getDrawable(R.drawable.circular_progress_drawable);
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                .findDrawableByLayerId(R.id.progress);
        gradientDrawable.setColor(textColor);
        progress_bar.setProgressDrawable(layerDrawable);
    }


    public void backIntent() {
        cancelTimer();
        quizModelList.clear();
        intent = new Intent(this, MixedLevelActivity.class);
        passData();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    private void setClick() {
        btn_op_1.setOnClickListener(this);
        btn_op_2.setOnClickListener(this);
        btn_op_3.setOnClickListener(this);
        btn_op_4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_op_1) {
            checkAnswer(btn_op_1);
        } else if (id == R.id.btn_op_2) {
            checkAnswer(btn_op_2);
        } else if (id == R.id.btn_op_3) {
            checkAnswer(btn_op_3);
        } else if (id == R.id.btn_op_4) {
            checkAnswer(btn_op_4);
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

    public void onBackPressed() {
        backIntent();
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
        NotificationScheduler.showNotification(getApplicationContext(),level);
        Constant.addModel(getApplicationContext(), historyModels);
        quizModelList.clear();
        intent = new Intent(this, MixedScoreActivity.class);
        passData();
        intent.putExtra(Constant.SCORE, score);
        intent.putExtra(Constant.RIGHT_ANSWER, right_answer_count);
        intent.putExtra(Constant.WRONG_ANSWER, wrong_answer_count);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void passData() {
        intent.putExtra(Constant.TITLE, title);
        intent.putExtra(Constant.THEMEPOSITION, themePosition);
        intent.putExtra(Constant.MAIN_ID, main_id);
        intent.putExtra(Constant.LEVEL, level);
        intent.putExtra(Constant.ID, id);
    }


    final Runnable r = this::setNextData;


    public void setTrueAction(TextView textView) {
        if (!isCount) {
            isCount = true;
            right_answer_count++;
            addCoins();
            setCoins();
            tv_right_count.setText(String.valueOf(right_answer_count));
            score = score + plusScore;
            setScore();
        }
        speak(R.raw.right);
        textView.setBackgroundResource(R.drawable.right_bg);
        handler.postDelayed(r, DELAY_SEOCND);
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


    public void setFalseAction(TextView textView) {
        if (!isCount) {
            isCount = true;
            wrong_answer_count++;
            tv_wrong_count.setText(String.valueOf(wrong_answer_count));
            if ((score - 250) > 0) {
                score = score - 250;
            }
            setScore();
        }
        speak(R.raw.wrong);
        textView.setBackgroundResource(R.drawable.wrong_bg);
        helpLineCount++;
        setHelpLineView();
        if (helpLineCount > 3) {
            if (!isClick) {
                isClick = true;
                passIntent();
            }
        }
        handler.postDelayed(r, DELAY_SEOCND);
    }


    public void checkAnswer(TextView textView) {
        if (quizModel != null) {
            textView.setTextColor(Color.WHITE);
            if (!isCount) {
                historyUserAnswer = textView.getText().toString();
                history_id++;
                historyModels.add(new HistoryModel(history_id, historyQuestion, historyAnswer, historyUserAnswer));
            }
            String answer = quizModel.answer.trim();
            if (textView.getText().toString().trim().equals(answer)) {
                setTrueAction(textView);
            } else {
                checkAnswer(quizModel.answer);
                setFalseAction(textView);
            }
        }
    }


    public void setScore() {
        tv_score.setText(String.valueOf(score));
    }


    public void checkAnswer(String s) {
        for (int i = 0; i < optionViewList.size(); i++) {
            if (optionViewList.get(i).getText().toString().trim().equals(s.trim())) {
                optionViewList.get(i).setBackgroundResource(R.drawable.right_bg);
                optionViewList.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    public void setBackground(TextView btn_op_1) {
        btn_op_1.setTextColor(Color.WHITE);
        if (btn_op_1.getText().toString().trim().equals(quizModelList.get(position).answer.trim())) {
            btn_op_1.setBackgroundResource(R.drawable.right_bg);
        } else {
            btn_op_1.setBackgroundResource(R.drawable.wrong_bg);
        }
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
            for (int i = 0; i < Constant.DEFAULT_QUESTION_SIZE; i++) {
                if (id == Constant.TYPE_MIXED) {
                    quizModelList.add(randomMixedData.getMixedData());
                } else if (id == Constant.TYPE_PERCENTAGE) {
                    quizModelList.add(randomMixedData.getPercentageData());
                } else if (id == Constant.TYPE_SQUARE) {
                    quizModelList.add(randomMixedData.getSquareData());
                } else if (id == Constant.TYPE_SQUARE_ROOT) {
                    quizModelList.add(randomMixedData.getSquareRootData());
                } else if (id == Constant.TYPE_CUBE) {
                    quizModelList.add(randomMixedData.getCubeData());
                } else if (id == Constant.TYPE_CUBE_ROOT) {
                    quizModelList.add(randomMixedData.getCubeRootData());
                } else if (id == Constant.TYPE_FIND_MISSING) {
                    quizModelList.add(randomMixedData.getMixedFindMissingNumber(level));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            new GetSuffleData().execute();
        }
    }

    public class GetSuffleData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
            progressDialog.setCancelable(false);

        }

        @Override
        protected String doInBackground(Void... voids) {

            for (int i = 0; i < quizModelList.size(); i++) {
                List<String> optionList = new ArrayList<>();
                quizModel = quizModelList.get(i);
                if (!TextUtils.isEmpty(quizModel.op_1)) {
                    optionList.add(quizModel.op_1);
                }
                if (!TextUtils.isEmpty(quizModel.op_2)) {
                    optionList.add(quizModel.op_2);
                }
                if (!TextUtils.isEmpty(quizModel.op_3)) {
                    optionList.add(quizModel.op_3);
                }
                if (!TextUtils.isEmpty(quizModel.answer)) {
                    optionList.add(quizModel.answer);
                }
                Collections.shuffle(optionList);
                quizModelList.get(i).setOptionList(optionList);

                if (id == Constant.TYPE_MIXED || id == Constant.TYPE_PERCENTAGE) {
                    quizModelList.get(i).setQuestion(quizModel.question + str_format);
                }
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


    public void setOptionView() {
        optionViewList.clear();
        optionViewList.add(btn_op_1);
        optionViewList.add(btn_op_2);
        optionViewList.add(btn_op_3);
        optionViewList.add(btn_op_4);
        for (int i = 0; i < optionViewList.size(); i++) {
            optionViewList.get(i).setBackgroundResource(R.drawable.quiz_bg);
            optionViewList.get(i).setTextColor(textColor);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startTimer(countTime);
    }

    public void setData(int position) {

        cancelTimer();
        plusScore = 500;
        countTime = Constant.TIMER(getApplicationContext());
        startTimer(countTime);
        isCount = false;
        setOptionView();
        List<String> optionList;

        quizModel = quizModelList.get(position);
        optionList = quizModel.optionList;
        textView1.setText(String.valueOf(quizModel.question));
        textView2.setVisibility(View.GONE);
        textView1.setGravity(Gravity.CENTER);

        historyQuestion = quizModel.question;
        if (id == Constant.TYPE_SQUARE) {
            textView1.setPadding(8, 8, 8, 8);
            textView1.setText(Html.fromHtml(quizModel.question + getString(R.string.square_format)));
        } else if (id == Constant.TYPE_CUBE) {
            textView1.setPadding(8, 8, 8, 8);
            textView1.setText(Html.fromHtml(quizModel.question + getString(R.string.cube_format)));
        }
        tv_question_count.setText(String.valueOf((position + 1)));
        quizModelList.get(position).setOptionList(optionList);
        for (int i = 0; i < optionViewList.size(); i++) {
            optionViewList.get(i).setText(String.valueOf(optionList.get(i)));
            if (id == Constant.TYPE_SQUARE || id == Constant.TYPE_CUBE) {
                optionViewList.get(i).setTextSize(getResources().getDimension(R.dimen.rem_text_size));
            }
        }
        historyAnswer = quizModel.answer;

    }


}
