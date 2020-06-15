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
import com.wachi.hesabu.utils.Constant;
import com.wachi.hesabu.receiver.NotificationScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wachi.hesabu.utils.Constant.DELAY_SEOCND;
import static com.wachi.hesabu.utils.Constant.getLayout;
import static com.wachi.hesabu.utils.Constant.getPlusScore;
import static com.wachi.hesabu.utils.Constant.getTextString;

public class QuizActivity extends BaseActivity implements View.OnClickListener {

    TextView tv_set, tv_score, tv_plus_score, tv_right_count, tv_wrong_count, tv_timer, tv_coin, tv_question_count, tv_total_count, textView1, textView2, btn_op_1, btn_op_2, btn_op_3, btn_op_4;
    List<QuizModel> quizModelList = new ArrayList<>();
    ProgressDialog progressDialog;
    boolean isCount = false, isDivision, isClick = false, isRemider, isTimer;
    List<TextView> optionViewList = new ArrayList<>();
    String title, tableName, dataTableName, historyQuestion, historyAnswer, historyUserAnswer;
    QuizModel quizModel;
    int history_id, helpLineCount, practice_set, id, level, textColor, position, main_id, countTime, score, main_theme, plusScore, themePosition, wrong_answer_count, coin, right_answer_count;
    LinearLayout helpLineView;
    List<HistoryModel> historyModels = new ArrayList<>();
    Intent intent;
    ImageView layout_set;
    Handler handler = new Handler();
    ProgressBar progress_bar;
    RelativeLayout layout_cell;
    Toolbar toolbar;

    CountDownTimer countDownTimer;
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

        themePosition = getIntent().getIntExtra(Constant.THEMEPOSITION, 0);
        id = getIntent().getIntExtra(Constant.ID, 0);
        main_theme = getIntent().getIntExtra(Constant.MAIN_THEME, 0);
        level = getIntent().getIntExtra(Constant.LEVEL, 0);
        tableName = getIntent().getStringExtra(Constant.TABLE_NAME);
        main_id = getIntent().getIntExtra(Constant.MAIN_ID, 0);
        isRemider = getIntent().getBooleanExtra(Constant.IsReminder, false);
        title = getIntent().getStringExtra(Constant.TITLE);
        practice_set = getIntent().getIntExtra(Constant.PRACTICE_SET, 0);

        textColor = ContextCompat.getColor(getApplicationContext(), Constant.getDrawbles().get(themePosition).DarkColor);
        progressDialog = new ProgressDialog(this);

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

        tv_question_count = findViewById(R.id.tv_question_count);
        tv_right_count = findViewById(R.id.tv_right_count);
        tv_score = findViewById(R.id.tv_score);
        tv_plus_score = findViewById(R.id.tv_plus_score);
        tv_wrong_count = findViewById(R.id.tv_wrong_count);
        tv_set = findViewById(R.id.tv_set);
        layout_cell = findViewById(R.id.layout_cell);
        tv_coin = findViewById(R.id.tv_coin);
        helpLineView = findViewById(R.id.helpLineView);
        tv_total_count = findViewById(R.id.tv_total_count);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        btn_op_1 = findViewById(R.id.btn_op_1);
        btn_op_2 = findViewById(R.id.btn_op_2);
        btn_op_3 = findViewById(R.id.btn_op_3);
        btn_op_4 = findViewById(R.id.btn_op_4);
        progress_bar = findViewById(R.id.progress_bar);
        tv_timer = findViewById(R.id.tv_timer);
        TextView mTitle = findViewById(R.id.toolbar_title);
        layout_set = findViewById(R.id.layout_set);
        if (Constant.getLanguageCode(getApplicationContext()).equals(getString(R.string.es_code))) {
            layout_set.setBackgroundResource(R.drawable.level_set_es);
        } else {
            layout_set.setBackgroundResource(R.drawable.level_set);
        }


        progress_bar.setMax(Constant.TIMER(getApplicationContext()));

        tv_set.setText(getTextString(level + "\n" + getString(R.string.set)));


        String title;
        if (tableName.equals(getString(R.string.addition_set))) {
            title = getString(R.string.addition);
            dataTableName = getString(R.string.addition_data_table);
        } else if (tableName.equals(getString(R.string.multiplication_set))) {
            dataTableName = getString(R.string.multiplication_data_table);
            title = getString(R.string.multiplication);
        } else if (tableName.equals(getString(R.string.division_set))) {
            isDivision = true;
            dataTableName = getString(R.string.division_data_table);
            title = getString(R.string.division);
        } else {
            dataTableName = getString(R.string.subtraction_data_table);
            title = getString(R.string.subtraction);
        }
        mTitle.setText(title);
        setCoins();
        quizModelList.clear();
        setClick();
        setHelpLineView();
        setTheme();
        new GetAllData().execute();
    }

    public void setTheme() {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        startTimer(countTime);
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
        btn_op_1.setOnClickListener(this);
        btn_op_2.setOnClickListener(this);
        btn_op_3.setOnClickListener(this);
        btn_op_4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_op_1) {
            checkAnswer(btn_op_1, 0);
        } else if (id == R.id.btn_op_2) {
            checkAnswer(btn_op_2, 1);
        } else if (id == R.id.btn_op_3) {
            checkAnswer(btn_op_3, 2);
        } else if (id == R.id.btn_op_4) {
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

    public void onBackPressed() {
        backIntent();
    }

    public void backIntent() {
        cancelTimer();
        quizModelList.clear();
        intent = new Intent(this, LevelActivity.class);
        passData();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        passData();
        intent.putExtra(Constant.RIGHT_ANSWER, right_answer_count);
        intent.putExtra(Constant.WRONG_ANSWER, wrong_answer_count);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void passData() {
        intent.putExtra(Constant.TITLE, title);
        intent.putExtra(Constant.PRACTICE_SET, practice_set);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.MAIN_ID, main_id);
        intent.putExtra(Constant.IsReminder, isRemider);
        intent.putExtra(Constant.LEVEL, level);
        intent.putExtra(Constant.THEMEPOSITION, themePosition);
        intent.putExtra(Constant.MAIN_THEME, main_theme);
        intent.putExtra(Constant.TABLE_NAME, tableName);

    }

    public void setScore() {
        tv_score.setText(String.valueOf(score));
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
        helpLineCount++;
        setHelpLineView();
        textView.setBackgroundResource(R.drawable.wrong_bg);
        if (helpLineCount > 3) {
            if (!isClick) {
                isClick = true;
                passIntent();
            }
        }
        speak(R.raw.wrong);
        handler.postDelayed(r, DELAY_SEOCND);
    }

    public void setTrueAction(TextView textView) {
        if (!isCount) {
            isCount = true;
            right_answer_count++;
            addCoins();
            setCoins();
            score = score + plusScore;
            tv_right_count.setText(String.valueOf(right_answer_count));
            setScore();
        }
        speak(R.raw.right);

        textView.setBackgroundResource(R.drawable.right_bg);
        handler.postDelayed(r, DELAY_SEOCND);
    }

    final Runnable r = this::setNextData;


    public void checkAnswer(String s) {
        for (int i = 0; i < optionViewList.size(); i++) {
            if (optionViewList.get(i).getText().toString().equals(s)) {
                optionViewList.get(i).setBackgroundResource(R.drawable.right_bg);
                optionViewList.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    public void checkAnswer(TextView textView, int pos) {
        if (quizModel != null) {

            if (!isCount) {
                historyUserAnswer = textView.getText().toString();
                history_id++;
                historyModels.add(new HistoryModel(history_id, historyQuestion, historyAnswer, historyUserAnswer));
            }
            textView.setTextColor(Color.WHITE);
            if (isDivision && isRemider) {
                if (textView.getText().toString().equals(quizModel.answer + " " + getString(R.string.rem) + " " + quizModel.rem)) {
                    setTrueAction(textView);
                } else {
                    checkAnswer(quizModel.answer + " " + getString(R.string.rem) + " " + quizModel.rem);
                    setFalseAction(textView);
                }
            } else {
                if (textView.getText().toString().equals((quizModel.answer))) {
                    setTrueAction(textView);
                } else {
                    checkAnswer(quizModel.answer);
                    setFalseAction(textView);
                }
            }
        }
    }


    public void setBackground(TextView btn_op_1) {
        btn_op_1.setTextColor(Color.WHITE);
        if (btn_op_1.getText().toString().equals(quizModelList.get(position).answer)) {
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
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(QuizActivity.this);
            databaseAccess.open();
            quizModelList = databaseAccess.getQuizdata(dataTableName, id);
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
                if (isDivision) {
                    if (isRemider) {
                        if (main_id == 1) {
                            quizModelList.get(i).setRem(String.valueOf((Integer.parseInt(quizModel.firstDigit) % Integer.parseInt(quizModel.secondDigit))));
                        } else {
                            quizModelList.get(i).setRem(String.valueOf(getFormatValue2((Double.parseDouble(quizModel.firstDigit) % Double.parseDouble(quizModel.secondDigit)))));
                        }
                    }
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

    public void setData(int position) {
        cancelTimer();
        plusScore = 500;
        countTime = Constant.TIMER(getApplicationContext());
        startTimer(countTime);
        isCount = false;

        String space = getString(R.string.str_space);
        setOptionView();
        List<String> optionList;
        quizModel = quizModelList.get(position);
        optionList = quizModel.optionList;
        textView1.setText(String.valueOf(quizModel.firstDigit));
        textView2.setText(getTextString(quizModel.sign + getString(R.string.single_space) + quizModel.secondDigit));
        historyQuestion = quizModel.firstDigit + space + quizModel.sign + space + quizModel.secondDigit;
        tv_question_count.setText(String.valueOf((position + 1)));
        quizModelList.get(position).setOptionList(optionList);

        for (int i = 0; i < optionViewList.size(); i++) {
            if (isRemider) {
                optionViewList.get(i).setTextSize(getResources().getDimension(R.dimen.rem_text_size));
            }
            if (main_id == 1) {
                if (isRemider) {
                    if (Integer.parseInt(optionList.get(i)) == Integer.parseInt(quizModel.answer)) {
                        optionViewList.get(i).setText(getTextString(optionList.get(i) + " " + getString(R.string.rem) + " " + quizModel.rem));
                    } else {
                        optionViewList.get(i).setText(getTextString(optionList.get(i) + " " + getString(R.string.rem) + " " + (Integer.parseInt(optionList.get(i)) % Integer.parseInt(quizModel.secondDigit))));
                    }
                } else {
                    optionViewList.get(i).setText(String.valueOf(optionList.get(i)));
                }
            } else {
                if (isRemider) {
                    if (Double.parseDouble(optionList.get(i)) == Double.parseDouble(quizModel.answer)) {
                        optionViewList.get(i).setText(getTextString(optionList.get(i) + " " + getString(R.string.rem) + " " + getFormatValue2(Double.parseDouble(quizModel.rem))));
                    } else {
                        optionViewList.get(i).setText(getTextString(optionList.get(i) + " " + getString(R.string.rem) + " " + getFormatValue2((Double.parseDouble(optionList.get(i)) % Double.parseDouble(quizModel.secondDigit)))));
                    }
                } else {
                    if (!optionList.get(i).contains(getString(R.string.str_dot))) {
                        if (String.valueOf(optionList.get(i)).equals(quizModel.answer)) {
                            quizModelList.get(position).setAnswer(optionList.get(i) + getString(R.string.str_dot) + 0);
                        }
                        optionViewList.get(i).setText(getTextString(optionList.get(i) + getString(R.string.str_dot) + 0));
                        optionList.set(i, optionList.get(i) + getString(R.string.str_dot) + 0);
                        quizModel = quizModelList.get(position);
                    } else {
                        optionViewList.get(i).setText(optionList.get(i));
                    }
                }
            }
        }

        if (isDivision && isRemider) {
            historyAnswer = quizModel.answer + " " + getString(R.string.rem) + " " + quizModel.rem;
        } else {
            historyAnswer = quizModel.answer;
        }
    }

    private double getFormatValue2(double value) {
        return Double.parseDouble(String.format(getString(R.string.answer_2_format), value));
    }

}
