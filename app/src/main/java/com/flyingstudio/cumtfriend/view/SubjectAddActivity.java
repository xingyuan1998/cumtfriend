package com.flyingstudio.cumtfriend.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingstudio.cumtfriend.MainActivity;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.Subject;
import com.flyingstudio.cumtfriend.utils.SPUtil;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.List;

public class SubjectAddActivity extends AppCompatActivity {
    private EditText subjectName, subjectRoom, subjectTeacher;
    private TextView subjectWeek, subjectDay;
    private Button submit;
    private final String[] weekName = new String[25];
    private final boolean[] weekChoose = new boolean[25];
    private final static String TAG = "SubjectAddActivity";

    private int daySec, startSec, endSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_add);
        initView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("添加课程");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        daySec = intent.getIntExtra("day", -1);
        startSec = intent.getIntExtra("start", -1);
        endSec = startSec;
        subjectName = findViewById(R.id.subject_name);
        subjectRoom = findViewById(R.id.subject_room);
        subjectTeacher = findViewById(R.id.subject_teacher);
        subjectWeek = findViewById(R.id.subject_week);
        subjectDay = findViewById(R.id.subject_day);
        submit = findViewById(R.id.subject_submit);

        if (daySec != -1 && startSec != -1) {
            subjectDay.setText("周" + daySec + " 第" + startSec + "节");
        } else {
            daySec = 1;
            startSec = 1;
            endSec = 1;
            subjectDay.setText("周" + daySec + " 第" + startSec + "节");
        }
        String weekNow = SPUtil.getValue(SubjectAddActivity.this, "target_week");
        if (TextUtils.isEmpty(weekNow)) weekNow = "1";

        // 注意这是从0开始的emmmmm
        for (int i = 0; i < 25; i++) {
            weekName[i] = "第" + (i + 1) + "周";
            weekChoose[i] = false;
        }

        daySec --;
        startSec --;

        weekChoose[Integer.parseInt(weekNow)] = true;
        subjectWeek.setText("第" + (Integer.parseInt(weekNow) + 1) + "周");


        subjectWeek.setOnClickListener(v -> {

            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("多选对话框").setIcon(R.drawable.chengji)
                    .setNegativeButton("取消", null).setPositiveButton("确定", null)
                    .setPositiveButton("确定", (dialog12, which) -> {
                        boolean first = true;
                        String weeks = "";
                        for (int i = 0; i < weekChoose.length; i++) {
                            if (weekChoose[i]) {
                                if (first) {
                                    weeks += (i + 1);
                                    first = false;
                                } else weeks = weeks + "," + (i + 1);
                            }
                            Log.d(TAG, "initView: " + weeks);
                        }
                        subjectWeek.setText(weeks + "周");
                    })
                    .setMultiChoiceItems(weekName, weekChoose, (dialog1, which, isChecked) -> {
                        if (isChecked) weekChoose[which] = true;
                    }).create();
            dialog.show();
        });

        subjectDay.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.choose_day_item, null);
            WheelView wheelViewWeek = view.findViewById(R.id.week);
            WheelView wheelViewStart = view.findViewById(R.id.start);
            WheelView wheelViewEnd = view.findViewById(R.id.end);
            List<String> weeks = new ArrayList<String>() {{
                add("周一");
                add("周二");
                add("周三");
                add("周四");
                add("周五");
                add("周六");
                add("周日");
            }};

            wheelViewWeek.setWheelAdapter(new ArrayWheelAdapter(this));
//            wheelView.setSkin(WheelView.Skin.Common);
            wheelViewWeek.setWheelData(weeks);
            wheelViewWeek.setSkin(WheelView.Skin.Holo);
            wheelViewWeek.setLoop(true);
            List<String> days = new ArrayList<>();
            for (int i = 1; i < 13; i++) {
                days.add("第" + i + "节");
            }
            wheelViewWeek.setSelection(daySec);
            wheelViewWeek.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    daySec = position;
                }
            });


            wheelViewStart.setWheelAdapter(new ArrayWheelAdapter(this));
            wheelViewStart.setWheelData(days);
            wheelViewStart.setSkin(WheelView.Skin.Holo);
            wheelViewStart.setLoop(true);
            wheelViewStart.setSelection(startSec);

            wheelViewStart.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    startSec = position;
                }
            });


            wheelViewEnd.setWheelAdapter(new ArrayWheelAdapter(this));
            wheelViewEnd.setWheelData(days);
            wheelViewEnd.setSkin(WheelView.Skin.Holo);
            wheelViewEnd.setLoop(true);
            wheelViewEnd.setSelection(endSec);
            wheelViewEnd.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    if (position < startSec) {
                        wheelViewEnd.setSelection(startSec);
                        endSec = startSec;
                    } else {
                        endSec = position;
                    }

                }
            });


            builder.setView(view);
            builder.setCancelable(true);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    subjectDay.setText(weeks.get(daySec) + " " + (startSec + 1) + "-" + (endSec + 1) + "节");
                }
            });
            //取消或确定按钮监听事件处理
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        submit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(subjectName.getText().toString())) {
                subjectName.setError("课程名称不能为空");
            }
            if (TextUtils.isEmpty(subjectRoom.getText().toString())) {
                subjectRoom.setError("教室不能为空");
            }

            if (TextUtils.isEmpty(subjectTeacher.getText().toString())) {
                subjectTeacher.setError("老师名称不能为空");
            }

            Subject subject = new Subject();
            subject.setTeacher(subjectTeacher.getText().toString());
            subject.setName(subjectName.getText().toString());
            subject.setRoom(subjectRoom.getText().toString());
            subject.setStart(startSec + 1);
            subject.setStep(endSec - startSec + 1);
            subject.setDay(daySec + 1);
            List<Integer> weekList = new ArrayList<>();
            for (int i = 0; i < weekChoose.length; i++) {
                if (weekChoose[i]) weekList.add(i + 1);
            }
            subject.setWeek_list(weekList);
            subject.save();
            Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        });


    }
}
