package com.flyingstudio.cumtfriend.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingstudio.cumtfriend.R;
import com.flyingstudio.cumtfriend.entity.Index;
import com.flyingstudio.cumtfriend.view.WebViewActivity;

import java.util.List;

public class IndexRecAdapter extends RecyclerView.Adapter<IndexRecAdapter.ViewHolder> {
    private List<Index> indices;
    private Context context;

    public IndexRecAdapter(List<Index> indices, Context context) {
        this.indices = indices;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.index_item, viewGroup, false);
        return new IndexRecAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Index index = indices.get(i);
        viewHolder.title.setText(index.getTitle());
        Glide.with(context).load(index.getIcon()).into(viewHolder.icon);
        viewHolder.itemView.setOnClickListener(v -> {
            if (index.getType().equals("web")) {
                Log.d("HHHH", "onBindViewHolder: " + index.getValue());
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", index.getValue());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return indices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.index_icon);
            title = itemView.findViewById(R.id.index_title);
        }
    }
}
