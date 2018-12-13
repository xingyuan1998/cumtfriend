package com.flyingstudio.cumtfriend;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.cache.model.CacheMode;

import org.litepal.LitePal;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyHttp.init(this);
        EasyHttp.getInstance()
//                .setCacheMode(CacheMode.CACHEANDREMOTE)
//                .setCacheDiskConverter(new SerializableDiskConverter())
//                .setCacheVersion(1)
                .debug("HTTP",true);
        initX5();
        LitePal.initialize(this);
    }

    private void initX5() {
        //如果没有这个内核，允许在WIFI情况下去下载内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
