package org.tensorflow.demo.photoSearch;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.photoSearch.Adapter.GridAdapter;
import org.tensorflow.demo.photoSearch.Adapter.WordCategoryAdapter;
import org.tensorflow.demo.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by mgo983 on 11/13/17.
 */

public class WordCategoriesActivity extends AppCompatActivity {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_sense_disambiguation);

        GridAdapter adapter;

        Intent intent = this.getIntent();
        String[] searchParam = {""};
        searchParam[0] = intent.getStringExtra(ButtonTextAdapter.SEARCH_PARAM).toLowerCase();

        //get preferred search engine
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String prefSearchParam = sharedPref.getString(getString(R.string.pref_search_key),getString(R.string.pref_search_default_value));

        TextView textView = (TextView) findViewById(R.id.search_word);
        textView.setText(searchParam[0]);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.explanationProgress);
        progressBar.setVisibility(View.INVISIBLE);

        adapter = new WordCategoryAdapter(this, R.layout.item_category, prefSearchParam);


        CheckInternetConnection checkInternetConnection = new CheckInternetConnection(this);

        if (checkInternetConnection.isNetworkConnected()){

            FetchClipArt fetchClipArt = new FetchClipArt(adapter,this, prefSearchParam);
            fetchClipArt.execute(searchParam);
            adapter = fetchClipArt.getAdapter();
        }

        ListView listView = (ListView) findViewById(R.id.word_categories);
        listView.setAdapter(adapter);

    }


    public void signInAnonymously() {
        firebaseAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

            }


        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}
