package com.flyingstudio.cumtfriend;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingstudio.cumtfriend.adapter.FragmentAdapter;
import com.flyingstudio.cumtfriend.entity.Update;
import com.flyingstudio.cumtfriend.fragment.InfoFragment;
import com.flyingstudio.cumtfriend.fragment.IndexFragment;
import com.flyingstudio.cumtfriend.fragment.MyFragment;
import com.flyingstudio.cumtfriend.fragment.TimeTableFragment;
import com.flyingstudio.cumtfriend.net.Constant;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.flyingstudio.cumtfriend.utils.UiUtil;
import com.flyingstudio.cumtfriend.utils.VersionUtil;
import com.flyingstudio.cumtfriend.view.LoginActivity;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.utils.HttpLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity {
    private List<Fragment> fragments = new ArrayList<>();
    private TextView mTextMessage;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView navigation;
    private ProgressDialog progressDialog;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_my:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUpdate();

        viewPager = findViewById(R.id.viewPager);
        navigation = findViewById(R.id.navigation);
        mTextMessage = findViewById(R.id.message);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        BottomNavigationViewHelper.disableShiftMode(navigation);
        fragments.add(new IndexFragment());
        fragments.add(new TimeTableFragment());
        fragments.add(new InfoFragment());
        fragments.add(new MyFragment());
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        viewPager.setCurrentItem(1);
//        navigation.getMenu().getItem(1).setChecked(true);
        // 申请权限
        PermissionGen.with(MainActivity.this).addRequestCode(100)
                .permissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.REQUEST_INSTALL_PACKAGES,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();

        UiUtil.setImmerseLayout(getWindow());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_white_slight));

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        // Toast.makeText(getContext(), "已授权", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
//        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
    }




    private void checkUpdate() {
        EasyHttp.get(Constant.GET_UPDATE)
                .baseUrl(Constant.BASE_URL)
                .execute(new SimpleCallBack<Update>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.d("GET_UPDATE", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onSuccess(Update update) {
                        if (update != null) {
                            if (update.getVersion_id() > VersionUtil.getVersionCode(MainActivity.this)) {
                                final AlertDialog.Builder normalDialog =
                                        new AlertDialog.Builder(MainActivity.this);
                                normalDialog.setCancelable(false);
                                normalDialog.setTitle("应用更新");
                                normalDialog.setMessage(update.getContent());
                                normalDialog.setPositiveButton("更新",
                                        (dialog, which) -> {

                                            progressDialog = new ProgressDialog(MainActivity.this);
                                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                            progressDialog.setMessage("正在下载中");
                                            progressDialog.setMax(100);
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                            Log.e("GET UPDATE APK", "onNext: " + update.getUrl());
                                            EasyHttp.downLoad(update.getUrl())
                                                    .savePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())
                                                    .saveName("release.apk")//不设置默认名字是时间戳生成的
                                                    .execute(new DownloadProgressCallBack<String>() {
                                                        @Override
                                                        public void update(long bytesRead, long contentLength, boolean done) {
                                                            int progress = (int) (bytesRead * 100 / contentLength);
                                                            HttpLog.e(progress + "% ");
                                                            Log.e("UPDATE", "update: " + progress);
                                                            progressDialog.setProgress(progress);
                                                            if (done) {//下载完成

                                                            }

                                                        }
                                                        @Override
                                                        public void onStart() {
                                                            //开始下载
                                                        }
                                                        @Override
                                                        public void onComplete(String path) {
                                                            //下载完成，path：下载文件保存的完整路径
                                                            Log.d("DOWNLOAD COMPLATE", "onComplete: " + path);
                                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                                boolean canInstall = getPackageManager().canRequestPackageInstalls();

                                                            }
                                                            VersionUtil.installApk(MainActivity.this, new File(path));
                                                            Log.d("DOWNLOAD COMPLATE", "onComplete:gg " );
                                                        }
                                                        @Override
                                                        public void onError(ApiException e) {
                                                            //下载失败
                                                            Log.e("DOWNLOAD ERROR", "onError: ");
                                                        }
                                                    });


                                        });
                                normalDialog.setNegativeButton("取消",
                                        (dialog, which) -> {
                                            if (update.getForce() == 1) {
                                                finish();
                                            }
                                        });
                                // 显示
                                normalDialog.show();
                            }
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
}
