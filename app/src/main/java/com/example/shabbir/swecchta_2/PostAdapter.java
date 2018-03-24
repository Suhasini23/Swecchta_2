package com.example.shabbir.swecchta_2;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.renderscript.Sampler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Shabbir Hussain on 4/21/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>  {

    private List<Post> postList;
    int i;



    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public ImageView imageView,foodType;
        public Button bt1,bt2;
        public MyViewHolder(View view){
            super(view);
            title = (TextView)view.findViewById(R.id.posttitle);
            imageView = (ImageView)view.findViewById(R.id.postimage);
//            foodType = (ImageView)view.findViewById(R.id.foodType);
//            Typeface custom_font = Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/pacifico.ttf");
//            title.setTypeface(custom_font);

        }
    }

    public PostAdapter(List<Post> post){
        postList = post;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_list_row,parent,false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);
        return  myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        i=position;
        Post post = postList.get(position);
//        String f = post.getFood_type();

        holder.title.setText(post.getDescription());
        Picasso.get()
                .load(post.getPostImageUrl())
                .placeholder(R.mipmap.placeholder)
                .error(R.mipmap.error)
                .fit()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }




}
