package com.dabai.ChangeModel2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.dabai.ChangeModel2.R;
import com.dabai.ChangeModel2.utils.DabaiUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            switch (preference.getKey()) {
                case "tomarket":
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=" + getContext().getPackageName()));
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "您的系统中没有安装应用市场", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "github":
                    new DabaiUtils().openLink(getContext(), "https://github.com/dabai2017/ChangeModel2.git");
                    break;
                case "alipay":
                    try {
                        Intent intent1 = new Intent();
                        //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent1.setAction("android.intent.action.VIEW");
                        //支付宝二维码解析
                        Uri content_url = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&qrcode=HTTPS://QR.ALIPAY.COM/FKX08574RJXQHHF1SRRFIB2");
                        intent1.setData(content_url);
                        startActivity(intent1);
                        Toast.makeText(getContext(), "嘿嘿😀", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "调起支付宝失败！", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case "about":
                    try {
                        new DabaiUtils().openLink(getContext(),"https://dabai2017.gitee.io/blog/2019/08/23/机型更改关于信息/");
                    } catch (Exception e) {
                    }
                    break;
                case "codes":
                    try {
                        new DabaiUtils().openLink(getContext(),"https://dabai2017.gitee.io/blog/2019/08/22/机型修改代码库/");
                    } catch (Exception e) {
                    }
                    break;
                case "peoples":
                    try {
                        new DabaiUtils().openLink(getContext(),"https://dabai2017.gitee.io/blog/2019/08/22/机型修改社区支持/");
                    } catch (Exception e) {
                    }
                    break;
                case "magiskmoudles":
                    try {
                        new DabaiUtils().openLink(getContext(),"https://www.lanzous.com/b925945");
                    } catch (Exception e) {
                    }
                    break;



            }
            return false;
        }
    }
}