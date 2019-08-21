package com.dabai.ChangeModel2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.ChangeModel2.utils.shell;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout cons;
    Context context;

    TextView text_info, title;
    ImageButton bu_cloud, bu_shutdown;
    ImageView img_root, img_magisk;
    boolean isroot,ismagisk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        check_root();
        check_magisk();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 检查 magisk
     */
    private void check_magisk() {
        ismagisk = is_Magisk();
        if (ismagisk){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img_magisk.setImageDrawable(getDrawable(R.drawable.ok));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img_magisk.setImageDrawable(getDrawable(R.drawable.err));
            }
        }
    }


    /**
     * 检查root
     */
    private void check_root() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                 isroot = is_Root();
                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isroot) {

                            bu_shutdown.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                img_root.setImageDrawable(getDrawable(R.drawable.ok));
                            }
                        } else {
                            bu_shutdown.setVisibility(View.GONE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                img_root.setImageDrawable(getDrawable(R.drawable.err));
                            }
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化程序
     */
    private void init() {

        init_val();

        init_info();

        setListener();

    }

    /**
     * 加载 配置信息
     */
    private void init_info() {

        new Thread(new Runnable() {
            private String info_txt;

            @Override
            public void run() {

                info_txt = "model : " + Build.MODEL +
                        "\nbrand : " + Build.BRAND +
                        "\nmanufacturer : " + Build.MANUFACTURER +
                        "\nproduct : " + Build.PRODUCT +
                        "\ndevice : " + Build.DEVICE +
                        "\n\nandroid version : " + Build.VERSION.RELEASE +
                        "\nSDK version : " + Build.VERSION.SDK_INT;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText("" + Build.MODEL);
                        title.setSelected(true);

                        text_info.setText(info_txt);
                    }
                });

            }
        }).start();


    }


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
                                String a[] = {"reboot"};
                                new shell().execCommand(a, true);
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
        img_magisk = findViewById(R.id.img_magisk);
        img_root = findViewById(R.id.img_root);
    }


    /**
     * 运行shell  返回 结果
     *
     * @param cmdStr
     * @return
     */
    public static boolean exeCmdWithRoot(String cmdStr) {
        if (null == cmdStr || "".equals(cmdStr)) {
            return false;
        }
        Process process = null;
        String[] cmds = new String[]{cmdStr};
        try {
            process = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String cmd : cmds) {
                os.write(new String(cmd + "\n").getBytes());
            }
            os.flush();
            os.close();
        } catch (Exception e) {
            String message = "executeCmd: " + cmdStr + " error: " + e.toString();
        }

        if (process != null) {
            try {
                int status = process.waitFor();
                process.getOutputStream().close();
                process.getErrorStream().close();
                process.getInputStream().close();
                //这里是关键代码，其实只有status为1的时候是没有权限，这里个人直接把所有运行shell命令的异常都归为失败
                if (0 == status) {
                    return true;
                } else {
                    return false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取 是否有root权限
     */
    public boolean is_Root() {
        return exeCmdWithRoot("su");
    }
    /**
     * 获取 是否有magisk权限
     */
    public boolean is_Magisk() {
        return new File("/sbin/magisk").exists();
    }
}
