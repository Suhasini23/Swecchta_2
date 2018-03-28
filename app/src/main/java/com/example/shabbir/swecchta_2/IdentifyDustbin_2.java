package com.example.shabbir.swecchta_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

public class IdentifyDustbin_2 extends AppCompatActivity {

    ImageView pickImage;
    CardView loading;
    TextView latlong;
    TextView address;
    Button submit;

    private FirebaseStorage mStorage;
    private FirebaseDatabase database;
    private SharedPreferences shared;
    private DatabaseReference mDatabase;

    String userID,postImageUrl,postId,Useraddress,latlongText,author;

    //Location Variabe
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify_dustbin2);

        pickImage = (ImageView)findViewById(R.id.dustbinImage);
        loading = (CardView)findViewById(R.id.loading);
        latlong = (TextView)findViewById(R.id.latlong);
        address = (TextView)findViewById(R.id.address);
        submit = (Button)findViewById(R.id.submit);

        submit.setEnabled(false);
        getLocation();
        submit.setEnabled(true);
        //Important Initialization
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userID = shared.getString("uid","");
        postId= database.getReference("users").push().getKey();
        author=shared.getString("name","");
        //
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity()
                        .getIntent(getApplicationContext());
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });


    }

    public void post(View view){
        loading.setVisibility(View.VISIBLE);

        pickImage.setDrawingCacheEnabled(true);
        pickImage.buildDrawingCache();
        Bitmap bitmap = pickImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        Log.d("Shabbir",bitmap.getHeight()+"");
        byte[] data = baos.toByteArray();

        StorageReference storageRef = mStorage.getReference();
        StorageReference riversRef = storageRef.child("dustbinImages").child(userID+"bin").child(postId+".jpg");

        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                postImageUrl =""+ taskSnapshot.getDownloadUrl();
                mDatabase = database.getReference("dustbin").child(postId);
                Post post = new Post(postId,author,postImageUrl,latlongText,userID,Useraddress);
                mDatabase.setValue(post);
                mDatabase = database.getReference("dusbinLocation").child(postId).child("loaction");
                mDatabase.setValue(latlongText);
                loading.setVisibility(View.INVISIBLE);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int s =sharedPreferences.getInt("dustbin",0);
                editor.putInt("dustbin",(s+1));
                editor.apply();
                onBackPressed();

            }
        });
    }

    //This fetch Lat and Lang of the user
    public void getLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
//                            Toast.makeText(getApplicationContext(),location.getLatitude()+" hii "+location.getLongitude(),Toast.LENGTH_LONG).show();
                            // Start service and update UI to reflect new location
                            // startIntentService();
                            latlongText=location.getLatitude()+","+location.getLongitude();
                            latlong.setText(latlongText);
                            try {
                                displayAddress(mLastLocation);

                            }catch (Exception e){

                            }

                        }
                    }
                });

    }

    //Method converts Lat and Lang to Address
    public  void displayAddress(Location mLastLocation) throws Exception{
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        Useraddress = address+" "+city+" "+country+" "+postalCode;
        this.address.setText(Useraddress);

//        Toast.makeText(getContext(),address+"/"+city,Toast.LENGTH_LONG).show();
    }

    //Image result comes back
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                pickImage.setImageURI(uri);
                //imageCheck=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
