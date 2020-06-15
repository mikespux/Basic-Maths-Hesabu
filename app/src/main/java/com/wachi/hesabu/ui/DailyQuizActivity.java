package com.wachi.hesabu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.wachi.hesabu.R;
import com.wachi.hesabu.methods.GetDailyData;
import com.wachi.hesabu.model.DailyModel;
import com.wachi.hesabu.model.HistoryModel;
import com.wachi.hesabu.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.wachi.hesabu.utils.Constant.DEFAULT_QUESTION_SIZE;
import static com.wachi.hesabu.utils.Constant.DELAY_SEOCND;
import static com.wachi.hesabu.utils.Constant.getLayout;
import static com.wachi.hesabu.utils.Constant.getTextString;
import static com.wachi.hesabu.utils.Constant.setDefaultLanguage;

public class DailyQuizActivity extends BaseActivity implements View.OnClickListener {

    TextView tv_right_count, tv_wrong_count, tv_question_count, tv1_fraction, text_sign, tv2_fraction, tv_total_count, textView1, textView2, btn_op_1, btn_op_2, btn_op_3, btn_op_4, btn_op_1_fraction, btn_op_2_fraction, btn_op_3_fraction, btn_op_4_fraction;
    List<DailyModel> quizModelList = new ArrayList<>();
    ProgressDialog progressDialog;
    boolean isCount = false, isDivision, isClick = false, isRemider;
    List<TextView> optionViewList = new ArrayList<>();
    String historyQuestion, historyAnswer, historyUserAnswer;
    DailyModel quizModel;
    int id;
    int history_id, position, main_id, wrong_answer_count, right_answer_count;
    LinearLayout helpLineView;
    List<HistoryModel> historyModels = new ArrayList<>();
    Intent intent;
    Handler handler = new Handler();
    RelativeLayout view_1, view_2, view_3, view_4;
    List<View> viewArrayList = new ArrayList<>();
    RelativeLayout layout_cell;
    Toolbar toolbar;
    LinearLayout layout_fraction, layout_daily_quiz;
    MediaPlayer answerPlayer;


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
        setDefaultLanguage(this);
        setContentView(getLayout(this, false, true));
        init();
    }


    private void init() {
        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());
        getSupportActionBar().setTitle(null);


        tv_question_count = findViewById(R.id.tv_question_count);
        tv_right_count = findViewById(R.id.tv_right_count);
        tv_wrong_count = findViewById(R.id.tv_wrong_count);

        layout_cell = findViewById(R.id.layout_cell);
        helpLineView = findViewById(R.id.helpLineView);
        layout_daily_quiz = findViewById(R.id.layout_daily_quiz);
        layout_fraction = findViewById(R.id.layout_fraction);
        tv1_fraction = findViewById(R.id.tv1_fraction);
        text_sign = findViewById(R.id.text_sign);
        tv2_fraction = findViewById(R.id.tv2_fraction);
        tv_total_count = findViewById(R.id.tv_total_count);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        btn_op_1 = findViewById(R.id.btn_op_1);
        btn_op_2 = findViewById(R.id.btn_op_2);
        btn_op_3 = findViewById(R.id.btn_op_3);
        btn_op_4 = findViewById(R.id.btn_op_4);
        view_1 = findViewById(R.id.view_1);
        view_2 = findViewById(R.id.view_2);
        view_3 = findViewById(R.id.view_3);
        view_4 = findViewById(R.id.view_4);
        btn_op_1_fraction = findViewById(R.id.btn_op_1_fraction);
        btn_op_2_fraction = findViewById(R.id.btn_op_2_fraction);
        btn_op_3_fraction = findViewById(R.id.btn_op_3_fraction);
        btn_op_4_fraction = findViewById(R.id.btn_op_4_fraction);

        TextView mTitle = findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.daily_test));
        quizModelList.clear();
        setClick();


        new GetAllData().execute();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }


    private void setClick() {
        btn_op_1.setOnClickListener(this);
        btn_op_2.setOnClickListener(this);
        btn_op_3.setOnClickListener(this);
        btn_op_4.setOnClickListener(this);
        view_1.setOnClickListener(this);
        view_2.setOnClickListener(this);
        view_3.setOnClickListener(this);
        view_4.setOnClickListener(this);

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
        } else if (id == R.id.view_1) {
            checkAnswer(btn_op_1_fraction, 0);
        } else if (id == R.id.view_2) {
            checkAnswer(btn_op_2_fraction, 1);
        } else if (id == R.id.view_3) {
            checkAnswer(btn_op_3_fraction, 2);
        } else if (id == R.id.view_4) {
            checkAnswer(btn_op_4_fraction, 3);
        }
    }


    public void setNextData() {
        if (position < quizModelList.size() - 1) {
            position++;
            setData(position);
        } else {
            if (!isClick) {
                isClick = true;
                new AddData().execute();

            }
        }
    }

    public class AddData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            for (int i = 0; i < historyModels.size(); i++) {
                Constant.addHistoryData(DailyQuizActivity.this, historyModels.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Constant.showDoneDialog(DailyQuizActivity.this);
        }
    }

    public void onBackPressed() {
        backIntent();
    }

    public void backIntent() {
        cancelTimer();
        quizModelList.clear();
        intent = new Intent(this, ActivityDailyTest.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void cancelTimer() {

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


    public void setFalseAction(TextView textView) {
        if (!isCount) {
            isCount = true;
            wrong_answer_count++;
            tv_wrong_count.setText(String.valueOf(wrong_answer_count));

        }
        speak(R.raw.wrong);
        textView.setBackgroundResource(R.drawable.wrong_bg);
        handler.postDelayed(r, DELAY_SEOCND);
    }

    public void setTrueAction(TextView textView) {
        if (!isCount) {
            isCount = true;
            right_answer_count++;
            tv_right_count.setText(String.valueOf(right_answer_count));

        }

        speak(R.raw.right);
        textView.setBackgroundResource(R.drawable.right_bg);
        handler.postDelayed(r, DELAY_SEOCND);
    }


    public void setFractionTrueAction() {
        if (!isCount) {
            isCount = true;
            right_answer_count++;
            tv_right_count.setText(String.valueOf(right_answer_count));
        }
        speak(R.raw.right);
        handler.postDelayed(r, DELAY_SEOCND);
    }

    final Runnable r = this::setNextData;


    public void checkAnswer(String s) {
        for (int i = 0; i < optionViewList.size(); i++) {
            if (optionViewList.get(i).getText().toString().equals(s)) {
                if (quizModel.operation_id == 3) {
                    viewArrayList.get(i).setBackgroundResource(R.drawable.right_bg);
                    optionViewList.get(i).setTextColor(Color.WHITE);
                    optionViewList.get(i).setBackgroundResource(R.drawable.white_line);
                } else {
                    optionViewList.get(i).setBackgroundResource(R.drawable.right_bg);
                    optionViewList.get(i).setTextColor(Color.WHITE);
                }
            }
        }
    }

    public void checkAnswer(TextView textView, int pos) {
        if (quizModel != null) {
            textView.setTextColor(Color.WHITE);
            if (!isCount) {
                historyUserAnswer = textView.getText().toString();
                if (quizModel.operation_id == 3) {
                    historyUserAnswer = historyUserAnswer.replace("\n", "/");
                }
                history_id++;
                historyModels.add(new HistoryModel(historyQuestion, historyAnswer, historyUserAnswer, Constant.getCurrentDate(this)));
            }

            if (quizModel.operation_id == 1 || quizModel.operation_id == 2) {

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
            } else if (quizModel.operation_id == 3) {
                textView.setBackgroundResource(R.drawable.white_line);
                if (textView.getText().toString().equals(getSplitAnswer(quizModel.answer))) {
                    setFractionTrueAction();
                    viewArrayList.get(pos).setBackgroundResource(R.drawable.right_bg);
                } else {
                    setWrongFalseAction();
                    checkAnswer(getSplitAnswer(quizModel.answer));
                    viewArrayList.get(pos).setBackgroundResource(R.drawable.wrong_bg);
                }
            } else {

                String answer = quizModel.answer.trim();
                if (textView.getText().toString().trim().equals(answer)) {
                    setTrueAction(textView);
                } else {
                    checkAnswer(quizModel.answer);
                    setFalseAction(textView);
                }
            }
        }
    }


    public String getSplitAnswer(String answer) {
        return splitString(answer)[0] + "\n" + splitString(answer)[1];
    }

    public void setWrongFalseAction() {
        if (!isCount) {
            isCount = true;
            wrong_answer_count++;
            tv_wrong_count.setText(String.valueOf(wrong_answer_count));
        }
        speak(R.raw.right);
        handler.postDelayed(r, DELAY_SEOCND);
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


            for (int i = 0; i < DEFAULT_QUESTION_SIZE; i++) {
                int randomNumber = new Random().nextInt(4) + 1;
                quizModelList.add(GetDailyData.getDailyModel(DailyQuizActivity.this, randomNumber));
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

        if (quizModel.operation_id == 3) {
            optionViewList.clear();
            optionViewList.add(btn_op_1_fraction);
            optionViewList.add(btn_op_2_fraction);
            optionViewList.add(btn_op_3_fraction);
            optionViewList.add(btn_op_4_fraction);
            for (int i = 0; i < optionViewList.size(); i++) {
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.daily_test_line);
                assert drawable != null;
                drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.cyanColorPrimaryDark), PorterDuff.Mode.SRC_IN);
                optionViewList.get(i).setBackground(drawable);
                optionViewList.get(i).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.cyanColorPrimaryDark));
            }

        } else {
            optionViewList.clear();
            optionViewList.add(btn_op_1);
            optionViewList.add(btn_op_2);
            optionViewList.add(btn_op_3);
            optionViewList.add(btn_op_4);
            for (int i = 0; i < optionViewList.size(); i++) {
                optionViewList.get(i).setBackgroundResource(R.drawable.quiz_bg);
                optionViewList.get(i).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.cyanColorPrimaryDark));
            }

        }


    }

    public void setData(int position) {
        cancelTimer();
        isCount = false;
        layout_fraction.setVisibility(View.GONE);
        layout_daily_quiz.setVisibility(View.VISIBLE);
        quizModel = quizModelList.get(position);
        setOptionView();
        List<String> optionList;
        optionList = quizModel.optionList;
        quizModel = quizModelList.get(position);
        historyAnswer = quizModel.answer;
        if (quizModel.operation_id == 1 || quizModel.operation_id == 2) {


            textView1.setGravity(Gravity.END);
            textView2.setVisibility(View.VISIBLE);
            textView1.setText(String.valueOf(quizModel.firstDigit));
            textView2.setText(getTextString(quizModel.sign + getString(R.string.single_space) + quizModel.secondDigit));
            historyQuestion = quizModel.firstDigit + getString(R.string.str_space) + quizModel.sign + getString(R.string.str_space) + quizModel.secondDigit;
            tv_question_count.setText(String.valueOf((position + 1)));
            quizModelList.get(position).setOptionList(optionList);

            main_id = quizModel.main_id;
            if (quizModel.sign.equals(getString(R.string.division_sign))) {
                isDivision = true;
                isRemider = quizModel.isRemainder.equals(getString(R.string.str_division_set));
            } else {
                isDivision = false;
                isRemider = false;
            }

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

        } else if (quizModel.operation_id == 3) {
            layout_fraction.setVisibility(View.VISIBLE);
            layout_daily_quiz.setVisibility(View.GONE);
            setBGView();

            text_sign.setText(quizModel.sign);
            setText(tv1_fraction, quizModel.firstDigit);
            setText(tv2_fraction, quizModel.secondDigit);
            tv_question_count.setText(String.valueOf((position + 1)));
            quizModelList.get(position).setOptionList(optionList);
            historyQuestion = quizModel.firstDigit + quizModel.sign + quizModel.secondDigit;
            for (int i = 0; i < optionViewList.size(); i++) {
                setText(optionViewList.get(i), optionList.get(i));
            }

        } else {
            id = quizModel.id;

            textView1.setText(String.valueOf(quizModel.question));
            textView1.setGravity(Gravity.CENTER);
            textView2.setVisibility(View.GONE);

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

    public void setText(TextView textView, String s) {
        textView.setText(getTextString(splitString(s)[0] + "\n" + splitString(s)[1]));
    }

    private double getFormatValue2(double value) {
        return Double.parseDouble(String.format(getString(R.string.answer_2_format), value));
    }

    public String[] splitString(String s) {
        return s.split(getString(R.string.slash));
    }


}
