package com.example.shabbir.swecchta_2;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CreatePost extends Fragment {

    //User variables
    private String userID,postId,postImageUrl,author,Useraddress,descriptionText;

    //Database Variables
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private SharedPreferences shared;


    //Location Variabe
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;



    //ImageHolder Variable
    private ImageView pickImage;
    private EditText description;
    private TextView location;
    ProgressBar progressBar;
    private FragmentActivity myContext;


    public static CreatePost newInstance() {
        CreatePost fragment = new CreatePost();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = PreferenceManager.getDefaultSharedPreferences(getContext());
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userID = shared.getString("uid","");
        postId= database.getReference("users").push().getKey();
        author=shared.getString("name","");
        //Useraddress == location
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.createpost, container, false);

//        Calling  Location service
        if(isInternetOn()){
            getLocation();
        }else{
            Toast.makeText(getContext(),"No internet Connection is available",Toast.LENGTH_LONG).show();
        }


        progressBar = (ProgressBar)view.findViewById(R.id.mProgress);
        //Getting the image when it is clicked
        //
        pickImage = (ImageView)view.findViewById(R.id.image);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity()
                        .getIntent(getContext());
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        description = (EditText)view.findViewById(R.id.description);
        location    = (TextView)view.findViewById(R.id.location);

        //Posting to the database
        Button Submit = (Button)view.findViewById(R.id.Submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(description.getText().toString() == null){

                }else{
                    new Demo().execute();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });


        return view;
    }

    class Demo extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            //POsting Image
            pickImage.setDrawingCacheEnabled(true);
            pickImage.buildDrawingCache();
            Bitmap bitmap = pickImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            Log.d("Shabbir",bitmap.getHeight()+"");
            byte[] data = baos.toByteArray();

            StorageReference storageRef = mStorage.getReference();
            StorageReference riversRef = storageRef.child("images").child(userID).child(postId+".jpg");

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
                    mDatabase = database.getReference("posts").child(postId);

                    Post post = new Post(postId,author,postImageUrl,descriptionText,userID,Useraddress);

                    mDatabase.setValue(post);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    int s =sharedPreferences.getInt("Post",0);
                    editor.putInt("Post",(s+1));


                }
            });
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            descriptionText = description.getText().toString();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.INVISIBLE);
            Fragment selectedFragment = CreatePost.newInstance();
            FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout,selectedFragment);
            transaction.commit();
//            Toast.makeText(getContext(),"Posted",Toast.LENGTH_LONG).show();
        }
    }



    //This fetch Lat and Lang of the user
    public void getLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
//                            Toast.makeText(getContext(),location.getLatitude()+" hii "+location.getLongitude(),Toast.LENGTH_LONG).show();
                            // Start service and update UI to reflect new location
                            // startIntentService();
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
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        Useraddress = address+" "+city+" "+country+" "+postalCode;
        location.setText(Useraddress);

//        Toast.makeText(getContext(),address+"/"+city,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
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

            Toast.makeText(getContext(), " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}