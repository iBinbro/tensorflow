package org.tensorflow.demo.photoSearch.Adapter;

import android.content.Context;
import android.net.Uri;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Word;
import org.tensorflow.demo.photoSearch.CheckInternetConnection;
import org.tensorflow.demo.photoSearch.FetchClipArt;
import org.tensorflow.demo.photoSearch.JSONHandler;
import org.tensorflow.demo.photoSearch.OpenClipArtJSONHandler;
import org.tensorflow.demo.photoSearch.OpenGalleryObjectActivity;
import org.tensorflow.demo.photoSearch.PixabayJSONHandler;
import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.data.AddWord;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mgo983 on 11/13/17.
 */

public class WordCategoryAdapter extends GridAdapter {

    private LayoutInflater inflater;
    private ArrayList<Words> mData = new ArrayList<>();
    private Context context;
    private String searchEngine;
    private TextToSpeech myTTS;

    public WordCategoryAdapter(Context _context, int resource, String _searchEngine, TextToSpeech tts){
        super(_context, resource);
        inflater = LayoutInflater.from(_context);
        context = _context;
        searchEngine = _searchEngine;
        this.myTTS = tts;
    }

    public void addItem(final String category, final Words wordData){
        mData.add(wordData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    @Override
    public Words getItem(int position){
        return  mData.get(position);
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
        if (convertView == null) { convertView = inflater.inflate(R.layout.item_category,null);}

        final Words word = mData.get(position);
        Log.d("The position ", position + " " + word.getCategory());

        TextView textView = (TextView) convertView.findViewById(R.id.text_category);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_category);

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.more_category);

        final String category = word.getCategory();

        textView.setText(category);

        String url = word.getUrl();
        String[] jsonUrl ={};
        JSONHandler jsonHandler;
        final int FIRST_POSITION = 0;
        try{
            switch (searchEngine){
                case FetchClipArt.ENGINE_PIXABAY:
                    jsonHandler = new PixabayJSONHandler();
                    jsonUrl = jsonHandler.getImageUrl(url);
                    break;
                case FetchClipArt.ENGINE_OPENCLIPART:
                    jsonHandler = new OpenClipArtJSONHandler();
                    jsonUrl = jsonHandler.getImageUrl(url);
                    break;
            }

        }catch (JSONException e){

            Log.e("JSON Error: ", e.toString());

        }

        setImageOnClickListener(imageView, category, word);

        setMoreOnClickListener(imageButton, category, word);

        if (jsonUrl != null) url = jsonUrl[FIRST_POSITION];

        Glide
            .with(context)
            .load(url)
            .into(imageView);

        return convertView;

    }
    private void setImageOnClickListener(ImageView imageView, final String category, final Words word){
        final String[] searchWord = {""};
        searchWord[0] = word.getWord();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInternetConnection checkInternetConnection = new CheckInternetConnection(context);

                GridAdapter adapter = new GridAdapter(context, R.id.item_grid);

                TextView textView = (TextView) ((AppCompatActivity) context).findViewById(R.id.search_word);
                textView.setText(searchWord[0]);

                if (category.equals("INTERNET")){
                    if (checkInternetConnection.isNetworkConnected()){

                        FetchClipArt fetchClipArt = new FetchClipArt(adapter,context, searchEngine);
                        fetchClipArt.execute(searchWord);
                    }
                }else{
                    localSearch(searchWord[0],category, adapter);
                }

            }
        });
    }

    private void localSearch(final String searchString,  final String category, final GridAdapter adapter){


            FirebaseUser firebaseUser = OpenGalleryObjectActivity.firebaseAuth.getCurrentUser();
            if (firebaseUser == null){
                ((OpenGalleryObjectActivity) context).signInAnonymously();
            }
            final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child(searchString.toLowerCase());
            final String WORD_IMAGE_REFERENCE  = "symbols";
            Log.d("the Query", mDatabaseQuery.toString());

            mDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final ArrayList lUrls = new ArrayList();
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            String wordEntries = (String) child.getValue();
                            String[] getFileName = wordEntries.split("/");
                            Log.d("The entries: ", wordEntries);
                            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                            String currCategory = getFileName[0];
                            String fileName = getFileName[2];
                            if (category.equals(currCategory)){
                                firebaseStorage.child( WORD_IMAGE_REFERENCE + "/" + currCategory + "/" + fileName).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                lUrls.add(uri.toString());
                                                setImageAdapter(adapter,lUrls);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Download error ", e.toString());
                                    }
                                });
                            }


                        }


                    }else{
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void setImageAdapter(GridAdapter adapter, ArrayList<String> lUrls){
            String [] url = new String[lUrls.size()];
            int count = 0;
            for (String urls : lUrls){
                    url[count] = urls;
                count++;
            }
            adapter = new GridAdapter(context, url);
            GridView gridView = (GridView) ((AppCompatActivity) context).findViewById(R.id.image_gridview);
            gridView.setAdapter(adapter);

            ProgressBar progressBar = (ProgressBar) ((AppCompatActivity) context).findViewById(R.id.explanationProgress);
            progressBar.setVisibility(View.INVISIBLE);
        }

    private void setImageAdapter(ArrayListGridAdapter adapter){

        GridView gridView = (GridView) ((AppCompatActivity) context).findViewById(R.id.image_gridview);
        gridView.setAdapter(adapter);

        ProgressBar progressBar = (ProgressBar) ((AppCompatActivity) context).findViewById(R.id.explanationProgress);
        progressBar.setVisibility(View.INVISIBLE);
    }



    private void setMoreOnClickListener(ImageButton imageButton, final String category, final Words word){
        final String[] searchWord = {""};
        searchWord[0] = word.getWord();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressBar progressBar = (ProgressBar) ((AppCompatActivity) context).findViewById(R.id.explanationProgress);
                progressBar.setVisibility(View.VISIBLE);

                TextView textView = (TextView) ((AppCompatActivity) context).findViewById(R.id.search_word);
                textView.setText(category);

                ArrayListGridAdapter adapter = new ArrayListGridAdapter(context, R.id.item_grid, myTTS);

                if (!category.equals("INTERNET")){

                    localCategorySearch(category, adapter);
                }

            }
        });
    }

    public boolean compareWord(ArrayList<HashMap> allEntries, HashMap newEntry ){
        //int i = mWord.size() - 1;
        int i = allEntries.size() - 1;
        if (i < 0) return false;
        String prevWord =  (String) allEntries.get(i).get("word");
        return (prevWord.equals(newEntry.get("word")));
    }
    private void localCategorySearch( final String category, final ArrayListGridAdapter adapter){


        FirebaseUser firebaseUser = OpenGalleryObjectActivity.firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            ((OpenGalleryObjectActivity) context).signInAnonymously();
        }
        final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_CATEGORY_CHILD).child(category);
        final String WORD_IMAGE_REFERENCE  = "symbols";
        Log.d("the Query", mDatabaseQuery.toString());

        final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

        final ArrayList<HashMap>  Allentries = new ArrayList<>();

        mDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final ArrayList lUrls = new ArrayList();
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        if (child.getValue() instanceof HashMap){
                            HashMap wordEntries = (HashMap) child.getValue();
                            // compare words do that unique words are displayed
                            if (! compareWord(Allentries, wordEntries))
                            Allentries.add(wordEntries);
                            Log.d("The entries: ", wordEntries.toString());
                        }
                    }
                    getImageUrl(0,
                            Allentries,
                            firebaseStorage,
                            category,
                            WORD_IMAGE_REFERENCE,
                            adapter);
                }else{
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setImageAdapter(adapter);
    }


    private void getImageUrl(final int count, final ArrayList<HashMap> AllEntries, final StorageReference firebaseStorage, final String category,
                             final String WORD_IMAGE_REFERENCE,
                             final ArrayListGridAdapter adapter
    ){
        if (count < AllEntries.size()) {


            String fileName = (String) AllEntries.get(count).get("fileName");
            final String word = (String) AllEntries.get(count).get("word");
            firebaseStorage.child(WORD_IMAGE_REFERENCE + "/" + category + "/" + fileName).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //lUrls.add(uri.toString());
                            adapter.addItem(uri.toString(), word);
                            getImageUrl(count + 1, AllEntries, firebaseStorage, category, WORD_IMAGE_REFERENCE, adapter);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Download error ", e.toString());
                }
            });
        }/*else{
            setImageAdapter(adapter);
        }*/
    }
}
