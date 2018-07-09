package org.tensorflow.demo.photoSearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.photoSearch.Adapter.GridAdapter;
import org.tensorflow.demo.R;

import java.util.Locale;


/**
 * Created by mgo983 on 5/4/17.
 */

public class ImageExplanationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    public ImageExplanationActivity(){}
    public GridAdapter adapter;

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;


    public final static String EXTRA_DIALOG_IMAGE = "com.example.android.sunshine.extraImage";



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explainfragment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        String searchParam = intent.getStringExtra(ButtonTextAdapter.SEARCH_PARAM);
        String [] searchParams = {""};
        final String searchParamToUpperCase = searchParam.substring(0,1).toUpperCase() + searchParam.substring(1);
        searchParams[0] = searchParamToUpperCase;

        CheckInternetConnection checkInternetConnection = new CheckInternetConnection(this);

        //get preferred search engine
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String prefSearchParam = sharedPref.getString(getString(R.string.pref_search_key),getString(R.string.pref_search_default_value));
        //searchParams[1] = prefSearchParam;
        Log.v("The second",prefSearchParam);


        if (checkInternetConnection.isNetworkConnected()){

            FetchClipArt fetchClipArt = new FetchClipArt(adapter,this, prefSearchParam);
            fetchClipArt.execute(searchParams);
            adapter = fetchClipArt.getAdapter();
        }


        final TextView textView =  findViewById(R.id.grid_text);
        textView.setText(searchParamToUpperCase);

        ImageButton textView1 = findViewById(R.id.share_text);

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Is this a picture of the word \"" + searchParamToUpperCase + "\". Find more pictures of this word here.";
                String shareSubject = "I need help identifying the object in the picture";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });


        final GridView gridView =  findViewById(R.id.image_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {



                DialogFragment newFragment = new ImageDialog();
                Bundle bundle = new Bundle();
                TextView textInItem =  view.findViewById(R.id.list_item_word_textview);
                bundle.putString(EXTRA_DIALOG_IMAGE, /*textInItem.getText().toString()*/  ((GridAdapter)gridView.getAdapter()).getImageUrl(position));

                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"what?");
            }
        });

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);

                Intent args = this.getIntent();

                if (args != null) {

                    final ImageButton textView =  findViewById(R.id.speak_text);
                    final TextView textView1 =  findViewById(R.id.grid_text);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String  speech = textView1.getText().toString();
                            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                        }
                    });

                }

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
