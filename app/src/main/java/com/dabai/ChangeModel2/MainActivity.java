package com.dabai.ChangeModel2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.ChangeModel2.activity.SettingsActivity;
import com.dabai.ChangeModel2.utils.Base64;
import com.dabai.ChangeModel2.utils.DabaiUtils;
import com.dabai.ChangeModel2.utils.HtmlUtils;
import com.dabai.ChangeModel2.utils.shell;
import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String b1, b2, b3, b4, b5;
    private MaterialDialog mddia;
    private PopupWindow pw;
    private Document doc;
    private String TAG = "dabai";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        check_root();
        check_magisk();


        if (get_sharedString("first", "yes").equals("yes")) {

            new MaterialDialog.Builder(this)
                    .title("免责声明")
                    .content("使用本软件，对设备造成的所有伤害以及对其他APP造成的影响，软件开发者和程序概不负责")
                    .cancelable(false)
                    .positiveText("同意")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            set_sharedString("first", "no");
                        }
                    })
                    .neutralText("退出")
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .show();

        }


        View pwview = getLayoutInflater().inflate(R.layout.menu_cloud, null);
        pw = new PopupWindow(MainActivity.this);
        pw.setContentView(pwview);
        pw.setFocusable(true);
        pw.setBackgroundDrawable(new BitmapDrawable());


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
                                                new DabaiUtils().openLink(context, link);
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
                                                        new DabaiUtils().openLink(context, link);
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

        File backupdir = new File("/sdcard/.modelbackup");
        if (!backupdir.exists()) {
            backupdir.mkdir();
        }


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

                pw.showAsDropDown(bu_cloud);


            }
        });
    }


    public void backcode(View b) {


        new MaterialDialog.Builder(MainActivity.this)
                .title("提示")
                .content("你要把本机机型代码提交到代码库嘛？点击确定申请，等待审核通过即可")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        //检查有没有重复

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    doc = Jsoup.connect("https://dabai2017.gitee.io/blog/2019/08/22/机型修改代码库/").get();
                                    Elements elements = doc.select("p");
                                    if (elements.html().contains(getModelCode())) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new MaterialDialog.Builder(MainActivity.this)
                                                        .title("提示")
                                                        .content("你的机型码已存在于机型库，请勿重复提交")
                                                        .positiveText("确认")
                                                        .show();
                                            }
                                        });

                                    } else {

                                        feedback("[" + Build.MODEL + "]提交机型代码", getModelCode());

                                    }

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
                })
                .negativeText("取消")
                .show();


        pw.dismiss();

    }

    public void downcode(View b) {
        try {
            showCodes();
        } catch (Exception e) {
            Toast.makeText(context, "可能是没网:)\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        pw.dismiss();
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

                        try {
                            String text = null;
                            try {
                                text = "" + getClipText();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            parseCode(text);
                        } catch (Exception e) {
                            Toast.makeText(context, "剪切板异常:)", Toast.LENGTH_SHORT).show();
                        }


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
                Toast.makeText(context, "导入成功！", Toast.LENGTH_SHORT).show();
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
                        try {
                            setClipText(getModelCode());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    /**
     * 设置和获取剪切板
     *
     * @param a
     */
    public void setClipText(String a) throws Exception {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(a);
        Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
    }

    public String getClipText() throws Exception {
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


    /**
     * magisk修改方式
     *
     * @param v
     */
    public void change_magisk(View v) {

        //magisk模块模式
        b1 = m1.getText().toString();
        b2 = m2.getText().toString();
        b3 = m3.getText().toString();
        b4 = m4.getText().toString();
        b5 = m5.getText().toString();


        if (!b1.isEmpty() && !b2.isEmpty() && !b3.isEmpty() && !b4.isEmpty() && !b5.isEmpty()) {
            new MaterialDialog.Builder(this)
                    .title("magisk模式")
                    .content("magisk模块挂载机型信息即将更改为\n\nmodel:" + b1 + "\nbrand: " + b2 + "\nmanufacturer: " + b3 + "\nproduct: " + b4 + "\ndevice: " + b5 + "\n\n需要注意的是，magisk模块方式修改的机型不会创建备份，把模块卸载就会恢复")
                    .positiveText("确认更改")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            changeModel_magisk(b1, b2, b3, b4, b5);
                        }
                    })
                    .negativeText("取消")
                    .show();
        } else {
            Toast.makeText(context, "机型预置每一项都必须填写！", Toast.LENGTH_SHORT).show();
        }


    }


    //更改magisk挂载型号
    public void changeModel_magisk(final String mmodel, final String mbrand, final String mmanufacturer, final String mproduct, final String mdevice) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                //新线程操作
                String mode[] = {"mount -o rw,remount /system"
                        , "cp /sbin/.magisk/modules/changemodel2/system.prop /data/system.prop"
                        , "chmod 0644 /data/system.prop"
                        , "sed -i 's/^ro.product.brand=.*/ro.product.brand=" + mbrand + "/' /data/system.prop"
                        , "sed -i 's/^ro.product.model=.*/ro.product.model=" + mmodel + "/' /data/system.prop"
                        , "sed -i 's/^ro.product.manufacturer=.*/ro.product.manufacturer=" + mmanufacturer + "/' /data/system.prop"
                        , "sed -i 's/^ro.product.device=.*/ro.product.device=" + mdevice + "/' /data/system.prop"
                        , "sed -i 's/^ro.build.product=.*/ro.build.product=" + mproduct + "/' /data/system.prop"
                        , "cp /data/system.prop /sbin/.magisk/modules/changemodel2/system.prop"};

                new shell().execCommand(mode, true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "更改完成，重启生效", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        }.start();
    }


    /**
     * root修改方式
     *
     * @param v
     */
    public void change_root(View v) {

        //magisk模块模式
        b1 = m1.getText().toString();
        b2 = m2.getText().toString();
        b3 = m3.getText().toString();
        b4 = m4.getText().toString();
        b5 = m5.getText().toString();


        if (!b1.isEmpty() && !b2.isEmpty() && !b3.isEmpty() && !b4.isEmpty() && !b5.isEmpty()) {
            new MaterialDialog.Builder(this)
                    .title("ROOT模式")
                    .content("build.prop文件里的各项值即将更改为\n\nmodel:" + b1 + "\nbrand: " + b2 + "\nmanufacturer: " + b3 + "\nproduct: " + b4 + "\ndevice: " + b5 + "\n\n需要注意的是，ROOT方式修改机型会创建备份，但会有一定风险，我推荐你使用magisk模块方式")
                    .positiveText("确认更改")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            changeModel_root(b1, b2, b3, b4, b5);

                        }
                    })
                    .negativeText("取消")
                    .show();
        } else {
            Toast.makeText(context, "机型预置每一项都必须填写！", Toast.LENGTH_SHORT).show();
        }
    }


    //改机型
    public void changeModel_root(final String mmodel, final String mbrand, final String mmanufacturer, final String mproduct, final String mdevice) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                //新线程操作

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

                String mode[] = {"mount -o rw,remount /system"
                        , "cp /system/build.prop /sdcard/.modelbackup/" + Build.MODEL.replace(" ", "") + "_" + sdf.format(new Date()) + ".prop"
                        , "cp /system/build.prop /data/build.prop"
                        , "chmod 0644 /data/build.prop"
                        , "sed -i 's/^ro.product.brand=.*/ro.product.brand=" + mbrand + "/' /data/build.prop"
                        , "sed -i 's/^ro.product.model=.*/ro.product.model=" + mmodel + "/' /data/build.prop"
                        , "sed -i 's/^ro.product.manufacturer=.*/ro.product.manufacturer=" + mmanufacturer + "/' /data/build.prop"
                        , "sed -i 's/^ro.product.device=.*/ro.product.device=" + mdevice + "/' /data/build.prop"
                        , "sed -i 's/^ro.build.product=.*/ro.build.product=" + mproduct + "/' /data/build.prop"
                        , "cp /data/build.prop /system/build.prop"};

                new shell().execCommand(mode, true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新UI操作
                        Toast.makeText(context, "更改完成，重启生效", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        }.start();
    }


    public void propbp(View v) {


        try {
            View view = getLayoutInflater().inflate(R.layout.dialog_propbackup, null);

            mddia = new MaterialDialog.Builder(this)
                    .title("build文件恢复")
                    .customView(view, false)
                    .positiveText("关闭")
                    .show();

            ListView lv = view.findViewById(R.id.lv);
            final File backupdir = new File("/sdcard/.modelbackup");


            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, backupdir.list());
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final File file = new File(backupdir, backupdir.list()[i]);

                    new MaterialDialog.Builder(MainActivity.this)
                            .title("警告")
                            .content("确认恢复到 " + file.getName() + " 嘛？")
                            .positiveText("确认")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            //新线程操作

                                            String mode[] = {"mount -o rw,remount /system"
                                                    , "cp " + file.getAbsolutePath() + " /system/build.prop"};

                                            new shell().execCommand(mode, true);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //更新UI操作
                                                    Toast.makeText(context, "恢复完成，重启生效", Toast.LENGTH_SHORT).show();
                                                    mddia.dismiss();
                                                }

                                            });
                                        }
                                    }.start();

                                }
                            })
                            .negativeText("取消")
                            .show();

                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "没有备份文件", Toast.LENGTH_SHORT).show();
        }


    }


    private View view;
    private MaterialDialog codedia;
    private ArrayList<String> data;

    public void showCodes() throws Exception {

        view = getLayoutInflater().inflate(R.layout.dialog_codes, null);


        new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    data = new HtmlUtils().getHtmlSubText("https://dabai2017.gitee.io/blog/2019/08/22/机型修改代码库/", "<p>", "</p>");
                } catch (Exception e) {
                }


                runOnUiThread(new Runnable() {
                    private MaterialDialog showCodeInfo;

                    @Override
                    public void run() {


                        if (data == null) {
                            Toast.makeText(context, "数据库出错！", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        ListView lv = view.findViewById(R.id.lv);
                        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, data);
                        lv.setAdapter(adapter);


                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                final String tect = data.get(i);

                                final String text = tect.split("\n")[1];

                                Base64 base = new Base64();
                                String mDerive = base.decode(text);
                                String a[] = mDerive.split("@");

                                showCodeInfo = new MaterialDialog.Builder(MainActivity.this)
                                        .title(a[0])
                                        .content("机型码:\n" + text + "\n\n机型信息:\n" + "model:" + a[0] + "\nbrand:" + a[1] + "\nmanufacturer:" + a[2] + "\nproduct:" + a[3] + "\ndevice:" + a[4])
                                        .positiveText("直接导入")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                parseCode(text);
                                                codedia.dismiss();
                                            }
                                        })
                                        .negativeText("复制机型码")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                try {
                                                    setClipText(text);
                                                } catch (Exception e) {
                                                }
                                            }
                                        })
                                        .neutralText("取消")
                                        .show();
                            }
                        });


                    }
                });
            }
        }).start();


        if (data == null) {
            //Toast.makeText(context, "数据库已刷新，请再次点击导入", Toast.LENGTH_SHORT).show();

            new MaterialDialog.Builder(this)
                    .title("提示")
                    .content("数据库已刷新，请点击确认查看机型库")
                    .positiveText("确认")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                showCodes();
                            } catch (Exception e) {
                                Toast.makeText(context, "可能是没网:)\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .negativeText("取消")
                    .show();

            return;
        }
        codedia = new MaterialDialog.Builder(this)
                .title("代码库")
                .customView(view, false)
                .positiveText("关闭")
                .show();


    }


}
