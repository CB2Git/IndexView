package com.enjoywatch.indexview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.enjoywatch.library.IndexView;

public class MainActivity extends AppCompatActivity implements IndexView.OnIndexChangeListener {
    private IndexView mIndexView;
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = (TextView) findViewById(R.id.tv);

        mIndexView = (IndexView) findViewById(R.id.indexView);
        //设置选中第一个
        mIndexView.setSelectIndex(1);
        //设置索引切换监听，Activity实现OnIndexChangeListener接口
        mIndexView.setOnIndexChangeListener(this);
        // 设置不显示悬浮窗
        // mIndexView.setShowPopup(false);
    }

    @Override
    public void OnIndexChange(String index, int postion) {
        mTv.setText("当前选中:" + index.charAt(postion));
    }
}
