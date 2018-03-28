package com.example.shabbir.swecchta_2;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment {
    public static Profile newInstance() {
        Profile fragment = new Profile();
        return fragment;
    }

    private SharedPreferences shared;
    String name, email, pic,post,dustbin,uid;

    TextView postNo,dustbinNo,userName,userEmail;
    CircleImageView profilePic;
    private FragmentActivity myContext;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = PreferenceManager.getDefaultSharedPreferences(getContext());
        name=shared.getString("name","John Doe");
        email=shared.getString("email","");
        pic=shared.getString("pic","android.resource://com.example.shabbir.swecchta_2/mipmap/man");
        post=shared.getInt("Post",0)+"";
        dustbin=shared.getInt("dustbin",0)+"";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.profile, container, false);

        postNo=(TextView)view.findViewById(R.id.postNo);
        dustbinNo=(TextView)view.findViewById(R.id.dustbinNo);
        userName=(TextView)view.findViewById(R.id.name);
        userEmail=(TextView)view.findViewById(R.id.email);
         profilePic=(CircleImageView) view.findViewById(R.id.profile);

        ImageView edit=(ImageView)view.findViewById(R.id.edit);

        postNo.setText(post+" Posts");
        dustbinNo.setText(dustbin+" Dustbins Identified");
        userName.setText(name);
        userEmail.setText(email);

        Picasso.get()
                .load(pic)
                .placeholder(R.mipmap.man)
                .error(R.mipmap.error)
                .fit()
                .into(profilePic);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDetails();
            }
        });
        return view;
    }

    public void changeDetails(){
//        Toast.makeText(getContext(),"This is edit click",Toast.LENGTH_LONG).show();
        Intent intent = CropImage.activity()
                .getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    public void updateProfilePic(){
//        progressBar.setVisibility(View.VISIBLE);
        profilePic.setDrawingCacheEnabled(true);
        profilePic.buildDrawingCache();
        Bitmap bitmap = profilePic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        Log.d("Shabbir",bitmap.getHeight()+"");
        byte[] data = baos.toByteArray();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        uid=sharedPreferences.getString("uid","");
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference();
        StorageReference riversRef = storageRef.child("images").child(uid).child("myprofilepic"+".jpg");

        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                String postImageUrl =""+ taskSnapshot.getDownloadUrl();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("pic",postImageUrl);
                editor.apply();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid).child("urlToProfileImage");
                mDatabase.setValue(postImageUrl);

//                progressBar.setVisibility(View.INVISIBLE);


            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                profilePic.setImageURI(uri);
                updateProfilePic();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }


}