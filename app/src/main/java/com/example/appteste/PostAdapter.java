package com.example.appteste;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class PostAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<Post> posts;

    PostAdapter(List<Post> posts, Context ctx){

        this.posts = posts;
        inflater = LayoutInflater.from(ctx);

    }

    @Override
    public int getCount(){
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.activity_lista_principal, null);
        TextView txtPost = v.findViewById(R.id.txtTextoPost);
        TextView txtUsername = v.findViewById(R.id.txtUserName);
        ImageView imgPost = v.findViewById(R.id.imgPost);

        Post p = posts.get(position);
        txtPost.setText(p.getText());
        txtUsername.setText(p.getUserName());
        if(p.getImage() != null){
            byte imageData[] = Base64.decode(p.getImage(), Base64.DEFAULT);
            Bitmap img = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            imgPost.setImageBitmap(img);
        }

        return v;
    }
}
