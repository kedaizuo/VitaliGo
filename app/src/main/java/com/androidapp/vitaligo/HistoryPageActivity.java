package com.androidapp.vitaligo;
/*
How to set and get data from firebase and display it with adapter
Set data:
1. use map to store info
2. generate ref with auto key(push)
3. ref.setValue(map)
Get data:
1. generate ref
2. loop traverse all snapdata and get info with key
3. create HistoryData class where we create setter and getter.
4. create an instance of HistoryData with info and put it in list
5. create Adapater class where we write a getView method to regulate how info displays.
6. before writing getView, we need to write a item.xml to contain the views we want to display.
7. in getView, we set view content with the info in history
*/

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HistoryPageActivity extends AppCompatActivity {
    ImageButton communityButton;
    ImageButton userpageButton;
    ImageButton mainpageButton;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    ArrayList<HistoryData> histroyList;
    ArrayAdapter adapter;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_page);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        communityButton = findViewById(R.id.btnNav3);
        userpageButton = findViewById(R.id.btnNav4);
        mainpageButton = findViewById(R.id.btnNav1);

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryPageActivity.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(HistoryPageActivity.this, UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(HistoryPageActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(currentUser!=null){
//                    showInputDialog();
//                }
//                else{
//                    Toast.makeText(HistoryPageActivity.this, "Login first", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
        retriveData();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
    }
//    public void showInputDialog(){
//        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
//        builder.setTitle("Add history");
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.history_dialog, null);
//        builder.setView(dialogView);
//        final EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
//        final EditText editTextContent = dialogView.findViewById(R.id.editTextContent);
//
//
//
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                storeData(editTextTitle.getText().toString(), editTextContent.getText().toString());
//                dialog.dismiss();
//            }
//
//        }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//    public void storeData(String title, String content){
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("Title", title);
//        map.put("Content", content);
//
//        Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
//
//        String userEmail = currentUser.getEmail();
//        String validEmail = userEmail.replace(".", ",");
//        DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference()
//                .child("VitaliGo")
//                .child(validEmail)
//                .child("HistoryData")
//                .push();
//
//        recordRef.setValue(map)
//                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show());
//    }
    public void retriveData(){
        if(currentUser == null){
            return;
        }
        listView =  findViewById(R.id.listView);

        histroyList = new ArrayList<>();
        adapter = new HistoryDataAdapter(this, histroyList, getSupportFragmentManager());
        listView.setAdapter(adapter);
        String userEmail = currentUser.getEmail();
        String validEmail = userEmail.replace(".", ",");
        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child("VitaliGo").child(validEmail)
                .child("HistoryData");
        //Read all the data in sub branch 'USER NAME'
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                histroyList.clear();
                Log.d("data", "first level size = "+dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    List<LatLng> pathPointsList = new ArrayList<>();
                    for(DataSnapshot latlng: snapshot.child("pathPoints").getChildren()){
                        Map<String, Double> pathPoint = (Map<String, Double>) latlng.getValue();
                        LatLng point = new LatLng(pathPoint.get("latitude"), pathPoint.get("longitude"));
                        pathPointsList.add(point);
                    }
                    String runningDuration = snapshot.child("runningDuration").getValue(String.class);
                    String speed = snapshot.child("speed").getValue(String.class);
                    HistoryData historyData = new HistoryData(title, content,runningDuration, speed, pathPointsList, key);
                    //Log.d("data", "email = "+validEmail);
                    //Log.d("data", historyData.getTitle());
                    //Log.d("data", historyData.getPathPointsList().size()+"");
                    histroyList.add(historyData);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}
