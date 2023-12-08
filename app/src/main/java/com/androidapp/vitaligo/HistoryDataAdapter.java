package com.androidapp.vitaligo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class HistoryDataAdapter extends ArrayAdapter<HistoryData> {


    private FragmentManager fragmentManager;
    private Polyline pathPast;
    public HistoryDataAdapter(Context context, List<HistoryData> historyDataList, FragmentManager fragmentManager) {
        super(context, 0, historyDataList);
        this.fragmentManager = fragmentManager;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_item, parent, false);
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 处理长按事件
                showDialogWithMap(position);
                return true;
            }
        });
        HistoryData historyData = getItem(position);


        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewContent = convertView.findViewById(R.id.textViewContent);

        textViewTitle.setText(historyData.getTitle());
        textViewContent.setText(historyData.getContent());


        return convertView;
    }
    public void showDialogWithMap(int position) {

        // 使用自定义布局
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Record");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.history_map_dialog, null);
        HistoryData historyData = getItem(position);
        List<LatLng> pathPointsList = historyData.getPathPointsList();


        Log.d("path point count", ""+pathPointsList.size());
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.map);
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commit();
                }

                dialog.dismiss();
            }

        }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Fragment fragment = fragmentManager.findFragmentById(R.id.map);
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commit();
                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng point : pathPointsList) {
                        builder.include(point);
                    }
                    LatLngBounds bounds = builder.build();
                    int padding = 100;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    PolylineOptions polylineOptions = new PolylineOptions().addAll(pathPointsList).color(Color.RED).width(8);
                    pathPast = googleMap.addPolyline(polylineOptions);
                }
            });
        }
    }


}
