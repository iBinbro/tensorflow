package org.tensorflow.demo.photoSearch.data;

import android.content.Context;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.TextDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by mgo983 on 11/6/17.
 */

public class AddWord extends AppCompatActivity {

    public static String WORD_CATEGORY_CHILD = "word_categories";
    public static String WORD_REFERENCE = "word";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fagment_detail);


        final ArrayList<String> wordCategories = new ArrayList<>();

        final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference(WORD_CATEGORY_CHILD);
        ListView listView = (ListView) findViewById(R.id.list_view_word);

        final AddWordAdapter addWordAdapter = new AddWordAdapter(this, R.layout.list_item_result, wordCategories);
        listView.setAdapter(addWordAdapter);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_complete);
        progressBar.setVisibility(View.INVISIBLE);

        mFirebaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                wordCategories.add(dataSnapshot.getKey().toString());
                addWordAdapter.notifyDataSetChanged();
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


    public class AddWordAdapter extends ArrayAdapter{
        ArrayList wordCategories;
        LayoutInflater inflater;

        public AddWordAdapter(Context context, int resource, ArrayList _wordCategories){
            super(context, resource, _wordCategories);
            wordCategories = _wordCategories;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) view = inflater.inflate(R.layout.list_item_result, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.list_search_result_text);
            final String wordCategory = wordCategories.get(i).toString();
            textView.setText(wordCategory);

            final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference(WORD_CATEGORY_CHILD);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.DialogFragment newFragment = new TextDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString(TextDialog.DATABASECALLERTYPE,TextDialog.CREATE_WORD_DATABASE);
                    bundle.putString(TextDialog.WORDCATEGORY, wordCategory);
                    newFragment.setArguments(bundle);
                    newFragment.show(getFragmentManager(), "TextDialog");
                }
            });
            return view;
        }

        private void addCategory(){
            android.app.DialogFragment newFragment = new TextDialog();
            Bundle bundle = new Bundle();
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "TextDialog");
        }
    }

}
