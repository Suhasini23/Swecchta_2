package com.example.shabbir.swecchta_2;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {
    public static Profile newInstance() {
        Profile fragment = new Profile();
        return fragment;
    }

    private SharedPreferences shared;
    String name, email, pic,post,dustbin;

    TextView postNo,dustbinNo,userName,userEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = PreferenceManager.getDefaultSharedPreferences(getContext());
        name=shared.getString("name","John Doe");
        email=shared.getString("email","");
        pic=shared.getString("pic","android.resource://com.example.shabbir.swecchta_2/mipmap/man");
        post=shared.getInt("Post",5)+"";
        dustbin=shared.getInt("dustbin",10)+"";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.profile, container, false);

        postNo=(TextView)view.findViewById(R.id.postNo);
        dustbinNo=(TextView)view.findViewById(R.id.dustbinNo);
        userName=(TextView)view.findViewById(R.id.name);
        userEmail=(TextView)view.findViewById(R.id.email);
        CircleImageView profilePic=(CircleImageView) view.findViewById(R.id.profile);

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

        return view;
    }
}