package com.flyingstudio.cumtfriend.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.Record;

import java.util.List;

public class RecordRecAdapter extends RecyclerView.Adapter<RecordRecAdapter.ViewHolder> {
    private List<Record> records;
    private Context context;

    public RecordRecAdapter(List<Record> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.record_item, viewGroup, false);
        return new RecordRecAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Record record = records.get(i);
        viewHolder.name.setText(record.getName());
        viewHolder.record.setText(record.getGrade() + "分");
        viewHolder.credit.setText("绩点：" + record.getGpa());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, credit, record;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.record_name);
            credit = itemView.findViewById(R.id.record_credit);
            record = itemView.findViewById(R.id.record_value);
        }
    }
}
