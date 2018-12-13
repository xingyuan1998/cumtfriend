package com.flyingstudio.cumtfriend.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.flyingstudio.cumtfriend.entity.Subject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleTask extends AsyncTask<String, Void, String> {
    private Context context;
    private final String url = "http://jwxt.cumt.edu.cn";
    private Map<String, String> cookies = new HashMap<>();

    private Connection connection;
    private Connection.Response response;


    public ScheduleTask(Context context, String cookie){
        this.context = context;
        this.cookies.put("JSESSIONID", cookie);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            getStudentTimetable(2018, 1);
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
            Log.d("OK", "onPostExecute: ");
        } else {
            Log.d("ERR", "onPostExecute: ");
        }
    }

    public void getStudentTimetable(int year, int term) throws Exception {
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
            subject.setStep(Integer.parseInt(startEnd[1]));

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
}
