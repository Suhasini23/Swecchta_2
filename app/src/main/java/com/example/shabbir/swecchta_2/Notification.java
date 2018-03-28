package com.example.shabbir.swecchta_2;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Notification extends Fragment {
    public static Notification newInstance() {
        Notification fragment = new Notification();
        return fragment;
    }

    String msg;
    TextView notify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification, container, false);
        notify = (TextView)view.findViewById(R.id.notify);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString("uid", "");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notification").child(userId);//.child("posts");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                        notify.setText(msg);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        return view;
    }

    private void collectPhoneNumbers(Map<String, Object> users) {


        //ArrayList<String> phoneNumbers = new ArrayList<>();

        if (users == null) {
//            arr.add(new MyPost("qwerty","Unkknown",0,"qazxswedcvfrtb","Null","Null","Null",0,"Null","Null",
//                    "Null"));
            return;
        }
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            Map singleUser = (Map) entry.getValue();
            msg = (String) singleUser.get("msg");
//            arr.add((String)singleUser.get("loaction"));
        }
    }
}