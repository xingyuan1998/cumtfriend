package com.flyingstudio.cumtfriend.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.adapter.SubjectDetailAdapter;
import com.flyingstudio.cumtfriend.entity.Subject;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhuangfei.timetable.model.Schedule;

import java.lang.reflect.Type;
import java.util.List;

public class SubjectDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        String ScheduleText = intent.getStringExtra("subjects");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Schedule>>(){}.getType();
        List<Schedule>schedules = gson.fromJson(ScheduleText, type);
        System.out.print("Schedules"+schedules);

        RecyclerView subjectDeatilRec = findViewById(R.id.subject_detail_rec);
        subjectDeatilRec.setLayoutManager(new LinearLayoutManager(SubjectDetailActivity.this, LinearLayout.VERTICAL, false));
        SubjectDetailAdapter adapter = new SubjectDetailAdapter(this, schedules);
        subjectDeatilRec.setAdapter(adapter);
    }
}
