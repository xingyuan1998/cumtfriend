package com.flyingstudio.cumtfriend.utils;

import android.content.Context;
import android.util.Log;

import com.flyingstudio.cumtfriend.view.LoginActivity;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginThread extends Thread {
    private Context context;
    private final String url = "http://jwxt.cumt.edu.cn";
    private Map<String, String> cookies = new HashMap<>();
    private String modulus;
    private String exponent;
    private String csrftoken;
    private Connection connection;
    private Connection.Response response;
    private Document document;
    private String stuNum;
    private String password;

    public LoginThread( String stuNum, String password, LoginResultCallBack callBack) {
        this.stuNum = stuNum;
        this.password = password;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        try {
            getCsrftoken();
            getRSApublickey();
            beginLogin();
        } catch (Exception ex) {
            ex.printStackTrace();
            callBack.fail();
        }
    }

    // 获取csrftoken和Cookies
    private void getCsrftoken() throws IOException {
//        try {
            connection = Jsoup.connect(url + "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + new Date().getTime());
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
            response = connection.timeout(5000).execute();
            cookies = response.cookies();
            document = Jsoup.parse(response.body());
            csrftoken = document.getElementById("csrftoken").val();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
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
        Log.d("LOGIN", this.password + "getRSApublickey: " + this.stuNum + modulus + "\n"+ exponent);
        password = RSAEncoder.RSAEncrypt(this.password, B64.b64tohex(modulus), B64.b64tohex(exponent));
        password = B64.hex2b64(password);
    }

    //登录
    public boolean beginLogin() throws Exception {
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
        if (document.getElementById("tips") == null) {
            System.out.println("登陆成功" + response.body());
            callBack.success(this.cookies);
            return true;
        } else {
            System.out.println(document.getElementById("tips").text());
            callBack.fail();
            return false;
        }
    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }

    private LoginResultCallBack callBack;

    public void setCallBack(LoginResultCallBack callBack) {
        this.callBack = callBack;
    }

    public interface LoginResultCallBack {
        void success(Map<String, String> cookies);
        void fail();
    }

}