package com.example.shabbir.swecchta_2;



import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Dustbin extends Fragment {
    public static Dustbin newInstance() {
        Dustbin fragment = new Dustbin();
        return fragment;
    }

    Button identify;
    Button locate;

    ArrayList<String> arr=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dustbin, container, false);

        identify = (Button)view.findViewById(R.id.identify);
        locate = (Button)view.findViewById(R.id.locate);

        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext(),"Identify",Toast.LENGTH_LONG).show();
                if(isInternetOn()) {
                    Intent i = new Intent(getContext(), IdentifyDustbin_2.class);
                    startActivity(i);
                }
            }
        });

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();

//                Toast.makeText(getContext(),"Locate",Toast.LENGTH_LONG).show();

            }
        });



        return view;
    }

    void loadData(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("dusbinLocation");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void collectPhoneNumbers(Map<String,Object> users) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            arr.add((String)singleUser.get("loaction"));

        }


        String[] stockArr = new String[arr.size()];
        stockArr = arr.toArray(stockArr);


        Intent i=new Intent(getContext(),LocateDustbin.class);
        i.putExtra("arr",stockArr);
        startActivity(i);
//        Toast.makeText(this,arr.get(0),Toast.LENGTH_LONG).show();


    }
    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

//            Toast.makeText(getContext(), " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(getContext(), " Please check your internet connectivity ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


}