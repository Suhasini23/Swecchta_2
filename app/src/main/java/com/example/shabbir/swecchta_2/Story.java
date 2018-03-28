package com.example.shabbir.swecchta_2;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Story extends Fragment {
    public static Story newInstance() {
        Story fragment = new Story();
        return fragment;
    }

    private RecyclerView recyclerView;
    private PostAdapter mAdapter;
    private ProgressBar progressBar;
    private BottomNavigation bottomNavigation;
    private FragmentActivity myContext;
    private RecyclerView.LayoutManager layoutManager;
    private List<Post> arr;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         final View view=inflater.inflate(R.layout.story, container, false);

        progressBar=(ProgressBar)view.findViewById(R.id.progressBar2);
        arr=new ArrayList<>();

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Toast.makeText(getContext(),"This is a click",Toast.LENGTH_LONG).show();
            }
        });

        loadData();
         return view;
    }

    void loadData(){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getContext());
//        database = FirebaseDatabase.getInstance();
//        mStorage = FirebaseStorage.getInstance();
        String userID = shared.getString("uid","");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("posts");
        ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
                                if(!arr.isEmpty())
                                    mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
    }


    private void collectPhoneNumbers(Map<String,Object> users) {


        //ArrayList<String> phoneNumbers = new ArrayList<>();

        if(users == null){
//            arr.add(new MyPost("qwerty","Unkknown",0,"qazxswedcvfrtb","Null","Null","Null",0,"Null","Null",
//                    "Null"));
            return;
        }
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            String author = (String) singleUser.get("author");
            String description = (String) singleUser.get("description");
            String postId = (String) singleUser.get("postId");
            String postImageUrl = (String) singleUser.get("postImageUrl");
//            int price =  (int)(long) singleUser.get("price");
//            Object rating =  singleUser.get("rating");
//            String title = (String) singleUser.get("title");
            String userID = (String) singleUser.get("userID");
//            String locality = (String)singleUser.get("locality");
//            String food_type = (String)singleUser.get("food_type");
//            String recommendation = (String)singleUser.get("recommendation");
            String loc = (String)singleUser.get("location");
//            String sloc = (String)singleUser.get("sloc");
            arr.add(new Post(postId,author,postImageUrl,description,userID,loc));
//            shadow.add(new MyPost(postId,author,price,postImageUrl,description,title,userID,rating,locality,food_type,recommendation,loc,sloc,0));

        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new PostAdapter(arr);
        recyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
//        System.out.println(phoneNumbers.toString());
        // Toast.makeText(getContext(),phoneNumbers.toString(),Toast.LENGTH_LONG).show();
    }
}