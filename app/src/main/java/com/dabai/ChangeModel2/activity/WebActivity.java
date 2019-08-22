package com.dabai.ChangeModel2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.dabai.ChangeModel2.R;
import com.dabai.ChangeModel2.utils.DabaiUtils;
import com.google.android.material.snackbar.Snackbar;

public class WebActivity extends AppCompatActivity {

    WebView webview;
    String last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webview = findViewById(R.id.webview);

        String link = null;
        try {
            Intent intent = getIntent();
            link = intent.getStringExtra("link");

            if (link == null) {
                intent = getIntent();
                link = "" + intent.getData();
                webview.loadUrl(link);
            }
            last = link;
            webview.loadUrl(link);

        } catch (Exception e) {
            Toast.makeText(this, "程序错误", Toast.LENGTH_SHORT).show();
            finish();
        }


        //声明WebSettings子类
        WebSettings webSettings = webview.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
// 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式


        webview.setDownloadListener(new DownloadListener() {

            private AlertDialog ad;

            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                ad = new AlertDialog.Builder(WebActivity.this).setCancelable(false).setTitle("下载文件").setMessage("文件描述:" + contentDisposition + "\n类型:" + mimetype + "\n下载地址:" + url)
                        .setNeutralButton("回首页", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ad.dismiss();
                                webview.loadUrl(last);
                            }
                        }).setNegativeButton("取消", null).setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DabaiUtils().openLink(WebActivity.this, url);
                                Toast.makeText(WebActivity.this, "选择一个下载器", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).show();

            }
        });


        webview.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);

                    return false;//返回false 意思是不拦截，让webview自己处理
                } else {
                    // Otherwise allow the OS to handle things like tel, mailto, etc.


                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Snackbar.make(getWindow().getDecorView(), "异常:" + e.toString(), Snackbar.LENGTH_SHORT).show();
                    }


                    return true;
                }

            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();//返回上个页面
            return true;
        } else {
            finish();
            return false;
        }

    }
}
