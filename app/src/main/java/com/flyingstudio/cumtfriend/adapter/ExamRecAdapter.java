package com.flyingstudio.cumtfriend.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.Exam;
import com.flyingstudio.cumtfriend.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExamRecAdapter extends RecyclerView.Adapter<ExamRecAdapter.ViewHolder> {
    private List<Exam> exams;
    private Context context;

    public ExamRecAdapter(List<Exam> exams, Context context) {
        this.exams = exams;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.exam_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Exam exam = exams.get(i);
        viewHolder.room.setText(exam.getRoom());
        viewHolder.name.setText(exam.getName());
        viewHolder.time.setText(exam.getTime());
        String t = exam.getTime().split(" ")[0];

//        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Log.d("ddd", "onBindViewHolder: " + t);
            Log.d("GET", "onBindViewHolder: " + format.parse(t).getTime());
            long day = TimeUtil.getTimeDay(new Date(), format.parse(t));
            Log.d("GET_DAY", "onBindViewHolder: " + day);
            if (day <= 0) viewHolder.countDown.setText("已结束");
            else viewHolder.countDown.setText("剩 " + day + " 天");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView countDown, name, room, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countDown = itemView.findViewById(R.id.exam_count_down);
            name = itemView.findViewById(R.id.exam_name);
            room = itemView.findViewById(R.id.exam_room);
            time = itemView.findViewById(R.id.exam_time);
        }
    }
}
