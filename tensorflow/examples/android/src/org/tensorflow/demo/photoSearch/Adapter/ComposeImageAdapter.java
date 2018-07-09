package org.tensorflow.demo.photoSearch.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Word;
import org.tensorflow.demo.R;

import java.util.ArrayList;

/**
 * Created by mgo983 on 3/27/18.
 */

public class ComposeImageAdapter extends RecyclerView.Adapter<ComposeImageAdapter.MyViewHolder> {

    private ArrayList<Word> mWord = new ArrayList<>();
    private Context mContext;

    public ComposeImageAdapter(Context context){
        mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textView;

        public MyViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.item_compose_image);
            textView = view.findViewById(R.id.item_compose_text);
        }
    }

    public void addItem(Word word){
        mWord.add(word);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return mWord.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.compose_image_item,parent, false);


        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Word currMWOrd = mWord.get(position);
        Uri uri = currMWOrd.getUri();
        String word = currMWOrd.getWord();
        holder.textView.setText(word);
        Glide.with(mContext).load(uri).into(holder.imageView);
        Log.d("binder view", uri.toString());
    }
}
