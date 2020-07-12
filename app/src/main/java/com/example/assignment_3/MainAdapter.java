package com.example.assignment_3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainAdapter extends BaseAdapter {

    public MainAdapter(ArrayList<Memo> data, Context context) {
        mMemoList = data;
        mContext = context;
        mInflate = LayoutInflater.from(context);
    }

    private ArrayList<Memo> mMemoList;
    private Context mContext;
    private LayoutInflater mInflate;

    @Override
    public int getCount() {
        return mMemoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMemoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //inflate
        View view = mInflate.inflate(R.layout.item_memo_layout, parent, false);

        //memo를 가져온다.
        final Memo memo = mMemoList.get(position);

        //view 바인딩
        TextView btnSummary = view.findViewById(R.id.btn_summaryToBeFilled);
        TextView tvDate = view.findViewById(R.id.tv_dateToBeFilled);
        CheckBox checkBox = view.findViewById(R.id.chk_memo);

        //data 바인딩
        btnSummary.setText(memo.getContents());
        tvDate.setText(memo.getDate());

        //여기서 checkBox문제 해결!!
        checkBox.setChecked(memo.isChecked());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memo.isChecked()) {
                    memo.setChecked(false);
                } else {
                    memo.setChecked(true);
                }
            }
        });

        LinearLayout touchAreaToLoadMemo = (LinearLayout) view.findViewById(R.id.touchAreaToLoadMemo);
        touchAreaToLoadMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = mMemoList.get(position).getContents();
                Intent intent = new Intent(mContext, WritingActivity.class);
                intent.putExtra("existingMemo", contents);
                mContext.startActivity(intent);
                mMemoList.remove(position);
            }
        });

        LinearLayout touchAreaToLoadMemo_2 = (LinearLayout) view.findViewById(R.id.touchAreaToLoadMemo_2);
        touchAreaToLoadMemo_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = mMemoList.get(position).getContents();
                Intent intent = new Intent(mContext, WritingActivity.class);
                intent.putExtra("existingMemo", contents);
                mContext.startActivity(intent);
                mMemoList.remove(position);
            }
        });

        //뷰 반환
        return view;
    }
}
