package com.flyingstudio.cumtfriend.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.flyingstudio.cumtfriend.entity.Exam;
import com.flyingstudio.cumtfriend.entity.Record;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.litepal.LitePal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExamTask extends AsyncTask<String, Void, String> {
    private Context context;
    private final String url = "http://jwxt.cumt.edu.cn";
    private Map<String, String> cookies = new HashMap<>();
    private String stuNum;
    private int year, term;

    private Connection connection;
    private Connection.Response response;
    private ExamTaskFinish finish;

    public ExamTask(Context context, String cookie, String stuNum, int year, int term, ExamTaskFinish finish) {
        this.context = context;
        this.cookies.put("JSESSIONID", cookie);
        this.stuNum = stuNum;
        this.year = year;
        this.term = term;
        this.finish = finish;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            getStudentExam(this.year, this.term);
            this.finish.finish();
        } catch (Exception e) {
            e.printStackTrace();
            this.finish.fail();
        }
        return "";
    }

    public void getStudentExam(int year, int term) throws Exception {
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

    public interface ExamTaskFinish{
        void finish();
        void fail();
    }
}
