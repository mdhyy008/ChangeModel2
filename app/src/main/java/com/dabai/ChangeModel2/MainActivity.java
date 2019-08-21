package com.dabai.ChangeModel2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout cons;
    Context context;

    TextView text_info, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * 初始化程序
     */
    private void init() {

        init_val();

        title.setText("" + Build.MODEL);
        title.setSelected(true);

        String info_txt = "model : " + Build.MODEL +
                "\nbrand : " + Build.BRAND +
                "\nmanufacturer : " + Build.MANUFACTURER +
                "\nproduct : " + Build.PRODUCT +
                "\ndevice : " + Build.DEVICE +
                "\n\nandroid version : " + Build.VERSION.RELEASE +
                "\nSDK version : " + Build.VERSION.SDK_INT;

        text_info.setText(info_txt);


    }

    /**
     * 初始化控件 & 变量
     */
    private void init_val() {
        cons = findViewById(R.id.cons);
        context = getApplicationContext();
        text_info = findViewById(R.id.text_info);
        title = findViewById(R.id.title);
    }
}
