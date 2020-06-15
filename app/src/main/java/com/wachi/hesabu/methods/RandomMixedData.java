package com.wachi.hesabu.methods;

import android.content.Context;

import com.wachi.hesabu.R;
import com.wachi.hesabu.model.MixedModel;

import java.util.Random;

public class RandomMixedData {

    private int firstDigit, secondDigit, answer, min, max, op_1 = 0, op_2 = 0, op_3 = 0;
    private Context context;
    private String space, question_sign, equal_sign, addition_sign, multiplication_sign, division_sign, subtraction_sign;
    private String easy, medium, hard, question, root_sign, str_of, type;
    private boolean isSign;
    private boolean isPdf;

    public RandomMixedData(Context context, String type, boolean isPdf) {
        this.context = context;
        this.type = type;
        this.isPdf = isPdf;
        addition_sign = context.getString(R.string.addition_sign);
        multiplication_sign = context.getString(R.string.multiplication_sign);
        subtraction_sign = context.getString(R.string.subtraction_sign);
        division_sign = context.getString(R.string.division_sign);
        easy = context.getString(R.string.easy);
        medium = context.getString(R.string.medium);
        hard = context.getString(R.string.hard);
        root_sign = context.getString(R.string.root_sign);
        str_of = context.getString(R.string.percentage_sign) + context.getString(R.string.str_of);
        space = context.getString(R.string.str_space);
        question_sign = context.getString(R.string.sign_question);
        equal_sign = context.getString(R.string.equal_sign);
    }


    public MixedModel getMixedData() {
        if (type.equals(context.getString(R.string.easy))) {
            return getEasyMixedData();
        } else if (type.equals(context.getString(R.string.medium))) {
            return getMediumMixedData();
        } else {
            return getHardMixedData();
        }
    }


    private MixedModel getEasyMixedData() {
        Random random = new Random();
        Random answerRandom = new Random();
        int digit_1, digit_2, digit_3, digit_4;
        firstDigit = random.nextInt(8) + 1;


        digit_1 = firstDigit;

        firstDigit = random.nextInt(8) + 1;

        digit_2 = firstDigit;

        firstDigit = random.nextInt(8) + 1;

        digit_3 = firstDigit;

        firstDigit = random.nextInt(8) + 1;

        digit_4 = firstDigit;

        int randomSign = answerRandom.nextInt(3);

        if (randomSign == 1) {
            answer = digit_2 * digit_3;
            answer = digit_1 + answer;
            answer = answer - digit_4;

            question = digit_1 + addition_sign + digit_2 + multiplication_sign + digit_3 + subtraction_sign + digit_4;


        } else if (randomSign == 2) {

            answer = digit_1 * digit_2;
            answer = answer-(digit_3 + digit_4);
           // answer = answer - digit_3;

            question = digit_1 + multiplication_sign + digit_2 + subtraction_sign + digit_3 + addition_sign + digit_4;


        } else {

            answer = digit_3 * digit_4;
            answer = answer + digit_2;
            answer=   digit_1-answer;

            question = digit_1 + subtraction_sign + digit_2 + addition_sign + digit_3 + multiplication_sign + digit_4;

        }
        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;
        return addMixedModel();


    }


    private MixedModel getHardMixedData() {
        Random random = new Random();
        Random answerRandom = new Random();
        int digit_1, digit_2, digit_3, digit_4;
        firstDigit = random.nextInt(885) + 100;


        digit_1 = firstDigit;

        firstDigit = random.nextInt(885) + 100;

        digit_2 = firstDigit;

        firstDigit = random.nextInt(885) + 100;

        digit_3 = firstDigit;

        firstDigit = random.nextInt(885) + 100;

        digit_4 = firstDigit;

        int randomSign = answerRandom.nextInt(3);

        if (randomSign == 1) {
             
            answer = digit_3 / digit_4;
            answer = answer * digit_2;
            answer = answer + digit_1;
            
            question = digit_1 + addition_sign + digit_2 + multiplication_sign + digit_3 + division_sign + digit_4;
        } else if (randomSign == 2) {
            answer = digit_1 * digit_2;
            answer = answer - (digit_3 + digit_4);
           // answer = digit_3 + digit_4;
            question = digit_1 + multiplication_sign + digit_2 + subtraction_sign + digit_3 + addition_sign + digit_4;
        } else {
            answer = digit_1 / digit_2;
            answer = answer + (digit_3 * digit_4);
            //answer = digit_3 * digit_4;
            question = digit_1 + division_sign + digit_2 + addition_sign + digit_3 + multiplication_sign + digit_4;
        }

        if ((answer - 5) > 10) {
            min = answer - 5;
        } else {
            min = answer + 5;
        }
        max = answer + 10;


        if (max < 0) {
            max = 400;
        }

        if (min < 0) {
            min = 100;
        }
        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;
        return addMixedModel();


    }


    private MixedModel getMediumMixedData() {
        Random random = new Random();
        Random answerRandom = new Random();
        int digit_1, digit_2, digit_3, digit_4;
        firstDigit = random.nextInt(85) + 10;


        digit_1 = firstDigit;

        firstDigit = random.nextInt(85) + 10;

        digit_2 = firstDigit;

        firstDigit = random.nextInt(85) + 10;

        digit_3 = firstDigit;

        firstDigit = random.nextInt(85) + 10;

        digit_4 = firstDigit;

        int randomSign = answerRandom.nextInt(3);

        if (randomSign == 1) {



            answer = digit_3 / digit_4;
            answer = digit_2 * answer;
            answer = digit_1 + answer;

            question = digit_1 + addition_sign + digit_2 + multiplication_sign + digit_3 + division_sign + digit_4;


        } else if (randomSign == 2) {

            answer = digit_1 * digit_2;
            answer = answer - (digit_3 + digit_4);
           // answer = digit_3 + digit_4;

            question = digit_1 + multiplication_sign + digit_2 + subtraction_sign + digit_3 + addition_sign + digit_4;


        } else {

            answer = digit_3 * digit_4;
            answer = answer + digit_2;
            answer = digit_1 - answer;

            question = digit_1 + subtraction_sign + digit_2 + addition_sign + digit_3 + multiplication_sign + digit_4;

        }

        if ((answer - 5) > 10) {
            min = answer - 5;
        } else {
            min = answer + 5;
        }
        max = answer + 10;

        if (max < 0) {
            max = 400;
        }

        if (min < 0) {
            min = 100;
        }

        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;
        return addMixedModel();

    }


    public MixedModel getPercentageData() {
        Random random = new Random();
        double digit_1, digit_2;
        firstDigit = random.nextInt(100) + 1;
        digit_1 = firstDigit;


        if (type.equals(context.getString(R.string.easy))) {
            digit_2 = random.nextInt(600) + 100;
        } else if (type.equals(context.getString(R.string.medium))) {
            digit_2 = random.nextInt(2000) + 1000;
        } else {
            digit_2 = random.nextInt(8888) + 1000;
        }


        double answer1 = (digit_1 / 100);

        answer1 = (answer1 * digit_2);

        question = (int) digit_1 + str_of + (int) digit_2;


        answer = (int) answer1;
        if ((answer - 5) > 100) {
            min = answer - 5;
        } else {
            min = answer + 5;
        }
        max = answer + 10;

        if (max <= 0) {
            max = 100;
        }
        if (min <= 0) {
            min = 400;
        }


        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;
        return addMixedModel();


    }

    public MixedModel getSquareData() {
        Random random = new Random();


        if (type.equals(context.getString(R.string.easy))) {
            firstDigit = random.nextInt(200) + 1;
        } else if (type.equals(context.getString(R.string.medium))) {
            firstDigit = random.nextInt(500) + 100;
        } else {
            firstDigit = random.nextInt(1000) + 900;
        }

        answer = firstDigit * firstDigit;

        question = String.valueOf(firstDigit);
        if ((answer - 5) > 100) {
            min = answer - 5;
        } else {
            min = answer + 5;
        }
        max = answer + 10;

        if (max <= 0) {
            max = 100;
        }
        if (min <= 0) {
            min = 400;
        }

        if (isPdf) {
            question = firstDigit + context.getString(R.string.space) + context.getString(R.string.square_sign);
        }

        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;

        return addMixedModel();


    }

    public MixedModel getSquareRootData() {
        Random random = new Random();


        if (type.equals(context.getString(R.string.easy))) {
            firstDigit = random.nextInt(300) + 1;
        } else if (type.equals(context.getString(R.string.medium))) {
            firstDigit = random.nextInt(2000) + 500;
        } else {
            firstDigit = random.nextInt(4000) + 2000;
        }


        double answer1 = Math.sqrt(firstDigit);

        answer = (int) answer1;

        question = String.valueOf(firstDigit);
        if ((answer - 5) > 10) {
            min = answer - 5;
        } else {
            min = answer + 5;
        }
        max = answer + 10;
        question = root_sign + firstDigit;

        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;
        return addMixedModel();


    }


    public MixedModel getCubeData() {
        Random random = new Random();

        min = 0;
        max = 0;

        if (type.equals(context.getString(R.string.easy))) {
            firstDigit = random.nextInt(100) + 1;
        } else if (type.equals(context.getString(R.string.medium))) {
            firstDigit = random.nextInt(500) + 100;
        } else {
            firstDigit = random.nextInt(700) + 500;
        }


        double answer1 = Math.pow(firstDigit, 3);

        answer = (int) answer1;

        question = String.valueOf(firstDigit);


        if ((answer - 5) > 100) {
            min = answer - 5;
        } else {
            min = answer + 5;
        }
        max = answer + 10;

        if (max <= 0) {
            max = 100;
        }
        if (min <= 0) {
            min = 400;
        }

        if (isPdf) {
            question = firstDigit + context.getString(R.string.space) + context.getString(R.string.cube_sign);
        }


        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;


        return addMixedModel();


    }

    public MixedModel getCubeRootData() {
        Random random = new Random();


        if (type.equals(context.getString(R.string.easy))) {
            firstDigit = random.nextInt(300) + 1;
        } else if (type.equals(context.getString(R.string.medium))) {
            firstDigit = random.nextInt(2000) + 500;
        } else {
            firstDigit = random.nextInt(4000) + 2000;
        }


        double answer1 = Math.cbrt(firstDigit);

        answer = (int) answer1;

        question = String.valueOf(firstDigit);
        if ((answer - 5) > 10) {
            min = answer - 5;
        } else {
            min = answer + 5;
        }
        max = answer + 10;

        question = root_sign + firstDigit;


        op_1 = answer + 2;
        op_2 = answer + 5;
        op_3 = answer - 5;

        return addMixedModel();


    }


    public MixedModel getMixedFindMissingNumber(int level) {
        isSign = false;
        MixedModel mixedModel;
        if (level <= 8) {
            return getAdditionNumber(easy);
        } else if (level <= 15) {
            return getAdditionNumber(medium);
        } else if (level <= 20) {
            return getAdditionNumber(hard);
        } else if (level <= 28) {
            return getSubtractionNumber(easy);
        } else if (level <= 35) {
            return getSubtractionNumber(medium);
        } else if (level <= 40) {
            return getSubtractionNumber(hard);
        } else if (level <= 48) {
            return getMultiplicationNumber(easy);
        } else if (level <= 55) {
            return getMultiplicationNumber(medium);
        } else if (level <= 60) {
            return getMultiplicationNumber(hard);
        } else if (level <= 68) {
            return getDivisionNumber(easy);
        } else if (level <= 75) {
            return getDivisionNumber(medium);
        } else if (level <= 80) {
            return getDivisionNumber(hard);
        } else {
            Random random = new Random();

            isSign = true;
            int randomNumber = random.nextInt(4);

            if (randomNumber == 1) {
                mixedModel = getAdditionNumber(easy);
            } else if (randomNumber == 2) {
                mixedModel = getSubtractionNumber(easy);
            } else if (randomNumber == 3) {
                mixedModel = getMultiplicationNumber(easy);
            } else {
                mixedModel = getDivisionNumber(easy);
            }

        }
        return mixedModel;
    }

    private MixedModel getAdditionNumber(String type) {

        Random random = new Random();


        if (context.getString(R.string.easy).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt(40) + 8;
            secondDigit = random.nextInt(40) + 8;
        } else if (context.getString(R.string.medium).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt((1000 - 100) + 1) + 90;
            secondDigit = random.nextInt((500) + 50) + 1;

        } else if (context.getString(R.string.hard).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt((2000 - 500) + 1) + 500;
            secondDigit = random.nextInt((1000 - 200)) + 1;

        }


        answer = firstDigit;


        op_1 = (answer + 10);
        op_2 = (answer - 10);
        op_3 = (answer - 8);


        question = question_sign + addition_sign + space + secondDigit + equal_sign + space + (firstDigit + secondDigit);

        if (isSign) {
            question = firstDigit + question_sign + secondDigit + equal_sign + space + (firstDigit + secondDigit);
            return new MixedModel(question, addition_sign, multiplication_sign, division_sign, subtraction_sign);
        } else {
            int random_number = random.nextInt(3);

            if (random_number == 1) {
                question = question_sign + addition_sign + space + secondDigit + equal_sign + space + (firstDigit + secondDigit);
                answer = firstDigit;
            } else if (random_number == 2) {
                question = firstDigit + space + addition_sign + question_sign + equal_sign + space + (firstDigit + secondDigit);
                answer = secondDigit;
            } else {
                question = firstDigit + space + addition_sign + space + secondDigit + equal_sign + question_sign;
                answer = (firstDigit + secondDigit);
            }


            op_1 = (answer + 10);
            op_2 = (answer - 10);
            op_3 = (answer - 8);


            return addMixedModel();
        }


    }


    private MixedModel addMixedModel() {
        return new MixedModel(question, String.valueOf(answer), String.valueOf(op_1), String.valueOf(op_2), String.valueOf(op_3));
    }


    private MixedModel getSubtractionNumber(String type) {

        Random random = new Random();


        if (context.getString(R.string.easy).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt(100) + 1;
            secondDigit = random.nextInt(100) + 1;

        }
        if (context.getString(R.string.medium).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt(500) + 100;
            secondDigit = random.nextInt((400) + 50) + 1;

        }
        if (context.getString(R.string.hard).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt((2000 - 500) + 1) + 500;
            secondDigit = random.nextInt((1000 - 200)) + 1;

        }


        answer = firstDigit;

        op_1 = (answer + 10);
        op_2 = (answer - 10);
        op_3 = (answer - 8);

        question = question_sign + subtraction_sign + space + secondDigit + equal_sign + space + (firstDigit - secondDigit);


        if (isSign) {
            question = firstDigit + question_sign + secondDigit + equal_sign + space + (firstDigit - secondDigit);
            return new MixedModel(question, subtraction_sign, multiplication_sign, division_sign, addition_sign);
        } else {


            int random_number = random.nextInt(3);


            if (random_number == 1) {
                question = question_sign + subtraction_sign + space + secondDigit + equal_sign + space + (firstDigit - secondDigit);
                answer = firstDigit;
            } else if (random_number == 2) {
                question = firstDigit + subtraction_sign + addition_sign + question_sign + equal_sign + space + (firstDigit - secondDigit);
                answer = secondDigit;
            } else {
                question = firstDigit + subtraction_sign + addition_sign + space + secondDigit + equal_sign + question_sign;
                answer = (firstDigit - secondDigit);
            }


            op_1 = (answer + 10);
            op_2 = (answer - 10);
            op_3 = (answer - 8);


            return addMixedModel();
        }


    }

    private MixedModel getMultiplicationNumber(String type) {

        Random random = new Random();


        if (context.getString(R.string.easy).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt(10) + 1;
            secondDigit = random.nextInt(10) + 1;


        }
        if (context.getString(R.string.medium).equalsIgnoreCase(type)) {

            secondDigit = random.nextInt(10) + 21;
            secondDigit = random.nextInt(10) + 21;

        }
        if (context.getString(R.string.hard).equalsIgnoreCase(type)) {

            secondDigit = random.nextInt(1000) + 10;
            secondDigit = random.nextInt(50) + 10;

        }


        answer = firstDigit;


        op_1 = (answer + 10);
        op_2 = (answer + 12);
        op_3 = (answer - 2);


        if (isSign) {
            question = firstDigit + question_sign + secondDigit + equal_sign + space + (firstDigit * secondDigit);
            return new MixedModel(question, multiplication_sign, addition_sign, division_sign, subtraction_sign);
        } else {


            int random_number = random.nextInt(3);


            if (random_number == 1) {
                question = question_sign + multiplication_sign + space + secondDigit + equal_sign + space + (firstDigit * secondDigit);
                answer = firstDigit;
            } else if (random_number == 2) {
                question = firstDigit + multiplication_sign + addition_sign + question_sign + equal_sign + space + (firstDigit * secondDigit);
                answer = secondDigit;
            } else {
                question = firstDigit + multiplication_sign + addition_sign + space + secondDigit + equal_sign + question_sign;
                answer = (firstDigit * secondDigit);
            }


            op_1 = (answer + 10);
            op_2 = (answer - 12);
            op_3 = (answer - 2);

            return addMixedModel();
        }


    }

    private MixedModel getDivisionNumber(String type) {

        Random random = new Random();


        if (context.getString(R.string.easy).equalsIgnoreCase(type)) {
            firstDigit = random.nextInt((99 - 10) + 1) + 10;
            secondDigit = random.nextInt((10 - 5) + 1) + 5;

        }
        if (context.getString(R.string.medium).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt((999 - 100) + 1) + 100;
            secondDigit = random.nextInt((10 - 5) + 1) + 5;

        }
        if (context.getString(R.string.hard).equalsIgnoreCase(type)) {

            firstDigit = random.nextInt((9999 - 500) + 1) + 500;
            secondDigit = random.nextInt((100 - 50) + 1) + 50;

        }


        answer = firstDigit;

        op_1 = (answer + 10);
        op_2 = (answer + 12);
        op_3 = (answer - 5);
        question = question_sign + division_sign + space + secondDigit + equal_sign + space + (firstDigit / secondDigit);


        if (isSign) {
            question = firstDigit + question_sign + secondDigit + equal_sign + space + (firstDigit / secondDigit);
            return new MixedModel(question, division_sign, addition_sign, multiplication_sign, subtraction_sign);
        } else {

            int random_number = random.nextInt(3);


            if (random_number == 1) {
                question = question_sign + division_sign + space + secondDigit + equal_sign + space + (firstDigit / secondDigit);
                answer = firstDigit;
            } else if (random_number == 2) {
                question = firstDigit + division_sign + addition_sign + question_sign + equal_sign + space + (firstDigit / secondDigit);
                answer = secondDigit;
            } else {
                question = firstDigit + division_sign + addition_sign + space + secondDigit + equal_sign + question_sign;
                answer = (firstDigit / secondDigit);
            }


            op_1 = (answer + 10);
            op_2 = (answer - 12);
            op_3 = (answer - 2);

            return addMixedModel();
        }


    }


}
