package com.flyingstudio.cumtfriend.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.NoDataEntity;
import com.flyingstudio.cumtfriend.entity.SystemConfig;
import com.flyingstudio.cumtfriend.net.Constant;
import com.flyingstudio.cumtfriend.net.ExamTask;
import com.flyingstudio.cumtfriend.net.GradeTask;
import com.flyingstudio.cumtfriend.net.LoginTask;
import com.flyingstudio.cumtfriend.net.ScheduleTask;
import com.flyingstudio.cumtfriend.net.UserInfoTask;
import com.flyingstudio.cumtfriend.utils.ACache;
import com.flyingstudio.cumtfriend.utils.MD5Util;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.flyingstudio.cumtfriend.utils.UiUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
//    @SuppressLint("HandlerLeak")
//    private Handler uiHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                // 成功
//                case 1:
//                    SystemConfig.out.println("handleMessage thread id " + Thread.currentThread().getId());
//                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
//                    SystemConfig.out.println(msg.obj);
//                    Map<String, String> cookies = (HashMap<String, String>) msg.obj;
//                    Log.d("COOKIES", "handleMessage: " + cookies.get("JSESSIONID"));
//                    // 保存cookies cookies 目前就一个数值emmm 后面取 感觉需要搞个失效时间 emmm 到时候cookie失效了就不好弄了 感觉可以封装一下
//                    SPUtil.setValue(LoginActivity.this, "JSESSIONID", cookies.get("JSESSIONID"));
//                    break;
//                // 失败
//                case 2:
//                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
//        Button regButton = findViewById(R.id.reg);
        Button loginButton = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        String usernameT = SPUtil.getValue(LoginActivity.this, "username");
        String pwdT = SPUtil.getValue(LoginActivity.this, "password");
        if (TextUtils.isEmpty(usernameT)) username.setText(usernameT);
        if (TextUtils.isEmpty(pwdT)) password.setText(pwdT);

        loginButton.setOnClickListener(view -> {
            // 检测输入问题
            String usernameText = username.getText().toString();
            String pwdText = password.getText().toString();
            if (TextUtils.isEmpty(usernameText)) {
                username.setError("学号不能为空");
                return;
            }
            if (TextUtils.isEmpty(pwdText)) {
                password.setError("密码不能为空");
                return;
            }

            LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                    .setMessage("登录中...")
                    .setCancelable(true)
                    .setCancelOutside(true);
            LoadingDailog dialog = loadBuilder.create();
            dialog.show();


            EasyHttp.get("system")
                    .baseUrl(Constant.BASE_URL)
                    .readTimeOut(30 * 1000)
                    .execute(new CallBack<SystemConfig>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(ApiException e) {
                            Log.e("LOGIN_ERROR", "onError: " + e.getCode());
                        }

                        @Override
                        public void onSuccess(SystemConfig systemConfig) {
                            Log.d("EASY_HTTP", "onSuccess: " + systemConfig.getOpen());

                            new LoginTask(LoginActivity.this, usernameText, pwdText, systemConfig.getYear(), systemConfig.getTerm(), new LoginTask.LoginCall() {
                                @Override
                                public void success(String s) {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                    SPUtil.setValue(LoginActivity.this, "username", usernameText);
                                    SPUtil.setValue(LoginActivity.this, "password", pwdText);
                                    SPUtil.setValue(LoginActivity.this, "JSESSIONID", s);
                                    Log.d("GET TIMETABLE", "onPostExecute: ");
                                    ACache cache = ACache.get(LoginActivity.this);
                                    cache.put("good", "nice", 8 * 60 * 60);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");
                                    Date date1;
                                    try {
                                        date1 = simpleDateFormat.parse(systemConfig.getOpen());
                                        long curr_week = ((new Date().getTime()) - date1.getTime()) / (7 * 24 * 60 * 60 * 1000);
                                        long curr_day = ((new Date().getTime()) - date1.getTime()) / (24 * 60 * 60 * 1000) % 7 + 1;
                                        Log.d("LOGIN SUCCESS", "success: " + date1.getTime() + ", " + curr_week + ", " + curr_day);
                                        SPUtil.setValue(LoginActivity.this, "target_week", curr_week + "");


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }



                                    dialog.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);


                                }

                                @Override
                                public void finish() {
                                }

                                @Override
                                public void fail() {

                                }
                            }).execute("");

                        }
                    });


            UiUtil.setImmerseLayout(getWindow());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.app_white_slight));


//            EasyHttp.post(Constant.Login)
//                    .baseUrl(Constant.BASE_URL)
//                    .readTimeOut(30 * 1000)//局部定义读超时
//                    .writeTimeOut(30 * 1000)
//                    .connectTimeout(30 * 1000)
//                    .params("username", usernameText)
//                    .params("password", pwdText)
//                    .timeStamp(true)
//                    .execute(new CallBack<NoDataEntity>() {
//                        @Override
//                        public void onStart() {
//
//                        }
//
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(ApiException e) {
//                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            Log.e("LOGIN_ERROR", "onError: " + e.getCode());
//                        }
//
//                        @Override
//                        public void onSuccess(NoDataEntity noDataEntity) {
//                            if (noDataEntity != null) {
////                                Toast.makeText(LoginActivity.this, noDataEntity.getMsg(), Toast.LENGTH_LONG).show();
//                                SPUtil.setValue(LoginActivity.this, "token", noDataEntity.getMsg());
//                                SPUtil.setValue(LoginActivity.this, "username", usernameText);
//                                SPUtil.setValue(LoginActivity.this, "password", pwdText);
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });

        });
    }


}
