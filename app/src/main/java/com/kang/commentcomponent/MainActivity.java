package com.kang.commentcomponent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kang.commentcomponent.demo2.BaseInfo;
import com.kang.commentcomponent.dialog.CommentBottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textView).setOnClickListener(v -> {

            BaseInfo baseInfo = new BaseInfo("0", "002945");
            CommentBottomSheetDialog dialog = new CommentBottomSheetDialog(MainActivity.this, baseInfo);
            dialog.show();
        });
    }
}