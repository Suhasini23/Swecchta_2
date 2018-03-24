package com.example.shabbir.swecchta_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class LocateDustbin extends AppCompatActivity
        implements OnMapReadyCallback {


    String[] arr;
    LatLng sydney;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_dustbin);

        Bundle b=getIntent().getExtras();
         arr=b.getStringArray("arr");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

//        Toast.makeText(this,"hdhdhd"+arr.length,Toast.LENGTH_LONG).show();
        for(String address : arr){
            String[] myLoc=address.split(",");
            Toast.makeText(this,myLoc[0]+"haha",Toast.LENGTH_LONG).show();
             sydney = new LatLng(Double.parseDouble(myLoc[0]), Double.parseDouble(myLoc[1]));
//            LatLng sydney = new LatLng(-33.852, 151.211);
            googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Dustbin Location"));
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        LatLng sydney = new LatLng(-33.852, 151.211);
//        LatLng sydney2 = new LatLng(33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Marker in Sydney"));
//        googleMap.addMarker(new MarkerOptions().position(sydney2)
//                .title("Marker in Sydney"));
//

    }


}
