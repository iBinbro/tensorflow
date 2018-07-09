package org.tensorflow.demo.photoSearch;


import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;



/**
 * Created by mgo983 on 4/24/17.
 */

public class OpenGalleryObjectActivity extends CommonDetailOpen {

    //public final static String ARG_OBJECT = "IMGFILENAME";

    public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";
    public final static String IMGFILEKEY = "com.example.android.sunshine.IMG_FILE_KEY";

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private String TAG = "OpenGalleryObject";

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fagment_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Prepare for text to speech
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //checks whether the user has the TTS data installed. If it is not, the user will be prompted to install it.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

                add_new_caption();


                //get preferred search engine
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String prefSearchParam = sharedPref.getString(getString(R.string.pref_search_key),getString(R.string.pref_search_default_value));

                myTTS = new TextToSpeech(this, this);



                ButtonTextAdapter adapter = new ButtonTextAdapter(this, myTTS, prefSearchParam);

                Intent args = this.getIntent();


                if (args != null) {

                    SharedPreferences sharedPreferences;
                    String FileName;
                    sharedPreferences = getApplicationContext().getSharedPreferences(IMGFILENAME, Context.MODE_PRIVATE);
                    _FileName = FileName = sharedPreferences.getString(IMGFILEKEY, null);
                    try{
                        String[] FileNameArray = FileName.split("/");
                        File imgFile = new File(FileName);

                        TxtFileName = FileNameArray[FileNameArray.length - 1].replace(".jpg", ".txt");
                        String TxtFileContent = readFromFile(this, TxtFileName);
                        listOfWords = TxtFileContent.split(" ");

                        Log.d("listOfWords", listOfWords + "");

                        // an Empty space if listOfWords is null

                        FetchClipArt fetchClipArt = new FetchClipArt(adapter,this ,prefSearchParam, listOfWords);

                        adapter.addImage(imgFile.toString());
                        Log.v("Textfile content is ", TxtFileContent);
                        Log.v("OpenGallery img fname ", _FileName);


                        if (! TxtFileContent.equals("") ){
                            adapter.addResult(TxtFileContent);
                            CheckInternetConnection checkInternetConnection = new CheckInternetConnection(this);
                            if (checkInternetConnection.isNetworkConnected())
                                fetchClipArt.execute(listOfWords);
                        }

                    }catch (NullPointerException e){
                        Log.e(TAG, ": " + e);
                    }

                }
                list = (ListView) this.findViewById(R.id.list_view_word);
                list.setAdapter(adapter);

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


    }

    private void add_new_caption(){


       /* final String message = "Enter new caption";
        final String editOrDelete = "edit";
        ImageButton edit_caption_btn = this.findViewById(R.id.edit_image_caption);
        edit_caption_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SafeAction newFragment = SafeAction.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(ButtonTextAdapter.EXTRA_SAFE_ACTION_MSG, message);
                bundle.putString(ButtonTextAdapter.EXTRA_SAFE_ACTION_MENU_ITEM, editOrDelete);

                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(),"SAFE_ACTION_MSG" );
            }
        });*/


    }

    private String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
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
