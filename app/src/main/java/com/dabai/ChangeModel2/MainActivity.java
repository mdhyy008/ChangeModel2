package com.dabai.ChangeModel2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.ChangeModel2.activity.SettingsActivity;
import com.dabai.ChangeModel2.utils.Base64;
import com.dabai.ChangeModel2.utils.DabaiUtils;
import com.dabai.ChangeModel2.utils.shell;
import com.google.android.material.textfield.TextInputEditText;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout cons;
    Context context;

    TextView text_info, title;
    ImageButton bu_cloud, bu_shutdown;
    ImageView img_root, img_magisk;
    boolean isroot, ismagisk;

    CardView card_infoset, not_noper;
    CardView root_card, magisk_card;


    TextInputEditText m1, m2, m3, m4, m5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        check_root();
        check_magisk();


    }

    /**
     * 二次检查 美化ui
     */
    private void double_check() {
        if (ismagisk || isroot) {
            card_infoset.setVisibility(View.VISIBLE);
            not_noper.setVisibility(View.GONE);
        } else {
            card_infoset.setVisibility(View.GONE);
            not_noper.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 检查 magisk
     */
    private void check_magisk() {
        ismagisk = is_Magisk();
        //ismagisk = false;
        if (ismagisk) {
            magisk_card.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img_magisk.setImageDrawable(getDrawable(R.drawable.ok));
            }

            if (!new File("/sbin/.magisk/modules/changemodel2").exists()) {
                magisk_card.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    img_magisk.setImageDrawable(getDrawable(R.drawable.help2));

                    img_magisk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title("提示")
                                    .content("检测到本机已经安装magisk程序支持，但你没有挂载特定模块")
                                    .positiveText("下载模块")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            String link = "https://www.lanzous.com/b925945";
                                            try {
                                                new DabaiUtils().web_openLink(context, link);
                                            } catch (Exception e) {
                                                Toast.makeText(context, "打开链接失败!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .negativeText("取消")
                                    .show();
                        }
                    });
                }
            }

        } else {
            magisk_card.setVisibility(View.GONE);
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
                            root_card.setVisibility(View.VISIBLE);

                            bu_shutdown.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                img_root.setImageDrawable(getDrawable(R.drawable.ok));
                            }
                        } else {
                            root_card.setVisibility(View.GONE);

                            bu_shutdown.setVisibility(View.GONE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                img_root.setImageDrawable(getDrawable(R.drawable.err));
                            }


                            img_root.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new MaterialDialog.Builder(MainActivity.this)
                                            .title("提示")
                                            .content("本程序未获得ROOT权限，需要ROOT的功能已经禁用，由于机型不同，ROOT的方法也是不一样的，点击社区支持查看部分机型的ROOT教程")
                                            .positiveText("社区支持")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    String link = "https://dabai2017.gitee.io/blog/2019/08/22/机型修改社区支持/";
                                                    try {
                                                        new DabaiUtils().web_openLink(context, link);
                                                    } catch (Exception e) {
                                                        Toast.makeText(context, "打开链接失败!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .negativeText("取消")
                                            .show();
                                }
                            });


                        }
                        double_check();
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

        bu_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交到社区

                if (get_sharedString("firstsend", "yes").equals("yes")) {

                    new MaterialDialog.Builder(MainActivity.this)
                            .title("提示")
                            .content("你要把本机机型代码提交到代码库嘛？点击确定申请，等待审核通过即可")
                            .positiveText("确定")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    feedback("[" + Build.MODEL + "]提交机型代码", getModelCode());

                                }
                            })
                            .negativeText("取消")
                            .show();

                } else {
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("提示")
                            .content("一个设备只能提交一次哦")
                            .positiveText("确认")
                            .show();
                }

            }
        });
    }


    public void feedback(final String title, final String text) {
        new Thread(new Runnable() {

            private int qucode;

            @Override
            public void run() {
                try {

                    URL url = new URL("https://sc.ftqq.com/SCU35649Tec88ecad70ac8f2375a6c5a6e323c8425be9602402c5b.send?text=" + title + "&desp=" + text);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    final int code = urlConnection.getResponseCode();
                    qucode = code;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 200) {
                                Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
                                set_sharedString("firstsend", "no");
                            } else {
                                Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "应该是没有网络吧", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
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
        card_infoset = findViewById(R.id.infoset_card);
        not_noper = findViewById(R.id.not_notper);
        root_card = findViewById(R.id.root_card);
        magisk_card = findViewById(R.id.magisk_card);

        m1 = findViewById(R.id.m1);
        m2 = findViewById(R.id.m2);
        m3 = findViewById(R.id.m3);
        m4 = findViewById(R.id.m4);
        m5 = findViewById(R.id.m5);
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

    public void toSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * 获取机型代码
     *
     * @return
     */
    public String getModelCode() {
        String base64String =
                Build.MODEL + "@"
                        + Build.BRAND + "@"
                        + Build.MANUFACTURER + "@"
                        + Build.PRODUCT + "@"
                        + Build.DEVICE;

        Base64 base = new Base64();
        String mDerive = base.encode(base64String);
        return mDerive;
    }

    public void into_code(View view) {


        new MaterialDialog.Builder(this)
                .title("导入机型代码")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("请输入机型代码", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String text = "" + input;
                        parseCode(text);

                    }
                })
                .positiveText("确定")
                .neutralText("剪切板导入")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String text = "" + getClipText();
                        parseCode(text);

                    }
                })
                .show();


    }

    /**
     * 解析机型码 到编辑框
     *
     * @param code
     */
    public void parseCode(String code) {


        if (code.length() < 30) {
            Toast.makeText(context, "你输入的不是正确的机型代码", Toast.LENGTH_SHORT).show();
        } else {

            try {
                Base64 base = new Base64();
                String mDerive = base.decode(code);
                String a[] = mDerive.split("@");
                m1.setText(a[0]);
                m2.setText(a[1]);
                m3.setText(a[2]);
                m4.setText(a[3]);
                m5.setText(a[4]);
            } catch (Exception e) {
                Toast.makeText(context, "机型代码包含错误的信息", Toast.LENGTH_SHORT).show();
            }
        }


    }


    /**
     * 本地查看机型码
     *
     * @param view
     */
    public void backup_code(View view) {
        new MaterialDialog.Builder(this)
                .title("机型代码")
                .content(getModelCode())
                .positiveText("确认")
                .neutralText("复制")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setClipText(getModelCode());
                    }
                })
                .show();
    }

    /**
     * 设置和获取剪切板
     *
     * @param a
     */
    public void setClipText(String a) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(a);
        Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
    }

    public String getClipText() {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboardManager.getText().toString();
    }


    /**
     * 提交与获取
     *
     * @param key
     * @param value
     */
    public void set_sharedString(String key, String value) {
        SharedPreferences sp = this.getSharedPreferences("data", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get_sharedString(String key, String moren) {
        SharedPreferences sp = this.getSharedPreferences("data", 0);
        return sp.getString(key, moren);
    }


}
