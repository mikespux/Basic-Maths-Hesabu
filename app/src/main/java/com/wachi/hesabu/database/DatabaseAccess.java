package com.wachi.hesabu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wachi.hesabu.model.DailyModel;
import com.wachi.hesabu.model.HistoryModel;
import com.wachi.hesabu.model.ProgressModel;
import com.wachi.hesabu.model.QuizModel;
import com.wachi.hesabu.model.SetModel;
import com.wachi.hesabu.utils.Constant;


import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private static DatabaseAccess instance;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private String ID = "id";
    private String REF_ID = "ref_id";
    private String PROGRESS = "progress";
    private String TABLENAME = "tableName";
    private String MAIN_ID = "main_id";
    private String LEVEL_NO = "level_no";
    private String SCORE = "score";
    private String SIGN = "sign";
    private String OP_3 = "op_3";
    private String OP_2 = "op_2";
    private String OP_1 = "op_1";
    private String ANSWER = "answer";
    private String SECOND_DIGIT = "secondDigit";
    private String FIRST_DIGIT = "firstDigit";
    private String PROGRESS_TABLE = "progress_table";

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        if (db != null) {
            this.db.close();
        }
    }

    public void insertProgressData(ProgressModel progressModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROGRESS, progressModel.progress);
        contentValues.put(TABLENAME, progressModel.tableName);
        contentValues.put(REF_ID, progressModel.ref_id);
        contentValues.put(MAIN_ID, progressModel.main_id);
        contentValues.put(LEVEL_NO, progressModel.level_no);
        db.insert(PROGRESS_TABLE, null, contentValues);
    }

    public void insertHistoryDataData(HistoryModel historyModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("question", historyModel.question);
        contentValues.put("answer", historyModel.answer);
        contentValues.put("userAnswer", historyModel.userAnswer);
        contentValues.put("date", historyModel.date);
        db.insert("historyData", null, contentValues);
    }


    public void updateProgress(int ref_id, int level_no, int main_id, String tableName, int progress) {
        try {
            String ExecSql = "UPDATE " + PROGRESS_TABLE + " SET " + PROGRESS + "=" + progress + " WHERE " + TABLENAME + " = '" + tableName + "' and " + LEVEL_NO + "= " + level_no + " and " + MAIN_ID + " = " + main_id + " and " + REF_ID + "=" + ref_id;
            db.execSQL(ExecSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateScore(int ref_id, int level_no, int main_id, String tableName, int score) {
        try {
            String ExecSql = "UPDATE " + PROGRESS_TABLE + " SET " + SCORE + " = " + score + " WHERE " + TABLENAME + " = '" + tableName + "' and " + LEVEL_NO + "= " + level_no + " and " + MAIN_ID + " = " + main_id + " and " + REF_ID + "=" + ref_id;
            db.execSQL(ExecSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<ProgressModel> getProgressdata(String tableName, int ref_id, int main_id) {
        Cursor cur = db.rawQuery("select * from " + PROGRESS_TABLE + " where " + REF_ID + " =" + ref_id + " and " + TABLENAME + " ='" + tableName + "' and " + MAIN_ID + "=" + main_id, null);
        List<ProgressModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    ProgressModel obj = new ProgressModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.progress = cur.getInt(cur.getColumnIndex(PROGRESS));
                    obj.tableName = cur.getString(cur.getColumnIndex(TABLENAME));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));
                    obj.main_id = cur.getInt(cur.getColumnIndex(MAIN_ID));
                    obj.level_no = cur.getInt(cur.getColumnIndex(LEVEL_NO));
                    obj.score = cur.getInt(cur.getColumnIndex(SCORE));

                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }

    public List<ProgressModel> getScore(String tableName, int ref_id, int main_id, int level) {
        Cursor cur = db.rawQuery("select * from " + PROGRESS_TABLE + " where " + REF_ID + " =" + ref_id + " and " + TABLENAME + " ='" + tableName + "' and " + LEVEL_NO + " = " + level + " and " + MAIN_ID + "=" + main_id, null);
        List<ProgressModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    ProgressModel obj = new ProgressModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.progress = cur.getInt(cur.getColumnIndex(PROGRESS));
                    obj.tableName = cur.getString(cur.getColumnIndex(TABLENAME));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));
                    obj.main_id = cur.getInt(cur.getColumnIndex(MAIN_ID));
                    obj.level_no = cur.getInt(cur.getColumnIndex(LEVEL_NO));
                    obj.score = cur.getInt(cur.getColumnIndex(SCORE));

                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


    public List<QuizModel> getQuizdata(String tableName, int ref_id) {
        Cursor cur = db.rawQuery("select * from '" + tableName + "' where " + REF_ID + " =" + ref_id + " ORDER BY RANDOM() LIMIT " + Constant.DEFAULT_QUESTION_SIZE + ";", null);
        List<QuizModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    QuizModel obj = new QuizModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.firstDigit = cur.getString(cur.getColumnIndex(FIRST_DIGIT));
                    obj.secondDigit = cur.getString(cur.getColumnIndex(SECOND_DIGIT));
                    obj.answer = cur.getString(cur.getColumnIndex(ANSWER));
                    obj.op_1 = cur.getString(cur.getColumnIndex(OP_1));
                    obj.op_2 = cur.getString(cur.getColumnIndex(OP_2));
                    obj.op_3 = cur.getString(cur.getColumnIndex(OP_3));
                    obj.sign = cur.getString(cur.getColumnIndex(SIGN));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));
                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


    public List<DailyModel> getDailyQuizdata() {
        Cursor cur = db.rawQuery("select * from dailyTestdata ORDER BY RANDOM() LIMIT " + 1 + ";", null);
        List<DailyModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    DailyModel obj = new DailyModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.firstDigit = cur.getString(cur.getColumnIndex(FIRST_DIGIT));
                    obj.secondDigit = cur.getString(cur.getColumnIndex(SECOND_DIGIT));
                    obj.answer = cur.getString(cur.getColumnIndex(ANSWER));
                    obj.op_1 = cur.getString(cur.getColumnIndex(OP_1));
                    obj.op_2 = cur.getString(cur.getColumnIndex(OP_2));
                    obj.op_3 = cur.getString(cur.getColumnIndex(OP_3));
                    obj.sign = cur.getString(cur.getColumnIndex(SIGN));
                    obj.isRemainder = cur.getString(cur.getColumnIndex("isReminder"));
                    obj.main_id = cur.getInt(cur.getColumnIndex(MAIN_ID));
                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


    public List<QuizModel> getQuizWorkSheetdata(String tableName, int ref_id, int size) {
        Cursor cur = db.rawQuery("select * from '" + tableName + "' where " + REF_ID + " =" + ref_id + " ORDER BY RANDOM() LIMIT " + size + ";", null);
        List<QuizModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    QuizModel obj = new QuizModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.firstDigit = cur.getString(cur.getColumnIndex(FIRST_DIGIT));
                    obj.secondDigit = cur.getString(cur.getColumnIndex(SECOND_DIGIT));
                    obj.answer = cur.getString(cur.getColumnIndex(ANSWER));
                    obj.op_1 = cur.getString(cur.getColumnIndex(OP_1));
                    obj.op_2 = cur.getString(cur.getColumnIndex(OP_2));
                    obj.op_3 = cur.getString(cur.getColumnIndex(OP_3));
                    obj.sign = cur.getString(cur.getColumnIndex(SIGN));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));
                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


    public List<DailyModel> getDailyQuizFractionData() {
        Cursor cur = db.rawQuery("select * from fractionData  ORDER BY RANDOM() LIMIT  " + 1 + ";", null);
        List<DailyModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    DailyModel obj = new DailyModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.firstDigit = cur.getString(cur.getColumnIndex(FIRST_DIGIT));
                    obj.secondDigit = cur.getString(cur.getColumnIndex(SECOND_DIGIT));
                    obj.answer = cur.getString(cur.getColumnIndex(ANSWER));
                    obj.op_1 = cur.getString(cur.getColumnIndex(OP_1));
                    obj.op_2 = cur.getString(cur.getColumnIndex(OP_2));
                    obj.op_3 = cur.getString(cur.getColumnIndex(OP_3));
                    obj.sign = cur.getString(cur.getColumnIndex(SIGN));

                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


    public List<HistoryModel> getHistotySize(String date) {
        Cursor cur = db.rawQuery("select * from historyData where date= '" + date + "'", null);
        List<HistoryModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    HistoryModel obj = new HistoryModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.question = cur.getString(cur.getColumnIndex("question"));
                    obj.answer = cur.getString(cur.getColumnIndex("answer"));
                    obj.userAnswer = cur.getString(cur.getColumnIndex("userAnswer"));
                    obj.date = cur.getString(cur.getColumnIndex("date"));

                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }

    public List<HistoryModel> getHistoty(String date, List<HistoryModel> item_data) {
        Cursor cur = db.rawQuery("select * from historyData where date= '" + date + "'", null);
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    HistoryModel obj = new HistoryModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.question = cur.getString(cur.getColumnIndex("question"));
                    obj.answer = cur.getString(cur.getColumnIndex("answer"));
                    obj.userAnswer = cur.getString(cur.getColumnIndex("userAnswer"));
                    obj.date = cur.getString(cur.getColumnIndex("date"));

                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }

    public List<QuizModel> getFractionQuizData(int main_id, int ref_id) {
        Cursor cur = db.rawQuery("select * from fractionData where " + MAIN_ID + " =" + main_id + " and " + REF_ID + " =" + ref_id + " ORDER BY RANDOM() LIMIT  " + Constant.DEFAULT_QUESTION_SIZE + ";", null);
        List<QuizModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    QuizModel obj = new QuizModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.firstDigit = cur.getString(cur.getColumnIndex(FIRST_DIGIT));
                    obj.secondDigit = cur.getString(cur.getColumnIndex(SECOND_DIGIT));
                    obj.answer = cur.getString(cur.getColumnIndex(ANSWER));
                    obj.op_1 = cur.getString(cur.getColumnIndex(OP_1));
                    obj.op_2 = cur.getString(cur.getColumnIndex(OP_2));
                    obj.op_3 = cur.getString(cur.getColumnIndex(OP_3));
                    obj.sign = cur.getString(cur.getColumnIndex(SIGN));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));
                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }

    public List<QuizModel> getWorksheetFractionData(int main_id, int ref_id, int limit) {
        Cursor cur = db.rawQuery("select * from fractionData where " + MAIN_ID + " =" + main_id + " and " + REF_ID + " =" + ref_id + " ORDER BY RANDOM() LIMIT  " + limit + ";", null);
        List<QuizModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    QuizModel obj = new QuizModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    obj.firstDigit = cur.getString(cur.getColumnIndex(FIRST_DIGIT));
                    obj.secondDigit = cur.getString(cur.getColumnIndex(SECOND_DIGIT));
                    obj.answer = cur.getString(cur.getColumnIndex(ANSWER));
                    obj.op_1 = cur.getString(cur.getColumnIndex(OP_1));
                    obj.op_2 = cur.getString(cur.getColumnIndex(OP_2));
                    obj.op_3 = cur.getString(cur.getColumnIndex(OP_3));
                    obj.sign = cur.getString(cur.getColumnIndex(SIGN));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));
                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


    public List<SetModel> getSetdata(String tableName, int ref_id, boolean checkReminder) {
        Cursor cur = db.rawQuery("select * from '" + tableName + "' where " + REF_ID + " =" + ref_id, null);
        List<SetModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    SetModel obj = new SetModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    String TITLE = "title";
                    obj.title = cur.getString(cur.getColumnIndex(TITLE));
                    String EXAMPLE = "example";
                    obj.example = cur.getString(cur.getColumnIndex(EXAMPLE));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));

                    if (checkReminder) {
                        String ISREMIDER = "isReminder";
                        obj.isRemider = cur.getString(cur.getColumnIndex(ISREMIDER));
                    }
                    String PRACTICE_SET = "practice_set";
                    obj.practice_set = cur.getInt(cur.getColumnIndex(PRACTICE_SET));
                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


    public List<SetModel> getWorksheetSetdataForPDF(String tableName, boolean checkReminder, String isReminder, int ref_id) {
        Cursor cur;
        if (checkReminder) {
            cur = db.rawQuery("select * from '" + tableName + "' where isReminder='" + isReminder + "' and ref_id = " + ref_id, null);
        } else {
            cur = db.rawQuery("select * from '" + tableName + "' where ref_id =" + ref_id, null);
        }
        List<SetModel> item_data = new ArrayList<>();
        if (cur.getCount() != 0) {
            if (cur.moveToFirst()) {
                do {
                    SetModel obj = new SetModel();
                    obj.id = cur.getInt(cur.getColumnIndex(ID));
                    String TITLE = "title";
                    obj.title = cur.getString(cur.getColumnIndex(TITLE));
                    String EXAMPLE = "example";
                    obj.example = cur.getString(cur.getColumnIndex(EXAMPLE));
                    obj.ref_id = cur.getInt(cur.getColumnIndex(REF_ID));

                    if (checkReminder) {
                        String ISREMIDER = "isReminder";
                        obj.isRemider = cur.getString(cur.getColumnIndex(ISREMIDER));
                    }
                    String PRACTICE_SET = "practice_set";
                    obj.practice_set = cur.getInt(cur.getColumnIndex(PRACTICE_SET));
                    item_data.add(obj);
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return item_data;
    }


}
