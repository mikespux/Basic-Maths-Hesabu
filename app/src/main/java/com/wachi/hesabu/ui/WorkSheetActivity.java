package com.wachi.hesabu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.adapter.OperaionAdapter;
import com.wachi.hesabu.adapter.WorkSheetSetAdapter;
import com.wachi.hesabu.database.DatabaseAccess;
import com.wachi.hesabu.methods.RandomMixedData;
import com.wachi.hesabu.model.MixedModel;
import com.wachi.hesabu.model.OperationModel;
import com.wachi.hesabu.model.PDFModel;
import com.wachi.hesabu.model.PDFSetModel;
import com.wachi.hesabu.model.QuizModel;
import com.wachi.hesabu.model.SetModel;
import com.wachi.hesabu.model.StoreSetModel;
import com.wachi.hesabu.utils.Constant;
import com.wachi.hesabu.utils.HeaderFooterPageEvent;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.wachi.hesabu.utils.Constant.TEXT_LENGTH;
import static com.wachi.hesabu.utils.Constant.getAllOperationTypeList;
import static com.wachi.hesabu.utils.Constant.getPdfTitle;
import static com.wachi.hesabu.utils.Constant.getTextString;
import static com.wachi.hesabu.utils.Constant.setDefaultLanguage;

public class WorkSheetActivity extends AppCompatActivity implements WorkSheetSetAdapter.ItemClick {

    EditText edt_link, edt_watermark, edt_school_name, edt_number_question, edt_teacher_name;
    int question_limit;
    TextView text_image, edt_question_type, btn_question_category, edt_number_type, text_operation;
    Button btn_submit;
    List<QuizModel> quizModelList = new ArrayList<>();
    Bitmap bitmapLogo;
    String pdfName;
    List<PDFModel> pdfModels = new ArrayList<>();
    DatabaseAccess databaseAccess;
    String str_true, str_false, pdf_format, operation;
    int selected_position = 0;
    OperaionAdapter operaionAdapter;
    boolean isDecimal = false, isInteger = false, isMixed = false;
    int operation_id = 1;
    List<Integer> operationList = new ArrayList<>();
    List<String> numberTypeList = new ArrayList<>();
    List<StoreSetModel> storeSetModels = new ArrayList<>();
    List<Integer> operationIdList = new ArrayList<>();
    List<Integer> mixedIdList = new ArrayList<>();
    List<String> questionTypeList = new ArrayList<>();
    public static List<String> operationSet = new ArrayList<>();
    ProgressDialog progressDialog;
    RandomMixedData randomMixedData;
    TextView btn_logo;
    boolean isPdfSave = false;
    WorkSheetSetAdapter workSheetSetAdapter;
    LinearLayout layout_select_operation, layout_select_type, layout_select_set;
    List<PDFSetModel> setModels = new ArrayList<>();
    AlertDialog setDialog;
    List<OperationModel> stringOperation = new ArrayList<>();
    List<String> dummmyOperation = new ArrayList<>();
    List<Integer> dummmyOperationId = new ArrayList<>();
    String space;
    CheckBox check_auto_fill, check_with_answer, check_without_answer;

    SharedPreferences mSharedPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            setTheme(R.style.Okenwa_Black);
        }
        Constant.setDefaultLanguage(this);
        setContentView(R.layout.activity_worksheet);
        init();
    }

    public void backIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        backIntent();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(edt_school_name.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }


    private void init() {
        randomMixedData = new RandomMixedData(this, getString(R.string.easy), true);
        progressDialog = new ProgressDialog(this);
        pdf_format = getString(R.string.pdf_format);
        str_true = getString(R.string.str_true);
        str_false = getString(R.string.str_false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(view -> backIntent());

        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.worksheet_generate));

        edt_school_name = findViewById(R.id.edt_school_name);
        edt_school_name.setText("__");
        edt_teacher_name = findViewById(R.id.edt_teacher_name);
        edt_teacher_name.setText("__");
        edt_number_question = findViewById(R.id.edt_number_question);
        edt_number_type = findViewById(R.id.edt_number_type);
        btn_submit = findViewById(R.id.btn_submit);
        edt_question_type = findViewById(R.id.edt_question_type);
        text_image = findViewById(R.id.text_image);
        layout_select_operation = findViewById(R.id.layout_select_operation);
        layout_select_type = findViewById(R.id.layout_select_type);
        layout_select_set = findViewById(R.id.layout_select_set);
        check_with_answer = findViewById(R.id.check_with_answer);
        check_without_answer = findViewById(R.id.check_without_answer);
        check_auto_fill = findViewById(R.id.check_auto_fill);
        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            check_with_answer.setTextColor(Color.parseColor("#FFFFFF"));
            check_without_answer.setTextColor(Color.parseColor("#FFFFFF"));
            check_auto_fill.setTextColor(Color.parseColor("#FFFFFF"));
        }
        edt_link = findViewById(R.id.edt_link);
        edt_link.setText("__");

        Bitmap.Config conf = Bitmap.Config.ARGB_4444; // see other conf types
        bitmapLogo = Bitmap.createBitmap(100, 100, conf);

        btn_logo = findViewById(R.id.btn_logo);

        text_image = findViewById(R.id.text_image);

        edt_watermark = findViewById(R.id.edt_watermark);
        edt_watermark.setText("Okenwa");
        btn_question_category = findViewById(R.id.btn_question_category);
        text_operation = findViewById(R.id.text_operation);
        layout_select_operation.setVisibility(View.GONE);
        layout_select_type.setVisibility(View.GONE);
        layout_select_set.setVisibility(View.GONE);


        check_auto_fill.setChecked(Constant.getIsAutoFill(getApplicationContext()));
        if (Constant.getIsAutoFill(getApplicationContext())) {
            edt_link.setText(Constant.getAutoFillText(getApplicationContext()));
        }
        space = getString(R.string.space);

        setTexts();
        setClick();
    }

    public void setTexts() {
        text_operation.setText(getTextString(operationList.size() + space + getString(R.string.operation_selected)));
        edt_number_type.setText(getTextString(numberTypeList.size() + space + getString(R.string.number_type_selected)));
        edt_question_type.setText(getTextString(questionTypeList.size() + space + getString(R.string.question_type_selected)));
        btn_question_category.setText(getTextString(storeSetModels.size() + space + getString(R.string.question_category_selected)));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {


            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    try {
                        bitmapLogo = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        text_image.setVisibility(View.VISIBLE);
                        Uri uri = data.getData();

                        assert uri != null;

                        File file = new File(Objects.requireNonNull(uri.getPath()));//create path from uri
                        if (!TextUtils.isEmpty(file.toString())) {
                            final String[] split = file.getPath().split(":");//split the path.
                            text_image.setText(new File(split[1]).getName());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public class AddSetData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(WorkSheetActivity.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            setDataFromTable();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            showSetDialog();
        }
    }


    public void addDataFromCategory(String s, int position, boolean isReminder, String reminder, int ref_id) {
        List<SetModel> dummySetModel;


        if (isReminder) {
            if (questionTypeList.contains(s)) {
                databaseAccess = DatabaseAccess.getInstance(WorkSheetActivity.this);
                databaseAccess.open();
                dummySetModel = databaseAccess.getWorksheetSetdataForPDF(Constant.getTableName(getApplicationContext(), position), isReminder, reminder, ref_id);
                databaseAccess.close();

                for (int k = 0; k < dummySetModel.size(); k++) {
                    SetModel setModel = dummySetModel.get(k);
                    setModels.add(new PDFSetModel(setModel.id, setModel.title, Constant.getTableName(getApplicationContext(), position), setModel.example, setModel.practice_set, setModel.isRemider));
                }

            }
        } else {
            databaseAccess = DatabaseAccess.getInstance(WorkSheetActivity.this);
            databaseAccess.open();
            dummySetModel = databaseAccess.getWorksheetSetdataForPDF(Constant.getTableName(getApplicationContext(), position), isReminder, reminder, ref_id);
            databaseAccess.close();


            for (int k = 0; k < dummySetModel.size(); k++) {
                SetModel setModel = dummySetModel.get(k);
                setModels.add(new PDFSetModel(setModel.id, setModel.title, Constant.getTableName(getApplicationContext(), position), setModel.example, setModel.practice_set, setModel.isRemider));
            }

        }


    }

    public void setDataFromTable() {

        setModels.clear();
        for (int i = 0; i < numberTypeList.size(); i++) {

            boolean isInteger = false;
            boolean isDecimal = false;


            if (operationList.contains(1)) {

                isInteger = true;
            }
            if (operationList.contains(2)) {
                isDecimal = true;

            }
            if (numberTypeList.get(i).equals(getString(R.string.addition))) {

                if (isInteger) {
                    addDataFromCategory(getString(R.string.with_carry), 0, true, getString(R.string.str_true), 1);
                    addDataFromCategory(getString(R.string.without_carry), 0, true, getString(R.string.str_false), 1);
                }

                if (isDecimal) {
                    addDataFromCategory(getString(R.string.with_carry), 0, true, getString(R.string.str_true), 2);
                    addDataFromCategory(getString(R.string.without_carry), 0, true, getString(R.string.str_false), 2);

                }
            }

            if (numberTypeList.get(i).equals(getString(R.string.subtraction))) {

                if (isInteger) {
                    addDataFromCategory(getString(R.string.with_borrow), 1, true, getString(R.string.str_true), 1);
                    addDataFromCategory(getString(R.string.without_borrow), 1, true, getString(R.string.str_false), 1);
                }

                if (isDecimal) {
                    addDataFromCategory(getString(R.string.with_borrow), 1, true, getString(R.string.str_true), 2);
                    addDataFromCategory(getString(R.string.with_borrow), 1, true, getString(R.string.str_false), 2);

                }
            }
            if (numberTypeList.get(i).equals(getString(R.string.division))) {

                if (isInteger) {
                    addDataFromCategory(getString(R.string.with_remainder), 3, true, getString(R.string.str_true), 1);
                    addDataFromCategory(getString(R.string.without_remainder), 3, true, getString(R.string.str_false), 1);
                }

                if (isDecimal) {
                    addDataFromCategory(getString(R.string.with_remainder), 3, true, getString(R.string.str_true), 2);
                    addDataFromCategory(getString(R.string.without_remainder), 3, true, getString(R.string.str_false), 2);

                }
            }


            if (numberTypeList.get(i).equals(getString(R.string.multiplication))) {

                if (isInteger) {
                    addDataFromCategory(getString(R.string.with_remainder), 2, false, null, 1);
                }
                if (isDecimal) {
                    addDataFromCategory(getString(R.string.with_remainder), 2, false, null, 2);

                }
            }


        }


    }


    private void setClick() {
        edt_number_type.setOnClickListener(view -> showOperationDialog());

        text_operation.setOnClickListener(view -> showCategoryDialog());

        edt_question_type.setOnClickListener(view -> {
            showOperationTypeDialog();
        });


        btn_question_category.setOnClickListener(view -> {
            hideKeyboard();
            new AddSetData().execute();

        });
        btn_logo.setOnClickListener(view -> {
            isPdfSave = false;
            checkAndRunPdf();
        });

        btn_submit.setOnClickListener(view -> {
            hideKeyboard();


            if (isValid(edt_school_name)) {
                if (isValid(edt_teacher_name)) {
                    if (isValid(edt_link)) {
                        if (bitmapLogo != null) {
                            if (isValid(edt_number_question)) {
                                if (Integer.parseInt(edt_number_question.getText().toString()) < 100) {
                                    if (isValid(edt_watermark)) {
                                        Constant.setAutoFill(getApplicationContext(), check_auto_fill.isChecked(), edt_link.getText().toString());
                                        if (operationList.size() > 0 && numberTypeList.size() > 0) {
                                            question_limit = Integer.parseInt(edt_number_question.getText().toString());
                                            pdfModels.clear();
                                            new GeneratePDFData().execute();
                                        } else {
                                            Toast.makeText(WorkSheetActivity.this, "" + getString(R.string.select_operation), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        setError(edt_watermark);
                                    }
                                } else {
                                    edt_number_question.setError(getString(R.string.enter_valid_number));
                                }
                            } else {
                                setError(edt_number_question);
                            }
                        } else {
                            Toast.makeText(this, "" + getString(R.string.please_select_logo), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        setError(edt_link);
                    }
                } else {
                    setError(edt_teacher_name);
                }
            } else {
                setError(edt_school_name);
            }


        });


    }


    public void setError(EditText editText) {
        editText.setError(getString(R.string.empty_error));
    }

    public class GeneratePDFData extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdfModels.clear();
            progressDialog = new ProgressDialog(WorkSheetActivity.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            int id = 0;
            for (int i = 0; i < question_limit; i++) {
                int random_category = operationList.get(new Random().nextInt(operationList.size()));

                if (storeSetModels.size() > 0) {
                    if (random_category == 1 || random_category == 2) {


                        StoreSetModel storeSetModel = storeSetModels.get(new Random().nextInt(storeSetModels.size()));


                        String dataTableName, table_name;

                        table_name = getPdfTitle(WorkSheetActivity.this, storeSetModel.category);
                        if (table_name.equals(getString(R.string.addition_set))) {
                            dataTableName = getString(R.string.addition_data_table);
                        } else if (table_name.equals(getString(R.string.multiplication_set))) {
                            dataTableName = getString(R.string.multiplication_data_table);
                        } else if (table_name.equals(getString(R.string.division_set))) {
                            dataTableName = getString(R.string.division_data_table);
                        } else {
                            dataTableName = getString(R.string.subtraction_data_table);
                        }


                        databaseAccess = DatabaseAccess.getInstance(WorkSheetActivity.this);
                        databaseAccess.open();
                        quizModelList = databaseAccess.getQuizWorkSheetdata(dataTableName, storeSetModel.id, 1);
                        databaseAccess.close();


                        if (quizModelList.size() > 0) {
                            QuizModel quizModel = quizModelList.get(0);
                            if (quizModel != null) {
                                pdfModels.add(new PDFModel(id++, quizModel.firstDigit + space + quizModel.sign + space + quizModel.secondDigit, quizModel.answer, random_category, 0));
                            }
                        }


                    } else if (random_category == 3) {
                        getMixedData(id);
                    }
                } else {
                    getMixedData(id);

                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (pdfModels.size() == question_limit) {
                isPdfSave = true;
                checkAndRunPdf();
            } else {
                Toast.makeText(WorkSheetActivity.this, "" + getString(R.string.fill_details), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getMixedData(int id) {

        int random_operation = mixedIdList.get(new Random().nextInt(mixedIdList.size()));

        MixedModel mixedModel;


        mixedModel = getMixModel(random_operation);

        if (mixedModel != null) {
            pdfModels.add(new PDFModel(id++, mixedModel.question, mixedModel.answer, 3, random_operation));
        }
    }


    public MixedModel getMixModel(int random_operation) {
        if (random_operation == 5) {
            return randomMixedData.getMixedData();
        } else if (random_operation == 6) {
            return randomMixedData.getPercentageData();
        } else if (random_operation == 7) {
            return randomMixedData.getSquareData();
        } else if (random_operation == 8) {
            return randomMixedData.getSquareRootData();
        } else if (random_operation == 9) {
            return randomMixedData.getCubeData();
        } else if (random_operation == 10) {
            return randomMixedData.getCubeRootData();
        }
        return null;
    }

    private boolean isValid(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString());
    }

    public void checkAndRunPdf() {
        if (!checkPermission()) {
            requestPermission();
        } else {
            if (isPdfSave) {
                new GeneratePDF().execute();
            } else {
                openGallery();
            }
        }
    }

    public boolean checkPermission() {
        int read;
        read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 123);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 111) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (isPdfSave) {
                        new GeneratePDF().execute();
                    } else {
                        openGallery();
                    }
                }
            }
        }
    }


    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
        }, 111);
    }


    String FILE;


    public void showOperationDialog() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_operation, null);
        builder.setView(view);


        TextView btn_ok, btn_cancel;

        btn_ok = view.findViewById(R.id.btn_ok);
        btn_cancel = view.findViewById(R.id.btn_cancel);


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        List<OperationModel> strings;


        strings = stringOperation;

        for (int i = 0; i < numberTypeList.size(); i++) {
            for (int j = 0; j < strings.size(); j++) {
                if (numberTypeList.get(i).equals(strings.get(j).name)) {
                    strings.get(j).setCheck(true);
                }
            }
        }


        operaionAdapter = new OperaionAdapter(this, strings);
        recyclerView.setAdapter(operaionAdapter);


        operaionAdapter.setClickListener(position -> {
            if (strings.get(position).isCheck) {
                strings.get(position).setCheck(false);
                numberTypeList.remove(String.valueOf(strings.get(position).name));
                operationIdList.remove(Integer.valueOf(strings.get(position).id));
                if (strings.get(position).id >= 5) {
                    mixedIdList.remove(Integer.valueOf(strings.get(position).id));
                }
            } else {
                strings.get(position).setCheck(true);
                numberTypeList.add(strings.get(position).name);
                operationIdList.add(strings.get(position).id);

                if (strings.get(position).id >= 5) {
                    mixedIdList.add(stringOperation.get(position).id);
                }
            }
            operaionAdapter.setSelectPosition(strings);
        });


        final AlertDialog dialog = builder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        btn_ok.setOnClickListener(view12 -> {

            hideKeyboard();

            if (!isInteger && !isDecimal) {

                layout_select_type.setVisibility(View.GONE);
                operationSet.clear();
                storeSetModels.clear();
                questionTypeList.clear();
                layout_select_set.setVisibility(View.GONE);
                layout_select_type.setVisibility(View.GONE);
            } else {

                if (isSetEnabled()) {
                    layout_select_type.setVisibility(View.VISIBLE);
                } else {
                    layout_select_type.setVisibility(View.GONE);
                    if (isMultiplication()) {
                        layout_select_set.setVisibility(View.VISIBLE);
                    }
                }

            }


            if (numberTypeList.size() <= 0) {
                layout_select_type.setVisibility(View.GONE);

                storeSetModels.clear();
                questionTypeList.clear();
                layout_select_set.setVisibility(View.GONE);
                layout_select_type.setVisibility(View.GONE);

                if (isMultiplication()) {
                    layout_select_set.setVisibility(View.VISIBLE);
                } else {
                    operationSet.clear();
                }
            }


            setTexts();
            dialog.dismiss();
        });

        btn_cancel.setOnClickListener(view12 -> {
            hideKeyboard();
            if (!isInteger && !isDecimal) {
                layout_select_type.setVisibility(View.GONE);
                operationSet.clear();
                storeSetModels.clear();
                questionTypeList.clear();
                layout_select_set.setVisibility(View.GONE);
                layout_select_type.setVisibility(View.GONE);
            } else {
                if (isSetEnabled()) {
                    layout_select_type.setVisibility(View.VISIBLE);
                } else {
                    layout_select_type.setVisibility(View.GONE);
                    if (isMultiplication()) {
                        layout_select_set.setVisibility(View.VISIBLE);
                    }
                }

            }


            if (numberTypeList.size() <= 0) {
                layout_select_type.setVisibility(View.GONE);

                storeSetModels.clear();
                questionTypeList.clear();
                layout_select_set.setVisibility(View.GONE);
                layout_select_type.setVisibility(View.GONE);

                if (isMultiplication()) {
                    layout_select_set.setVisibility(View.VISIBLE);
                } else {
                    operationSet.clear();
                }
            }


            setTexts();
            dialog.dismiss();
        });

    }

    public void showOperationTypeDialog() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_operation, null);
        builder.setView(view);

        TextView btn_ok, btn_cancel, textView;

        btn_ok = view.findViewById(R.id.btn_ok);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        textView = view.findViewById(R.id.textView);

        textView.setText(getString(R.string.select_question_type));
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        List<OperationModel> strings;

        strings = getAllOperationTypeList(this, numberTypeList);

        for (int i = 0; i < questionTypeList.size(); i++) {
            for (int j = 0; j < strings.size(); j++) {
                if (questionTypeList.get(i).equals(strings.get(j).name)) {
                    strings.get(j).setCheck(true);
                }
            }
        }
        OperaionAdapter operaionAdapter = new OperaionAdapter(this, strings);
        recyclerView.setAdapter(operaionAdapter);

        operaionAdapter.setClickListener(position -> {
            if (strings.get(position).isCheck) {
                strings.get(position).setCheck(false);
                questionTypeList.remove(String.valueOf(strings.get(position).name));
            } else {
                strings.get(position).setCheck(true);
                questionTypeList.add(strings.get(position).name);

            }

            operaionAdapter.setSelectPosition(strings);

        });


        final AlertDialog dialog = builder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        btn_ok.setOnClickListener(view12 -> {
            hideKeyboard();

            for (int i = 0; i < operationList.size(); i++) {
                if (operationList.get(i) == 1 || operationList.get(i) == 2) {
                    if (questionTypeList.size() > 0) {
                        layout_select_set.setVisibility(View.VISIBLE);
                    } else {
                        operationSet.clear();
                        storeSetModels.clear();
                        layout_select_set.setVisibility(View.GONE);
                    }
                }
            }
            setTexts();

            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(view12 -> {
            hideKeyboard();

            for (int i = 0; i < operationList.size(); i++) {
                if (operationList.get(i) == 1 || operationList.get(i) == 2) {
                    if (questionTypeList.size() > 0) {
                        layout_select_set.setVisibility(View.VISIBLE);
                    } else {
                        operationSet.clear();
                        storeSetModels.clear();
                        layout_select_set.setVisibility(View.GONE);
                    }
                }
            }
            setTexts();

            dialog.dismiss();
        });


    }

    public void clearMixedValue() {


        dummmyOperation.clear();
        dummmyOperationId.clear();
        for (int i = 0; i < stringOperation.size(); i++) {
            dummmyOperation.add(stringOperation.get(i).name);
            dummmyOperationId.add(stringOperation.get(i).id);
        }


        List<Integer> storeOperationId = new ArrayList<>(operationIdList);
        operationIdList.clear();
        List<String> storeNumberType = new ArrayList<>(numberTypeList);
        numberTypeList.clear();
        mixedIdList.clear();


        for (int i = 0; i < storeNumberType.size(); i++) {
            if (dummmyOperation.contains(storeNumberType.get(i))) {
                numberTypeList.add(storeNumberType.get(i));
            }

        }


        for (int i = 0; i < storeOperationId.size(); i++) {
            if (dummmyOperationId.contains(storeOperationId.get(i))) {
                operationIdList.add(storeOperationId.get(i));

                if (storeOperationId.get(i) >= 5) {
                    mixedIdList.add(storeOperationId.get(i));
                }

            }

        }


        setTexts();
    }

    public boolean isMultiplication() {

        boolean isMultiplication = false;
        for (int j = 0; j < numberTypeList.size(); j++) {
            if (getString(R.string.multiplication).contains(numberTypeList.get(j))) {
                isMultiplication = true;
                break;
            }
        }
        return isMultiplication;
    }

    public boolean isSetEnabled() {

        boolean isMultiplication = false;
        for (int j = 0; j < numberTypeList.size(); j++) {
            if (getString(R.string.addition).contains(numberTypeList.get(j)) || getString(R.string.subtraction).contains(numberTypeList.get(j))
                    || getString(R.string.division).contains(numberTypeList.get(j))) {
                isMultiplication = true;
                break;
            }
        }
        return isMultiplication;
    }


    public void showCategoryDialog() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        setDefaultLanguage(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_operation_type, null);
        builder.setView(view);

        final CheckBox btn_integer, btn_decimal, btn_fraction, btn_mixed;
        TextView btn_ok, btn_cancel;

        btn_integer = view.findViewById(R.id.btn_integer);
        btn_decimal = view.findViewById(R.id.btn_decimal);
        btn_mixed = view.findViewById(R.id.btn_mixed);
        btn_fraction = view.findViewById(R.id.btn_fraction);
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        btn_fraction.setVisibility(View.GONE);

        for (int i = 0; i < operationList.size(); i++) {

            if (operationList.get(i) == 1) {
                btn_integer.setChecked(true);
            }
            if (operationList.get(i) == 2) {
                btn_decimal.setChecked(true);
            }
            if (operationList.get(i) == 4) {
                btn_fraction.setChecked(true);
            }
            if (operationList.get(i) == 3) {
                btn_mixed.setChecked(true);
            }

        }

        btn_integer.setOnCheckedChangeListener((compoundButton, b) -> {
            isInteger = b;
            if (b) {
                operationList.add(1);
            } else {
                operationList.remove(Integer.valueOf(1));
            }
            if (isMixed) {
                if (isInteger || isDecimal) {
                    Constant.getIntegerOrMixedOpertaion(this, stringOperation);
                } else {
                    Constant.getMixedOpertaion(this, stringOperation);
                }
            } else {
                Constant.getIntegerOpertaion(this, stringOperation);
            }
            clearMixedValue();
        });

        btn_decimal.setOnCheckedChangeListener((compoundButton, b) -> {
            isDecimal = b;
            if (b) {
                operationList.add(2);
            } else {
                operationList.remove(Integer.valueOf(2));
            }
            if (isMixed) {
                if (isInteger || isDecimal) {
                    Constant.getIntegerOrMixedOpertaion(this, stringOperation);
                } else {
                    Constant.getMixedOpertaion(this, stringOperation);
                }
            } else {
                Constant.getIntegerOpertaion(this, stringOperation);
            }
            clearMixedValue();

        });

        btn_fraction.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                operationList.add(3);
            } else {
                operationList.remove(Integer.valueOf(3));
            }
        });

        btn_mixed.setOnCheckedChangeListener((compoundButton, b) -> {
            isMixed = b;
            if (b) {
                operationList.add(3);
            } else {
                operationList.remove(Integer.valueOf(3));

            }

            if (isMixed) {
                if (isInteger || isDecimal) {
                    Constant.getIntegerOrMixedOpertaion(this, stringOperation);
                } else {
                    Constant.getMixedOpertaion(this, stringOperation);
                }
            } else {
                Constant.getIntegerOpertaion(this, stringOperation);
            }

            clearMixedValue();


        });


        final AlertDialog dialog = builder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        btn_ok.setOnClickListener(view12 -> {
            hideKeyboard();


            if (operationList.size() > 0) {
                layout_select_operation.setVisibility(View.VISIBLE);
                if (isMixed) {
                    if (!isInteger && !isDecimal) {
                        operationSet.clear();
                        storeSetModels.clear();
                        questionTypeList.clear();
                        layout_select_set.setVisibility(View.GONE);
                        layout_select_type.setVisibility(View.GONE);
                    }
                }

            } else {
                operationSet.clear();
                storeSetModels.clear();
                questionTypeList.clear();
                numberTypeList.clear();
                layout_select_operation.setVisibility(View.GONE);
                layout_select_set.setVisibility(View.GONE);
                layout_select_type.setVisibility(View.GONE);
            }


            setTexts();
            dialog.dismiss();
        });

        btn_cancel.setOnClickListener(view12 -> {
            hideKeyboard();


            if (operationList.size() > 0) {
                layout_select_operation.setVisibility(View.VISIBLE);
                if (isMixed) {
                    if (!isInteger && !isDecimal) {
                        operationSet.clear();
                        storeSetModels.clear();
                        questionTypeList.clear();
                        layout_select_set.setVisibility(View.GONE);
                        layout_select_type.setVisibility(View.GONE);
                    }
                }

            } else {
                operationSet.clear();
                storeSetModels.clear();
                questionTypeList.clear();
                numberTypeList.clear();
                layout_select_operation.setVisibility(View.GONE);
                layout_select_set.setVisibility(View.GONE);
                layout_select_type.setVisibility(View.GONE);
            }


            setTexts();
            dialog.dismiss();
        });
    }


    public void showSetDialog() {

        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_type, null);
        builder.setView(view);

        TextView btn_ok, btn_cancel;
        final RecyclerView recyclerView;

        btn_ok = view.findViewById(R.id.btn_ok);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        workSheetSetAdapter = new WorkSheetSetAdapter(WorkSheetActivity.this, setModels);
        recyclerView.setAdapter(workSheetSetAdapter);
        workSheetSetAdapter.setClickListener(this);
        recyclerView.scrollToPosition(selected_position);

        setDialog = builder.create();
        setDialog.show();
        Objects.requireNonNull(setDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        btn_cancel.setOnClickListener(view12 -> setDialog.dismiss());
        btn_ok.setOnClickListener(view1 -> {
            hideKeyboard();
            setDialog.dismiss();
        });

    }


    @Override
    public void itemClick(int position, int id, String category) {
        if (operationSet.size() > 0) {
            if (operationSet.contains(setModels.get(position).title)) {
                operationSet.remove(setModels.get(position).title);
                for (int i = 0; i < storeSetModels.size(); i++) {
                    if ((storeSetModels.get(i).category.equalsIgnoreCase(category) && storeSetModels.get(i).id == id)) {
                        storeSetModels.remove(i);
                    }
                }
            } else {
                storeSetModels.add(new StoreSetModel(id, setModels.get(position).title, category));
                operationSet.add(setModels.get(position).title);
            }
        } else {
            storeSetModels.add(new StoreSetModel(id, setModels.get(position).title, category));
            operationSet.add(setModels.get(position).title);
        }
        setTexts();
        workSheetSetAdapter.notifyDataSetChanged();
    }


    public class GeneratePDF extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            createPDF();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new AddWaterMark().execute();
        }
    }
//    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
//        PdfReader reader = new PdfReader(src);
//        int n = reader.getNumberOfPages();
//        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
//        stamper.setRotateContents(false);
//        // text watermark
//        Font f = new Font(Font.FontFamily.HELVETICA, 30);
//        Phrase p = new Phrase("My watermark (text)", f);
//        // image watermark
//        Image img = Image.getInstance(IMG);
//        float w = img.getScaledWidth();
//        float h = img.getScaledHeight();
//        // transparency
//        PdfGState gs1 = new PdfGState();
//        gs1.setFillOpacity(0.5f);
//        // properties
//        PdfContentByte over;
//        Rectangle pagesize;
//        float x, y;
//        // loop over every page
//        Log.e("img===",""+n);
//        for (int i = 1; i <= n; i++) {
//            pagesize = reader.getPageSize(i);
//            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
//            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
//            over = stamper.getOverContent(i);
//            over.saveState();
//            over.setGState(gs1);
//            Log.e("img===",""+img);
//            if (i % 2 == 1)
//                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
//            else
//                over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
//            over.restoreState();
//        }
//        stamper.close();
//        reader.close();
//    }

    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        stamper.setRotateContents(true);
        Font f = new Font(Font.FontFamily.HELVETICA, 30);
        Phrase p = new Phrase(edt_watermark.getText().toString(), f);
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.2f);
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        for (int i = 1; i <= n; i++) {
            pagesize = reader.getPageSizeWithRotation(i);

            Log.e("n======", "" + pagesize);
//            x = (pagesize.getLeft() + pagesize.getRight());
//            x = (pagesize.getRight()) / 2;
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
//            y = (pagesize.getBottom()) / 2;
//            y = (pagesize.getTop() + pagesize.getBottom());
            over = stamper.getOverContent(i);
            over.saveState();
            over.setGState(gs1);
            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 45f);
            over.restoreState();
        }
        stamper.close();
        reader.close();
    }


    public class AddWaterMark extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                File root = getExternalFilesDir(null);
                assert root != null;
                String path = root.getAbsolutePath() + Constant.PDF_FOLDER_NAME;

                File file = new File(path);

                Log.e("path===", "" + path);
                manipulatePdf(FILE, file.getAbsolutePath() + pdfName + 1 + pdf_format);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            File root = getExternalFilesDir(null);
            assert root != null;
            String path = root.getAbsolutePath() + Constant.PDF_FOLDER_NAME;

            File dir = new File(path);

            if (dir.exists()) {
                File from = new File(dir, pdfName + 1 + pdf_format);
                File to = new File(dir, pdfName + pdf_format);
                if (from.exists())
                    from.renameTo(to);
                if (!TextUtils.isEmpty(to.getAbsolutePath())) {
                    Intent intent = new Intent(WorkSheetActivity.this, AllPdfActivity.class);
                    startActivity(intent);
                }
                isPdfSave = false;
                finish();
            }


            Toast.makeText(WorkSheetActivity.this, "" + getString(R.string.save_pdf), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    public void createPDF() {
        operation = getString(R.string.pdf);
        pdfName = operation + System.currentTimeMillis();

        // Create New Blank Document
        Document document = new Document(PageSize.A4);
        // Create Directory in External Storage
//        File myDir = new File(Constant.PDF_PATH);
//        if (!myDir.exists()) {
//            myDir.mkdirs();
//        }

        File root = getExternalFilesDir(null);
        assert root != null;
        String path = root.getAbsolutePath() + Constant.PDF_FOLDER_NAME;

        File file = new File(path);

        if (!file.exists()) {
            file.mkdir();
        }

        FILE = path + "/" + pdfName + pdf_format;

        // Create Pdf Writer for Writting into New Created Document
        try {
            final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(this, document.getPageSize().getTop(), document.getPageSize().getRight(), edt_school_name.getText().toString(), edt_link.getText().toString(), bitmapLogo,edt_watermark.getText().toString());
            writer.setPageEvent(event);
            // Open Document for Writting into document
            int margin = 18;
//            int marginTop = 80;
            int marginTop = 65;

//            if (check_with_answer.isChecked()) {
//                marginTop = 80;
//            }

            Log.e("getPageNumber", "" + writer.getPageNumber() + "===" + HeaderFooterPageEvent.page_number);
            document.setMargins(margin, margin, marginTop, marginTop + (margin + margin));
            document.open();

            // User Define Method
            addData(document);


            if (check_without_answer.isChecked()) {
                createQuestionSheet(document, (int) document.getPageSize().getRight());
            }
            if (check_with_answer.isChecked()) {
                createAnswerSheet(document, (int) document.getPageSize().getRight());
            }


        } catch (FileNotFoundException | DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//                // Close Document after writting all content
        document.close();

    }


    private Bitmap getBitmapFromAsset(int operation_id) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            String iconName;
            if (operation_id == 8) {
                iconName = getString(R.string.square_root_sign);
            } else {
                iconName = getString(R.string.cube_root_sign);
            }
            istr = assetManager.open(iconName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }


    public PdfPCell addFirstCell(String title, boolean isBold, boolean isRight) {
        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);


        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph();
        if (isBold) {
            p.add(new Chunk(title, boldFont));
        } else {
            p.add(new Chunk(title, normalFont));
        }

        if (isRight) {
            p.setAlignment(Element.ALIGN_RIGHT);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        } else {
            p.setAlignment(Element.ALIGN_LEFT);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }
        cell.setPaddingTop(10);
        cell.addElement(p);

        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }


    public PdfPCell addFontCell(String title, String content, boolean isRight) {
        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
        Font whiteFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK);


        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph();
        p.add(new Chunk(title, boldFont));
        p.add(new Chunk(content, normalFont));

        StringBuilder stringBuilder = new StringBuilder();


        for (int i = 0; i < TEXT_LENGTH; i++) {
            stringBuilder.append("_");
        }


        if (isRight) {

            if (content.equals(getString(R.string.space))) {
                p.add(new Chunk(stringBuilder.toString(), whiteFont));
            }
            p.setAlignment(Element.ALIGN_RIGHT);

            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        } else {
            p.setAlignment(Element.ALIGN_LEFT);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }
        cell.setPaddingTop(10);
        cell.addElement(p);

        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }


    public PdfPCell getPaddingPdfCell(PdfPCell pdfPCell) {
        pdfPCell.setPaddingLeft(-40);
        pdfPCell.setPaddingRight(-40);
        return pdfPCell;
    }

    public void addData(Document document) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        header.addCell(getPaddingPdfCell(addFontCell(getString(R.string.teacher_name_pdf) + space, edt_teacher_name.getText().toString(), false)));
        document.add(header);

        header = new PdfPTable(2);
        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        header.addCell(getPaddingPdfCell(addFontCell(getString(R.string.type_of_question_pdf) + space, getString(R.string.multi_operation_pdf), false)));


        PdfPTable pdfPTable = new PdfPTable(2);
        pdfPTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        pdfPTable.setWidths(new int[]{100, 25});
        pdfPTable.addCell(getPaddingPdfCell(addFirstCell(getString(R.string.total_question_pdf) + space, true, true)));
        PdfPCell pdfPCell = addFirstCell(edt_number_question.getText().toString(), false, true);
        pdfPCell.setPaddingRight(-15);
        pdfPTable.addCell(pdfPCell);


        header.addCell(pdfPTable);


        document.add(header);


        header = new PdfPTable(2);
        header.getDefaultCell().setNoWrap(false);
        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        header.addCell(getPaddingPdfCell(addFontCell(getString(R.string.date_pdf), space, false)));
        header.addCell(getPaddingPdfCell(addFontCell(getString(R.string.marks_pdf), space, true)));

        document.add(header);


        header = new PdfPTable(1);
        header.getDefaultCell().setNoWrap(false);
        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        header.addCell(getPaddingPdfCell(addFontCell(getString(R.string.studentName_pdf), space + getString(R.string.answer_dash), false)));

        document.add(header);
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(BaseColor.BLACK);
        lineSeparator.setOffset(-15);

        document.add(new Chunk(lineSeparator));


    }


    private void createQuestionSheet(Document document, int width) throws DocumentException {

        String equal = getString(R.string.equal);
        String answer_dash = getString(R.string.sign_question);
        PdfPCell cell;
        String addSpace;
        PdfPTable table;
        String addString;

        for (int i = 0; i < pdfModels.size(); i++) {
            PDFModel pdfModel = pdfModels.get(i);
            if (pdfModel.category_id == 3 && pdfModel.sub_category_id == 8 || pdfModel.sub_category_id == 10) {
                table = new PdfPTable(3);
                table.setTotalWidth(width);
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                table.setWidths(new int[]{40, 40, width - 80});
                cell = new PdfPCell();
                addString = (i + 1) + ".";
                addSpace = "\n";
                cell.addElement(getPragraph(addSpace + addString));
                cell.setBorder(Rectangle.NO_BORDER);

                table.addCell(cell);
                table.addCell(getPdfTable(false, pdfModel.sub_category_id));

                cell = new PdfPCell();
                cell.setVerticalAlignment(1);
                addString = space + pdfModel.question + space + equal + answer_dash;
                addSpace = "\n";
                cell.setPaddingLeft(-13);
                cell.addElement(getPragraph(addSpace + addString));
                cell.addElement(getSpacePragraph("\n"));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);


                document.add(table);


                table = new PdfPTable(2);
                table.setWidths(new int[]{120, width - 100});
                cell = new PdfPCell();
                cell.setPaddingTop(-8);
                cell.addElement(getPragraph(getString(R.string.str_answer_pdf)));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell();
                cell.addElement(getAnswerLineDash());
                cell.addElement(getSpacePragraph("\n"));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                document.add(table);


            } else {
                table = new PdfPTable(1);
                cell = new PdfPCell();

                addString = (i + 1) + "." + space + space + pdfModel.question + space + equal + space + answer_dash;
                if (i != 0) {
                    addSpace = "\n";
                } else {
                    addSpace = "";
                }
                cell.addElement(getPragraph(addSpace + addString));
                cell.addElement(getSpacePragraph("\n"));

                if ((i + 1) == 1) {
                    cell.setPaddingTop(20);
                }
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                document.add(table);
                table = new PdfPTable(2);
                table.setWidths(new int[]{120, width - 100});
                cell = new PdfPCell();
                cell.setPaddingTop(-8);
                cell.addElement(getPragraph(getString(R.string.str_answer_pdf)));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell();
                cell.addElement(getAnswerLineDash());
                cell.addElement(getSpacePragraph("\n"));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                document.add(table);


            }
        }

        document.newPage();

    }

    public PdfPTable getPdfTable(boolean isLine, int operation_id) {
        PdfPTable imageTable = new PdfPTable(1);
        imageTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setColspan(2);
        pdfPCell.setVerticalAlignment(3);
        pdfPCell.setFixedHeight(15);
        pdfPCell.addElement(new Paragraph("\n\n"));
        pdfPCell.addElement(getIconImage(getBitmapFromAsset(operation_id)));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        pdfPCell.setPaddingLeft(-6);
        if (isLine) {
            LineSeparator ls = new LineSeparator();
            ls.setLineColor(BaseColor.LIGHT_GRAY);
            ls.setOffset(-9f);
            pdfPCell.addElement(ls);
        }
        imageTable.addCell(pdfPCell);
        return imageTable;
    }


    public Paragraph getPragraph(String s) {
        Font largeold = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
        Paragraph p1 = new Paragraph(s);
        p1.setFont(largeold);
        return p1;
    }

    public Paragraph getSpacePragraph(String s) {
        Font largeold = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL);
        Paragraph p1 = new Paragraph(s);
        p1.setFont(largeold);
        return p1;
    }


    public LineSeparator getAnswerSheetLine() {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.LIGHT_GRAY);
        ls.setOffset(-20);
        return ls;
    }


    public LineSeparator getAnswerLine() {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.BLACK);
        ls.setOffset(-35);
        return ls;
    }


    public LineSeparator getAnswerLineDash() {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.BLACK);
        ls.setOffset(-25);
        return ls;
    }

    public PdfPTable getPdfTableAnswer() {
        PdfPTable imageTable = new PdfPTable(1);
        imageTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setColspan(2);
        pdfPCell.setVerticalAlignment(3);
        pdfPCell.addElement(new Paragraph("\n\n"));
        pdfPCell.addElement(getIconImageAnswer());
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        pdfPCell.setPaddingLeft(-6);
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.LIGHT_GRAY);
        ls.setOffset(-9);
        pdfPCell.addElement(ls);

        imageTable.addCell(pdfPCell);
        return imageTable;
    }

    public LineSeparator getLine() {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.LIGHT_GRAY);
        ls.setOffset(-20);
        return ls;
    }


    private Bitmap getBitmapFromAssetAnswer() {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            String iconName;
            if (operation_id == 8) {
                iconName = getString(R.string.square_root_sign);
            } else {
                iconName = getString(R.string.cube_root_sign);
            }
            istr = assetManager.open(iconName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }


    public Image getIconImageAnswer() {
        Bitmap bm = getBitmapFromAssetAnswer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        Image image = null;

        try {
            image = Image.getInstance(out.toByteArray());
            image.setAlignment(Image.ALIGN_BOTTOM);
            image.setScaleToFitLineWhenOverflow(true);
        } catch (IOException | BadElementException e) {
            e.printStackTrace();
        }

        return image;
    }


    private void createAnswerSheet(Document document, int width) throws DocumentException {

        PdfPCell cell;
        PdfPTable table;
        String equal = getString(R.string.equal);
        String addString, addSpace;

        for (int i = 0; i < pdfModels.size(); i++) {
            PDFModel pdfModel = pdfModels.get(i);
            if (pdfModel.category_id == 3 && pdfModel.sub_category_id == 8 || pdfModel.sub_category_id == 10) {

                operation_id = pdfModel.sub_category_id;
                table = new PdfPTable(3);
                table.setTotalWidth(width);
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                table.setWidths(new int[]{40, 40, width - 80});

                cell = new PdfPCell();

                addString = (i + 1) + ".";
                addSpace = "\n";
                cell.addElement(getPragraph(addSpace + addString));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(getLine());
                table.addCell(cell);

                table.addCell(getPdfTableAnswer());

                cell = new PdfPCell();
                cell.setVerticalAlignment(1);
                addString = space + pdfModel.question + space + equal + space + pdfModel.answer;
                addSpace = "\n";
                cell.setPaddingLeft(-13);
                cell.addElement(getPragraph(addSpace + addString));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(getLine());
                table.addCell(cell);


            } else {
                table = new PdfPTable(1);

                addString = (i + 1) + "." + space + space + pdfModel.question + space + equal + space + pdfModel.answer;

                cell = new PdfPCell();
                if (i != 0) {
                    addSpace = "\n";
                } else {
                    addSpace = "";
                }
                cell.addElement(getPragraph(addSpace + addString));
                cell.addElement(getAnswerSheetLine());
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);


            }

            document.add(table);
        }

        document.newPage();

    }


    public Image getIconImage(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        Image image = null;
        try {
            image = Image.getInstance(out.toByteArray());
            image.setAlignment(Image.ALIGN_BOTTOM);
            image.setScaleToFitLineWhenOverflow(true);
        } catch (IOException | BadElementException e) {
            e.printStackTrace();
        }
        return image;
    }
}

