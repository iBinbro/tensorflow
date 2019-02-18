package org.tensorflow.demo.photoSearch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
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
import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.photoSearch.Adapter.EvocationWordAdapter;
import org.tensorflow.demo.photoSearch.Evocation.Evocation;
import org.tensorflow.demo.photoSearch.data.AddWord;

import java.util.ArrayList;

/**
 * Created by mgo983 on 8/1/18.
 */

public class DisplayEvocationWords extends Activity  {

    EvocationWordAdapter adapter;
    // contains evoked words and their url
    ArrayList<String []> evoked_words_url = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // borrowing the gallery layout to display evocation
        setContentView(R.layout.activity_evocation);

        adapter = new EvocationWordAdapter(this, R.layout.grid_item_word);


        //from intent, get the search parameter
        Intent intent = this.getIntent();
        String[] searchParam = {""};
        searchParam[0] = intent.getStringExtra(ButtonTextAdapter.SEARCH_PARAM).toLowerCase();

        //get preferred search engine
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String prefSearchParam = sharedPref.getString(getString(R.string.pref_search_key),getString(R.string.pref_search_default_value));

        //Set initial word as title
        TextView textView = findViewById(R.id.first_word);
        textView.setText(searchParam[0]);

        Evocation evocation = new Evocation(this);
        ArrayList<String> evoked_words = evocation.findEvocationWords(searchParam[0]);
        getImageUrl(evoked_words);
        //adapter.set_all_evocative_words(evoked_words_url);

        GridView gridView = findViewById(R.id.grid_view_evocation);
        gridView.setAdapter(adapter);

        //make progressbar invisible after loading all evoked words
        ProgressBar progressBar = findViewById(R.id.loading_evoked_words);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void getImageUrl(ArrayList<String> evoked_words){

        for (String word : evoked_words){
            //if exists get from smarty symbols
            //if not found get from the Internet

            //if not found use default image
            getImageFromFirebase(word);

        }

    }


    private void getImageFromFirebase(final String evoked_word){
        FirebaseUser firebaseUser = OpenGalleryObjectActivity.firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            ((OpenGalleryObjectActivity) getApplicationContext()).signInAnonymously();
        }
        final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child(evoked_word.toLowerCase()).limitToFirst(1);
        final String WORD_IMAGE_REFERENCE  = "symbols";
        Log.d("the Query", mDatabaseQuery.toString());

        mDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d("exists ", "exists!");
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        String child_value = child.getValue().toString();
                        String[] getFileName = child_value.split("/");

                        //now that we have the file name retrieve image from firebase storage
                        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                        String currCategory = getFileName[0];
                        String fileName = getFileName[2];
                        firebaseStorage.child( WORD_IMAGE_REFERENCE + "/" + currCategory + "/" + fileName).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String [] word_url = {evoked_word, uri.toString() };
                                    adapter.addItem(word_url);
                                    //evoked_words_url.add(word_url);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //no corresponding image for this word
                                String [] word_url = {evoked_word, "NIL"};
                                adapter.addItem(word_url);
                                //evoked_words_url.add(word_url);
                            }
                        });
                        Log.d("child", child.getValue().toString());
                    }
                }else{
                    //no corresponding image for this word
                    String [] word_url = {evoked_word, "NIL"};
                    adapter.addItem(word_url);
                    //evoked_words_url.add(word_url);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
