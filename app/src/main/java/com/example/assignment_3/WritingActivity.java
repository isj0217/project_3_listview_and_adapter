package com.example.assignment_3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WritingActivity extends AppCompatActivity {

    private ImageButton mBtn_writing_save;
    private EditText mEt_writing_memo;
    private boolean flag_popUpShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        flag_popUpShown = false;
        initSaveButton();

        mEt_writing_memo = findViewById(R.id.et_memo);

    }

    @Override
    protected void onStart() {
        openExistingMemo();
        super.onStart();
    }

    @Override
    protected void onResume() {
//        mEt_writing_memo.setSelection(mEt_writing_memo.getText().length());
//        mEt_writing_memo.requestFocus();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onRestart() {
        if (!flag_popUpShown) {
            flag_popUpShown = true;
            buildPopup();
        }
        super.onRestart();
    }

    private void initSaveButton() {
        mBtn_writing_save = findViewById(R.id.btn_save);
        mBtn_writing_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemoAndGoMain();
            }
        });
    }

    public void buildPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
        builder.setTitle("작성중이던 메모가 있습니다").setMessage("이어서 작성하시겠습니까?").setCancelable(true);
        builder.setPositiveButton("저장하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag_popUpShown = false;
                saveMemoAndGoMain();
            }
        }).setNegativeButton("이어서 작성하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag_popUpShown = false;
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    public void saveMemoAndGoMain() {
        Memo newMemo = new Memo();
        newMemo.setContents(getContents());
        newMemo.setDate(getDate());

        Intent intent = new Intent(WritingActivity.this, MainActivity.class);
        if (!newMemo.getContents().equals("")) {
            intent.putExtra("newMemo", newMemo);
            Toast.makeText(this, "저장됨", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "입력한 내용이 없어 저장되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
        startActivity(intent);
        finish();
    }

    //    지금까지 EditText에 작성한 내용을 String으로 return해주는 메서드
    public String getContents() {
        return mEt_writing_memo.getText().toString();
    }

    //    지금 시간을 String으로 return해주는 메서드
    public String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일");
        return dateFormat.format(date);
    }

    private void openExistingMemo() {

        Intent intent = getIntent();
        String existingMemo = (String) intent.getStringExtra("existingMemo");
        if (existingMemo != null) {
            mEt_writing_memo.setText(existingMemo);
        }
    }

    @Override
    public void onBackPressed() {
        saveMemoAndGoMain();
    }
}