package com.flyingstudio.cumtfriend.net;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.entity.Exam;
import com.flyingstudio.cumtfriend.entity.NoDataEntity;
import com.flyingstudio.cumtfriend.entity.Record;
import com.flyingstudio.cumtfriend.entity.Subject;
import com.flyingstudio.cumtfriend.entity.User;
import com.flyingstudio.cumtfriend.utils.ACache;
import com.flyingstudio.cumtfriend.utils.B64;
import com.flyingstudio.cumtfriend.utils.MD5Util;
import com.flyingstudio.cumtfriend.utils.RSAEncoder;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Log.d("LOGIN", this.pwdForward + "getRSApublickey: " + this.stuNum + modulus + "\n" + exponent);
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
                .method(Connection.Method.POST);
        response = connection.execute();
        document = Jsoup.parse(response.body());
        Log.d("LOGIN", "beginLogin: " + cookies);
        if (document.getElementById("tips") == null) {
            String resBody = response.body();
            System.out.println("登陆成功" + response.body());
            cookies = response.cookies();
            Log.d("LOGIN", "beginLogin: " + cookies);
            String ss = cookies.get("JSESSIONID");
            getStudentInformaction(cookies);
            getStudentExam(cookies, 2019, 1);
            getStudentGrade(cookies, 2019, 1);
            getStudentTimetable(cookies, 2019, 1);


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
            }


        }
    }

    public void getStudentInformaction(Map<String, String>cookies) throws Exception {
        connection = Jsoup.connect(url + "/jwglxt/xsxxxggl/xsxxwh_cxCkDgxsxx.html?gnmkdm=N100801&su=" + stuNum);
//            connection.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        response = connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.GET).execute();
        Log.d("GET USERINFO", "getStudentInformaction: " + response.body());
        JSONObject jsonObject = new JSONObject(response.body());
        System.out.println("--- 基本信息 ---");
        User user = new User();
        user.setName(jsonObject.getString("xm"));
        user.setStuNum(jsonObject.getString("xh_id"));
        user.setGender(jsonObject.getString("xbm"));
        user.setSchool(jsonObject.getString("jg_id"));
        user.setMajor(jsonObject.getString("zyh_id"));
        user.setYear(Integer.parseInt(jsonObject.getString("njdm_id")));
        user.save();

    }

    public void getStudentExam(Map<String, String>cookies, int year, int term) throws Exception {
        Map<String, String> datas = new HashMap<>();
        datas.put("xnm", String.valueOf(year));
        datas.put("xqm", String.valueOf(term * term * 3));
        datas.put("_search", "false");
        datas.put("nd", String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount", "20");
        datas.put("queryModel.currentPage", "1");
        datas.put("queryModel.sortName", "");
        datas.put("queryModel.sortOrder", "asc");
        datas.put("queryModel.sortName", "");
        datas.put("time", "0");
        connection = Jsoup.connect(url + "/jwglxt/kwgl/kscx_cxXsksxxIndex.html?doType=query&gnmkdm=N358105");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

        response = connection.cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray exams = new JSONArray(jsonObject.getString("items"));
        LitePal.deleteAll(Exam.class);
        for (int i = 0; i < exams.length(); i++) {
            JSONObject lesson = (JSONObject) exams.get(i);
            Exam exam = new Exam();
            exam.setCourse_id(lesson.getString("kch"));
            exam.setName(lesson.getString("kcmc"));
            exam.setRoom(lesson.getString("cdxqmc") + "-" + lesson.getString("cdmc"));
            exam.setTime(lesson.getString("kssj"));
            exam.save();
            Log.d("EXAM SAVE", "getStudentExam: ");
        }
    }


    public void getStudentGrade(Map<String, String>cookies, int year, int term) throws Exception {
        Map<String, String> datas = new HashMap<>();
        datas.put("xnm", String.valueOf(year));
        datas.put("xqm", String.valueOf(term * term * 3));
        datas.put("_search", "false");
        datas.put("nd", String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount", "20");
        datas.put("queryModel.currentPage", "1");
        datas.put("queryModel.sortName", "");
        datas.put("queryModel.sortOrder", "asc");
        datas.put("queryModel.sortName", "");
        datas.put("time", "0");

//        connection = Jsoup.connect(url + "/jwglxt/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=" + stuNum);
//        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
//        response = connection.cookies(cookies).method(Connection.Method.POST)
//                .data(datas).ignoreContentType(true).execute();
        connection = Jsoup.connect(url + "/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005");
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray gradeTable = new JSONArray(jsonObject.getString("items"));
        LitePal.deleteAll(Record.class);
        for (int i = 0; i < gradeTable.length(); i++) {
            JSONObject lesson = (JSONObject) gradeTable.get(i);
            Record record = new Record();
            record.setCourse_id(lesson.getString("kch_id"));
            record.setGpa(lesson.getString("jd"));
            record.setName(lesson.getString("kcmc"));
            record.setCredit(lesson.getString("xf"));
            record.setTeacher(lesson.getString("jsxm"));
            record.setCreate_time(new Date().toString());
            record.setGrade(lesson.getString("cj"));
            record.save();
        }
    }

    public void getStudentTimetable(Map<String, String>cookies, int year, int term) throws Exception {
        connection = Jsoup.connect(url + "/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xnm", String.valueOf(year));
        connection.data("xqm", String.valueOf(term * term * 3));
        response = connection.cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
        JSONObject jsonObject = new JSONObject(response.body());
        if (jsonObject.get("kbList") == null) {
            System.out.println("暂时没有安排课程");
            return;
        }
        JSONArray timeTable = new JSONArray(jsonObject.getString("kbList"));
        System.out.println(String.valueOf(year) + " -- " + String.valueOf(year + 1) + "学年 " + "第" + term + "学期");
        LitePal.deleteAll(Subject.class);
        for (int i = 0; i < timeTable.length(); i++) {
            JSONObject object = (JSONObject) timeTable.get(i);
            Subject subject = new Subject();
            subject.setName(object.getString("kcmc"));
            subject.setId(object.getString("kch_id"));
            subject.setTeacher(object.getString("xm"));
            subject.setRoom(object.getString("xqmc") + "-" + object.getString("cdmc"));
            subject.setDay(Integer.parseInt(object.getString("xqj")));

            //
            String[] startEnd = object.getString("jcor").split("-");
            subject.setStart(Integer.parseInt(startEnd[0]));
            subject.setStep(Integer.parseInt(startEnd[1]) - Integer.parseInt(startEnd[0]) + 1);

            // 处理时间相关的 这里很蛋疼 很容易出问题emmm
            List<Integer> week_list = new ArrayList<>();
            String[] weeks = object.getString("zcd").split(",");

            // 正则匹配啊
            String pattern = "(\\d+)";
            // 创建 Pattern 对象
            Pattern r = Pattern.compile(pattern);

            for (int j = 0; j < weeks.length; j++) {
                List<Integer> weekStartEnd = new ArrayList<>();
                // 现在创建 matcher 对象
                Matcher m = r.matcher(weeks[j]);
                while (m.find()) {
                    weekStartEnd.add(Integer.parseInt(m.group(0)));
                }
                if (weekStartEnd.size() <= 0 || weekStartEnd.size() > 2) {
                    System.out.print("getStudentTimetable:" + weekStartEnd.size());
                    Log.d("GET WEEK ERROR", "getStudentTimetable: ");
                } else if (weekStartEnd.size() == 1) {
                    week_list.add(weekStartEnd.get(0));
                } else if (weeks[j].contains("单") || weeks[j].contains("双")) {
                    for (int k = weekStartEnd.get(0); k < weekStartEnd.get(1) + 1; k = k + 2) {
                        week_list.add(k);
                    }
                } else {
                    for (int k = weekStartEnd.get(0); k < weekStartEnd.get(1) + 1; k++) {
                        week_list.add(k);
                    }
                }
            }
            subject.setWeek_list(week_list);
            subject.save();
            Log.d("GET TIMETABLE ", "getStudentTimetable: " + i);

        }
    }


    public interface LoginCall {
        void success(String s);
        void finish();
        void fail();
    }
}
