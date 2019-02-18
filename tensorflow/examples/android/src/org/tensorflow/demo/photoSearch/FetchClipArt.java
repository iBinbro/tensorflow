package org.tensorflow.demo.photoSearch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Color;
import org.tensorflow.demo.photoSearch.Adapter.AphasiaAdapter;
import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.photoSearch.Adapter.GridAdapter;
import org.tensorflow.demo.photoSearch.Adapter.WordCategoryAdapter;
import org.tensorflow.demo.photoSearch.Adapter.Words;
import org.tensorflow.demo.photoSearch.Evocation.Evocation;
import org.tensorflow.demo.photoSearch.data.AddWord;
import org.tensorflow.demo.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.json.JSONException;
import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Color;
import org.tensorflow.demo.photoSearch.Adapter.AphasiaAdapter;
import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.photoSearch.Adapter.GridAdapter;
import org.tensorflow.demo.photoSearch.Adapter.WordCategoryAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import opennlp.tools.stemmer.PorterStemmer;

/**
 * Created by mgo983 on 4/30/17.
 */

public class FetchClipArt extends AsyncTask<String[], Void, ArrayList<ArrayList<String>>> {
    private AphasiaAdapter adapter;
    private Context context;
    private String chooseEngine;
    private String[] listOfWords;
    private Color availableColors = new Color();

    public static final String ENGINE_PIXABAY = "1";
    public static final String ENGINE_OPENCLIPART = "2";

    //ArrayList<ArrayList<String>> ClipArtJson = new ArrayList<ArrayList<String>>();



    public FetchClipArt(ButtonTextAdapter newAdapter, Context newContext , String newChooseEngine, String[] _listOfWords ){
        adapter = newAdapter;
        chooseEngine = newChooseEngine;
        context = newContext;
        listOfWords = _listOfWords;
    }

    public FetchClipArt(GridAdapter newAdapter, Context newContext, String newChooseEngine)
    {
        adapter = newAdapter;
        context = newContext;
        chooseEngine = newChooseEngine;
    }


    private final String LOG_TAG = FetchClipArt.class.getSimpleName();

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> Result) {
        Log.d("I got here: ", "Post Execute");
        if (Result == null){
            Log.d("no results", "absolutely no results");
            return;

        }
        if (adapter instanceof ButtonTextAdapter){
            localSearch(listOfWords, Result, 0, listOfWords.length);
            hideProgressBar((ProgressBar) ((AppCompatActivity) context).findViewById(R.id.search_complete));

        }else if(adapter instanceof WordCategoryAdapter){
            final int FIRST_POSITION = 0;
                ArrayList<String> currResult = Result.get(FIRST_POSITION);
                if (currResult != null){
                    String searchString = currResult.get(FIRST_POSITION);
                    //localWordNetSearch(searchString);
                    localCategorySearch(searchString, Result, FIRST_POSITION);
                    //addInternetItem(searchString,Result,FIRST_POSITION);

                }


        }else {
            String [] ImageUrls = new String [1];
            ArrayList<String> currResult = Result.get(0);
            String searchString = currResult.get(0);
            if (!availableColors.searchColor(searchString.toLowerCase())) {
                ImageUrls = parseJSONString(Result);
                orderImages(ImageUrls, searchString);

            }
            else{
                ImageUrls[0] = searchString ;
                setGridViewAdapter(ImageUrls);
                hideProgressBar((ProgressBar) ((AppCompatActivity) context).findViewById(R.id.explanationProgress));

            }
        }
    }

    private void orderImages(String[] ImageUrls, String searchString){

        PorterStemmer stemmer = new PorterStemmer();
        String word = stemmer.stem(searchString);
        setGridViewAdapter(ImageUrls);
        ProgressBar progressBar = (ProgressBar) ((AppCompatActivity) context).findViewById(R.id.explanationProgress);
        hideProgressBar(progressBar);

        //CBIR tryClarify = new CBIR(word, (ImageGridAdapter) adapter, context);
        //tryClarify.execute(ImageUrls);

        //String[] orderedImageUrls = ;
        //return orderedImageUrls;
    }

    private void setGridViewAdapter(String[] ImageUrls){
        if (ImageUrls != null){
            adapter = new GridAdapter(context, ImageUrls /*ImgStringArr*/);
            GridView gridView = (GridView) ((AppCompatActivity) context).findViewById(R.id.image_gridview);
            gridView.setAdapter(adapter);

        }
    }

    private void hideProgressBar(ProgressBar currProgressBar){
        currProgressBar.setVisibility(View.INVISIBLE);
    }

    private String[] parseJSONString(ArrayList<ArrayList<String>> Result){
        try{
            if (chooseEngine .equals("1")){
                PixabayJSONHandler jsonHandler = new PixabayJSONHandler();

                return jsonHandler.getImageUrl(Result.get(0).get(1));
            }else{
                OpenClipArtJSONHandler jsonHandler = new OpenClipArtJSONHandler();
                return jsonHandler.getImageUrl(Result.get(0).get(1));
            }

        }catch(JSONException e){
            Log.e("JSON Error: ", e.toString());
        }
        return null;
    }

    @Override
    protected  ArrayList<ArrayList<String>> doInBackground(String[]...params) {

        ArrayList<ArrayList<String>> ClipArtJson = new ArrayList<ArrayList<String>>();

        if (params.length == 0)
            return null;
        switch (chooseEngine){
            case ENGINE_PIXABAY:
                if (adapter instanceof  ButtonTextAdapter){
                    for (int i = 0; i < params[0].length; i++){

                        if (!addColorDataIfColor(ClipArtJson, params[0][i])){
                            ClipArtJson.add(getJSONData(buildPixaBayUri("https://pixabay.com/api/","5321405-e3d51a927066916f670cf60c0",params[0][i]),params[0][i]));
                        }
                    }
                }else if(adapter instanceof WordCategoryAdapter){
                    Log.d("Instance of ", "WordCategoryAdapter");
                    if(!addColorDataIfColor(ClipArtJson, params[0][0].toLowerCase())){
                        ClipArtJson.add(getJSONData(buildPixaBayUri("https://pixabay.com/api/","5321405-e3d51a927066916f670cf60c0",params[0][0]), params[0][0]));
                    }
                }
                else{
                    Log.d("Instance of ", "GridAdapter");
                    if (!addColorDataIfColor(ClipArtJson, params[0][0].toLowerCase())){
                        ClipArtJson.add(getJSONData(buildPixaBayUri("https://pixabay.com/api/","5321405-e3d51a927066916f670cf60c0",params[0][0]),params[0][0]));
                    }
                }
                break;
            case ENGINE_OPENCLIPART:
                if (adapter instanceof  ButtonTextAdapter){
                    for (int i = 0; i < params[0].length; i++){
                        if (!addColorDataIfColor(ClipArtJson, params[0][i])){
                            ClipArtJson.add(getJSONData(buildOpenClipArtUri("https://openclipart.org/search/json/",params[0][i], "1"),params[0][i]));
                        }
                    }
                }else if(adapter instanceof WordCategoryAdapter){
                    Log.d("Instance of ", "WordCategoryAdapter");
                    if(!addColorDataIfColor(ClipArtJson, params[0][0].toLowerCase())){
                        ClipArtJson.add(getJSONData(buildOpenClipArtUri("https://openclipart.org/search/json/",params[0][0], "2"), params[0][0]));
                    }
                }else{
                    if (!addColorDataIfColor(ClipArtJson, params[0][0].toLowerCase())){
                        ClipArtJson.add(getJSONData(buildOpenClipArtUri("https://openclipart.org/search/json/",params[0][0], "10"), params[0][0]));
                    }
                }
                break;

        }

        return ClipArtJson;
    }

    public GridAdapter getAdapter(){ return (GridAdapter) adapter;}


    private boolean addColorDataIfColor(ArrayList<ArrayList<String>> ClipArtJSON, String searchValue) {
        ArrayList<String> color = new ArrayList<>();

        if (availableColors.searchColor(searchValue)){
            color.add(searchValue);
            color.add(searchValue);
            ClipArtJSON.add(color);
            return true;
        }else{
            return false;
        }
    }

    private Uri buildOpenClipArtUri(String baseUrl, String queryParameter, String amount){
        final String QUERY = "query";
        final String AMOUNT = "amount";

        final String SORT = "sort";

        Uri buildUri;

        buildUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(QUERY, queryParameter)
                .appendQueryParameter(AMOUNT,amount)
                .appendQueryParameter(SORT, "downloads")
                .build();
        return buildUri;


    }


    private Uri buildPixaBayUri(String baseUrl, String apiKey, String queryParameter){
        final String API_KEY = "key";
        final String QUERY = "q";

        Uri buildUri;

        buildUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_KEY,apiKey)
                .appendQueryParameter(QUERY, queryParameter)

                .build();
        return buildUri;
    }


    private ArrayList<String> getJSONData(Uri SearchUri, String queryParameter /*String baseUrl, String apiKey, String queryParameter, String amount*/){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        ArrayList<String> ClipArtJsonStr = new ArrayList<String>();

        try{

            Uri buildUri;

            buildUri = SearchUri;

            URL url = new URL(buildUri.toString());
            Log.v(LOG_TAG,"The built Uri " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return null;
            }

            ClipArtJsonStr.add(queryParameter);
            ClipArtJsonStr.add(buffer.toString());


            return ClipArtJsonStr;

        }catch (IOException e){
            Log.e(LOG_TAG, "Error", e);
            return null;
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                }catch (final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

    }



    private void localSearch(final String[] listOfWords, final ArrayList<ArrayList<String>> Result, final int position, final int lengthOfResult ){

        if (position < lengthOfResult){
            FirebaseUser firebaseUser = OpenGalleryObjectActivity.firebaseAuth.getCurrentUser();
            if (firebaseUser == null){
                ((OpenGalleryObjectActivity) context).signInAnonymously();
            }
            final String searchString = listOfWords[position];
            final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child(searchString.toLowerCase()).limitToFirst(1);
            final String WORD_IMAGE_REFERENCE  = "symbols";
            Log.d("the Query", mDatabaseQuery.toString());

            mDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            String wordEntries = (String) child.getValue();
                            String[] getFileName = wordEntries.split("/");
                            Log.d("The entries: ", wordEntries);
                            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                            firebaseStorage.child( WORD_IMAGE_REFERENCE + "/" + getFileName[0] + "/" + getFileName[2]).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            adapter.addItem(searchString + "&&" + uri.toString());
                                            int newPosition = position + 1;
                                            localSearch(listOfWords, Result, newPosition, lengthOfResult);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    final int QUERY_PARAMETER = 0;
                                    final int URL_JSON = 1;
                                    ArrayList<String> currResult = Result.get(position);
                                    adapter.addItem(currResult.get(QUERY_PARAMETER) + "&&" + currResult.get(URL_JSON));
                                    localSearch(listOfWords, Result, position + 1, lengthOfResult);
                                    Log.d("Download error ", e.toString());
                                }
                            });
                        }

                    }else{
                        final int QUERY_PARAMETER = 0;
                        final int URL_JSON = 1;
                        try{
                            if (Result != null && Result.size() != 0){
                                Log.e("positionE", position + "" + Result.toString());

                                ArrayList<String> currResult = Result.get(position);
                                if (currResult != null){
                                    adapter.addItem(currResult.get(QUERY_PARAMETER) + "&&" + currResult.get(URL_JSON));
                                    localSearch(listOfWords, Result, position + 1, lengthOfResult);
                                }
                            }
                            else if(Result.size() == 0){
                                localSearch(listOfWords, Result, position + 1, lengthOfResult);
                            }

                        }catch (Exception e){
                            Log.e("position error", position + "");
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void localWordNetSearch(String searchString){
        // first find out if it is in the evocation database;
        Evocation evocation = new Evocation(context);
        ArrayList<String> evocative_words = evocation.findEvocationWords(searchString);
        getImageFromFirebase(evocative_words, 0);
    }

    private void getImageFromFirebase(final ArrayList<String> evocative_words, final int position){
        //grid images for evocative words
            if (position < evocative_words.size()){
                final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child(evocative_words.get(position).toLowerCase()).limitToFirst(1);
                final String WORD_IMAGE_REFERENCE  = "symbols";
                mDatabaseQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                String wordEntries = (String) child.getValue();
                                String[] getFileName = wordEntries.split("/");
                                Log.d("The entries: ", wordEntries);
                                StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
                                final String category = getFileName[0];
                                String fileName = getFileName[2];

                                firebaseStorage.child( WORD_IMAGE_REFERENCE + "/" + getFileName[0] + "/" + getFileName[2]).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Words words = new Words(evocative_words.get(position), uri.toString(), category, "LOCAL");

                                                adapter.addItem(category, words);
                                                int newPosition = position + 1;
                                                getImageFromFirebase(evocative_words, newPosition);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        final int QUERY_PARAMETER = 0;
                                        final int URL_JSON = 1;
                                        //ArrayList<String> currResult = evocative_words.get(position);
                                        adapter.addItem(evocative_words.get(QUERY_PARAMETER) + "&&" + evocative_words.get(URL_JSON));
                                        getImageFromFirebase(evocative_words, position + 1);
                                        Log.d("Download error ", e.toString());

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }



    }

    private void localCategorySearch(final String searchString, final ArrayList Result, final int position){

            FirebaseUser firebaseUser = WordCategoriesActivity.firebaseAuth.getCurrentUser();
            if (firebaseUser == null){
                ((WordCategoriesActivity) context).signInAnonymously();
            }
            final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child(searchString.toLowerCase());
            final String WORD_IMAGE_REFERENCE  = "symbols";
            Log.d("the Query", mDatabaseQuery.toString());

            mDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final HashMap wordsHashMap = new HashMap<>();
                        final ArrayList count = new ArrayList();


                        for (final DataSnapshot child: dataSnapshot.getChildren()){
                            String wordEntries = (String) child.getValue();
                            String[] getFileName = wordEntries.split("/");
                            Log.d("The category entries: ", wordEntries);
                            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
                            final String category = getFileName[0];
                            String fileName = getFileName[2];


                            firebaseStorage.child( WORD_IMAGE_REFERENCE + "/" + category + "/" + fileName).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // get category, word and url;
                                            if (wordsHashMap.get(category) == null){
                                                Words words = new Words(searchString, uri.toString(), category, "LOCAL");
                                                wordsHashMap.put(category, words);
                                                adapter.addItem(category, words);
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //addInternetItem(searchString, Result, position);
                                    Log.d("Download error ", e.toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    count.add("counter");
                                    if (count.size() == dataSnapshot.getChildrenCount()) addInternetItem(searchString, Result, position);
                                }
                            });
                        }
                        //after going through the data
                        //addInternetItem(searchString,Result,position);

                    }else{
                        addInternetItem(searchString,Result,position);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



    }

    private void addInternetItem(String searchString, ArrayList<ArrayList<String>> Result, int position){
        final int URL_JSON = 1;
        ArrayList<String> currResult = Result.get(position);
        Words words = new Words(searchString, currResult.get(URL_JSON), "INTERNET", "INTERNET");
        adapter.addItem("INTERNET", words);
    }


    private void wordNetSearch(String[] listOfWords){

    }

    private Boolean isWordNet(){

        // tests to know the preferred category organization - Smarty Symbols or WordNet

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String prefDatabaseParam = sharedPref.getString("database", "1");

        return prefDatabaseParam.equals("1");
    }
}
