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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Word;
import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.data.DatabaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by mgo983 on 3/21/18.
 */

public class SpeechCategoryAdapter extends ArrayAdapter {

    private ArrayList<String> mCategory = new ArrayList<>();
    private LayoutInflater inflater;
    private SpeechWordAdapter speechWordAdapter;
    private Context mContext;
    private int prevPosition = 0;
    private  Word firstWord = new Word();

    private class CategoryViewHolder{
        public ImageView imageView;
        public TextView textView;
        public ImageButton imageButton;
        public LinearLayout linearLayout;
        public boolean IsGreen = true;

        public CategoryViewHolder(View view){
            imageView = view.findViewById(R.id.image_category);
            textView = view.findViewById(R.id.text_category);
            imageButton = view.findViewById(R.id.more_category);
            linearLayout = view.findViewById(R.id.l_grid_item_word);
        }
    }

    public SpeechCategoryAdapter(Context context, int resource, SpeechWordAdapter mSpeechWordAdapter){
        super(context, resource);
        inflater = LayoutInflater.from(context);
        speechWordAdapter = mSpeechWordAdapter;
        mContext = context;
    }

    public void addItem(String category){
        mCategory.add(category);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mCategory.size();
    }

    @Override
    public String getItem(int position){
        return mCategory.get(position);
    }



    @Override
    public View getView(int position, View view, final ViewGroup parent) {
        if (null == view){
            view = inflater.inflate(R.layout.item_category, null);
        }
        final CategoryViewHolder viewHolder = new CategoryViewHolder(view);
        String category = mCategory.get(position);
        String repCategory = category.replace("_"," ");
        String CapCategory = repCategory.substring(0,1).toUpperCase() + repCategory.substring(1);
        viewHolder.textView.setText(CapCategory);
        viewHolder.imageButton.setVisibility(View.INVISIBLE);
        getImageUrl(category, viewHolder);
        getCategoryImage(firstWord, viewHolder, category);


        imageViewOnClickListener(view, viewHolder, category, parent, position);


        return view;
    }

    public void getImageUrl(final String category, final CategoryViewHolder viewHolder){
        final DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference(DatabaseConstants.WORD_CATEGORY).child(category);

        categoryReference.orderByChild(category).limitToFirst(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Word word = dataSnapshot.getValue(Word.class);
                getCategoryImage(word, viewHolder, category);
                Log.d("1st word in category ", word.getFileName().toString());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCategoryImage(final Word word, final CategoryViewHolder viewHolder, String category){
        if (word.getFileName() == null) return;
        String referencePath = DatabaseConstants.WORD_IMAGE_REFERENCE + "/" + category + "/" + word.getFileName();
        Log.d("reference path", referencePath);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(referencePath);
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        word.setUri(uri);
                        Glide.with(mContext).load(uri).into(viewHolder.imageView);
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

    private void imageViewOnClickListener(final View sview, final CategoryViewHolder viewHolder, final String category, final ViewGroup parent, final int position){

       sview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechWordAdapter.clearMWord();
                speechWordAdapter.setmCategory(category);
                populateSpeechWordAdapter(category, view);
                sview.setSelected(true);
                //((ListView) parent).setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                //((ListView) parent).setSelector(mContext.getResources().getDrawable(R.drawable.dialog_border));
                //sview.setFocusable(true);
                //sview.setPressed(true);


            }
        });

    }

    private void populateSpeechWordAdapter(final String category, final View view){
        final DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference(DatabaseConstants.WORD_CATEGORY).child(category);
        categoryReference.orderByChild(category).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Word word = dataSnapshot.getValue(Word.class);
                        if (!speechWordAdapter.compareWord(word))
                            speechWordAdapter.addItem(word);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        view.setSelected(true);
    }

}
