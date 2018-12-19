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
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

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
//                    System.out.println("handleMessage thread id " + Thread.currentThread().getId());
//                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
//                    System.out.println(msg.obj);
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
//            new LoginThread(usernameText, pwdText, new LoginThread.LoginResultCallBack() {
//                @Override
//                public void success(Map<String, String> cookies) {
//                    Log.d("LOGIN", "success: ");
//                    Message message = new Message();
//                    message.obj = cookies;
//                    message.what = 1;
//                    uiHandler.sendMessage(message);
////                    return;
////                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void fail() {
//                    Log.d("LOGIN", "fail: ");
//                    Message message = new Message();
//                    message.what = 2;
//                    uiHandler.sendMessage(message);
//                }
//            }).start();

            LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                    .setMessage("登录中...")
                    .setCancelable(true)
                    .setCancelOutside(true);
            LoadingDailog dialog = loadBuilder.create();
            dialog.show();


            new LoginTask(LoginActivity.this, usernameText, pwdText, new LoginTask.LoginCall() {
                @Override
                public void success(String s) {
//                    String user = SPUtil.getValue(LoginActivity.this, "username");
//                    // 说明是第一次emmm
//                    if (TextUtils.isEmpty(user)) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    SPUtil.setValue(LoginActivity.this, "username", usernameText);
                    SPUtil.setValue(LoginActivity.this, "password", pwdText);
                    SPUtil.setValue(LoginActivity.this, "JSESSIONID", s);
                    Log.d("GET TIMETABLE", "onPostExecute: ");


                    new UserInfoTask(LoginActivity.this, s, usernameText, new UserInfoTask.GetUserInfoCallback() {

                        @Override
                        public void success() {
                            Toast.makeText(LoginActivity.this, "获取用户信息成功", Toast.LENGTH_LONG).show();

                            new ScheduleTask(LoginActivity.this, s, new ScheduleTask.ScheduleTaskFinish() {
                                @Override
                                public void finish() {
                                    Toast.makeText(LoginActivity.this, "获取课表成功", Toast.LENGTH_LONG).show();
                                    new ExamTask(LoginActivity.this, s, usernameText, 2018, 1, new ExamTask.ExamTaskFinish() {
                                        @Override
                                        public void finish() {
                                            new GradeTask(LoginActivity.this, s, usernameText, 2018, 1, new GradeTask.GradeTaskFinish() {
                                                @Override
                                                public void finish() {
                                                    Toast.makeText(LoginActivity.this, "获取成绩成功", Toast.LENGTH_LONG).show();
                                                    ACache cache = ACache.get(LoginActivity.this);
                                                    cache.put("good", "nice", 8 * 60 * 60);
//                                                    cache.put("good", "nice",  60);
                                                    SPUtil.setValue(LoginActivity.this, "target_week", "1");
                                                    dialog.dismiss();

                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }

                                                @Override
                                                public void fail() {
                                                    Toast.makeText(LoginActivity.this, "获取成绩失败", Toast.LENGTH_LONG).show();
                                                }
                                            }).execute("");
                                        }

                                        @Override
                                        public void fail() {

                                        }
                                    }).execute("");
                                }

                                @Override
                                public void fail() {
                                    Toast.makeText(LoginActivity.this, "获取课表失败", Toast.LENGTH_LONG).show();
                                }
                            }).execute("");
                        }

                        @Override
                        public void error() {
                            Toast.makeText(LoginActivity.this, "获取用户信息失败", Toast.LENGTH_LONG).show();
                        }
                    }).execute("");

//                    } else {
//                        new ExamTask(LoginActivity.this, s, usernameText, 2018, 1, new ExamTask.ExamTaskFinish() {
//                            @Override
//                            public void finish() {
//                                new GradeTask(LoginActivity.this, s, usernameText, 2018, 1, new GradeTask.GradeTaskFinish() {
//                                    @Override
//                                    public void finish() {
//                                        ACache cache = ACache.get(LoginActivity.this);
//                                        cache.put("good", "nice", 24 * 60 * 60);
//                                    }
//
//                                    @Override
//                                    public void fail() {
//
//                                    }
//                                }).execute("");
//                            }
//
//                            @Override
//                            public void fail() {
//                            }
//                        }).execute("");
//                    }
                }

                @Override
                public void finish() {
                }

                @Override
                public void fail() {

                }
            }).execute("");

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
