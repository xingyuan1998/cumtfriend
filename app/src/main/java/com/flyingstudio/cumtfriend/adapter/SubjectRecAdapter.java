package com.flyingstudio.cumtfriend.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.Subject;

import java.util.List;

public class SubjectRecAdapter extends RecyclerView.Adapter<SubjectRecAdapter.ViewHolder> {
    private Context context;
    private List<Subject> subjects;

    public SubjectRecAdapter(Context context, List<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_item, viewGroup, false);
        return new SubjectRecAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Subject subject = subjects.get(i);
        viewHolder.name.setText(subject.getName());
        viewHolder.room.setText(subject.getRoom());
        viewHolder.time.setText(subject.getStart() + "-" + (subject.getStart() + subject.getStep() - 1) + "节");
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, room, name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.subject_time);
            room = itemView.findViewById(R.id.subject_room);
            name = itemView.findViewById(R.id.subject_name);

        }
    }
}
