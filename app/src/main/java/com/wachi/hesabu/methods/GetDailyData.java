package com.wachi.hesabu.methods;

import android.app.Activity;


import com.wachi.hesabu.R;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.model.DailyModel;
import com.wachi.hesabu.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GetDailyData {


    private static DailyModel getDecimalOrIntegerData(Activity activity, int randomNumber) {
        List<DailyModel> dailyModels;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(activity);
        databaseAccess.open();
        dailyModels = databaseAccess.getDailyQuizdata();
        databaseAccess.close();

        DailyModel dailyModel = dailyModels.get(0);


        List<String> optionList = new ArrayList<>();
        optionList.add(dailyModel.op_1);
        optionList.add(dailyModel.op_2);
        optionList.add(dailyModel.op_3);
        optionList.add(dailyModel.answer);
        Collections.shuffle(optionList);
        dailyModel.setOptionList(optionList);


        boolean isDivision;
        boolean isRemider;
        int main_id = dailyModel.main_id;
        if (dailyModel.sign.equals(activity.getString(R.string.division_sign))) {
            isDivision = true;
            isRemider = dailyModel.isRemainder.equals(activity.getString(R.string.str_division_set));
        } else {
            isDivision = false;
            isRemider = false;
        }

        if (isDivision) {
            if (isRemider) {
                if (main_id == 1) {
                    dailyModel.setRem(String.valueOf((Integer.parseInt(dailyModel.firstDigit) % Integer.parseInt(dailyModel.secondDigit))));
                } else {
                    dailyModel.setRem(String.valueOf(getFormatValue2(activity, (Double.parseDouble(dailyModel.firstDigit) % Double.parseDouble(dailyModel.secondDigit)))));
                }
            }
        }

        dailyModel.setOperation_id(randomNumber);

        return dailyModel;
    }

    private static DailyModel getFractionData(Activity activity, int randomNumber) {
        List<DailyModel> dailyModels;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(activity);
        databaseAccess.open();
        dailyModels = databaseAccess.getDailyQuizFractionData();
        databaseAccess.close();

        List<String> optionList = new ArrayList<>();
        DailyModel quizModel = dailyModels.get(0);
        optionList.add(quizModel.op_1);
        optionList.add(quizModel.op_2);
        optionList.add(quizModel.op_3);
        optionList.add(quizModel.answer);
        Collections.shuffle(optionList);
        quizModel.setOptionList(optionList);


        quizModel.setOperation_id(randomNumber);

        return quizModel;
    }

    private static DailyModel getMixedData(Activity activity, int randomNumber) {
        RandomMixedData randomMixedData = new RandomMixedData(activity, Constant.getType(activity, new Random().nextInt(100)), false);
        int id = new Random().nextInt(6) + 1;

        DailyModel quizModel = null;
        if (id == Constant.TYPE_MIXED) {
            quizModel = new DailyModel(randomMixedData.getMixedData(), randomNumber);
        } else if (id == Constant.TYPE_PERCENTAGE) {
            quizModel = new DailyModel(randomMixedData.getPercentageData(), randomNumber);
        } else if (id == Constant.TYPE_SQUARE) {
            quizModel = new DailyModel(randomMixedData.getSquareData(), randomNumber);
        } else if (id == Constant.TYPE_SQUARE_ROOT) {
            quizModel = new DailyModel(randomMixedData.getSquareRootData(), randomNumber);
        } else if (id == Constant.TYPE_CUBE) {
            quizModel = new DailyModel(randomMixedData.getCubeData(), randomNumber);
        } else if (id == Constant.TYPE_CUBE_ROOT) {
            quizModel = new DailyModel(randomMixedData.getCubeRootData(), randomNumber);
        }
        List<String> optionList = new ArrayList<>();
        assert quizModel != null;
        optionList.add(quizModel.mixedModel.op_1);
        optionList.add(quizModel.mixedModel.op_2);
        optionList.add(quizModel.mixedModel.op_3);
        optionList.add(quizModel.mixedModel.answer);
        Collections.shuffle(optionList);
        quizModel.setOptionList(optionList);
        quizModel.setQuestion(quizModel.mixedModel.question);
        quizModel.setAnswer(quizModel.mixedModel.answer);
        quizModel.setOp_1(quizModel.mixedModel.op_1);
        quizModel.setOp_2(quizModel.mixedModel.op_2);
        quizModel.setOp_3(quizModel.mixedModel.op_3);
        quizModel.setId(id);

        return quizModel;
    }

    public static DailyModel getDailyModel(Activity activity, int randomNumber) {
        if (randomNumber == 1 || randomNumber == 2) {
            return GetDailyData.getDecimalOrIntegerData(activity, randomNumber);
        } else if (randomNumber == 3) {
            return GetDailyData.getFractionData(activity, randomNumber);
        } else {
            return GetDailyData.getMixedData(activity, randomNumber);

        }
    }


    private static double getFormatValue2(Activity activity, double value) {
        return Double.parseDouble(String.format(activity.getString(R.string.answer_2_format), value));
    }


}
