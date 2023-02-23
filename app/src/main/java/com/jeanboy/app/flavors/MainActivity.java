package com.jeanboy.app.flavors;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv_info = findViewById(R.id.tv_info);

        String appName = AppUtil.INSTANCE.getAppName(this);
        String currentProcessName = AppUtil.INSTANCE.getCurrentProcessName(this);
        String channel = AppUtil.INSTANCE.getMetaData(this, "channel_name");
        String flavors = AppUtil.INSTANCE.getMetaData(this, "flavors_name");
        AppUtil.VersionInfo versionInfo = AppUtil.INSTANCE.getVersionInfo(this);

        tv_info.setText("name: " + appName +
                "\nprocessName: " + currentProcessName +
                "\nchannel: " + channel +
                "\nflavors: " + flavors +
                "\nversionName: " + versionInfo.getName() +
                "\nversionCode: " + versionInfo.getCode());
    }
}
