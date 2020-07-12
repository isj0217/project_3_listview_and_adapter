package com.example.assignment_3;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String SETTINGS_MEMOLIST_JSON = "settings_memo_list_json";

    private long mBackKeyPressedTime = 0;
    private Toast mToast;

    private ArrayList<Memo> mMemoList;
    private MainAdapter mMainAdapter;
    private ListView mLvMemoList;

    private ImageButton mBtn_newNote, mBtn_delete;

    private SharedPreferences sharedPreferences;

    private Gson gson = new GsonBuilder().create();

    private boolean flag_deleteMoreThanOne;
    private boolean flag_popUpShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        receiveNewMemo();
    }

    @Override
    protected void onStart() {
        setMemoArrayPref();
        super.onStart();
    }

    @Override
    protected void onPause() {
        setMemoArrayPref();
        super.onPause();
    }

    @Override
    protected void onStop() {
        setMemoArrayPref();
        super.onStop();
    }

    private void init() {
        initArrayList();
        initListView();
        initNewNoteButton();
        initDeleteButton();
    }

    private void initArrayList() {
        mMemoList = new ArrayList<>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // MemoList가 없다면 최초로 생성하고 이미 있다면 가져온다
        if (sharedPreferences.getString(SETTINGS_MEMOLIST_JSON, null) == null) {

            for (int i = 0; i < 12; i++) {
                mMemoList.add(new Memo());
            }

            for (int i = 0; i < mMemoList.size(); i++) {
                mMemoList.get(i).setContents("Test용 메모 " + (i + 1));
                mMemoList.get(i).setDate((i + 1) + "월 " + (i + 1) + "일");
            }

            setMemoArrayPref();
        } else {
            getMemoArrayPref();
        }
    }

    private void initListView() {
        mLvMemoList = findViewById(R.id.lv_main_memo_list);

        mMainAdapter = new MainAdapter(mMemoList, this);
        mLvMemoList.setAdapter(mMainAdapter);
    }

    private void initNewNoteButton() {
        mBtn_newNote = findViewById(R.id.btn_addNewNote);
        mBtn_newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WritingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initDeleteButton() {
        mBtn_delete = findViewById(R.id.btn_delete);
        mBtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_deleteMoreThanOne = false;
                for (int i = mMemoList.size() - 1; i >= 0; i--) {
                    if (mMemoList.get(i).isChecked()) {
                        flag_deleteMoreThanOne = true;
                    }
                }
                if (flag_deleteMoreThanOne) {
                    buildDeleteWarningPopup();
                } else {
                    Toast.makeText(MainActivity.this, "먼저 삭제할 노트를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void buildDeleteWarningPopup() {
        if (!flag_popUpShown) {
            flag_popUpShown = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("선택한 메모를 모두 삭제하시겠습니까?").setMessage("한 번 삭제된 메모는 되돌릴 수 없습니다.").setCancelable(true);
            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = mMemoList.size() - 1; i >= 0; i--) {
                        if (mMemoList.get(i).isChecked()) {
                            mMemoList.remove(i);
                        }
                    }
                    restartActivity(MainActivity.this);
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
        flag_popUpShown = false;
    }

    private void restartActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.finish();
    }

    private void receiveNewMemo() {

        Intent intent = getIntent();
        Memo newMemo = (Memo) intent.getSerializableExtra("newMemo");
        if (newMemo != null) {
            mMemoList.add(0, newMemo);
        }
    }

    private void setMemoArrayPref() {                                                                   // ArrayList<Memo> 형태의 mMemoList를 JSON 형태로 직렬화
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(mMemoList);                                                           // GSON을 이용하여 ArrayList를 JSON 형태로 만들어서 set한다.
        editor.putString(SETTINGS_MEMOLIST_JSON, json);                                                 // 이때 GSON의 toJSON 메소드를 이용한다.
        editor.apply();
    }

    private void getMemoArrayPref() {                                                                   // JSON 형태의 mMemoList를 자바의 객체인 ArrayList<Memo> 형태로 역직렬화
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = sharedPreferences.getString(SETTINGS_MEMOLIST_JSON, null);                // JSON 형태로 저장되어있는 String을 불러온다.
        Type type = new TypeToken<ArrayList<Memo>>() {
        }.getType();                                    // TypeToken을 사용하여 'Memo' 타입을 얻어온다.
        if (json != null) {
            mMemoList = gson.fromJson(json, type);
        }                                                                          // JSON 형태로 직렬화된 String을 다시 Memo 타입으로 바꾸어 mMemoList에 넣어준다.
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > mBackKeyPressedTime + 2000) {
            mBackKeyPressedTime = System.currentTimeMillis();
            mToast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            mToast.show();
        } else if (System.currentTimeMillis() <= mBackKeyPressedTime + 2000) {
            finish();
            mToast.cancel();
        }
    }
}
