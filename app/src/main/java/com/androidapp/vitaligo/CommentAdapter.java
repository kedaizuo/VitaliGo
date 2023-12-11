package com.androidapp.vitaligo;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class CommentAdapter extends ArrayAdapter<CommentItem>{
    public CommentAdapter(Context context, List<CommentItem> comments) {
        super(context, 0, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentItem commentItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.community_details_item, parent, false);
        }

        TextView tvCommenter = convertView.findViewById(R.id.tvCommenter);
        TextView tvComment = convertView.findViewById(R.id.tvComment);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        String userId = commentItem.getAuthorID();
        Log.d("userid", userId+"");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    tvCommenter.setText(username);
                } else {
                    tvCommenter.setText("Unknown User");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("CommentAdapter", "Failed to read username: ", databaseError.toException());
            }
        });

        tvComment.setText(commentItem.getComment());

        return convertView;
    }
}

