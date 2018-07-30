package org.tensorflow.demo.photoSearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.R;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.storage.FirebaseStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Created by mgo983 on 4/21/17.
 */

public class DetailActivity extends CommonDetailOpen{
    private ShareActionProvider mShareActionProvider;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private Uri imageFile = null;


    //public ArrayAdapter<String> adapter;
    //public ButtonTextAdapter btnAdapter;

    public DetailActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fagment_detail);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }


    //checks whether the user has the TTS data installed. If it is not, the user will be prompted to install it.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
    @Override
    public void onInit(int initStatus) {

        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.US);
            myTTS.setSpeechRate(0.5f);
        }
        captureAndSearchImage();
    }



    private Uri getImage() {
        Intent intent = this.getIntent();
        Uri newFile = (Uri) intent.getExtras().get(SelectActionFragment.EXTRA_IMAGE);
        Log.d("new file path ", newFile.toString());

        return newFile;

    }


    private void captureAndSearchImage() {

        imageFile = getImage();
        File imgFile = new File(imageFile.getPath());

        _FileName = imgFile.toString();

        if (imgFile.exists()) {
            //run CloudSight Search
            Log.d("Check Exist ", "Check Exist");
            DetailActivity.FetchImageDescription fetchImageDescription = new DetailActivity.FetchImageDescription(this);
            fetchImageDescription.execute(imgFile);
        }


    }


    private void getSymbol(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
    }




    public class FetchImageDescription extends AsyncTask<File, Void, /*String[]*/ CSGetResult> {

        private final String LOG_TAG = DetailActivity.FetchImageDescription.class.getSimpleName();

        private Context context;

        public FetchImageDescription(Context mContext){
            context = mContext;
        }

        @Override
        protected void onPostExecute(final CSGetResult Result /* String[] Result*/) {


            String searchResult = Result.getName();

            listOfWords = searchResult.split(" ");

            _FileName = imageFile.getPath().toString();


            Log.v("DetailActivity fname ", _FileName);

            //String[] FileNameArray = imageFile.toString().split("/");
            String[] FileNameArray = _FileName.split("/");

            TxtFileName = FileNameArray[FileNameArray.length - 1].replace(".jpg", ".txt");

            //Save the retrieved text
            writeToFile(searchResult,TxtFileName,context);

            //get preferred search engine
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefSearchParam = sharedPref.getString(getString(R.string.pref_search_key),getString(R.string.pref_search_default_value));
            //list
            adapter = new ButtonTextAdapter(context, myTTS, prefSearchParam) ;


            adapter.addImage(_FileName);
            adapter.addResult(searchResult);


            CheckInternetConnection checkInternetConnection = new CheckInternetConnection(context);
            if (checkInternetConnection.isNetworkConnected()) {
                //fetchClipArt
                FetchClipArt fetchClipArt = new FetchClipArt(adapter, context,prefSearchParam, listOfWords);
                fetchClipArt.execute(listOfWords);


                list = (ListView) findViewById(R.id.list_view_word);
                list.setAdapter(adapter);
            }


            ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_complete);
            progressBar.setVisibility(View.INVISIBLE);

        }


        @Override
        protected /*String[]*/ CSGetResult doInBackground(File... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if (params.length == 0){
                return null;
            }


            String API_KEY = "waWnmJu7yxqlJ_vKxcvoXg";

            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JsonFactory JSON_FACTORY = new JacksonFactory();

            try {

                CSApi api = new CSApi(
                        HTTP_TRANSPORT,
                        JSON_FACTORY,
                        API_KEY
                );

                CSPostConfig imageToPost = CSPostConfig.newBuilder()
                        .withImage(params[0]).build();

                CSPostResult portResult = api.postImage(imageToPost);



                System.out.println("Post result: " + portResult);

                try {
                    Thread.sleep(30000);
                }catch (InterruptedException e){
                    Log.e(LOG_TAG,"Error",e);
                }


                CSGetResult scoredResult = api.getImage(portResult);

                System.out.println(scoredResult);

                return scoredResult;


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

        }

    }



    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
    }


}
