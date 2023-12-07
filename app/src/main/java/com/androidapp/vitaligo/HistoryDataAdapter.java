package com.androidapp.vitaligo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class HistoryDataAdapter extends ArrayAdapter<HistoryData> {

    public HistoryDataAdapter(Context context, List<HistoryData> historyDataList) {
        super(context, 0, historyDataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_item, parent, false);
        }


        HistoryData historyData = getItem(position);


        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewContent = convertView.findViewById(R.id.textViewContent);

        textViewTitle.setText(historyData.getTitle());
        textViewContent.setText(historyData.getContent());

        return convertView;
    }
}
