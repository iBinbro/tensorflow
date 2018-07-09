package org.tensorflow.demo.photoSearch.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Word;
import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.data.DatabaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by mgo983 on 3/22/18.
 */

public class SpeechWordAdapter extends ArrayAdapter {

    private ArrayList<Word> mWord = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;
    private String mCategory;
    private ComposeImageAdapter mComposeImageAdapter;

    public SpeechWordAdapter(Context context, int resource, ComposeImageAdapter composeImageAdapter){
        super(context, resource);
        inflater = LayoutInflater.from(context);
        mContext = context;
        mComposeImageAdapter = composeImageAdapter;
    }

    public boolean compareWord(Word word){
        int i = mWord.size() - 1;
        if (i < 0) return false;
        Word prevWord = mWord.get(i);
        return (prevWord.getWord().equals(word.getWord()));
    }

    public void addItem(Word word){
        mWord.add(word);
        notifyDataSetChanged();
    }

    public void setmCategory(String category){
        mCategory = category;
    }

    public void clearMWord(){
        mWord.clear();
    }

    @Override
    public int getCount(){
        return mWord.size();
    }

    @Override
    public Word getItem(int position){
        return mWord.get(position);
    }

    @Override

    public View getView(int position, View view, ViewGroup parent){
        if(null == view){
            view = inflater.inflate(R.layout.grid_item_word, null);
        }

        WordViewHolder wordViewHolder = new WordViewHolder(view);

        Word currWord = mWord.get(position);
        String word = currWord.getWord();
        String fileName = currWord.getFileName();
        wordViewHolder.mTextView.setText(word);

        //setOnClickListener(wordViewHolder, position);
        loadImage(fileName, wordViewHolder, position);
        return view;
    }

    private static class WordViewHolder{
        TextView mTextView;
        ImageView mImageView;
        LinearLayout linearLayout;

        public WordViewHolder(View view){
            mTextView =  view.findViewById(R.id.text_word);
            mImageView =  view.findViewById(R.id.image_word);
            linearLayout =  view.findViewById(R.id.l_grid_item_word);
        }
    }

    private void setOnClickListener(final WordViewHolder wordViewHolder, final int position){
        wordViewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mComposeImageAdapter.addItem(mWord.get(position));
            }
        });
    }

    private void loadImage(String fileName, final WordViewHolder wordViewHolder, final int position){
        String referencePath = DatabaseConstants.WORD_IMAGE_REFERENCE + "/" + mCategory + "/" + fileName;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(referencePath);
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mWord.get(position).setUri(uri);
                        setOnClickListener(wordViewHolder, position);
                        Glide.with(mContext).load(uri).into(wordViewHolder.mImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("image Error ", e.toString());
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

            }
        });
    }

}
