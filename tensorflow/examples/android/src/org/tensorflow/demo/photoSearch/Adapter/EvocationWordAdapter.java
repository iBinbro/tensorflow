package org.tensorflow.demo.photoSearch.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.OpenGalleryObjectActivity;
import org.tensorflow.demo.photoSearch.data.AddWord;

import java.util.ArrayList;

/**
 * Created by mgo983 on 8/1/18.
 */

public class EvocationWordAdapter extends GridAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String []> all_evocative_words = new ArrayList<String []>();


    public EvocationWordAdapter(Context context, int resource){
        super(context, resource);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addItem(String[] evocative_word){
        all_evocative_words.add(evocative_word);
        notifyDataSetChanged();
    }

    public void set_all_evocative_words( ArrayList<String[]> new_all_evocative_words){
        all_evocative_words = new_all_evocative_words;
    }

    @Override
    public int getCount(){
        return all_evocative_words.size();
    }

    @Override
    public String[] getItem(int position){
        return  all_evocative_words.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) { convertView = inflater.inflate(R.layout.grid_item_word,null);}

        String[] evoked_word = all_evocative_words.get(position);

        TextView textView = convertView.findViewById(R.id.text_word);
        textView.setText(evoked_word[0]);

        ImageView imageView = convertView.findViewById(R.id.image_word);

        // the url is the second item in the string
        getImageUrl(evoked_word[1],imageView );

        return convertView;
    }

    private void getImageUrl(String url, final ImageView imageView){
        if (url.equals("NIL")){
            Glide.with(context).load(R.drawable.ic_add_black_18dp).into(imageView);
        }else{
            Glide.with(context).load(Uri.parse(url)).into(imageView);
        }
    }
}


