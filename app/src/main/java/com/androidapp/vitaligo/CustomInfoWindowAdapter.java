package com.androidapp.vitaligo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;



public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private final Context context;
    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);

        TextView tvLat = view.findViewById(R.id.tv_lat);
        TextView tvLng = view.findViewById(R.id.tv_lng);

        LatLng latLng = marker.getPosition();
        tvLat.setText("Latitude: " + latLng.latitude);
        tvLng.setText("Longitude: " + latLng.longitude);

        return view;
    }
}
