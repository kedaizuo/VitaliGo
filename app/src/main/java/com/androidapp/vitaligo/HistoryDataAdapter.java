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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HistoryDataAdapter extends ArrayAdapter<HistoryData> {


    private FragmentManager fragmentManager;
    private Polyline pathPast;
    FirebaseUser currentUser;
    public HistoryDataAdapter(Context context, List<HistoryData> historyDataList, FragmentManager fragmentManager) {
        super(context, 0, historyDataList);
        this.fragmentManager = fragmentManager;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_item, parent, false);
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                showDialogWithMap(position);
                return true;
            }
        });
        HistoryData historyData = getItem(position);


        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewContent = convertView.findViewById(R.id.textViewContent);
        TextView runningDurationView = convertView.findViewById(R.id.runningDuration);
        TextView speedView = convertView.findViewById(R.id.speed);

        textViewTitle.setText(historyData.getTitle());
        textViewContent.setText("Distance covered: "+historyData.getContent());
        runningDurationView.setText("Running time: "+historyData.getRunningDuration());
        speedView.setText("Speed: "+historyData.getSpeed());


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

        }).setNegativeButton("Delete",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userEmail = currentUser.getEmail();
                String validEmail = userEmail.replace(".", ",");
                DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child("VitaliGo").child(validEmail)
                        .child("HistoryData").child(historyData.getDataBaseId());
                reference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                    }
                });

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
                    Marker startPos = googleMap.addMarker(new MarkerOptions()
                            .position(pathPointsList.get(0))
                            .title("Start Position")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    Marker endPos = googleMap.addMarker(new MarkerOptions()
                            .position(pathPointsList.get(pathPointsList.size()-1))
                            .title("End Position")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            });
        }
    }


}
