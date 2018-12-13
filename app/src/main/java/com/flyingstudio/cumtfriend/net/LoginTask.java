package com.flyingstudio.cumtfriend.net;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.flyingstudio.cumtfriend.utils.B64;
import com.flyingstudio.cumtfriend.utils.RSAEncoder;
import com.flyingstudio.cumtfriend.utils.SPUtil;

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

    public LoginTask(Context context, String stuNum, String password) {
        this.context = context;
        this.stuNum = stuNum;
        this.pwdForward = password;
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
        // 登录失败 返回为 null
        Log.d("LOGIN", "onPostExecute: " + s);
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
            SPUtil.setValue(context, "JSESSIONID", s);
            SPUtil.setValue(context, "username", stuNum);
            SPUtil.setValue(context, "password", pwdForward);
            Log.d("GET TIMETABLE", "onPostExecute: ");
            new ScheduleTask(context, s).execute("");
        }
    }
}
