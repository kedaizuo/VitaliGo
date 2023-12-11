package com.androidapp.vitaligo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CommunityItemAdapter extends ArrayAdapter<CommunityItem> {
    private Context context;
    private List<CommunityItem> items;

    public CommunityItemAdapter(Context context, List<CommunityItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommunityItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.community_item, parent, false);
        }

        TextView tvTopic = convertView.findViewById(R.id.tvTopic);
        TextView tvPublisher = convertView.findViewById(R.id.tvPublisher);
        TextView tvContent = convertView.findViewById(R.id.tvContent);

        if (item != null) {
            tvTopic.setText(item.getTopic());
            tvPublisher.setText(item.getPublisher());
            tvContent.setText(item.getContent());
        }

        return convertView;
    }
}

