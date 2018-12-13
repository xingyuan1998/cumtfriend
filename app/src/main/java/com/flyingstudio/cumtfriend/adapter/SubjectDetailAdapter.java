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
import com.flyingstudio.cumtfriend.entity.Subject;
import com.zhuangfei.timetable.model.Schedule;

import org.jsoup.helper.StringUtil;

import java.util.List;

public class SubjectDetailAdapter extends RecyclerView.Adapter<SubjectDetailAdapter.ViewHolder> {
    private Context context;
    private List<Schedule> schedules;

    public SubjectDetailAdapter(Context context, List<Schedule> subjects) {
        this.context = context;
        this.schedules = subjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_detail_item, viewGroup, false);
        return new SubjectDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Schedule subject = schedules.get(i);
        Log.d("SubjectDetailAdapter", "onBindViewHolder: " + subject.getTeacher());
        viewHolder.teacher.setText(subject.getTeacher());
        viewHolder.week.setText(StringUtil.join(subject.getWeekList(), ",") + "周");
        viewHolder.day.setText(subject.getStart() + "-" + (subject.getStart() + subject.getStep() - 1) + "节");
        viewHolder.room.setText(subject.getRoom());
        viewHolder.name.setText(subject.getName());
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, room, teacher, day, week;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.subject_name);
            room = itemView.findViewById(R.id.subject_room);
            teacher = itemView.findViewById(R.id.subject_teacher);
            day = itemView.findViewById(R.id.subject_day);
            week = itemView.findViewById(R.id.subject_week);
        }
    }
}
