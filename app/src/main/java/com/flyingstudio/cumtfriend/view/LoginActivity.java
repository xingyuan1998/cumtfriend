package com.flyingstudio.cumtfriend.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.NoDataEntity;
import com.flyingstudio.cumtfriend.net.Constant;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;

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
            EasyHttp.post(Constant.Login)
                    .baseUrl(Constant.BASE_URL)
                    .readTimeOut(30 * 1000)//局部定义读超时
                    .writeTimeOut(30 * 1000)
                    .connectTimeout(30 * 1000)
                    .params("username", usernameText)
                    .params("password", pwdText)
                    .timeStamp(true)
                    .execute(new CallBack<NoDataEntity>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(ApiException e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("LOGIN_ERROR", "onError: " + e.getCode());
                        }

                        @Override
                        public void onSuccess(NoDataEntity noDataEntity) {
                            if (noDataEntity != null) {
//                                Toast.makeText(LoginActivity.this, noDataEntity.getMsg(), Toast.LENGTH_LONG).show();
                                SPUtil.setValue(LoginActivity.this, "token", noDataEntity.getMsg());
                                SPUtil.setValue(LoginActivity.this, "username", usernameText);
                                SPUtil.setValue(LoginActivity.this, "password", pwdText);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        });
    }
}
