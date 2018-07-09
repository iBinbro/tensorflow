package org.tensorflow.demo.photoSearch;

import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import org.tensorflow.demo.photoSearch.Adapter.ComposeImageAdapter;
import org.tensorflow.demo.photoSearch.Adapter.SpeechCategoryAdapter;
import org.tensorflow.demo.photoSearch.Adapter.SpeechWordAdapter;
import org.tensorflow.demo.photoSearch.data.DatabaseConstants;
import org.tensorflow.demo.R;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mgo983 on 3/21/18.
 */

public class SpeakActivity extends AppCompatActivity {

    SpeechCategoryAdapter speechCategoryAdapter;
    SpeechWordAdapter speechWordAdapter;
    ComposeImageAdapter composeImageAdapter;

    ListView categoryListView;
    GridView wordGridView;
    RecyclerView recyclerView;

    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);

        composeImageAdapter = new ComposeImageAdapter(this);
        speechWordAdapter = new SpeechWordAdapter(this, R.layout.grid_item_word, composeImageAdapter);
        speechCategoryAdapter = new SpeechCategoryAdapter(this, R.layout.item_category, speechWordAdapter);

        categoryListView = findViewById(R.id.category_list);
        wordGridView = findViewById(R.id.word_grid);
        recyclerView = findViewById(R.id.compose_image);

        getAllCategories();
        //getAllWords();


        categoryListView.setAdapter(speechCategoryAdapter);
        wordGridView.setAdapter(speechWordAdapter);
        recyclerView.setAdapter(composeImageAdapter);
        //categoryListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //categoryListView.setSelector(getResources().getDrawable(R.drawable.list_item_select));

        // LinearLayoutManager.HORIZONTAL allows for horizontal scrolling
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }


    private void getAllCategories(){
        DatabaseReference categoryDatabaseReference = FirebaseDatabase.getInstance().getReference(DatabaseConstants.WORD_CATEGORY);
        categoryDatabaseReference.orderByChild(DatabaseConstants.WORD_CATEGORY).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null){
                    String category = dataSnapshot.getKey();
                    //Log.d("dataSnapshot values", s + "");
                    speechCategoryAdapter.addItem(category);
                }

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
}

