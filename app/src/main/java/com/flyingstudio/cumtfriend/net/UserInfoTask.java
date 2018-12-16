package com.flyingstudio.cumtfriend.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.flyingstudio.cumtfriend.entity.User;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoTask extends AsyncTask<String, Void, String> {
    private Context context;
    private Map<String, String> cookies = new HashMap<>();
    private final static String url = "http://jwxt.cumt.edu.cn";
    private String stuNum;
    private GetUserInfoCallback callback;

    public UserInfoTask(Context context, String cookie, String stuNum) {
        this.context = context;
        this.cookies.put("JSESSIONID", cookie);
        this.stuNum = stuNum;
    }

    public UserInfoTask(Context context, String cookie, String stuNum, GetUserInfoCallback callback) {
        this.context = context;
        this.cookies.put("JSESSIONID", cookie);
        this.stuNum = stuNum;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            getStudentInformaction();
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "err";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals("ok")) {
            if (callback != null) callback.success();
        } else {
            if (callback != null) callback.error();
        }
    }

    public void getStudentInformaction() throws Exception {
        Connection connection = Jsoup.connect(url + "/jwglxt/xsxxxggl/xsxxwh_cxCkDgxsxx.html?gnmkdm=N100801&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        Connection.Response response = connection.cookies(cookies).ignoreContentType(true).execute();
        JSONObject jsonObject = new JSONObject(response.body());
        System.out.println("--- 基本信息 ---");
        Log.d("GET USERINFO", "getStudentInformaction: " + response.body());
//        List<User>users = LitePal.where("stuNum = ?", jsonObject.getString("xh_id")).find(User.class);
        User user = new User();
        user.setName(jsonObject.getString("xm"));
        user.setStuNum(jsonObject.getString("xh_id"));
        user.setGender(jsonObject.getString("xbm"));
        user.setSchool(jsonObject.getString("jg_id"));
        user.setMajor(jsonObject.getString("zszyh_id"));
        user.setYear(Integer.parseInt(jsonObject.getString("njdm_id")));
        user.save();
        System.out.println("学号：" + jsonObject.getString("xh_id"));
        System.out.println("性别：" + jsonObject.getString("xbm"));
        System.out.println("民族：" + jsonObject.getString("mzm"));
        System.out.println("学院：" + jsonObject.getString("jg_id"));
        System.out.println("班级：" + jsonObject.getString("bh_id"));
        System.out.println("专业：" + jsonObject.getString("zszyh_id"));
        System.out.println("状态：" + jsonObject.getString("xjztdm"));
        System.out.println("入学年份：" + jsonObject.getString("njdm_id"));
        System.out.println("证件号码：" + jsonObject.getString("zjhm"));
        System.out.println("政治面貌：" + jsonObject.getString("zzmmm"));
    }

    public interface GetUserInfoCallback {
        void success();

        void error();
    }
}
