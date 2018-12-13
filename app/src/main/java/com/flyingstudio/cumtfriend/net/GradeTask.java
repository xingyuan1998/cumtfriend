package com.flyingstudio.cumtfriend.net;

import android.content.Context;
import android.os.AsyncTask;

import com.flyingstudio.cumtfriend.entity.Record;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.litepal.LitePal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GradeTask extends AsyncTask<String, Void, String> {
    private Context context;
    private final String url = "http://jwxt.cumt.edu.cn";
    private Map<String, String> cookies = new HashMap<>();

    private Connection connection;
    private Connection.Response response;
    private String stuNum;
    private int year, term;

    public GradeTask(Context context, String cookie, String stuNum, int year, int term, GradeTaskFinish finish) {
        this.context = context;
        this.cookies.put("JSESSIONID", cookie);
        this.stuNum = stuNum;
        this.year = year;
        this.term = term;
        this.taskFinish = finish;
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            getStudentGrade(this.year, this.term);
            this.taskFinish.finish();
        } catch (Exception e) {
            e.printStackTrace();
            this.taskFinish.fail();
        }
        return null;
    }

    public void getStudentGrade(int year, int term) throws Exception {
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

        connection = Jsoup.connect(url + "/jwglxt/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();
        connection = Jsoup.connect(url + "/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005");
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

    private GradeTaskFinish taskFinish;

    public interface GradeTaskFinish {
        void finish();
        void fail();
    }
}
