package com.dabai.ChangeModel2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.ChangeModel2.utils.shell;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout cons;
    Context context;

    TextView text_info, title;
    ImageButton bu_cloud, bu_shutdown;

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

        setListener();

    }

    public static final String ACTION_REBOOT = "android.intent.action.REBOOT";
    public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";

/**
     * 设置监听
     */
    private void setListener() {
        bu_shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(MainActivity.this)
                        .title("提示")
                        .content("确定重启嘛？")
                        .positiveText("确定")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String a[]={"reboot"};
                                new shell().execCommand(a,true);
                            }
                        })
                        .negativeText("取消")
                        .show();
            }
        });
    }

    /**
     * 初始化控件 & 变量
     */
    private void init_val() {
        cons = findViewById(R.id.cons);
        context = getApplicationContext();
        text_info = findViewById(R.id.text_info);
        title = findViewById(R.id.title);
        bu_cloud = findViewById(R.id.bu_cloud);
        bu_shutdown = findViewById(R.id.bu_shutdown);
    }
}
