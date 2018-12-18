package com.flyingstudio.cumtfriend.net;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.entity.NoDataEntity;
import com.flyingstudio.cumtfriend.utils.ACache;
import com.flyingstudio.cumtfriend.utils.B64;
import com.flyingstudio.cumtfriend.utils.MD5Util;
import com.flyingstudio.cumtfriend.utils.RSAEncoder;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginTask extends AsyncTask<String, Void, String> {
    private String stuNum;
    private String password;
    private Context context;
    private final String url = "http://jwxt.cumt.edu.cn";
    private Map<String, String> cookies = new HashMap<>();
    private String modulus;
    private String exponent;
    private String csrftoken;
    private Connection connection;
    private Connection.Response response;
    private Document document;
    private String pwdForward;
    private LoginCall loginCall;

    public LoginTask(Context context, String stuNum, String password) {
        this.context = context;
        this.stuNum = stuNum;
        this.pwdForward = password;
    }

    public LoginTask(Context context, String stuNum, String password, LoginCall call) {
        this.context = context;
        this.stuNum = stuNum;
        this.pwdForward = password;
        this.loginCall = call;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            getCsrftoken();
            getRSApublickey();
            return beginLogin();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    // 获取csrftoken和Cookies
    private void getCsrftoken() throws IOException {
        connection = Jsoup.connect(url + "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.timeout(5000).execute();
        cookies = response.cookies();
        document = Jsoup.parse(response.body());
        csrftoken = document.getElementById("csrftoken").val();
    }

    // 获取公钥并加密密码
    private void getRSApublickey() throws Exception {
        connection = Jsoup.connect(url + "/jwglxt/xtgl/login_getPublicKey.html?" +
                "time=" + new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).ignoreContentType(true).timeout(5000).execute();
        JSONObject jsonObject = new JSONObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
        Log.d("LOGIN", this.password + "getRSApublickey: " + this.stuNum + modulus + "\n" + exponent);
        password = RSAEncoder.RSAEncrypt(this.pwdForward, B64.b64tohex(modulus), B64.b64tohex(exponent));
        password = B64.hex2b64(password);
    }

    //登录
    public String beginLogin() throws Exception {
        connection = Jsoup.connect(url + "/jwglxt/xtgl/login_slogin.html");
        connection.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("csrftoken", csrftoken);
        connection.data("yhm", stuNum);
        connection.data("mm", password);
        connection.data("mm", password);
        connection.cookies(cookies).ignoreContentType(true)
                .method(Connection.Method.POST).execute();
        response = connection.execute();
        document = Jsoup.parse(response.body());
        Log.d("LOGIN", "beginLogin: " + cookies);
        if (document.getElementById("tips") == null) {
            String resBody = response.body();
            System.out.println("登陆成功" + response.body());
            Log.d("LOGIN", "beginLogin: " + cookies);
            String ss = cookies.get("JSESSIONID");
            return ss;
        } else {
            System.out.println(document.getElementById("tips").text());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // 登录失败 返回为 null 否则传回来是 cookie的一个键值 只有一个键
        Log.d("LOGIN", "onPostExecute: " + s);
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
            if (loginCall != null) loginCall.fail();
        } else {
            if (loginCall != null) {
                loginCall.success(s);
                return;
            }

            String user = SPUtil.getValue(context, "username");
            // 说明是第一次emmm
            if (TextUtils.isEmpty(user)) {
                Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
                SPUtil.setValue(context, "username", stuNum);
                SPUtil.setValue(context, "password", pwdForward);
                SPUtil.setValue(context, "JSESSIONID", s);
                Log.d("GET TIMETABLE", "onPostExecute: ");




                new UserInfoTask(context, s, stuNum, new UserInfoTask.GetUserInfoCallback() {

                    @Override
                    public void success() {
                        String token = null;
//                        try {
//                            Algorithm algorithm = Algorithm.HMAC256("secret");
//                            token = JWT.create()
//                                    .withClaim("stuNum", stuNum)
//                                    .withClaim("password", pwdForward)
//                                    .withClaim("JSESSIONID", s)
//                                    .sign(algorithm);
//                        } catch (JWTCreationException exception){
//                            //Invalid Signing configuration / Couldn't convert Claims.
//                        }
                        token = MD5Util.crypt(stuNum + s + "good");
                        EasyHttp.post("app/login")
                                .baseUrl("https://school.chpz527.cn/api/")
                                .params("token", token)
                                .params("stuNum", stuNum)
                                .params("JSESSIONID", s)
                                .execute(new SimpleCallBack<NoDataEntity>() {
                                    @Override
                                    public void onError(ApiException e) {
                                        Log.d("POST USE INFO", "onError: " + e.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(NoDataEntity noDataEntity) {
                                        if (noDataEntity != null) {
                                            Log.d("TOKEN", "onSuccess: " + noDataEntity.getMsg());
                                        }
                                    }
                                });

                        new ScheduleTask(context, s, new ScheduleTask.ScheduleTaskFinish() {
                            @Override
                            public void finish() {
                                new ExamTask(context, s, stuNum, 2018, 1, new ExamTask.ExamTaskFinish() {
                                    @Override
                                    public void finish() {
                                        new GradeTask(context, s, stuNum, 2018, 1, new GradeTask.GradeTaskFinish() {
                                            @Override
                                            public void finish() {

                                                ACache cache = ACache.get(context);
                                                cache.put("good", "nice", 24 * 60 * 60);
                                                SPUtil.setValue(context, "target_week", "1");
                                                Intent intent = new Intent(context, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                            }

                                            @Override
                                            public void fail() {

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
                            }
                        }).execute("");
                    }

                    @Override
                    public void error() {

                    }
                }).execute("");

            } else {
                new ExamTask(context, s, stuNum, 2018, 1, new ExamTask.ExamTaskFinish() {
                    @Override
                    public void finish() {
                        new GradeTask(context, s, stuNum, 2018, 1, new GradeTask.GradeTaskFinish() {
                            @Override
                            public void finish() {
                                ACache cache = ACache.get(context);
                                cache.put("good", "nice", 24 * 60 * 60);
                            }

                            @Override
                            public void fail() {

                            }
                        }).execute("");
                    }

                    @Override
                    public void fail() {
                    }
                }).execute("");
            }


        }
    }


    public interface LoginCall {
        void success(String s);

        void fail();
    }
}
