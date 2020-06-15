package com.wachi.hesabu.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wachi.hesabu.R;
import com.wachi.hesabu.adapter.PDFAdapter;
import com.wachi.hesabu.utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AllPdfActivity extends AppCompatActivity implements PDFAdapter.ItemClick {
    PDFAdapter pdfAdapter;
    List<String> pdfPathList;
    public static List<String> deletePathList;
    MenuItem item_delete;
    String pdfPath;
    File[] files;
    int position;
    RecyclerView recyclerView;
    boolean isDelete;
    CheckBox check_box;
    SharedPreferences mSharedPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPrefs.getBoolean("enablethemeMode", false) == true) {
            setTheme(R.style.Okenwa_Black);
        }
        setContentView(R.layout.activity_all_pdf);
        init();
    }

    private void getFromSDCard() {
        pdfPathList.clear();


        File root = getExternalFilesDir(null);
        assert root != null;
        String path = root.getAbsolutePath() + Constant.PDF_FOLDER_NAME;

        File file = new File(path);
        if (file.isDirectory()) {
            files = file.listFiles();
            assert files != null;
            for (File file1 : files) {
                pdfPathList.add(file1.getAbsolutePath());
            }
        }
    }


    private void init() {
        pdfPathList = new ArrayList<>();
        deletePathList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(view -> backIntent());

        TextView txt_header = findViewById(R.id.toolbar_title);
        check_box = findViewById(R.id.check_box);
        txt_header.setText(getString(R.string.show_worksheet));


        checkAndRunPdf();


    }

    public void setAdapter() {
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        getFromSDCard();
        if (pdfPathList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }

        Collections.reverse(pdfPathList);

        pdfAdapter = new PDFAdapter(getApplicationContext(), pdfPathList);
        pdfAdapter.setListener(this);

        recyclerView.setAdapter(pdfAdapter);


        check_box.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                deletePathList.clear();
                deletePathList.addAll(pdfPathList);
                pdfAdapter.notifyDataSetChanged();
            } else {
                deletePathList.clear();
                pdfAdapter.notifyDataSetChanged();
            }
        });


    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
        }, 111);
    }


    public void openPdf() {
        if (!TextUtils.isEmpty(pdfPath)) {
            Uri path = FileProvider.getUriForFile(getApplicationContext(), getApplication().getApplicationContext().getPackageName() + ".provider", new File(pdfPath));
            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfOpenintent.setDataAndType(path, "application/pdf");
            try {
                startActivity(pdfOpenintent);
            } catch (ActivityNotFoundException ignored) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_app_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkAndRunPdf() {
        if (!checkPermission()) {
            requestPermission();
        } else {
            setAdapter();
        }
    }

    public boolean checkPermission() {
        int read;
        read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 111) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setAdapter();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        backIntent();
    }

    public void doneDeleteAction() {
        if (item_delete != null) {
            item_delete.setVisible(false);
        }
        pdfAdapter.isDelete(false);
        isDelete = false;
        deletePathList.clear();
        check_box.setChecked(false);
        check_box.setVisibility(View.GONE);
    }

    public void backIntent() {
        if (isDelete) {
            doneDeleteAction();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        item_delete = menu.findItem(R.id.btn_delete);
        if (item_delete != null) {
            item_delete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deletePathList.clear();
                pdfAdapter.notifyDataSetChanged();
                isDelete = true;
                pdfAdapter.isDelete(true);
                if (item_delete != null) {
                    item_delete.setVisible(true);
                }
                return true;

            case R.id.btn_delete:
                new DeleteFile().execute();
                return true;
            case R.id.action_select_all:


                isDelete = true;
                pdfAdapter.isDelete(true);
                if (item_delete != null) {
                    item_delete.setVisible(true);
                }
                deletePathList.clear();
                deletePathList.addAll(pdfPathList);
                pdfAdapter.notifyDataSetChanged();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class DeleteFile extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AllPdfActivity.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            for (int i = 0; i < deletePathList.size(); i++) {
                File file1 = new File(deletePathList.get(i));
                file1.delete();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            isDelete = false;
            doneDeleteAction();
            checkAndRunPdf();
        }
    }


    @Override
    public void itemClick(int position) {
        pdfPath = pdfPathList.get(position);
        openPdf();
    }

    @Override
    public void setSelectPath(boolean isCheck, String path) {

        if (deletePathList.contains(path)) {
            deletePathList.remove(path);
        } else {
            deletePathList.add(path);
        }
        pdfAdapter.notifyDataSetChanged();
    }
}
