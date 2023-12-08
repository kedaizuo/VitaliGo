package com.androidapp.vitaligo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Calendar;


public class MapsActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private PendingIntent mPendingIntent = null;

    private GnssStatus.Callback mGnssStatusCallback;
    private GoogleMap mMap;
    private LocationManager mLocationManager;

    private Toolbar mToolbar;

    private Marker currentUserLocationMarker;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final float MIN_DISTANCE_NEAR = 0.5F;


    private ImageButton centerButton;
    private ImageButton historyButton;
    private ImageButton communityButton;
    private ImageButton userpageButton;

    private static final String SharedPreferenceName = "MyPrefs";
    private static final String needToBeCenteredString = "needToBeCenrtered";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private List<LatLng> pathPointsList;
    private Polyline pathPast;
    private float meterPathPast = 0;
    private int indexCaculatedInPathPastList = 0;
    private EditText distanceTargetView;
    private TextView distanceRanView;
    private Button startButton;
    private boolean recordStarted = false;
    private float distanceTarget = 0;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (pathPointsList == null) {
            pathPointsList = new ArrayList<>();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        centerButton = findViewById(R.id.centerButton);
        historyButton = findViewById(R.id.btnNav2);
        communityButton = findViewById(R.id.btnNav3);
        userpageButton = findViewById(R.id.btnNav4);
        distanceRanView = findViewById(R.id.distanceRan);
        distanceTargetView = findViewById(R.id.targetDistance);
        startButton = findViewById(R.id.startButton);

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean curr_status = sharedPreferences.getBoolean(needToBeCenteredString, false);
                curr_status = !curr_status;
                editor.putBoolean(needToBeCenteredString, curr_status);
                editor.apply();
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, HistoryPageActivity.class);
                startActivity(intent);
            }
        });
        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, CommunityPageActivity.class);

                Log.d("AAA", "BBB");
                startActivity(intent);
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    Intent intent = new Intent(MapsActivity.this, UserPageActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }


            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!recordStarted) {
                    if (!isValidFloat(distanceTargetView.getText().toString())) {
                        Toast.makeText(MapsActivity.this, "Please input a valid goal", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    recordStarted = true;
                    startButton.setText("End");
                    distanceTargetView.setEnabled(false);
                    distanceTarget = Float.valueOf(distanceTargetView.getText().toString());


                } else {
                    recordStarted = false;
                    clearPathPast();

                }


            }
        });
        mToolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(mToolbar);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        currentUserLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        if (validatePoint(userLocation) && recordStarted) {
            drawPathPast(userLocation);
            calculateTotalDistance();
            updateDistanceRan();
            if(distanceTarget*1000 <=meterPathPast){
                sendNotification();
                clearPathPast();
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(needToBeCenteredString, false)) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
        }
    }

    private void drawPathPast(LatLng userLocation) {

        if (pathPointsList == null) {
            pathPointsList = new ArrayList<>();
        }

        pathPointsList.add(userLocation);

        if (pathPast == null) {
            PolylineOptions polylineOptions = new PolylineOptions().addAll(pathPointsList).color(Color.RED).width(8);
            pathPast = mMap.addPolyline(polylineOptions);
        } else {

            pathPast.setPoints(pathPointsList);
        }
    }

    private void clearPathPast() {
        saveDataToDatabase();
        if (pathPast != null) {
            pathPast.remove();
            pathPast = null;
        }
        pathPointsList.clear();
        recordStarted = false;
        setViewStatus();

//        meterPathPast = 0;
//        indexCaculatedInPathPastList = 0;
//        startButton.setText("start");
//        distanceTargetView.setEnabled(true);
//        distanceTarget = 0;
//        distanceTargetView.setText("Input Goal");
//        recordStarted = false;
    }

    private void savPathPastToLocal() {
        //Log.d("path", "save to local");
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pathPointsList);
        editor.putString("pathPointList", json);
        editor.putBoolean("recordStarted", recordStarted);
        editor.putFloat("distanceTarget", distanceTarget);
        editor.apply();
    }

    private void retrivePathPastFromLocal() {
        //Log.d("path", "retrieve from local");
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceName, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("pathPointList", null);
        Type type = new TypeToken<ArrayList<LatLng>>() {
        }.getType();
        pathPointsList = gson.fromJson(json, type);
        recordStarted = sharedPreferences.getBoolean("recordStarted", false);
//        if (recordStarted) {
//            startButton.setText("End");
//            distanceTargetView.setEnabled(false);
//            distanceTarget = sharedPreferences.getFloat("distanceTarget", 0);
//            distanceTargetView.setText(Float.toString(distanceTarget));
//
//
//        } else {
//            startButton.setText("Start");
//            distanceTargetView.setEnabled(true);
//            distanceTarget = 0;
//            distanceTargetView.setText("Input Goal");
//        }
        setViewStatus();
        //Log.d("path", ""+(pathPointsList==null));

    }
    public void saveDataToDatabase(){
        if(meterPathPast<=0.01){
            Toast.makeText(MapsActivity.this, "This running is too short, and can't be saved.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userEmail = currentUser.getEmail();
        String validEmail = userEmail.replace(".", ",");
        DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference()
                .child("VitaliGo")
                .child(validEmail)
                .child("HistoryData")
                .push();

        List<Map<String, Double>> latLngList = new ArrayList<>();
        for (LatLng latLng : pathPointsList) {
            Map<String, Double> latLngMap = new HashMap<>();
            latLngMap.put("latitude", latLng.latitude);
            latLngMap.put("longitude", latLng.longitude);
            latLngList.add(latLngMap);
        }


        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 月份是从 0 开始的，所以需要加 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制
        int minute = calendar.get(Calendar.MINUTE);


        String date = hour+":"+minute+" on "+month+"/"+day+"/"+year;

        recordRef.child("title").setValue("Finished at "+date);
        recordRef.child("content").setValue(Float.toString(meterPathPast/1000));
        recordRef.child("pathPoints").setValue(latLngList)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show());
    }
    private void setViewStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceName, MODE_PRIVATE);
        if (recordStarted) {
            startButton.setText("End");
            distanceTargetView.setEnabled(false);
            distanceTarget = sharedPreferences.getFloat("distanceTarget", 0);
            distanceTargetView.setText(Float.toString(distanceTarget));


        } else {
            startButton.setText("Start");
            distanceTargetView.setEnabled(true);
            distanceTarget = 0;
            distanceTargetView.setText("Input Goal");
            meterPathPast = 0;
            indexCaculatedInPathPastList = 0;

        }
    }
    public void calculateTotalDistance() {

        int curlen = pathPointsList.size();
        Log.d("path", "index = " + indexCaculatedInPathPastList);
        Log.d("path", "curlen = " + curlen);
        for (int i = indexCaculatedInPathPastList + 1; i < curlen; i++) {
            LatLng pointA = pathPointsList.get(i - 1);
            LatLng pointB = pathPointsList.get(i);

            float[] results = new float[1];
            Location.distanceBetween(pointA.latitude, pointA.longitude, pointB.latitude, pointB.longitude, results);
            meterPathPast += results[0];
        }

        indexCaculatedInPathPastList = curlen - 1;
        Log.d("path", "meters = " + meterPathPast);
    }

    public boolean validatePoint(LatLng point) {
        int len = pathPointsList.size();
        if (len < 1) {
            return true;
        }
        LatLng lastPoint = pathPointsList.get(len - 1);
        float[] results = new float[1];
        Location.distanceBetween(point.latitude, point.longitude, lastPoint.latitude, lastPoint.longitude, results);
        float distance = results[0];
        if (distance < MIN_DISTANCE_NEAR) {
            return false;
        } else {
            return true;
        }
    }

    private void updateDistanceRan() {
        distanceRanView.setText(String.format("%.2f km", meterPathPast / 1000));
    }

    private boolean isValidFloat(String text) {
        try {
            Float.parseFloat(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //When you target Android 8.0 (API level 26), you must implement notification channels to display notifications
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        // Create a notification channel if API level is 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my_channel";
            String description = "ECE 150 - UCSB Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            // Create a new Notification Channel
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("VitaliGo")
                .setContentText("You have finished your goal.")
                .setContentInfo("Info")
                .setContentIntent(pendingIntent);
        notificationManager.notify(/*notification id*/1, notificationBuilder.build());




    }
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    private GeofencingRequest getGeofenceRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() throws SecurityException {
        super.onStart();
        // [TODO] Ensure that necessary permissions are granted (look in AndroidManifest.xml to
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
            }, 1);
            return;
        }
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                    mLocationManager.registerGnssStatusCallback(mGnssStatusCallback);
                }
            } else {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrivePathPastFromLocal();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savPathPastToLocal();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();

        mLocationManager.removeUpdates(this);
        mLocationManager.unregisterGnssStatusCallback(mGnssStatusCallback);
    }

}
